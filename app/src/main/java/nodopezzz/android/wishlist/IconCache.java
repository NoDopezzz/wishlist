package nodopezzz.android.wishlist;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

public class IconCache extends LruCache<String, Bitmap> {

    private static IconCache sIconCache;

    public static IconCache get(Context context){
        int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        if(sIconCache == null){
            sIconCache = new IconCache(cacheSize);
        }
        return sIconCache;
    }

    public IconCache(int cacheSize) {
        super(cacheSize);
    }

    public Bitmap getBitmapFromMemory(String key){
        return this.get(key);
    }

    public void setBitmapToMemory(Bitmap drawable, String key){
        if(getBitmapFromMemory(key) == null){
            this.put(key, drawable);
        }
    }
}