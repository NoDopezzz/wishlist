package nodopezzz.android.wishlist.Database;

import android.os.AsyncTask;

import java.util.List;

import nodopezzz.android.wishlist.Content;
import nodopezzz.android.wishlist.Utils.GeneralSingleton;

abstract public class AsyncDatabaseGetByContent extends AsyncTask<Content, Void, List<DBItem>> {

    public abstract void onPostGet(List<DBItem> items);

    @Override
    protected List<DBItem> doInBackground(Content... content) {
        try {
            Database db = GeneralSingleton.getInstance().getDatabase();
            DBItemDao dao = db.dbItemDao();
            return dao.getAllByContent(content[0].name());
        } catch(RuntimeException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<DBItem> items) {
        onPostGet(items);
    }
}
