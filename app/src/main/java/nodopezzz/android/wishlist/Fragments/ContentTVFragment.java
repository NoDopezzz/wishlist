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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
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
import nodopezzz.android.wishlist.Models.TVShow;
import nodopezzz.android.wishlist.Network.UrlDownloader;
import nodopezzz.android.wishlist.Adapters.PicturesSliderAdapter;
import nodopezzz.android.wishlist.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static nodopezzz.android.wishlist.APIs.TMDBApi.IMAGE_URL_ENDPOINT_ORIGINAL;
import static nodopezzz.android.wishlist.APIs.TMDBApi.YOUTUBE_ENDPOINT;
import static nodopezzz.android.wishlist.DataParser.formDate;
import static nodopezzz.android.wishlist.DataParser.formYear;

public class ContentTVFragment extends Fragment {
    private static final String TAG = "ContentTVFragment";

    private static final String ARG_ID = "ARG_ID";
    private static final String ARG_TITLE = "ARG_TITLE";

    public static ContentTVFragment newInstance(String id, String title){
        ContentTVFragment fragment = new ContentTVFragment();
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
    private TVSeasonsListAdapter mTVSeasonsListAdapter;

    private LinearLayout mPicturesFrame;
    private LinearLayout mCastFrame;

    private FullscreenVideoView mVideoPlayer;
    private SliderView mPicturesSlider;
    private PicturesSliderAdapter mPicturesSliderAdapter;

    private LinearLayout mTVShowExtra;
    private TextView mStatusView;
    private TextView mNumberSeasonsView;

    private LinearLayout mTVSeasonsLayout;
    private RecyclerView mTVSeasonsList;

    private FloatingActionButton mFloatingActionButton;
    private boolean mIsFABShown = true;
    private AnimatorSet mFABAnimatorSet;
    private float mFABPositionY = 0;

    private TVShow mTVShow;
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
        View v = inflater.inflate(R.layout.fragment_tv_content, container, false);

        mBackgroundView = v.findViewById(R.id.content_image);
        mToolbar = v.findViewById(R.id.content_toolbar);

        mDateView = v.findViewById(R.id.content_date);
        mGenreView = v.findViewById(R.id.content_genre);
        mTimeView = v.findViewById(R.id.content_time);
        mRateView = v.findViewById(R.id.content_rating);
        mRateCountView = v.findViewById(R.id.content_rating_count);
        mOverviewView = v.findViewById(R.id.content_overview);
        mCastListView = v.findViewById(R.id.content_cast_list);

        mTVShowExtra = v.findViewById(R.id.content_tvshow_extra);
        mNumberSeasonsView = v.findViewById(R.id.content_tvshow_numberseasons);
        mStatusView = v.findViewById(R.id.content_tvshow_status);

        mTVSeasonsLayout = v.findViewById(R.id.content_seasons_layout);
        mTVSeasonsList = v.findViewById(R.id.content_seasons_list);

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
            initToolbar(title);
        }

        Log.i("Retrofit", "getTVShow()");
        getTVShow();

        return v;
    }

    private void getTVShow(){
        mProgressBarLayout.setVisibility(View.VISIBLE);
        mNestedScrollView.setVisibility(View.GONE);

        TMDBApi.getInstance().getAdapter().getShow(mId).enqueue(new Callback<TVShow>() {
            @Override
            public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                Log.i("Retrofit", response.toString());
                mTVShow = response.body();
                if(mTVShow == null){
                    Log.i("Retrofit", "null");
                }
                new ContentTVFragment.LoadDB().execute();
                new ContentTVFragment.LoadBackgroundImage().execute();
                getActors();
            }

            @Override
            public void onFailure(Call<TVShow> call, Throwable t) {
                Log.i("Retrofit", t.toString());
            }
        });
    }

    private void getActors(){
        TMDBApi.getInstance().getAdapter().getTVActors(mId).enqueue(new Callback<ActorsResult>() {
            @Override
            public void onResponse(Call<ActorsResult> call, Response<ActorsResult> response) {
                mActors = response.body().getActors();
                getUrlImages();
            }

            @Override
            public void onFailure(Call<ActorsResult> call, Throwable t) {

            }
        });
    }

    private void getUrlImages(){
        TMDBApi.getInstance().getAdapter().getTVImages(mId).enqueue(new Callback<ImageResult>() {
            @Override
            public void onResponse(Call<ImageResult> call, Response<ImageResult> response) {
                Log.i("Retrofit", "Images: " + response.toString());
                mImages = response.body().getImages();
                getVideos("ru");
            }

            @Override
            public void onFailure(Call<ImageResult> call, Throwable t) {
            }
        });
    }

    private void getVideos(final String language){
        TMDBApi.getInstance().getAdapter().getTVVideos(mId, language).enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                mVideos = response.body().getVideos();
                if(mVideos.isEmpty() && !language.equals("en")){
                    getVideos("en");
                } else {
                    updateUI();
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {

            }
        });
    }

    private void animateFABShow(){
        if (mFABAnimatorSet.isRunning()){
            mFABAnimatorSet.cancel();
        }

        float startY = mFloatingActionButton.getY();
        float endY = mFABPositionY;

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

        float px = DimensionsCalculator.calculateDipToPx(getActivity(), 24f);

        if(mFABPositionY == 0){
            mFABPositionY = mFloatingActionButton.getY();
        }

        float startY = mFloatingActionButton.getY();
        float endY = mFABPositionY + mFloatingActionButton.getHeight() + px;

        ObjectAnimator animator = ObjectAnimator.ofFloat(mFloatingActionButton, "y", startY, endY)
                .setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        mFABAnimatorSet = new AnimatorSet();
        mFABAnimatorSet.play(animator);
        mFABAnimatorSet.start();
    }

    private void initSlider(){
        List<String> urlImages = new ArrayList<>();
        for (int i = 1; i < (mImages.size() > 6 ? 6 : mImages.size()); i++){
            urlImages.add(IMAGE_URL_ENDPOINT_ORIGINAL + mImages.get(i).getImageUrl());
        }
        mPicturesSliderAdapter = new PicturesSliderAdapter(getActivity(), urlImages);
        mPicturesSlider.setSliderAdapter(mPicturesSliderAdapter);
    }

    private void setupVideoPlayer(){

        String url = "";
        for (int i = 0; i < mVideos.size(); i++){
            if(mVideos.get(i).getType().equals("Trailer") &&
                    mVideos.get(i).getSite().equals("YouTube")){
                url = YOUTUBE_ENDPOINT + "?v=" + mVideos.get(i).getKey();
            }
        }

        if(url.equals("")) {
            mVideoPlayer.setVisibility(View.GONE);
            return;
        }

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

    private void initVideoPlayer(String url){
        try {
            mVideoPlayer.videoUrl(url);
            mVideoPlayer.hideFullscreenButton();
        } catch(NullPointerException e){
            e.printStackTrace();
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
        mCastListAdapter = new CastListAdapter(getActivity(), mActors.subList(0,(mActors.size() > 10 ? 10 : mActors.size())));
        mCastListView.setLayoutManager(layoutManager);
        mCastListView.setAdapter(mCastListAdapter);
    }

    private class LoadDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            mIsSaved = mItemDao.getMovieById(mId, Content.TV.name()) != null;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            initFloatingButton();
        }
    }

    private class LoadBackgroundImage extends AsyncTask<Void, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... args) {
            try {
                String url = IMAGE_URL_ENDPOINT_ORIGINAL + mTVShow.getUrlBackground();
                if(mTVShow.getUrlBackground() == null || mTVShow.getUrlBackground().equals("null")){
                    url = IMAGE_URL_ENDPOINT_ORIGINAL + mTVShow.getUrlPoster();
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

    private void updateUI(){
        mProgressBarLayout.setVisibility(View.GONE);
        mNestedScrollView.setVisibility(View.VISIBLE);

        mDateView.setText(formDate(mTVShow.getDate()));
        mTimeView.setText(mTVShow.getTimes().get(0) + " мин");
        mRateView.setText(mTVShow.getVoteAverage());
        mRateCountView.setText("(" + mTVShow.getVoteCount() + ")");
        mOverviewView.setText(mTVShow.getOverview());

        StringBuilder genres = new StringBuilder();
        for (int i = 0; i < mTVShow.getGenres().size(); i++){
            String genre = mTVShow.getGenres().get(i).getName();
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

        updateUITV();

        setupVideoPlayer();
        initSlider();
        initToolbar(mTVShow.getTitle());
        initCastList();
    }

    private void initFloatingButton(){
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
                        GeneralSingleton.getInstance().getInternalStorage().deleteImage(mId, Content.MOVIE.name());
                        mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_border_white_24dp);

                        DBItem item = new DBItem();
                        item.setContent(Content.MOVIE.name());
                        item.setId(mId);
                        item.setTitle(mTVShow.getTitle());
                        item.setSubtitle(formYear(mTVShow.getDate()));

                        new AsyncDatabaseDelete() {
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                mIsSaved = false;
                                showSnackbar(R.string.state_deleted);
                            }
                        }.execute(item);
                    } catch(OutOfMemoryError e){
                        e.printStackTrace();
                        mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_white_24dp);
                        showSnackbar(R.string.error_saving);
                    }

                } else{
                    try {
                        GeneralSingleton.getInstance().getInternalStorage().saveToInternalStorage(mId, IMAGE_URL_ENDPOINT_ORIGINAL + mTVShow.getUrlPoster(), Content.TV.name());

                        mFloatingActionButton.setImageResource(R.drawable.ic_bookmark_white_24dp);

                        DBItem item = new DBItem();
                        item.setContent(Content.TV.name());
                        item.setId(mId);
                        item.setTitle(mTVShow.getTitle());
                        item.setSubtitle(formYear(mTVShow.getDate()));

                        new AsyncDatabaseInsert() {
                            @Override
                            protected void onPostExecute(Void aVoid) {
                                mIsSaved = true;
                                showSnackbar(R.string.state_saved);
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

    private void updateUITV(){
        mTVShowExtra.setVisibility(View.VISIBLE);
        mNumberSeasonsView.setText(mTVShow.getNumberOfSeasons());
        mStatusView.setText(mTVShow.getStatus());

        mTVSeasonsLayout.setVisibility(View.VISIBLE);
        initSeasonsList();

    }

    private void initSeasonsList(){
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mTVSeasonsListAdapter = new TVSeasonsListAdapter(getActivity(), this, mTVShow.getSeasons(), mTVShow.getId());
        mTVSeasonsList.setLayoutManager(layoutManager);
        mTVSeasonsList.setAdapter(mTVSeasonsListAdapter);
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

        if(mTVSeasonsListAdapter != null){
            mTVSeasonsListAdapter.clear();
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

        if(mTVSeasonsListAdapter != null){
            mTVSeasonsListAdapter.quit();
        }
    }

    private void showSnackbar(int resId){
        if(getView() == null) return;
        Snackbar.make(mFloatingActionButton, resId, Snackbar.LENGTH_LONG).show();
    }
}
