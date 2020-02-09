package nodopezzz.android.wishlist.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
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
import nodopezzz.android.wishlist.IconCache;
import nodopezzz.android.wishlist.Models.Season;
import nodopezzz.android.wishlist.Network.ThumbnailDownloader;
import nodopezzz.android.wishlist.R;

public class TVSeasonsListAdapter extends RecyclerView.Adapter<TVSeasonsListAdapter.SeasonHolder> {
    private static final String TAG = "CastListAdapter";

    private Context mContext;
    private List<Season> mSeasons;
    private Fragment mFragment;

    private IconCache mIconCache;
    private ThumbnailDownloader<SeasonHolder> mThumbnailDownloader;

    public TVSeasonsListAdapter(Context context, Fragment fragment, List<Season> seasons){
        mContext = context;
        mSeasons = seasons;
        mFragment = fragment;

        mIconCache = IconCache.get(mContext);
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

        private Season mSeason;

        public SeasonHolder(@NonNull View itemView) {
            super(itemView);

            mImageSeasonView = itemView.findViewById(R.id.season_item_image);
            mTitleView = itemView.findViewById(R.id.season_item_title);
            mDateView = itemView.findViewById(R.id.season_item_date);

            itemView.setOnClickListener(this);
        }

        public void bindView(Season season){
            mSeason = season;

            mTitleView.setText(season.getTitle());
            mDateView.setText(season.getDate());

            if(mIconCache.getBitmapFromMemory(season.getUrlImage()) == null){
                mThumbnailDownloader.queueMessage(season.getUrlImage(), this);
                bindImage(null);
            } else{
                bindImage(mIconCache.getBitmapFromMemory(season.getUrlImage()));
            }
        }

        public void bindImage(Bitmap image){
            mImageSeasonView.setImageBitmap(image);
        }

        @Override
        public void onClick(View v) {
            EpisodeListDialogFragment dialogFragment = EpisodeListDialogFragment.newInstance(
                    mSeason.getTitle(),
                    mSeason.getTVShowId(),
                    mSeason.getSeasonNumber());
            dialogFragment.setTargetFragment(mFragment, 0);
            dialogFragment.show(mFragment.getFragmentManager(), "EpisodeListDialogFragment");
        }
    }
}
