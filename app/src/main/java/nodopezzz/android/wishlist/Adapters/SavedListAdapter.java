package nodopezzz.android.wishlist.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import nodopezzz.android.wishlist.Activities.ContentBookActivity;
import nodopezzz.android.wishlist.Activities.ContentMediaActivity;
import nodopezzz.android.wishlist.Activities.MainActivity;
import nodopezzz.android.wishlist.Activities.SearchActivity;
import nodopezzz.android.wishlist.Database.AsyncDatabaseDelete;
import nodopezzz.android.wishlist.Database.DBItem;
import nodopezzz.android.wishlist.ItemTouchDelete.ItemTouchHelperAdapter;
import nodopezzz.android.wishlist.Utils.GeneralSingleton;
import nodopezzz.android.wishlist.APIs.GoogleBooksAPI;
import nodopezzz.android.wishlist.R;
import nodopezzz.android.wishlist.MemoryUtils.ThumbnailStorageDownloader;
import nodopezzz.android.wishlist.Vibration;

public class SavedListAdapter extends RecyclerView.Adapter<SavedListAdapter.SavedItemHolder>
        implements ItemTouchHelperAdapter {

    private List<DBItem> mItems;
    private Context mContext;
    private View mDeleteView;

    private ThumbnailStorageDownloader<SavedItemHolder> mDownloader;
    private onStateChangedListener mOnStateChangedListener;

    public void setOnStateChangedListener(onStateChangedListener onStateChangedListener) {
        mOnStateChangedListener = onStateChangedListener;
    }

    public interface onStateChangedListener{
        public void onSelected();
        public void onClear();
        public void onOverlapped();
        public void onOverlappedClear();
    }

    public SavedListAdapter(Context context, List<DBItem> items, View deleteView){
        mContext = context;
        mItems = items;
        mDeleteView = deleteView;

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

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(final RecyclerView.ViewHolder holder, final int position) {
        Vibration.vibrate(mContext);
        new AsyncDatabaseDelete(){
            @Override
            protected void onPostExecute(Void aVoid) {
                holder.itemView.setVisibility(View.GONE);
                mItems.remove(position);
                notifyItemRemoved(position);
            }
        }.execute(mItems.get(position));
    }

    @Override
    public void onItemSelected(int position, RecyclerView.ViewHolder holder) {
        SavedItemHolder savedItemHolder = (SavedItemHolder) holder;
        savedItemHolder.onSelectedState();
        if(mOnStateChangedListener != null){
            mOnStateChangedListener.onSelected();
        }
    }

    @Override
    public void onClearView(int position, RecyclerView.ViewHolder holder) {
        SavedItemHolder savedItemHolder = (SavedItemHolder) holder;
        savedItemHolder.onClearState();
        if(mOnStateChangedListener != null){
            mOnStateChangedListener.onClear();
        }
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

            itemView.setOnClickListener(this);
        }

        public void onSelectedState(){
            mTitleView.setVisibility(View.GONE);
            mSubtitleView.setVisibility(View.GONE);
        }

        public void onClearState(){
            mTitleView.setVisibility(View.VISIBLE);
            mSubtitleView.setVisibility(View.VISIBLE);
        }

        public void bindView(int position){
            mItem = mItems.get(position);

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
                ((AppCompatActivity)mContext).startActivityForResult(ContentMediaActivity.newInstance(
                        mContext,
                        mItem.getContent(),
                        mItem.getId(),
                        mItem.getTitle()), SearchActivity.REQUEST_CODE);
            }
        }
    }

    public boolean isOverlapping(View firstView) {
        View secondView = mDeleteView;

        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        // Rect constructor parameters: left, top, right, bottom
        Rect rectFirstView = new Rect(firstPosition[0], firstPosition[1],
                firstPosition[0] + firstView.getMeasuredWidth(), firstPosition[1] + firstView.getMeasuredHeight());
        Rect rectSecondView = new Rect(secondPosition[0], secondPosition[1],
                secondPosition[0] + secondView.getMeasuredWidth(), secondPosition[1] + secondView.getMeasuredHeight());
        return rectFirstView.intersect(rectSecondView);
    }

    @Override
    public void onOverlapping() {
        if(mOnStateChangedListener != null) {
            mOnStateChangedListener.onOverlapped();
        }
    }

    @Override
    public void onOverlappingClear() {
        if(mOnStateChangedListener != null){
            mOnStateChangedListener.onOverlappedClear();
        }
    }


    public void clear(){
        mDownloader.clearQueue();
        mDownloader.quit();
    }
}
