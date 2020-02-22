package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShow {

    @SerializedName("id")
    @Expose
    private String mId;

    @SerializedName("name")
    @Expose
    private String mTitle;

    @SerializedName("first_air_date")
    @Expose
    private String mDate;

    @SerializedName("overview")
    @Expose
    private String mOverview;

    @SerializedName("backdrop_path")
    @Expose
    private String mUrlBackground;

    @SerializedName("vote_average")
    @Expose
    private String mVoteAverage;

    @SerializedName("vote_count")
    @Expose
    private String mVoteCount;

    @SerializedName("episode_run_time")
    @Expose
    private List<Integer> mTimes;

    @SerializedName("poster_path")
    @Expose
    private String mUrlPoster;

    @SerializedName("genres")
    @Expose
    private List<Genre> mGenres;

    @SerializedName("status")
    @Expose
    private String mStatus;

    @SerializedName("number_of_seasons")
    @Expose
    private String mNumberOfSeasons;

    @SerializedName("seasons")
    @Expose
    private List<Season> mSeasons;

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

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getUrlBackground() {
        return mUrlBackground;
    }

    public void setUrlBackground(String urlBackground) {
        mUrlBackground = urlBackground;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(String voteCount) {
        mVoteCount = voteCount;
    }

    public String getUrlPoster() {
        return mUrlPoster;
    }

    public void setUrlPoster(String urlPoster) {
        mUrlPoster = urlPoster;
    }

    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setGenres(List<Genre> genres) {
        mGenres = genres;
    }

    public List<Season> getSeasons() {
        return mSeasons;
    }

    public void setSeasons(List<Season> seasons) {
        mSeasons = seasons;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getNumberOfSeasons() {
        return mNumberOfSeasons;
    }

    public void setNumberOfSeasons(String numberOfSeasons) {
        mNumberOfSeasons = numberOfSeasons;
    }

    public List<Integer> getTimes() {
        return mTimes;
    }

    public void setTimes(List<Integer> times) {
        mTimes = times;
    }

    public class Season{
        @SerializedName("id")
        @Expose
        private String mId;

        @SerializedName("name")
        @Expose
        private String mTitle;

        @SerializedName("air_date")
        @Expose
        private String mDate;

        @SerializedName("poster_path")
        @Expose
        private String mUrlImage;

        @SerializedName("season_number")
        @Expose
        private String mSeasonNumber;

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

        public String getDate() {
            return mDate;
        }

        public void setDate(String date) {
            mDate = date;
        }

        public String getUrlImage() {
            return mUrlImage;
        }

        public void setUrlImage(String urlImage) {
            mUrlImage = urlImage;
        }

        public String getSeasonNumber() {
            return mSeasonNumber;
        }

        public void setSeasonNumber(String seasonNumber) {
            mSeasonNumber = seasonNumber;
        }

    }

    public class Genre{

        @SerializedName("id")
        @Expose
        private int mId;

        @SerializedName("name")
        @Expose
        private String mName;

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }
    }
}
