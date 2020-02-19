package nodopezzz.android.wishlist.Utils;

import android.app.Application;

import androidx.room.Room;

import nodopezzz.android.wishlist.Database.Database;
import nodopezzz.android.wishlist.MemoryUtils.IconCache;
import nodopezzz.android.wishlist.MemoryUtils.InternalStorage;

public class GeneralSingleton extends Application {

    public static GeneralSingleton instance;

    private IconCache mIconCache;
    private Database mDatabase;
    private InternalStorage mInternalStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mIconCache = IconCache.get(this);
        mDatabase = Room.databaseBuilder(this, Database.class, "database").build();
        mInternalStorage = new InternalStorage(this);
    }

    public static GeneralSingleton getInstance(){
        return instance;
    }

    public IconCache getIconCache(){
        return mIconCache;
    }
    public Database getDatabase(){ return mDatabase; }
    public InternalStorage getInternalStorage(){ return mInternalStorage; }
}
