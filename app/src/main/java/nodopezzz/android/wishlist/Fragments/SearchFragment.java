package nodopezzz.android.wishlist.Fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nodopezzz.android.wishlist.Activities.ContentActivity;
import nodopezzz.android.wishlist.IconCache;
import nodopezzz.android.wishlist.OnScrolled;
import nodopezzz.android.wishlist.R;
import nodopezzz.android.wishlist.Models.SearchItem;
import nodopezzz.android.wishlist.TMDBAPI;
import nodopezzz.android.wishlist.Network.ThumbnailDownloader;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private static final String ARG_CONTENT = "ARG_CONTENT";

    public static SearchFragment newInstance(String content){
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    private enum State{
        EMPTY,
        SEARCHING,
        NOTHING,
        COMPLETE,
    }

    private State mCurrentState;

    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private SearchListAdapter mSearchAdapter;

    private String mContent;

    private int mPage = 1;
    private String mQuery;
    private List<SearchItem> mSearchItems = new ArrayList<>();
    private int mPreviousSize = 0;
    private GetSearchResult mAsyncTaskGetSearchResult;

    private RelativeLayout mProgressBarLayout;
    private RelativeLayout mEmptyQueryLayout;
    private RelativeLayout mNothingFoundLayout;

    private ThumbnailDownloader<SearchListAdapter.SearchItemHolder> mThumbnailDownloader;
    private IconCache mIconCache;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIconCache = IconCache.get(getActivity());
        mThumbnailDownloader = new ThumbnailDownloader<>("ThumbnailDownloader", new Handler());
        mThumbnailDownloader.setListener(new ThumbnailDownloader.DownloadedListener<SearchListAdapter.SearchItemHolder>() {
            @Override
            public void onDownloaded(SearchListAdapter.SearchItemHolder target, Bitmap image, String sUrl) {
                Drawable drawable = new BitmapDrawable(getResources(), image);
                mIconCache.setBitmapToMemory(image, sUrl);
                target.bindImage(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchView = v.findViewById(R.id.search_view);
        mRecyclerView = v.findViewById(R.id.search_items_recycler_view);
        mProgressBarLayout = v.findViewById(R.id.search_progress_bar_layout);
        mEmptyQueryLayout = v.findViewById(R.id.search_empty_layout);
        mNothingFoundLayout = v.findViewById(R.id.nothing_found_search_layout);

        mAsyncTaskGetSearchResult = new GetSearchResult();

        if(getArguments() != null && getArguments().getString(ARG_CONTENT) != null){
            mContent = getArguments().getString(ARG_CONTENT);
        }

        initUI();
        updateUI();

        return v;
    }

    private class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchHolder>{

        private static final int VIEW_TYPE_DATA = 1;
        private static final int VIEW_TYPE_PROGRESS = 2;

        @NonNull
        @Override
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if(viewType == VIEW_TYPE_DATA) {
                return new SearchItemHolder(inflater.inflate(R.layout.item_search, parent, false));
            } else{
                return new SearchProgressHolder(inflater.inflate(R.layout.item_progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
            if(holder instanceof SearchItemHolder) {
                holder.bindView(position);
            }
        }

        @Override
        public int getItemCount() {
            return mSearchItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(mSearchItems.get(position) == null){
                return VIEW_TYPE_PROGRESS;
            }
            return VIEW_TYPE_DATA;
        }

        private abstract class SearchHolder extends RecyclerView.ViewHolder {
            public SearchHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void bindView(int position){}
        }

        private class SearchProgressHolder extends SearchHolder{

            public SearchProgressHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        private class SearchItemHolder extends SearchHolder implements View.OnClickListener {

            private ImageView mImagePoster;
            private TextView mTextTitle;
            private TextView mTextDate;
            private TextView mTextOverview;

            private SearchItem mItem;

            public SearchItemHolder(@NonNull View itemView) {
                super(itemView);

                mImagePoster = itemView.findViewById(R.id.search_item_poster);
                mTextTitle = itemView.findViewById(R.id.search_item_title);
                mTextDate = itemView.findViewById(R.id.search_item_date);
                mTextOverview = itemView.findViewById(R.id.search_item_overview);
            }

            @Override
            public void bindView(int position){

                itemView.setOnClickListener(this);
                mItem = mSearchItems.get(position);
                mTextTitle.setText(mItem.getTitle());
                mTextDate.setText(mItem.getDate());
                mTextOverview.setText(mItem.getOverview());

                String url = mSearchItems.get(position).getImageUrl();
                if(mIconCache.getBitmapFromMemory(url) == null) {
                    mImagePoster.setImageDrawable(null);
                    mThumbnailDownloader.queueMessage(mSearchItems.get(position).getImageUrl(), this);
                } else{
                    Bitmap image = mIconCache.getBitmapFromMemory(url);
                    Drawable drawable = new BitmapDrawable(getResources(), image);
                    bindImage(drawable);
                }
            }

            public void bindImage(Drawable image){
                mImagePoster.setImageDrawable(image);
            }

            @Override
            public void onClick(View v) {
                if(getActivity() != null)
                    getActivity().startActivity(ContentActivity.newInstance(getActivity(), mContent, mItem.getId(), mItem.getTitle()));
            }
        }
    }

    private void initUI(){
        mCurrentState = State.EMPTY;

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery = newText;
                mAsyncTaskGetSearchResult.cancel(true);
                if(!newText.equals("")) {
                    mPage = 1;
                    mSearchItems.clear();
                    mAsyncTaskGetSearchResult = new GetSearchResult();
                    mAsyncTaskGetSearchResult.execute(mQuery);
                } else{
                    mCurrentState = State.EMPTY;
                    updateUI();
                }
                return true;
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new OnScrolled(mRecyclerView, linearLayoutManager) {
            @Override
            public void execute() {
                Log.i(TAG, "Scroll");
                new GetSearchResult().execute(mQuery);
            }
        });
    }

    private void updateUI(){
        if(mCurrentState == State.SEARCHING) {
            mSearchAdapter = new SearchListAdapter();
            mRecyclerView.setAdapter(mSearchAdapter);
        } else if(mCurrentState == State.COMPLETE){
            if(mPreviousSize < mSearchItems.size()) {
                mSearchAdapter.notifyItemRangeChanged(mPreviousSize - 1, mSearchItems.size() - mPreviousSize);
            } else {
                mSearchAdapter.notifyItemRemoved(mSearchItems.size());
            }
        }

        mRecyclerView.setVisibility(View.GONE);
        mEmptyQueryLayout.setVisibility(View.GONE);
        mProgressBarLayout.setVisibility(View.GONE);
        mNothingFoundLayout.setVisibility(View.GONE);

        switch(mCurrentState){
            case SEARCHING:
                mProgressBarLayout.setVisibility(View.VISIBLE);
                break;
            case NOTHING:
                mNothingFoundLayout.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                mEmptyQueryLayout.setVisibility(View.VISIBLE);
                break;
            case COMPLETE:
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private class GetSearchResult extends AsyncTask<String, Void, List<SearchItem>>{

        @Override
        protected void onPreExecute() {
            if(mPage == 1) {
                mCurrentState = State.SEARCHING;
            }
            updateUI();
        }

        @Override
        protected List<SearchItem> doInBackground(String... strings) {
            List<SearchItem> result = TMDBAPI.search(mContent, strings[0], mPage);
            if(isCancelled()) {
                return null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<SearchItem> searchItems) {
            mPage++;
            mPreviousSize = mSearchItems.size();
            mSearchItems.remove(null);
            if(searchItems != null && !searchItems.isEmpty()) {
                mSearchItems.addAll(searchItems);
            }

            mCurrentState = State.COMPLETE;
            if(mSearchItems.isEmpty()){
                mCurrentState = State.NOTHING;
            }
            updateUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchView.clearFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.clearQueue();
    }
}
