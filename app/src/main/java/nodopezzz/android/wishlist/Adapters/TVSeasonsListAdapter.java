package nodopezzz.android.wishlist.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nodopezzz.android.wishlist.Fragments.EpisodeListDialogFragment;
import nodopezzz.android.wishlist.MemoryUtils.DimensionsCalculator;
import nodopezzz.android.wishlist.Models.TVShow;
import nodopezzz.android.wishlist.Utils.GeneralSingleton;
import nodopezzz.android.wishlist.MemoryUtils.IconCache;
import nodopezzz.android.wishlist.Models.Season;
import nodopezzz.android.wishlist.Network.ThumbnailDownloader;
import nodopezzz.android.wishlist.R;

import static nodopezzz.android.wishlist.APIs.TMDBApi.IMAGE_URL_ENDPOINT_ORIGINAL;
import static nodopezzz.android.wishlist.DataParser.formYear;

public class TVSeasonsListAdapter extends RecyclerView.Adapter<TVSeasonsListAdapter.SeasonHolder> {
    private static final String TAG = "TVSeasonsListAdapter";

    private Context mContext;
    private List<TVShow.Season> mSeasons;
    private Fragment mFragment;

    private IconCache mIconCache;
    private ThumbnailDownloader<SeasonHolder> mThumbnailDownloader;

    private String mTVShowId;

    public TVSeasonsListAdapter(Context context, Fragment fragment, List<TVShow.Season> seasons, String tvShowId){
        mContext = context;
        mSeasons = seasons;
        mFragment = fragment;
        mTVShowId = tvShowId;

        mIconCache = GeneralSingleton.getInstance().getIconCache();
        mThumbnailDownloader = new ThumbnailDownloader<>("ThumbnailDownloader", new Handler());
        mThumbnailDownloader.setListener(new ThumbnailDownloader.DownloadedListener<SeasonHolder>() {
            @Override
            public void onDownloaded(SeasonHolder target, Bitmap image, String url) {
                target.bindImage(image);
                mIconCache.setBitmapToMemory(image, url);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
    }

    @NonNull
    @Override
    public SeasonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new SeasonHolder(inflater.inflate(R.layout.item_season, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonHolder holder, int position) {
        holder.bindView(mSeasons.get(position));
    }

    @Override
    public int getItemCount() {
        return mSeasons.size();
    }

    public class SeasonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageSeasonView;
        private TextView mTitleView;
        private TextView mDateView;

        private TVShow.Season mSeason;

        public SeasonHolder(@NonNull View itemView) {
            super(itemView);

            mImageSeasonView = itemView.findViewById(R.id.season_item_image);
            mTitleView = itemView.findViewById(R.id.season_item_title);
            mDateView = itemView.findViewById(R.id.season_item_date);

            itemView.setOnClickListener(this);
        }

        public void bindView(TVShow.Season season){
            mSeason = season;

            mTitleView.setText(season.getTitle());
            mDateView.setText(formYear((season.getDate())));

            if(mSeason.getUrlImage() == null || mSeason.getUrlImage().equals("null")){
                mImageSeasonView.setImageResource(R.drawable.placeholder_image_not_found);
            } else {
                String url = IMAGE_URL_ENDPOINT_ORIGINAL + mSeason.getUrlImage();
                if (mIconCache.getBitmapFromMemory(url) == null) {

                    int width = (int) DimensionsCalculator.calculateDipToPx(mContext, 150f);
                    int height = (int) DimensionsCalculator.calculateDipToPx(mContext, 225f);

                    mThumbnailDownloader.queueMessage(url, this, width, height);
                    bindImage(null);
                } else {
                    bindImage(mIconCache.getBitmapFromMemory(url));
                }
            }
        }

        public void bindImage(Bitmap image){
            mImageSeasonView.setImageBitmap(image);
        }

        @Override
        public void onClick(View v) {
            EpisodeListDialogFragment dialogFragment = EpisodeListDialogFragment.newInstance(
                    mSeason.getTitle(),
                    mTVShowId,
                    mSeason.getSeasonNumber());
            dialogFragment.setTargetFragment(mFragment, 0);
            dialogFragment.show(mFragment.getFragmentManager(), "EpisodeListDialogFragment");
        }
    }

    public void clear(){
        mThumbnailDownloader.clearQueue();
    }

    public void quit(){
        mThumbnailDownloader.quit();
    }
}
