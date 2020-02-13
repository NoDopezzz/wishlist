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

import nodopezzz.android.wishlist.Activities.ContentBookActivity;
import nodopezzz.android.wishlist.Activities.ContentMediaActivity;
import nodopezzz.android.wishlist.Adapters.SearchListAdapter;
import nodopezzz.android.wishlist.GoogleBooksAPI;
import nodopezzz.android.wishlist.IconCache;
import nodopezzz.android.wishlist.Models.Book;
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

    private void initUI(){
        mCurrentState = State.EMPTY;

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();

                mQuery = query;
                mAsyncTaskGetSearchResult.cancel(true);
                if(!mQuery.equals("")) {
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

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
    }

    private void updateUI(){
        if(mCurrentState == State.SEARCHING) {
            mSearchAdapter = new SearchListAdapter(getActivity(), mSearchItems);
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
            List<SearchItem> result = search(strings[0]);
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

    private List<SearchItem> search(String query){
        if(mContent.equals(GoogleBooksAPI.CONTENT_BOOKS)){
            return GoogleBooksAPI.search(query, mPage);
        } else{
            return TMDBAPI.search(mContent, query, mPage);
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
        if(mSearchAdapter != null){
            mSearchAdapter.clear();
        }
    }
}
