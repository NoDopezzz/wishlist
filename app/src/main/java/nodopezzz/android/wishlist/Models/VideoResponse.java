package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResponse {

    @SerializedName("results")
    @Expose
    private List<Video> mVideos;

    public List<Video> getVideos() {
        return mVideos;
    }

    public void setVideos(List<Video> videos) {
        mVideos = videos;
    }

    public class Video {
        @SerializedName("key")
        @Expose
        private String mKey;

        @SerializedName("iso_639_1")
        @Expose
        private String mLanguage;

        @SerializedName("site")
        @Expose
        private String mSite;

        @SerializedName("type")
        @Expose
        private String mType;

        public String getLanguage() {
            return mLanguage;
        }

        public void setLanguage(String language) {
            mLanguage = language;
        }

        public String getSite() {
            return mSite;
        }

        public void setSite(String site) {
            mSite = site;
        }

        public String getType() {
            return mType;
        }

        public void setType(String type) {
            mType = type;
        }

        public String getKey() {
            return mKey;
        }

        public void setKey(String key) {
            mKey = key;
        }
    }

}
