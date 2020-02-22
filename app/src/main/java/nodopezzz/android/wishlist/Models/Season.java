package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Season {

    @SerializedName("air_date")
    @Expose
    private String mDate;

    @SerializedName("episodes")
    @Expose
    private List<Episode> mEpisodes;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public List<Episode> getEpisodes() {
        return mEpisodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        mEpisodes = episodes;
    }

    public class Episode {

        @SerializedName("name")
        @Expose
        private String mTitle;

        @SerializedName("still_path")
        @Expose
        private String mUrlImage;

        @SerializedName("overview")
        @Expose
        private String mOverview;

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getUrlImage() {
            return mUrlImage;
        }

        public void setUrlImage(String urlImage) {
            mUrlImage = urlImage;
        }

        public String getOverview() {
            return mOverview;
        }

        public void setOverview(String overview) {
            mOverview = overview;
        }
    }

}
