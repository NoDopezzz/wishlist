package nodopezzz.android.wishlist.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import bg.devlabs.fullscreenvideoview.FullscreenVideoView;
import nodopezzz.android.wishlist.APIs.TMDBApi;
import nodopezzz.android.wishlist.Activities.SearchActivity;
import nodopezzz.android.wishlist.Adapters.CastListAdapter;
import nodopezzz.android.wishlist.Adapters.TVSeasonsListAdapter;
import nodopezzz.android.wishlist.Content;
import nodopezzz.android.wishlist.Database.AsyncDatabaseDelete;
import nodopezzz.android.wishlist.Database.AsyncDatabaseInsert;
import nodopezzz.android.wishlist.Database.DBItem;
import nodopezzz.android.wishlist.Database.DBItemDao;
import nodopezzz.android.wishlist.Database.Database;
import nodopezzz.android.wishlist.MemoryUtils.DimensionsCalculator;
import nodopezzz.android.wishlist.MemoryUtils.ImageSizeCalculator;
import nodopezzz.android.wishlist.Models.ActorsResult;
import nodopezzz.android.wishlist.Models.ImageResult;
import nodopezzz.android.wishlist.Models.VideoResponse;
import nodopezzz.android.wishlist.Utils.GeneralSingleton;
import nodopezzz.android.wishlist.Models.Movie;
import nodopezzz.android.wishlist.Network.UrlDownloader;
import nodopezzz.android.wishlist.Adapters.PicturesSliderAdapter;
import nodopezzz.android.wishlist.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static nodopezzz.android.wishlist.APIs.TMDBApi.IMAGE_URL_ENDPOINT_ORIGINAL;
import static nodopezzz.android.wishlist.APIs.TMDBApi.YOUTUBE_ENDPOINT;

public class ContentMovieFragment extends Fragment {
    private static final String TAG = "ContentMovieFragment";

    private static final String ARG_ID = "ARG_ID";
    private static final String ARG_TITLE = "ARG_TITLE";

    public static ContentMovieFragment newInstance(String id, String title){
        ContentMovieFragment fragment = new ContentMovieFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    private String mId;

    private boolean mIsSaved = false;
    private Database mDatabase;
    private DBItemDao mItemDao;

    private ImageView mBackgroundView;
    private Toolbar mToolbar;

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

    private LinearLayout mPicturesFrame;
    private LinearLayout mCastFrame;
    private LinearLayout mMoneyTextLayout;

    private FullscreenVideoView mVideoPlayer;
    private SliderView mPicturesSlider;
    private PicturesSliderAdapter mPicturesSliderAdapter;

    private LinearLayout mMovieExtra;
    private TextView mBudgetView;
    private TextView mRevenueView;

    private FloatingActionButton mFloatingActionButton;
    private boolean mIsFABShown = true;
    private AnimatorSet mFABAnimatorSet;
    private float mFABPositionY = 0;

    private Movie mMovie;
    private List<ActorsResult.Actor> mActors;
    private List<ImageResult.Image> mImages;
    private List<VideoResponse.Video> mVideos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = GeneralSingleton.getInstance().getDatabase();
        mItemDao = mDatabase.dbItemDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_content, container, false);

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

        mPicturesFrame = v.findViewById(R.id.content_pictures_layout);
        mCastFrame = v.findViewById(R.id.content_cast_layout);

        mNestedScrollView = v.findViewById(R.id.content_nestedscrollview);
        mProgressBarLayout = v.findViewById(R.id.content_progressbar);

        mFABAnimatorSet = new AnimatorSet();
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY - scrollY > 4 && !mIsFABShown){
                    animateFABShow();
                    mIsFABShown = true;
                } else if(oldScrollY - scrollY < -4 && mIsFABShown) {
                    animateFABHide();
                    mIsFABShown = false;
                }
            }
        });

        mVideoPlayer = v.findViewById(R.id.content_video_player);

        mPicturesSlider = v.findViewById(R.id.content_pictures_slider);
        mFloatingActionButton = v.findViewById(R.id.content_media_floating_button);

        Bundle args = getArguments();
        if(args == null || args.getString(ARG_ID) == null){
            closeFragment();
        } else {
            mId = args.getString(ARG_ID);
            String title = args.getString(ARG_TITLE);
            try {
                initToolbar(title);
            } catch (Exception e) {
                e.printStackTrace();
                closeFragment();
            }
        }

        Log.i("Retrofit", "ContentMovieFragment");

        getMovie();

        return v;
    }

    private void getMovie(){
        mProgressBarLayout.setVisibility(View.VISIBLE);
        mNestedScrollView.setVisibility(View.GONE);

        TMDBApi.getInstance().getAdapter().getMovie(mId).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {

                mMovie = response.body();
                new LoadDB().execute();
                new LoadBackgroundImage().execute();
                getActors();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                closeFragment();
            }
        });
    }

    private void getActors(){
        TMDBApi.getInstance().getAdapter().getMovieActors(mId).enqueue(new Callback<ActorsResult>() {
            @Override
            public void onResponse(Call<ActorsResult> call, Response<ActorsResult> response) {
                mActors = response.body().getActors();
                getUrlImages();
            }

            @Override
            public void onFailure(Call<ActorsResult> call, Throwable t) {
                closeFragment();
            }
        });
    }

    private void getUrlImages(){
        TMDBApi.getInstance().getAdapter().getMovieImages(mId).enqueue(new Callback<ImageResult>() {
            @Override
            public void onResponse(Call<ImageResult> call, Response<ImageResult> response) {
                Log.i("Retrofit", response.toString());
                mImages = response.body().getImages();
                getVideos("ru");
            }

            @Override
            public void onFailure(Call<ImageResult> call, Throwable t) {
                closeFragment();
            }
        });
    }

    private void getVideos(final String language){
        TMDBApi.getInstance().getAdapter().getMovieVideos(mId, language).enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                mVideos = response.body().getVideos();
                if(mVideos.isEmpty() && !language.equals("en")){
                    getVideos("en");
                } else {
                    try {
                        updateUI();
                    } catch (Exception e) {
                        e.printStackTrace();
                        closeFragment();
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                closeFragment();
            }
        });
    }

    private void animateFABShow(){
        if (mFABAnimatorSet.isRunning()){
            mFABAnimatorSet.cancel();
        }

        float px = DimensionsCalculator.calculateDipToPx(getActivity(), 16f);

        float startY = mFloatingActionButton.getY();
        float endY = getView().getHeight() - mFloatingActionButton.getHeight() - px;

        ObjectAnimator animator = ObjectAnimator.ofFloat(mFloatingActionButton, "y", startY, endY)
                .setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        mFABAnimatorSet = new AnimatorSet();
        mFABAnimatorSet.play(animator);
        mFABAnimatorSet.start();
    }

    private void animateFABHide(){
        if (mFABAnimatorSet.isRunning()){
            mFABAnimatorSet.cancel();
        }

        if(mFABPositionY == 0){
            mFABPositionY = mFloatingActionButton.getY();
        }

        float startY = mFloatingActionButton.getY();
        float endY = getView().getHeight();

        ObjectAnimator animator = ObjectAnimator.ofFloat(mFloatingActionButton, "y", startY, endY)
                .setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        mFABAnimatorSet = new AnimatorSet();
        mFABAnimatorSet.play(animator);
        mFABAnimatorSet.start();
    }

    private void initSlider() throws Exception{
        List<String> urlImages = new ArrayList<>();
        for (int i = 1; i < (mImages.size() > 6 ? 6 : mImages.size()); i++){
            urlImages.add(IMAGE_URL_ENDPOINT_ORIGINAL + mImages.get(i).getImageUrl());
        }
        mPicturesSliderAdapter = new PicturesSliderAdapter(getActivity(), urlImages);
        mPicturesSlider.setSliderAdapter(mPicturesSliderAdapter);
    }

    private void setupVideoPlayer() throws Exception{

        String url = "";
        for (int i = 0; i < mVideos.size(); i++){
            if(mVideos.get(i).getType().equals("Trailer") &&
                    mVideos.get(i).getSite().equals("YouTube")){
                url = YOUTUBE_ENDPOINT + "?v=" + mVideos.get(i).getKey();
            }
        }

        if(url.equals("")){
            mVideoPlayer.setVisibility(View.GONE);
            return;
        }

        if(getActivity() == null) return;
        new YouTubeExtractor(getActivity()) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int[] itag = new int[]{22, 18, 37, 136, 135, 137, 134, 133};
                    boolean noVideo = true;
                    for (int i = 0; i < itag.length; i++) {
                        if (ytFiles.get(itag[i]) != null) {
                            String url = ytFiles.get(itag[i]).getUrl();
                            initVideoPlayer(url);
                            noVideo = false;
                            break;
                        }
                    }
                    if(noVideo){
                        mVideoPlayer.setVisibility(View.GONE);
                    }
                } else{
                    mVideoPlayer.setVisibility(View.GONE);
                }
            }
        }.extract(url, true, true);
    }

    private void initVideoPlayer(String url) {
        try {
            mVideoPlayer.videoUrl(url);
            mVideoPlayer.hideFullscreenButton();
        } catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    private void initToolbar(String title) throws Exception{

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

    private void initCastList() throws Exception{
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCastListAdapter = new CastListAdapter(getActivity(), mActors.subList(0,(mActors.size() > 10 ? 10 : mActors.size())));
        mCastListView.setLayoutManager(layoutManager);
        mCastListView.setAdapter(mCastListAdapter);
    }

    private class LoadDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            mIsSaved = mItemDao.getMovieById(mId, Content.MOVIE.name()) != null;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                initFloatingButton();
            } catch (Exception e) {
                e.printStackTrace();
                closeFragment();
            }
        }
    }

    private class LoadBackgroundImage extends AsyncTask<Void, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... args) {
            try {
                String url = IMAGE_URL_ENDPOINT_ORIGINAL + mMovie.getUrlBackground();
                if(mMovie.getUrlBackground() == null || mMovie.getUrlBackground().equals("null")){
                    url = IMAGE_URL_ENDPOINT_ORIGINAL + mMovie.getUrlPoster();
                }

                final Bitmap bitmap;
                byte[] bytes = UrlDownloader.getResponseByte(url);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                if(getActivity() == null) return null;
                Display display = getActivity().getWindowManager(). getDefaultDisplay();
                Point size = new Point();
                display. getSize(size);
                int width = size. x;
                int height = (int) DimensionsCalculator.calculateDipToPx(getActivity(), 220f);

                options.inSampleSize = ImageSizeCalculator.calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                return bitmap;
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

    private void updateUI() throws Exception{
        mProgressBarLayout.setVisibility(View.GONE);
        mNestedScrollView.setVisibility(View.VISIBLE);

        mDateView.setText(formDate(mMovie.getDate()));
        mTimeView.setText(mMovie.getTime() + " мин");
        mRateView.setText(mMovie.getVoteAverage());
        mRateCountView.setText("(" + mMovie.getVoteCount() + ")");
        mOverviewView.setText(mMovie.getOverview());

        StringBuilder genres = new StringBuilder();
        for (int i = 0; i < mMovie.getGenres().size(); i++){
            String genre = mMovie.getGenres().get(i).getName();
            genres.append(genre).append(", ");
        }
        if(genres.length() != 0) {
            genres.delete(genres.length() - 2, genres.length());
        }
        mGenreView.setText(genres.toString());

        if(mActors.isEmpty()){
            mCastFrame.setVisibility(View.GONE);
        }
        if(mImages.isEmpty()){
            mPicturesFrame.setVisibility(View.GONE);
        }

        if(getActivity() == null) return;
        updateUIMovie();
        setupVideoPlayer();
        initSlider();
        initToolbar(mMovie.getTitle());
        initCastList();
    }

    private void initFloatingButton() throws Exception{
        ((View)mFloatingActionButton).setVisibility(View.VISIBLE);
        if(mIsSaved){
            mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_white_24dp);
        } else{
            mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
        }
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsSaved){
                    try {
                        mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_border_white_24dp);

                        DBItem item = new DBItem();
                        item.setContent(Content.MOVIE.name());
                        item.setId(mId);
                        item.setTitle(mMovie.getTitle());
                        item.setSubtitle(formYear(mMovie.getDate()));

                        new AsyncDatabaseDelete() {
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                mIsSaved = false;
                                showSnackbar(R.string.state_deleted);
                                GeneralSingleton.getInstance().getInternalStorage().deleteImage(mId, Content.MOVIE.name());
                            }
                        }.execute(item);
                    } catch(OutOfMemoryError e){
                        e.printStackTrace();
                        mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_white_24dp);
                        showSnackbar(R.string.error_saving);
                    }

                } else{
                    try {
                        mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_white_24dp);

                        DBItem item = new DBItem();
                        item.setContent(Content.MOVIE.name());
                        item.setId(mId);
                        item.setTitle(mMovie.getTitle());
                        item.setSubtitle(formYear(mMovie.getDate()));

                        new AsyncDatabaseInsert() {
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                mIsSaved = true;
                                showSnackbar(R.string.state_saved);
                                GeneralSingleton.getInstance().getInternalStorage().saveToInternalStorage(mId, IMAGE_URL_ENDPOINT_ORIGINAL + mMovie.getUrlPoster(), Content.MOVIE.name());
                            }
                        }.execute(item);
                    } catch(OutOfMemoryError e){
                        e.printStackTrace();
                        mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                        showSnackbar(R.string.error_saving);
                    }
                }
            }
        });
    }

    private static String formYear(String date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parseDate = ft.parse(date);
            return String.format("%tY", parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String formDate(String date){
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parseDate = ft.parse(date);
            return String.format("%te %<tB %<tY", parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void updateUIMovie() throws Exception{
        mMovieExtra.setVisibility(View.VISIBLE);
        if(mMovie.getRevenue() == 0 || mMovie.getBudget() == 0){
            mRevenueView.setVisibility(View.GONE);
            mBudgetView.setVisibility(View.GONE);
            mMoneyTextLayout.setVisibility(View.GONE);
        } else {
            String budget = mMovie.getBudget() / 1000 + " тыс. $";
            String revenue = mMovie.getRevenue() / 1000 + " тыс. $";
            mRevenueView.setText(revenue);
            mBudgetView.setText(budget);
        }
    }

    private void closeFragment(){
        if(getActivity() == null) return;
        getActivity().setResult(SearchActivity.RESULT_CODE_ERROR);
        Log.i(TAG, "closeFragment");
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mCastListAdapter != null){
            mCastListAdapter.clear();
        }

        if(mPicturesSliderAdapter != null){
            mPicturesSliderAdapter.clear();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPicturesSliderAdapter != null){
            mPicturesSliderAdapter.quit();
        }

        if(mCastListAdapter != null){
            mCastListAdapter.quit();
        }
    }

    private void showSnackbar(int resId){
        if(getView() == null) return;
        Snackbar.make(mFloatingActionButton, resId, Snackbar.LENGTH_LONG).show();
    }
}
