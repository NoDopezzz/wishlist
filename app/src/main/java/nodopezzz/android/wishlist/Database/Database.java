package nodopezzz.android.wishlist.Database;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {DBItem.class}, version = 1, exportSchema = false)
abstract public class Database extends RoomDatabase {
    public abstract DBItemDao dbItemDao();
}
