package nodopezzz.android.wishlist.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;

import nodopezzz.android.wishlist.Adapters.CastListAdapter;
import nodopezzz.android.wishlist.Models.Movie;
import nodopezzz.android.wishlist.Network.UrlDownloader;
import nodopezzz.android.wishlist.Adapters.PicturesSliderAdapter;
import nodopezzz.android.wishlist.R;
import nodopezzz.android.wishlist.TMDBAPI;

public class ContentFragment extends Fragment {
    private static final String TAG = "ContentFragment";

    private static final String ARG_CONTENT = "ARG_CONTENT";
    private static final String ARG_ID = "ARG_ID";
    private static final String ARG_TITLE = "ARG_TITLE";

    public static ContentFragment newInstance(String content, String id, String title){
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    private String mId;
    private Movie mMovie;

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

    private LinearLayout mPicturesFrame;
    private LinearLayout mCastFrame;

    private YouTubePlayerView mYoutubePlayer;
    private SliderView mPicturesSlider;
    private PicturesSliderAdapter mPicturesSliderAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_content, container, false);

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

        mPicturesFrame = v.findViewById(R.id.content_pictures_layout);
        mCastFrame = v.findViewById(R.id.content_cast_layout);

        mNestedScrollView = v.findViewById(R.id.content_nestedscrollview);
        mProgressBarLayout = v.findViewById(R.id.content_progressbar);

        mYoutubePlayer = v.findViewById(R.id.content_video_player);
        getActivity().getLifecycle().addObserver(mYoutubePlayer);

        mPicturesSlider = v.findViewById(R.id.content_pictures_slider);

        Bundle args = getArguments();
        if(args == null || args.getString(ARG_ID) == null){
            closeFragment();
        } else {
            mId = args.getString(ARG_ID);
            String title = args.getString(ARG_TITLE);
            initToolbar(title);
        }

        new LoadContent().execute();

        return v;
    }

    private void initSlider(){
        mPicturesSliderAdapter = new PicturesSliderAdapter(getActivity(), mMovie.getUrlImages());
        mPicturesSlider.setSliderAdapter(mPicturesSliderAdapter);
    }

    private void initYouTubePlayer(){
        if(mMovie.getYoutubeId() == null){
            mYoutubePlayer.setVisibility(View.GONE);
        } else {
            mYoutubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    String videoId = mMovie.getYoutubeId();
                    youTubePlayer.cueVideo(videoId, 0);
                    Log.i(TAG, videoId);
                }
            });
            mYoutubePlayer.getPlayerUiController()
                    .showMenuButton(false)
                    .showVideoTitle(false)
                    .showYouTubeButton(false);
        }
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
        CastListAdapter adapter = new CastListAdapter(getActivity(), mMovie.getCast());
        mCastListView.setLayoutManager(layoutManager);
        mCastListView.setAdapter(adapter);
    }

    private class LoadContent extends AsyncTask<Void, Void, Movie>{

        @Override
        protected void onPreExecute() {
            mProgressBarLayout.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.GONE);
        }

        @Override
        protected Movie doInBackground(Void... voids) {
            return TMDBAPI.getMovie(mId);
        }

        @Override
        protected void onPostExecute(Movie movie) {
            mMovie = movie;
            new LoadBackgroundImage().execute();

            mProgressBarLayout.setVisibility(View.GONE);
            mNestedScrollView.setVisibility(View.VISIBLE);

            mDateView.setText(movie.getDate());
            mTimeView.setText(movie.getTime() + " минут");
            mRateView.setText(movie.getVoteAverage());
            mRateCountView.setText("(" + movie.getVoteCount() + ")");
            mOverviewView.setText(movie.getOverview());

            StringBuilder genres = new StringBuilder();
            for (String genre : movie.getGenres()){
                genres.append(genre).append(", ");
            }
            if(genres.length() != 0) {
                genres.delete(genres.length() - 2, genres.length());
            }
            mGenreView.setText(genres.toString());

            if(mMovie.getCast().isEmpty()){
                mCastFrame.setVisibility(View.GONE);
            }
            if(mMovie.getUrlImages().isEmpty()){
                mPicturesFrame.setVisibility(View.GONE);
            }

            initYouTubePlayer();
            initSlider();
            initToolbar(mMovie.getTitle());
            initCastList();
        }
    }

    private class LoadBackgroundImage extends AsyncTask<Void, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... args) {
            try {
                String url = mMovie.getUrlBackground();
                if(url == null){
                    url = mMovie.getUrlPoster();
                }
                byte[] bytes = UrlDownloader.getResponseByte(url);
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mBackgroundView.setImageBitmap(bitmap);
        }
    }

    private void closeFragment(){
        getActivity().finish();
    }
}
