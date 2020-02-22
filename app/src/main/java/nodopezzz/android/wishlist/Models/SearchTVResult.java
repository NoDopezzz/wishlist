package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchTVResult {

    @SerializedName("total_pages")
    @Expose
    private int mTotalPages;

    @SerializedName("results")
    @Expose
    private List<SearchItemTV> mSearchItemTVs;

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int totalPages) {
        mTotalPages = totalPages;
    }

    public List<SearchItemTV> getSearchItemTVs() {
        return mSearchItemTVs;
    }

    public void setSearchItemTVs(List<SearchItemTV> searchItemTVs) {
        mSearchItemTVs = searchItemTVs;
    }

    public class SearchItemTV {

        @SerializedName("id")
        @Expose
        private String mId;

        @SerializedName("name")
        @Expose
        private String mTitle;

        @SerializedName("overview")
        @Expose
        private String mOverview;

        @SerializedName("poster_path")
        @Expose
        private String mThumbnailUrl;

        @SerializedName("first_air_date")
        @Expose
        private String mDate;

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getOverview() {
            return mOverview;
        }

        public void setOverview(String overview) {
            mOverview = overview;
        }

        public String getThumbnailUrl() {
            return mThumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            mThumbnailUrl = thumbnailUrl;
        }

        public String getDate() {
            return mDate;
        }

        public void setDate(String date) {
            mDate = date;
        }
    }

}
