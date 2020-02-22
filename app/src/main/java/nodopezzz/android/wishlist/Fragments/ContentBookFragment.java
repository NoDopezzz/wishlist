package nodopezzz.android.wishlist.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.IOException;

import nodopezzz.android.wishlist.Activities.SearchActivity;
import nodopezzz.android.wishlist.Content;
import nodopezzz.android.wishlist.Database.AsyncDatabaseDelete;
import nodopezzz.android.wishlist.Database.AsyncDatabaseInsert;
import nodopezzz.android.wishlist.Database.DBItem;
import nodopezzz.android.wishlist.Database.DBItemDao;
import nodopezzz.android.wishlist.Database.Database;
import nodopezzz.android.wishlist.Utils.GeneralSingleton;
import nodopezzz.android.wishlist.APIs.GoogleBooksAPI;
import nodopezzz.android.wishlist.Models.Book;
import nodopezzz.android.wishlist.Network.UrlDownloader;
import nodopezzz.android.wishlist.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentBookFragment extends Fragment {
    public static final String TAG = "ContentBookFragment";

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_ID = "ARG_ID";

    public static ContentBookFragment newInstance(String title, String id){
        ContentBookFragment fragment = new ContentBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_ID, id);

        fragment.setArguments(args);
        return fragment;
    }

    private TextView mTitleView;
    private TextView mAuthorView;
    private ExpandableTextView mOverviewView;
    private RelativeLayout mProgressBarLayout;
    private TextView mPublisherView;
    private ImageView mImageView;
    private Button mButtonReader;
    private LinearLayout mBookInfoLayout;
    private Toolbar mToolbar;

    private Book mBook;

    private boolean mIsSaved = false;
    private Database mDatabase;
    private DBItemDao mItemDao;

    private String mId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDatabase = GeneralSingleton.getInstance().getDatabase();
        mItemDao = mDatabase.dbItemDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_content, container, false);

        Bundle args = getArguments();
        if(args == null){
            closeFragment();
        }
        mId = args.getString(ARG_ID);

        mAuthorView = v.findViewById(R.id.content_book_author);
        mOverviewView = v.findViewById(R.id.content_book_overview);
        mImageView = v.findViewById(R.id.content_book_image);
        mButtonReader = v.findViewById(R.id.content_book_button_reader);
        mProgressBarLayout = v.findViewById(R.id.content_book_progressbar);
        mTitleView = v.findViewById(R.id.content_book_title);
        mPublisherView = v.findViewById(R.id.content_book_publisher);
        mBookInfoLayout = v.findViewById(R.id.content_book_info);
        mToolbar = v.findViewById(R.id.content_book_toolbar);

        new GetBookDB().execute();
        getBook();

        return v;
    }

    private void getBook(){
        GoogleBooksAPI.getInstance().getAdapter().getBook(mId).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                mBook = response.body();
                mProgressBarLayout.setVisibility(View.GONE);
                mBookInfoLayout.setVisibility(View.VISIBLE);
                mOverviewView.setVisibility(View.VISIBLE);

                mTitleView.setText(mBook.getVolumeInfo().getTitle());

                StringBuilder authorsBuilder = new StringBuilder();
                String authors = "";
                for (int i = 0; i < mBook.getVolumeInfo().getAuthors().size(); i++) {
                    authorsBuilder.append(mBook.getVolumeInfo().getAuthors().get(i)).append(", ");
                }
                if(authorsBuilder.length() > 0){
                    authors = authorsBuilder.substring(0, authorsBuilder.length() - 2);
                }

                mAuthorView.setText(authors);
                mOverviewView.setText(mBook.getVolumeInfo().getDescription());
                mPublisherView.setText(mBook.getVolumeInfo().getPublisher());

                if(!mBook.getAccessInfo().getAccessViewStatus().equals("NONE")
                        && mBook.getAccessInfo().getWebReaderLink() != null){
                    mButtonReader.setVisibility(View.VISIBLE);
                    mButtonReader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(mBook.getAccessInfo().getWebReaderLink()));
                            startActivity(intent);
                        }
                    });
                }
                new GetImage().execute();
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {

            }
        });
    }

    private class GetBookDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... strings) {
            mIsSaved = mItemDao.getMovieById(mId, Content.BOOK.name()) != null;
            return null;
        }

        @Override
        protected void onPostExecute(Void argVoid) {
            initToolbar();
        }
    }

    private class GetImage extends AsyncTask<Void, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                String url = "https://books.google.com/books/content/images/frontcover/" + mBook.getId() + "?fife=w400-h600";
                byte[] bytes = UrlDownloader.getResponseByte(url);

                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (IOException | OutOfMemoryError e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.content_book_menu, menu);
        MenuItem item = menu.findItem(R.id.content_book_menu_marker);
        if(mIsSaved){
            item.setIcon(R.drawable.ic_bookmark_black_24dp);
        } else{
            item.setIcon(R.drawable.ic_bookmark_border_black_24dp);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initToolbar(){
        if(getActivity() == null) return;
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

        mToolbar.inflateMenu(R.menu.content_book_menu);
        mToolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.content_book_menu_marker){
            if(mIsSaved){
                try {
                    menuItem.setIcon(R.drawable.ic_bookmark_border_black_24dp);

                    DBItem item = createItem();
                    new AsyncDatabaseDelete() {
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            mIsSaved = false;
                            showSnackbar(R.string.state_deleted);
                            GeneralSingleton.getInstance().getInternalStorage().deleteImage(mBook.getId(), Content.BOOK.name());
                        }
                    }.execute(item);
                } catch(OutOfMemoryError e){
                    e.printStackTrace();
                    showSnackbar(R.string.error_saving);
                    menuItem.setIcon(R.drawable.ic_bookmark_black_24dp);
                }

            } else{
                try {
                    menuItem.setIcon(R.drawable.ic_bookmark_black_24dp);

                    DBItem item = createItem();

                    new AsyncDatabaseInsert() {
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            mIsSaved = true;
                            showSnackbar(R.string.state_saved);
                            GeneralSingleton.getInstance().getInternalStorage().saveToInternalStorage(mBook.getId(), "https://books.google.com/books/content/images/frontcover/" + mBook.getId() + "?fife=w400-h600", Content.BOOK.name());
                        }
                    }.execute(item);
                } catch(OutOfMemoryError e){
                    e.printStackTrace();
                    menuItem.setIcon(R.drawable.ic_bookmark_border_black_24dp);
                    showSnackbar(R.string.error_saving);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void showSnackbar(int resId){
        Snackbar.make(getView(), resId, Snackbar.LENGTH_LONG).show();
    }

    private DBItem createItem(){
        DBItem item = new DBItem();
        item.setContent(Content.BOOK.name());
        item.setId(mBook.getId());
        item.setTitle(mBook.getVolumeInfo().getTitle());
        if(mBook.getVolumeInfo().getAuthors() != null) {
            item.setSubtitle(mBook.getVolumeInfo().getAuthors().get(0));
        }
        return item;
    }

    private void closeFragment(){
        if(getActivity() == null) return;
        getActivity().setResult(SearchActivity.RESULT_CODE_ERROR);
        Log.i(TAG, "closeFragment");
        getActivity().finish();
    }
}
