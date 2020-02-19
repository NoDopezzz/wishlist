package nodopezzz.android.wishlist.Database;

import android.os.AsyncTask;

import nodopezzz.android.wishlist.Utils.GeneralSingleton;

public class AsyncDatabaseDelete extends AsyncTask<DBItem, Void, Void> {

    @Override
    protected Void doInBackground(DBItem... dbItems) {
        Database db = GeneralSingleton.getInstance().getDatabase();
        DBItemDao dao = db.dbItemDao();
        dao.delete(dbItems[0]);
        return null;
    }
}