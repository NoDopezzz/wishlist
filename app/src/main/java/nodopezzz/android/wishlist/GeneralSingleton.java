package nodopezzz.android.wishlist;

import android.app.Application;

public class GeneralSingleton extends Application {

    public static GeneralSingleton instance;

    private IconCache mIconCache;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mIconCache = IconCache.get(this);
    }

    public static GeneralSingleton getInstance(){
        return instance;
    }

    public IconCache getIconCache(){
        return mIconCache;
    }
}
