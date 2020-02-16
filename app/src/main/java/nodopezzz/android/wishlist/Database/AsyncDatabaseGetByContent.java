package nodopezzz.android.wishlist.Database;

import android.os.AsyncTask;

import java.util.List;

import nodopezzz.android.wishlist.GeneralSingleton;

abstract public class AsyncDatabaseGetByContent extends AsyncTask<String, Void, List<DBItem>> {

    public abstract void onPostGet(List<DBItem> items);

    @Override
    protected List<DBItem> doInBackground(String... content) {
        Database db = GeneralSingleton.getInstance().getDatabase();
        DBItemDao dao = db.dbItemDao();
        return dao.getAllByContent(content[0]);
    }

    @Override
    protected void onPostExecute(List<DBItem> items) {
        onPostGet(items);
    }
}
