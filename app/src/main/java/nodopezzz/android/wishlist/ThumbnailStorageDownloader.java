package nodopezzz.android.wishlist;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ThumbnailStorageDownloader<T> extends HandlerThread {

    private static final String TAG = "StorageDownloader";

    public interface OnPostDownloaded<T>{
        public void onPostDownloaded(T target, Bitmap bitmap, String path);
    }

    private OnPostDownloaded mCallback;
    private Handler mResponse;
    private Handler mRequest;

    private int MESSAGE_CODE = 0;
    private Map<T, String[]> mMap = new HashMap<>();
    private boolean mHasQuit = false;

    public ThumbnailStorageDownloader(String name, OnPostDownloaded callback, Handler response) {
        super(name);
        mCallback = callback;
        mResponse = response;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequest = new Handler(){
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

    public void putInQueue(T target, String name, String content){
        Log.i(TAG, name + " " + content);
        mMap.put(target, new String[]{name, content});
        mRequest.obtainMessage(MESSAGE_CODE, target).sendToTarget();
    }

    private void handle(final T target){
        final String[] args = mMap.get(target);
        final Bitmap bitmap = GeneralSingleton.getInstance().getInternalStorage().loadImage(args[0], args[1]);
        mResponse.post(new Runnable() {
            @Override
            public void run() {
                if(mHasQuit) return;
                Log.i(TAG, args[0] + args[1]);
                mCallback.onPostDownloaded(target, bitmap, args[0] + "_" + args[1]);
                mMap.remove(target);
            }
        });
    }

    public void clearQueue(){
        mRequest.removeMessages(MESSAGE_CODE);
        mMap.clear();
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
}
