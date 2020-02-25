package nodopezzz.android.wishlist.Utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.Locale;

import nodopezzz.android.wishlist.Database.Database;
import nodopezzz.android.wishlist.MemoryUtils.IconCache;
import nodopezzz.android.wishlist.MemoryUtils.InternalStorage;

public class GeneralSingleton extends Application {

    public static GeneralSingleton instance;
    public static String LANGUAGE_CODE = "en";

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

    public static void setLanguageCode(Context context){
        LANGUAGE_CODE = Locale.getDefault().getISO3Country();
        Log.i("random", "code: " + LANGUAGE_CODE);
    }

    public IconCache getIconCache(){
        return mIconCache;
    }
    public Database getDatabase(){ return mDatabase; }
    public InternalStorage getInternalStorage(){ return mInternalStorage; }
}
