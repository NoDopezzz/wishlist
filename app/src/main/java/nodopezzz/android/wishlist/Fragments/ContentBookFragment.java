package nodopezzz.android.wishlist.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.IOException;

import nodopezzz.android.wishlist.GoogleBooksAPI;
import nodopezzz.android.wishlist.Models.Book;
import nodopezzz.android.wishlist.Network.UrlDownloader;
import nodopezzz.android.wishlist.R;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_content, container, false);

        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String id = args.getString(ARG_ID);

        mAuthorView = v.findViewById(R.id.content_book_author);
        mOverviewView = v.findViewById(R.id.content_book_overview);
        mImageView = v.findViewById(R.id.content_book_image);
        mButtonReader = v.findViewById(R.id.content_book_button_reader);
        mProgressBarLayout = v.findViewById(R.id.content_book_progressbar);
        mTitleView = v.findViewById(R.id.content_book_title);
        mPublisherView = v.findViewById(R.id.content_book_publisher);
        mBookInfoLayout = v.findViewById(R.id.content_book_info);
        mToolbar = v.findViewById(R.id.content_book_toolbar);

        new GetBook().execute(id);
        initToolbar();

        return v;
    }

    private class GetBook extends AsyncTask<String, Void, Book>{

        @Override
        protected Book doInBackground(String... strings) {
            return GoogleBooksAPI.getBook(strings[0]);
        }

        @Override
        protected void onPostExecute(Book book) {
            mBook = book;
            if(book == null) return;

            mProgressBarLayout.setVisibility(View.GONE);
            mBookInfoLayout.setVisibility(View.VISIBLE);
            mOverviewView.setVisibility(View.VISIBLE);

            mTitleView.setText(mBook.getTitle());
            mAuthorView.setText(mBook.getAuthors());
            mOverviewView.setText(mBook.getOverview());
            mPublisherView.setText(mBook.getPublisher());

            if(!mBook.getUrlBook().equals("")){
                mButtonReader.setVisibility(View.VISIBLE);
                mButtonReader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(mBook.getUrlBook()));
                        startActivity(intent);
                    }
                });
            }

            new GetImage().execute();
        }
    }

    private class GetImage extends AsyncTask<Void, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                String url = mBook.getUrlImage();
                if(url == null){
                    url = mBook.getThumbnailUrl();
                }
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

    private void initToolbar(){

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

        mToolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.content_book_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //TODO
        return super.onOptionsItemSelected(item);
    }
}
