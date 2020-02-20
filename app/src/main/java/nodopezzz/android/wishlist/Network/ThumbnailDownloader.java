package nodopezzz.android.wishlist.Network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nodopezzz.android.wishlist.MemoryUtils.ImageSizeCalculator;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

    private class TargetOptions{
        public TargetOptions(String urlImage, int reqWidth, int reqHeight) {
            this.reqWidth = reqWidth;
            this.reqHeight = reqHeight;
            this.urlImage = urlImage;
        }

        public int reqWidth;
        public int reqHeight;
        public String urlImage;
    }

    public void setListener(DownloadedListener listener) {
        this.listener = listener;
    }

    public interface DownloadedListener<T>{
        public void onDownloaded(T target, Bitmap image, String url);
    }

    private static final int MESSAGE_CODE = 0;

    private Handler mResponseHandler;
    private Handler mRequestHandler;

    private DownloadedListener listener;

    private Map<T, TargetOptions> mMap = new HashMap<>();
    private boolean mHasQuit = false;

    public ThumbnailDownloader(String name, Handler responseHandler) {
        super(name);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == MESSAGE_CODE){
                    T target = (T)msg.obj;
                    handle(target);
                }
            }
        };
    }

    public void queueMessage(String url, T target, int reqWidth, int reqHeight){
        if(url == null) mMap.remove(target);

        TargetOptions options = new TargetOptions(url, reqWidth, reqHeight);
        mMap.put(target, options);
        mRequestHandler.obtainMessage(MESSAGE_CODE, target).sendToTarget();
    }

    private void handle(final T target){
        if(mMap.get(target) == null) return;
        final String url = mMap.get(target).urlImage;
        int reqWidth = mMap.get(target).reqWidth;
        int reqHeight = mMap.get(target).reqHeight;

        try {
            if(url == null) return;

            try {
                final Bitmap bitmap;
                byte[] bytes = UrlDownloader.getResponseByte(url);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                options.inSampleSize = ImageSizeCalculator.calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mMap.get(target) == null || !url.equals(mMap.get(target).urlImage) || mHasQuit) return;

                        mMap.remove(target);
                        if (listener != null) {
                            listener.onDownloaded(target, bitmap, url);
                        }
                    }
                });
            } catch(OutOfMemoryError error){
                mMap.remove(target);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_CODE);
        mMap.clear();
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
}
