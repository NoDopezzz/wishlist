package nodopezzz.android.wishlist.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

import nodopezzz.android.wishlist.Utils.GeneralSingleton;
import nodopezzz.android.wishlist.MemoryUtils.IconCache;
import nodopezzz.android.wishlist.Network.ThumbnailDownloader;
import nodopezzz.android.wishlist.R;

public class PicturesSliderAdapter extends SliderViewAdapter<PicturesSliderAdapter.PictureHolder> {
    private static final String TAG = "PicturesSliderAdapter";

    private List<String> mPictures;
    private Context mContext;

    private IconCache mIconCache;
    private ThumbnailDownloader<PicturesSliderAdapter.PictureHolder> mThumbnailDownloader;

    public PicturesSliderAdapter(Context context, List<String> pictures){
        mPictures = pictures;
        mContext = context;
        mIconCache = GeneralSingleton.getInstance().getIconCache();

        mThumbnailDownloader = new ThumbnailDownloader<>("ThumbnailDownloader", new Handler());
        mThumbnailDownloader.setListener(new ThumbnailDownloader.DownloadedListener<PicturesSliderAdapter.PictureHolder>() {
            @Override
            public void onDownloaded(PicturesSliderAdapter.PictureHolder target, Bitmap image, String sUrl) {
                mIconCache.setBitmapToMemory(image, sUrl);
                target.bindImage(image);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
    }

    @Override
    public PictureHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new PictureHolder(inflater.inflate(R.layout.item_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(PictureHolder viewHolder, int position) {
        viewHolder.bindView(position);
    }

    @Override
    public int getCount() {
        return mPictures.size();
    }

    class PictureHolder extends SliderViewAdapter.ViewHolder{

        private ImageView mImageView;

        public PictureHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.picture_item_image);
        }

        public void bindView(int position){
            String url = mPictures.get(position);
            if(mIconCache.getBitmapFromMemory(url) == null) {
                //TODO Holder
                mThumbnailDownloader.queueMessage(url, this);
            } else{
                bindImage(mIconCache.getBitmapFromMemory(url));
            }
        }

        public void bindImage(Bitmap drawable){
            mImageView.setImageBitmap(drawable);
        }
    }

    public void clear(){
        mThumbnailDownloader.clearQueue();
    }
}
