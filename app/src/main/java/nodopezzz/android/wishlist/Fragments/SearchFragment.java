package nodopezzz.android.wishlist.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nodopezzz.android.wishlist.APIs.GoogleBooksAPI;
import nodopezzz.android.wishlist.APIs.GoogleBooksAPIAdapter;
import nodopezzz.android.wishlist.APIs.TMDBApi;
import nodopezzz.android.wishlist.Activities.ContentBookActivity;
import nodopezzz.android.wishlist.Activities.ContentMovieActivity;
import nodopezzz.android.wishlist.Activities.ContentTVActivity;
import nodopezzz.android.wishlist.Activities.SearchActivity;
import nodopezzz.android.wishlist.Adapters.SearchListAdapter;
import nodopezzz.android.wishlist.Content;
import nodopezzz.android.wishlist.Models.Book;
import nodopezzz.android.wishlist.Models.Movie;
import nodopezzz.android.wishlist.Models.SearchBookResult;
import nodopezzz.android.wishlist.Models.SearchItem;
import nodopezzz.android.wishlist.Models.SearchMovieResult;
import nodopezzz.android.wishlist.Models.SearchTVResult;
import nodopezzz.android.wishlist.OnScrolled;
import nodopezzz.android.wishlist.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static nodopezzz.android.wishlist.APIs.TMDBApi.IMAGE_URL_ENDPOINT_THUMBNAIL;
import static nodopezzz.android.wishlist.DataParser.formDate;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private static final String ARG_CONTENT = "ARG_CONTENT";

    public static SearchFragment newInstance(Content content){
        SearchFragment fragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTENT, content);
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

    private Content mContent;

    private int mPage = 1;
    private String mQuery;
    private List<SearchItem> mSearchItems = new ArrayList<>();
    private int mPreviousSize = 0;

    private RelativeLayout mProgressBarLayout;
    private RelativeLayout mEmptyQueryLayout;
    private RelativeLayout mNothingFoundLayout;

    private Call mCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchView = v.findViewById(R.id.search_view);
        mRecyclerView = v.findViewById(R.id.search_items_recycler_view);
        mProgressBarLayout = v.findViewById(R.id.search_progress_bar_layout);
        mEmptyQueryLayout = v.findViewById(R.id.search_empty_layout);
        mNothingFoundLayout = v.findViewById(R.id.nothing_found_search_layout);

        if(getArguments() != null && getArguments().getSerializable(ARG_CONTENT) != null){
            mContent = (Content)getArguments().getSerializable(ARG_CONTENT);
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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery = newText;
                if(mCall != null && mCall.isExecuted()){
                    mCall.cancel();
                }
                if(!newText.equals("")) {
                    mPage = 1;
                    mSearchItems.clear();
                    mCurrentState = State.SEARCHING;
                    updateUI();

                    makeRequest();
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
                if(mPage == 1) {
                    mCurrentState = State.SEARCHING;
                }
                updateUI();
                makeRequest();
            }
        });
    }

    private void makeRequest(){
        if(mContent == Content.MOVIE) {
            mCall = TMDBApi
                    .getInstance()
                    .getAdapter()
                    .searchItemMovie(mQuery, mPage);

            mCall.enqueue(new Callback<SearchMovieResult>() {
                @Override
                public void onResponse(Call<SearchMovieResult> call, Response<SearchMovieResult> response) {
                    SearchMovieResult result = response.body();
                    List<SearchMovieResult.SearchItemMovie> movies = result.getSearchItemMovies();
                    mPage++;
                    mPreviousSize = mSearchItems.size();
                    mSearchItems.remove(null);
                    if (movies != null && !movies.isEmpty()) {
                        mSearchItems.addAll(generateSearchItems(movies));
                    }

                    mCurrentState = State.COMPLETE;
                    if (mSearchItems.isEmpty()) {
                        mCurrentState = State.NOTHING;
                    }
                    updateUI();
                }

                @Override
                public void onFailure(Call<SearchMovieResult> call, Throwable t) {

                }
            });
        } else if(mContent == Content.TV){
            mCall = TMDBApi
                    .getInstance()
                    .getAdapter()
                    .searchItemTV(mQuery, mPage);

            mCall.enqueue(new Callback<SearchTVResult>() {
                @Override
                public void onResponse(Call<SearchTVResult> call, Response<SearchTVResult> response) {
                    SearchTVResult result = response.body();
                    List<SearchTVResult.SearchItemTV> tvshows = result.getSearchItemTVs();
                    mPage++;
                    mPreviousSize = mSearchItems.size();
                    mSearchItems.remove(null);
                    if (tvshows != null && !tvshows.isEmpty()) {
                        mSearchItems.addAll(generateSearchItems(tvshows));
                    }

                    mCurrentState = State.COMPLETE;
                    if (mSearchItems.isEmpty()) {
                        mCurrentState = State.NOTHING;
                    }
                    updateUI();
                }

                @Override
                public void onFailure(Call<SearchTVResult> call, Throwable t) {

                }
            });
        } else if(mContent == Content.BOOK){
            mCall = GoogleBooksAPI.getInstance().getAdapter().search(mQuery, Integer.toString((mPage - 1) * 10));
            mCall.enqueue(new Callback<SearchBookResult>(){

                @Override
                public void onResponse(Call<SearchBookResult> call, Response<SearchBookResult> response) {
                    SearchBookResult result = response.body();
                    List<Book> books = result.getBooks();
                    mPage++;
                    mPreviousSize = mSearchItems.size();
                    mSearchItems.remove(null);
                    if (books != null && !books.isEmpty()) {
                        mSearchItems.addAll(generateSearchItems(books));
                    }

                    mCurrentState = State.COMPLETE;
                    if (mSearchItems.isEmpty()) {
                        mCurrentState = State.NOTHING;
                    }
                    updateUI();
                }

                @Override
                public void onFailure(Call<SearchBookResult> call, Throwable t) {

                }
            });
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
    }

    private void updateUI(){
        if(mCurrentState == State.SEARCHING) {
            mSearchAdapter = new SearchListAdapter(getActivity(), mSearchItems, new SearchListAdapter.OnClickItemListener() {
                @Override
                public void onClickItem(int position) {
                    switch(mContent){
                        case MOVIE:
                            getActivity().startActivityForResult(ContentMovieActivity.newInstance(getActivity(), mSearchItems.get(position).getId(), mSearchItems.get(position).getTitle()), SearchActivity.REQUEST_CODE);
                            break;
                        case TV:
                            getActivity().startActivityForResult(ContentTVActivity.newInstance(getActivity(), mSearchItems.get(position).getId(), mSearchItems.get(position).getTitle()), SearchActivity.REQUEST_CODE);
                            break;
                        case BOOK:
                            getActivity().startActivityForResult(ContentBookActivity.newInstance(getActivity(), mSearchItems.get(position).getId(), mSearchItems.get(position).getTitle()), SearchActivity.REQUEST_CODE);
                            break;
                    }
                }
            });
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

    private <T> List<SearchItem> generateSearchItems(List<T> tList){
        List<SearchItem> items = new ArrayList<>();
        switch(mContent){
            case MOVIE:
                for (T t : tList){
                    SearchMovieResult.SearchItemMovie movie = (SearchMovieResult.SearchItemMovie)t;
                    SearchItem item = new SearchItem(
                            movie.getTitle(),
                            formDate(movie.getDate()),
                            movie.getOverview(),
                            IMAGE_URL_ENDPOINT_THUMBNAIL + movie.getThumbnailUrl(), movie.getId());
                    items.add(item);
                }
                break;
            case TV:
                for (T t : tList){
                    SearchTVResult.SearchItemTV tv = (SearchTVResult.SearchItemTV) t;
                    SearchItem item = new SearchItem(
                            tv.getTitle(),
                            formDate(tv.getDate()),
                            tv.getOverview(),
                            IMAGE_URL_ENDPOINT_THUMBNAIL + tv.getThumbnailUrl(),
                            tv.getId());
                    items.add(item);
                }
                break;
            case BOOK:
                for (T t : tList){
                    Book book = (Book)t;
                    String imageUrl = "";
                    if(book.getVolumeInfo().getImageLinks() != null){
                        imageUrl = book.getVolumeInfo().getImageLinks().getSmallThumbnail();
                    }

                    String author = "";
                    if(book.getVolumeInfo().getAuthors() != null && !book.getVolumeInfo().getAuthors().isEmpty()){
                        author = book.getVolumeInfo().getAuthors().get(0);
                    }
                    SearchItem item = new SearchItem(
                            book.getVolumeInfo().getTitle(),
                            author,
                            book.getVolumeInfo().getDescription(),
                            imageUrl, book.getId());
                    items.add(item);
                }
                break;
        }
        return items;
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
            mSearchAdapter.quit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mSearchAdapter != null){
            mSearchAdapter.clear();
        }
    }
}
