package nodopezzz.android.wishlist.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import nodopezzz.android.wishlist.GeneralSingleton;
import nodopezzz.android.wishlist.GoogleBooksAPI;
import nodopezzz.android.wishlist.IconCache;
import nodopezzz.android.wishlist.Models.Book;
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
        }

        @Override
        public void bindView(int position){

            itemView.setOnClickListener(this);
            mItem = mSearchItems.get(position);
            mTextTitle.setText(mItem.getTitle());
            mTextSubtitle.setText(mItem.getSubtitle());
            mTextOverview.setText(mItem.getOverview());

            String url = mSearchItems.get(position).getThumbnailUrl();
            if(mIconCache.getBitmapFromMemory(url) == null) {
                mImagePoster.setImageDrawable(null);
                mThumbnailDownloader.queueMessage(url, this);
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
                mItem.onClickSearchItem(mContext);
            }
        }
    }

    public void clear(){
        mThumbnailDownloader.clearQueue();
    }
}