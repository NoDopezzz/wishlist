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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nodopezzz.android.wishlist.Utils.GeneralSingleton;
import nodopezzz.android.wishlist.MemoryUtils.IconCache;
import nodopezzz.android.wishlist.Models.Actor;
import nodopezzz.android.wishlist.Network.ThumbnailDownloader;
import nodopezzz.android.wishlist.R;

public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.CastHolder> {
    private static final String TAG = "CastListAdapter";

    private Context mContext;
    private List<Actor> mActors;

    private IconCache mIconCache;
    private ThumbnailDownloader<CastHolder> mThumbnailDownloader;

    public CastListAdapter(Context context, List<Actor> actors){
        mContext = context;
        mActors = actors;

        mIconCache = GeneralSingleton.getInstance().getIconCache();
        mThumbnailDownloader = new ThumbnailDownloader<>("ThumbnailDownloader", new Handler());
        mThumbnailDownloader.setListener(new ThumbnailDownloader.DownloadedListener<CastHolder>() {
            @Override
            public void onDownloaded(CastHolder target, Bitmap image, String url) {
                target.bindImage(image);
                mIconCache.setBitmapToMemory(image, url);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
    }

    @NonNull
    @Override
    public CastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new CastHolder(inflater.inflate(R.layout.item_cast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CastHolder holder, int position) {
        holder.bindView(mActors.get(position));
    }

    @Override
    public int getItemCount() {
        return mActors.size();
    }

    public class CastHolder extends RecyclerView.ViewHolder{

        private ImageView mImageActorView;
        private TextView mCharacterView;
        private TextView mNameActorView;

        private Actor mActor;

        public CastHolder(@NonNull View itemView) {
            super(itemView);

            mImageActorView = itemView.findViewById(R.id.cast_item_image);
            mCharacterView = itemView.findViewById(R.id.cast_item_character);
            mNameActorView = itemView.findViewById(R.id.cast_item_name);
        }

        public void bindView(Actor actor){
            mActor = actor;

            mCharacterView.setText(actor.getCharacterName());
            mNameActorView.setText(actor.getName());

            if(mIconCache.getBitmapFromMemory(actor.getUrlProfilePhoto()) == null){
                if(!actor.getUrlProfilePhoto().equals("")){
                    mThumbnailDownloader.queueMessage(actor.getUrlProfilePhoto(), this);
                } else{
                    mImageActorView.setImageResource(R.drawable.placeholder_person);
                }
            } else{
                bindImage(mIconCache.getBitmapFromMemory(actor.getUrlProfilePhoto()));
            }
        }

        public void bindImage(Bitmap image){
            mImageActorView.setImageBitmap(image);
        }
    }

    public void clear(){
        mThumbnailDownloader.clearQueue();
    }
}
