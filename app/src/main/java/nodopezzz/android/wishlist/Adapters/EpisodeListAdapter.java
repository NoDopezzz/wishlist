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

import nodopezzz.android.wishlist.Fragments.EpisodeDialogFragment;
import nodopezzz.android.wishlist.IconCache;
import nodopezzz.android.wishlist.Models.Episode;
import nodopezzz.android.wishlist.Network.ThumbnailDownloader;
import nodopezzz.android.wishlist.R;

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.EpisodeHolder> {
    private static final String TAG = "EpisodeListAdapter";

    private Context mContext;
    private List<Episode> mEpisodes;
    private Fragment mTargetFragment;

    private ThumbnailDownloader<EpisodeHolder> mThumbnailDownloader;
    private IconCache mIconCache;

    public EpisodeListAdapter(Context context, Fragment fragment, List<Episode> episodes){
        mContext = context;
        mEpisodes = episodes;
        mTargetFragment = fragment;

        mIconCache = IconCache.get(mContext);
        mThumbnailDownloader = new ThumbnailDownloader<>("EpisodeHolder", new Handler());
        mThumbnailDownloader.setListener(new ThumbnailDownloader.DownloadedListener<EpisodeHolder>() {
            @Override
            public void onDownloaded(EpisodeHolder target, Bitmap image, String url) {
                target.bindImage(image);
                mIconCache.setBitmapToMemory(image, url);
            }
        });
        mThumbnailDownloader.getLooper();
        mThumbnailDownloader.start();
    }

    @NonNull
    @Override
    public EpisodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new EpisodeHolder(inflater.inflate(R.layout.item_episode, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeHolder holder, int position) {
        holder.bindView(mEpisodes.get(position));
    }

    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }

    class EpisodeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mEpisodeImage;
        private TextView mTitleView;
        private ImageView mArrowImageView;

        private Episode mEpisode;

        EpisodeHolder(@NonNull View itemView) {
            super(itemView);

            mEpisodeImage = itemView.findViewById(R.id.episode_item_image);
            mTitleView = itemView.findViewById(R.id.episode_item_title);
            mArrowImageView = itemView.findViewById(R.id.episode_item_arrow);

        }

        void bindView(Episode episode){
            mEpisode = episode;

            if(
                    mEpisode.getOverview() == null ||
                    mEpisode.getOverview().equals("") ||
                    mEpisode.getOverview().equals("null")
            ){
                    mArrowImageView.setVisibility(View.GONE);
            } else{
                itemView.setOnClickListener(this);
            }

            mTitleView.setText(episode.getTitle());
            Log.i(TAG, episode.getUrlImage());
            if(mIconCache.getBitmapFromMemory(episode.getUrlImage()) == null){
                if(episode.getUrlImage().equals("")){
                    mEpisodeImage.setImageResource(R.drawable.placeholder_image_not_found);
                } else {
                    mThumbnailDownloader.queueMessage(episode.getUrlImage(), this);
                    bindImage(null);
                }
            } else{
                bindImage(mIconCache.getBitmapFromMemory(episode.getUrlImage()));
            }
        }

        void bindImage(Bitmap bitmap){
            mEpisodeImage.setImageBitmap(bitmap);
        }

        @Override
        public void onClick(View v) {
            EpisodeDialogFragment fragment = EpisodeDialogFragment.newInstance(
                    mEpisode.getTitle(),
                    mEpisode.getUrlImage(),
                    mEpisode.getOverview());
            fragment.setTargetFragment(mTargetFragment, 0);
            fragment.show(mTargetFragment.getFragmentManager(), "EpisodeDialogFragment");
        }
    }

    public void clear(){
        mThumbnailDownloader.clearQueue();
    }
}
