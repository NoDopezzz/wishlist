package nodopezzz.android.wishlist.Network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";

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

    private Map<T, String> mMap = new HashMap<>();
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

    public void queueMessage(String url, T target){
        if(url == null){
            mMap.remove(target);
        } else {
            mMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_CODE, target).sendToTarget();
        }
    }

    private void handle(final T target){
        final String url = mMap.get(target);

        try {
            if(url == null) return;

            try {
                final Bitmap bitmap;
                byte[] bytes = UrlDownloader.getResponseByte(url);
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (url != mMap.get(target) || mHasQuit) return;

                        mMap.remove(target);
                        if (listener != null) {
                            listener.onDownloaded(target, bitmap, url);
                        }
                    }
                });
            } catch(OutOfMemoryError error){
                mMap.remove(target);
                queueMessage(url, target);
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
