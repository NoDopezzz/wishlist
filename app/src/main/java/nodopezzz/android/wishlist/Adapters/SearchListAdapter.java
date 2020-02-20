package nodopezzz.android.wishlist.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nodopezzz.android.wishlist.APIs.GoogleBooksAPI;
import nodopezzz.android.wishlist.Activities.ContentBookActivity;
import nodopezzz.android.wishlist.Activities.ContentMediaActivity;
import nodopezzz.android.wishlist.Activities.MainActivity;
import nodopezzz.android.wishlist.Activities.SearchActivity;
import nodopezzz.android.wishlist.MemoryUtils.DimensionsCalculator;
import nodopezzz.android.wishlist.Utils.GeneralSingleton;
import nodopezzz.android.wishlist.MemoryUtils.IconCache;
import nodopezzz.android.wishlist.Models.SearchItem;
import nodopezzz.android.wishlist.Network.ThumbnailDownloader;
import nodopezzz.android.wishlist.R;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchHolder>{

    private static final int VIEW_TYPE_DATA = 1;
    private static final int VIEW_TYPE_PROGRESS = 2;

    private Context mContext;
    private List<SearchItem> mSearchItems;

    private ThumbnailDownloader<SearchListAdapter.SearchItemHolder> mThumbnailDownloader;
    private IconCache mIconCache;

    public SearchListAdapter(Context context, List<SearchItem> searchItems){
        mContext = context;
        mSearchItems = searchItems;

        mIconCache = GeneralSingleton.getInstance().getIconCache();
        mThumbnailDownloader = new ThumbnailDownloader<>("ThumbnailDownloader", new Handler());
        mThumbnailDownloader.setListener(new ThumbnailDownloader.DownloadedListener<SearchListAdapter.SearchItemHolder>() {
            @Override
            public void onDownloaded(SearchListAdapter.SearchItemHolder target, Bitmap image, String sUrl) {
                Drawable drawable = new BitmapDrawable(mContext.getResources(), image);
                mIconCache.setBitmapToMemory(image, sUrl);
                target.bindImage(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(viewType == VIEW_TYPE_DATA) {
            return new SearchItemHolder(inflater.inflate(R.layout.item_search, parent, false));
        } else{
            return new SearchProgressHolder(inflater.inflate(R.layout.item_progressbar, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        if(holder instanceof SearchItemHolder) {
            holder.bindView(position);
        }
    }

    @Override
    public int getItemCount() {
        return mSearchItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mSearchItems.get(position) == null){
            return VIEW_TYPE_PROGRESS;
        }
        return VIEW_TYPE_DATA;
    }

    public abstract class SearchHolder extends RecyclerView.ViewHolder {
        public SearchHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindView(int position){}
    }

    private class SearchProgressHolder extends SearchHolder{

        public SearchProgressHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class SearchItemHolder extends SearchHolder implements View.OnClickListener {

        private ImageView mImagePoster;
        private TextView mTextTitle;
        private TextView mTextSubtitle;
        private TextView mTextOverview;

        private SearchItem mItem;

        public SearchItemHolder(@NonNull View itemView) {
            super(itemView);

            mImagePoster = itemView.findViewById(R.id.search_item_poster);
            mTextTitle = itemView.findViewById(R.id.search_item_title);
            mTextSubtitle = itemView.findViewById(R.id.search_item_subtitle);
            mTextOverview = itemView.findViewById(R.id.search_item_overview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void bindView(int position){

            mItem = mSearchItems.get(position);
            mTextTitle.setText(mItem.getTitle());
            mTextSubtitle.setText(mItem.getSubtitle());
            mTextOverview.setText(mItem.getOverview());

            final String url = mSearchItems.get(position).getThumbnailUrl();
            if(mIconCache.getBitmapFromMemory(url) == null) {

                int width = (int) DimensionsCalculator.calculateDipToPx(mContext, 100f);
                int height = (int) DimensionsCalculator.calculateDipToPx(mContext, 150f);

                mThumbnailDownloader.queueMessage(url, this, width, height);
                mImagePoster.setImageDrawable(null);
            } else{
                Bitmap image = mIconCache.getBitmapFromMemory(url);
                Drawable drawable = new BitmapDrawable(mContext.getResources(), image);
                bindImage(drawable);
            }
        }

        public void bindImage(Drawable image){
            mImagePoster.setImageDrawable(image);
        }

        @Override
        public void onClick(View v) {
            if(mContext != null) {
                if(mItem.getContent().equals(GoogleBooksAPI.CONTENT_BOOKS)){
                    mContext.startActivity(ContentBookActivity.newInstance(mContext, mItem.getId(), mItem.getTitle()));
                } else {
                    ((AppCompatActivity) mContext).startActivityForResult(ContentMediaActivity.newInstance(mContext, mItem.getContent(), mItem.getId(), mItem.getTitle()), SearchActivity.REQUEST_CODE);
                }
            }
        }
    }

    public void clear(){
        mThumbnailDownloader.clearQueue();
    }

    public void quit(){
        mThumbnailDownloader.quit();
    }
}