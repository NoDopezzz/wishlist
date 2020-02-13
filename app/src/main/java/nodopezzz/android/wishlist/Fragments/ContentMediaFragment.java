package nodopezzz.android.wishlist.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import bg.devlabs.fullscreenvideoview.FullscreenVideoView;
import nodopezzz.android.wishlist.Adapters.CastListAdapter;
import nodopezzz.android.wishlist.Adapters.TVSeasonsListAdapter;
import nodopezzz.android.wishlist.Models.MediaContent;
import nodopezzz.android.wishlist.Models.Movie;
import nodopezzz.android.wishlist.Models.TVShow;
import nodopezzz.android.wishlist.Network.UrlDownloader;
import nodopezzz.android.wishlist.Adapters.PicturesSliderAdapter;
import nodopezzz.android.wishlist.R;
import nodopezzz.android.wishlist.TMDBAPI;

public class ContentMediaFragment extends Fragment {
    private static final String TAG = "ContentMediaFragment";

    private static final String ARG_CONTENT = "ARG_CONTENT";
    private static final String ARG_ID = "ARG_ID";
    private static final String ARG_TITLE = "ARG_TITLE";

    public static ContentMediaFragment newInstance(String content, String id, String title){
        ContentMediaFragment fragment = new ContentMediaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    private String mId;
    private MediaContent mContentItem;
    private String mContent;

    private ImageView mBackgroundView;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;

    private NestedScrollView mNestedScrollView;
    private RelativeLayout mProgressBarLayout;

    private TextView mDateView;
    private TextView mGenreView;
    private TextView mTimeView;
    private TextView mRateView;
    private TextView mRateCountView;
    private ExpandableTextView mOverviewView;
    private RecyclerView mCastListView;

    private CastListAdapter mCastListAdapter;
    private TVSeasonsListAdapter mTVSeasonsListAdapter;

    private LinearLayout mPicturesFrame;
    private LinearLayout mCastFrame;
    private LinearLayout mMoneyTextLayout;

    private FullscreenVideoView mVideoPlayer;
    private SliderView mPicturesSlider;
    private PicturesSliderAdapter mPicturesSliderAdapter;

    private LinearLayout mMovieExtra;
    private TextView mBudgetView;
    private TextView mRevenueView;

    private LinearLayout mTVShowExtra;
    private TextView mStatusView;
    private TextView mNumberSeasonsView;

    private LinearLayout mTVSeasonsLayout;
    private RecyclerView mTVSeasonsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_media_content, container, false);

        mAppBarLayout = v.findViewById(R.id.content_appbar);
        mBackgroundView = v.findViewById(R.id.content_image);
        mToolbar = v.findViewById(R.id.content_toolbar);

        mDateView = v.findViewById(R.id.content_date);
        mGenreView = v.findViewById(R.id.content_genre);
        mTimeView = v.findViewById(R.id.content_time);
        mRateView = v.findViewById(R.id.content_rating);
        mRateCountView = v.findViewById(R.id.content_rating_count);
        mOverviewView = v.findViewById(R.id.content_overview);
        mCastListView = v.findViewById(R.id.content_cast_list);

        mMovieExtra = v.findViewById(R.id.content_movie_extra);
        mBudgetView = v.findViewById(R.id.content_budget);
        mRevenueView = v.findViewById(R.id.content_revenue);
        mMoneyTextLayout = v.findViewById(R.id.content_media_money_text);

        mTVShowExtra = v.findViewById(R.id.content_tvshow_extra);
        mNumberSeasonsView = v.findViewById(R.id.content_tvshow_numberseasons);
        mStatusView = v.findViewById(R.id.content_tvshow_status);

        mTVSeasonsLayout = v.findViewById(R.id.content_seasons_layout);
        mTVSeasonsList = v.findViewById(R.id.content_seasons_list);

        mPicturesFrame = v.findViewById(R.id.content_pictures_layout);
        mCastFrame = v.findViewById(R.id.content_cast_layout);

        mNestedScrollView = v.findViewById(R.id.content_nestedscrollview);
        mProgressBarLayout = v.findViewById(R.id.content_progressbar);

        mVideoPlayer = v.findViewById(R.id.content_video_player);

        mPicturesSlider = v.findViewById(R.id.content_pictures_slider);

        Bundle args = getArguments();
        if(args == null || args.getString(ARG_ID) == null){
            closeFragment();
        } else {
            mId = args.getString(ARG_ID);
            String title = args.getString(ARG_TITLE);
            mContent = args.getString(ARG_CONTENT);
            initToolbar(title);
        }

        new LoadContent().execute();

        return v;
    }

    private void initSlider(){
        mPicturesSliderAdapter = new PicturesSliderAdapter(getActivity(), mContentItem.getUrlImages());
        mPicturesSlider.setSliderAdapter(mPicturesSliderAdapter);
    }

    private void setupVideoPlayer(){
        new YouTubeExtractor(getActivity()) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int[] itag = new int[]{22, 18, 37, 136, 135, 137, 134, 133};
                    boolean noVideo = true;
                    for (int i = 0; i < itag.length; i++) {
                        if (ytFiles.get(itag[i]) != null) {
                            Log.i(TAG, ytFiles.get(itag[i]).getUrl());
                            String url = ytFiles.get(itag[i]).getUrl();
                            initVideoPlayer(url);
                            noVideo = false;
                            break;
                        }
                    }
                    if(noVideo){
                        Log.i(TAG, "true");
                        mVideoPlayer.setVisibility(View.GONE);
                    }
                } else{
                    mVideoPlayer.setVisibility(View.GONE);
                }
            }
        }.extract(mContentItem.getYoutubeUrl(), true, true);
    }

    private void initVideoPlayer(String url){
        mVideoPlayer.videoUrl(url);
        mVideoPlayer.hideFullscreenButton();

    }

    private void initToolbar(String title){

        mToolbar.setTitle(title);
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

    private void initCastList(){
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCastListAdapter = new CastListAdapter(getActivity(), mContentItem.getCast());
        mCastListView.setLayoutManager(layoutManager);
        mCastListView.setAdapter(mCastListAdapter);
    }

    private void initSeasonsList(){
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mTVSeasonsListAdapter = new TVSeasonsListAdapter(getActivity(), this, ((TVShow)mContentItem).getSeasons());
        mTVSeasonsList.setLayoutManager(layoutManager);
        mTVSeasonsList.setAdapter(mTVSeasonsListAdapter);
    }

    private class LoadContent extends AsyncTask<Void, Void, MediaContent>{

        @Override
        protected void onPreExecute() {
            mProgressBarLayout.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.GONE);
        }

        @Override
        protected MediaContent doInBackground(Void... voids) {
            return TMDBAPI.getContent(mContent, mId);
        }

        @Override
        protected void onPostExecute(MediaContent mediaContent) {
            if(getActivity() == null) return;
            if(mediaContent == null) closeFragment();

            mContentItem = mediaContent;
            new LoadBackgroundImage().execute();

            updateUI();
        }
    }

    private class LoadBackgroundImage extends AsyncTask<Void, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... args) {
            try {
                String url = mContentItem.getUrlBackground();
                if(url == null){
                    url = mContentItem.getUrlPoster();
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
            mBackgroundView.setImageBitmap(bitmap);
        }
    }

    private void updateUI(){
        mProgressBarLayout.setVisibility(View.GONE);
        mNestedScrollView.setVisibility(View.VISIBLE);

        mDateView.setText(mContentItem.getDate());
        mTimeView.setText(mContentItem.getTime() + " мин");
        mRateView.setText(mContentItem.getVoteAverage());
        mRateCountView.setText("(" + mContentItem.getVoteCount() + ")");
        mOverviewView.setText(mContentItem.getOverview());

        StringBuilder genres = new StringBuilder();
        for (String genre : mContentItem.getGenres()){
            genres.append(genre).append(", ");
        }
        if(genres.length() != 0) {
            genres.delete(genres.length() - 2, genres.length());
        }
        mGenreView.setText(genres.toString());

        if(mContentItem.getCast().isEmpty()){
            mCastFrame.setVisibility(View.GONE);
        }
        if(mContentItem.getUrlImages().isEmpty()){
            mPicturesFrame.setVisibility(View.GONE);
        }

        if(mContent.equals(TMDBAPI.CONTENT_MOVIE)){
            updateUIMovie();
        } else if(mContent.equals((TMDBAPI.CONTENT_TV))){
            updateUITV();
        }

        setupVideoPlayer();
        initSlider();
        initToolbar(mContentItem.getTitle());
        initCastList();
    }

    private void updateUIMovie(){
        mMovieExtra.setVisibility(View.VISIBLE);
        if(((Movie)mContentItem).getRevenue().equals("") || ((Movie)mContentItem).getBudget().equals("")){
            mRevenueView.setVisibility(View.GONE);
            mBudgetView.setVisibility(View.GONE);
            mMoneyTextLayout.setVisibility(View.GONE);
        } else {
            mRevenueView.setText(((Movie) mContentItem).getRevenue());
            mBudgetView.setText(((Movie) mContentItem).getBudget());
        }
    }

    private void updateUITV(){
        mTVShowExtra.setVisibility(View.VISIBLE);
        mNumberSeasonsView.setText(((TVShow)mContentItem).getNumberOfSeasons());
        mStatusView.setText(((TVShow)mContentItem).getStatus());

        mTVSeasonsLayout.setVisibility(View.VISIBLE);
        initSeasonsList();

    }

    private void closeFragment(){
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPicturesSliderAdapter != null){
            mPicturesSliderAdapter.clear();
        }

        if(mCastListAdapter != null){
            mCastListAdapter.clear();
        }

        if(mTVSeasonsListAdapter != null){
            mTVSeasonsListAdapter.clear();
        }
    }
}
