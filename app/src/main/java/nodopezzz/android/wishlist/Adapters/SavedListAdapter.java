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

import nodopezzz.android.wishlist.Activities.ContentBookActivity;
import nodopezzz.android.wishlist.Activities.ContentMediaActivity;
import nodopezzz.android.wishlist.Database.DBItem;
import nodopezzz.android.wishlist.GeneralSingleton;
import nodopezzz.android.wishlist.GoogleBooksAPI;
import nodopezzz.android.wishlist.R;
import nodopezzz.android.wishlist.ThumbnailStorageDownloader;

public class SavedListAdapter extends RecyclerView.Adapter<SavedListAdapter.SavedItemHolder> {

    private List<DBItem> mItems;
    private Context mContext;

    private ThumbnailStorageDownloader<SavedItemHolder> mDownloader;

    public SavedListAdapter(Context context, List<DBItem> items){
        mContext = context;
        mItems = items;

        mDownloader = new ThumbnailStorageDownloader<>("SavedListAdapter", new ThumbnailStorageDownloader.OnPostDownloaded<SavedItemHolder>() {
            @Override
            public void onPostDownloaded(SavedItemHolder target, Bitmap bitmap, String path) {
                target.bindImage(bitmap);
                GeneralSingleton.getInstance().getIconCache().setBitmapToMemory(bitmap, path);
            }
        }, new Handler());
        mDownloader.getLooper();
        mDownloader.start();
    }

    public void setItems(List<DBItem> items){
        mItems = items;
    }

    @NonNull
    @Override
    public SavedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_saved, parent, false);
        return new SavedItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedItemHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class SavedItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private TextView mTitleView;
        private TextView mSubtitleView;

        private DBItem mItem;

        public SavedItemHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.saved_image);
            mTitleView = itemView.findViewById(R.id.saved_title);
            mSubtitleView = itemView.findViewById(R.id.saved_subtitle);
        }

        public void bindView(int position){
            mItem = mItems.get(position);

            itemView.setOnClickListener(this);

            mTitleView.setText(mItem.getTitle());
            mSubtitleView.setText(mItem.getSubtitle());

            mImageView.setImageBitmap(null);
            if(GeneralSingleton.getInstance().getIconCache().getBitmapFromMemory(mItem.getId() + "_" + mItem.getContent()) == null){
                mDownloader.putInQueue(this, mItem.getId(), mItem.getContent());
            } else{
                bindImage(GeneralSingleton.getInstance().getIconCache().getBitmapFromMemory(mItem.getId() + "_" + mItem.getContent()));
            }

        }

        public void bindImage(Bitmap bitmap){
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onClick(View v) {
            if(mItem.getContent().equals(GoogleBooksAPI.CONTENT_BOOKS)) {
                mContext.startActivity(ContentBookActivity.newInstance(
                        mContext,
                        mItem.getId(),
                        mItem.getTitle()));
            } else{
                mContext.startActivity(ContentMediaActivity.newInstance(
                        mContext,
                        mItem.getContent(),
                        mItem.getId(),
                        mItem.getTitle()));
            }
        }
    }

    public void clear(){
        mDownloader.clearQueue();
        mDownloader.quit();
    }
}
