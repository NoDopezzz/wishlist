package nodopezzz.android.wishlist.Database;

import android.os.AsyncTask;

import nodopezzz.android.wishlist.Utils.GeneralSingleton;

public class AsyncDatabaseInsert extends AsyncTask<DBItem, Void, Void> {

    @Override
    protected Void doInBackground(DBItem... dbItems) {
        try {
            Database db = GeneralSingleton.getInstance().getDatabase();
            DBItemDao dao = db.dbItemDao();
            dao.insert(dbItems[0]);
        } catch(RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
}