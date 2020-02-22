package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchMovieResult {

    @SerializedName("total_pages")
    @Expose
    private int mTotalPages;

    @SerializedName("results")
    @Expose
    private List<SearchItemMovie> mSearchItemMovies;

    public int getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(int totalPages) {
        mTotalPages = totalPages;
    }

    public List<SearchItemMovie> getSearchItemMovies() {
        return mSearchItemMovies;
    }

    public void setSearchItemMovies(List<SearchItemMovie> searchItemMovies) {
        mSearchItemMovies = searchItemMovies;
    }

    public class SearchItemMovie {

        @SerializedName("id")
        @Expose
        private String mId;

        @SerializedName("title")
        @Expose
        private String mTitle;

        @SerializedName("release_date")
        @Expose
        private String mDate;

        @SerializedName("overview")
        @Expose
        private String mOverview;

        @SerializedName("poster_path")
        @Expose
        private String mThumbnailUrl;

        public void setId(String id) {
            mId = id;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public void setOverview(String overview) {
            mOverview = overview;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            mThumbnailUrl = thumbnailUrl;
        }

        public String getId() {
            return mId;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getOverview() {
            return mOverview;
        }

        public String getThumbnailUrl() {
            return mThumbnailUrl;
        }

        public String getDate() {
            return mDate;
        }

        public void setDate(String date) {
            mDate = date;
        }
    }
}
