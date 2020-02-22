package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageResult {

    @SerializedName("backdrops")
    @Expose
    private List<Image> mImages;

    public List<Image> getImages() {
        return mImages;
    }

    public void setImages(List<Image> images) {
        mImages = images;
    }

    public class Image {

        @SerializedName("file_path")
        @Expose
        private String mImageUrl;

        public String getImageUrl() {
            return mImageUrl;
        }

        public void setImageUrl(String imageUrl) {
            mImageUrl = imageUrl;
        }
    }
}
