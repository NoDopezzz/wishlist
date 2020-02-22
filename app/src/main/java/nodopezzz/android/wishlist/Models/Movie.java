package nodopezzz.android.wishlist.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Movie{
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

    @SerializedName("backdrop_path")
    @Expose
    private String mUrlBackground;

    @SerializedName("vote_average")
    @Expose
    private String mVoteAverage;

    @SerializedName("vote_count")
    @Expose
    private String mVoteCount;

    @SerializedName("runtime")
    @Expose
    private String mTime;

    @SerializedName("poster_path")
    @Expose
    private String mUrlPoster;

    @SerializedName("genres")
    @Expose
    private List<Genre> mGenres;

    @SerializedName("budget")
    @Expose
    private int mBudget;

    @SerializedName("revenue")
    @Expose
    private int mRevenue;

    
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

    
    public String getTime() {
        return mTime;
    }

    
    public void setTime(String time) {
        mTime = time;
    }

    
    public String getUrlPoster() {
        return mUrlPoster;
    }

    
    public void setUrlPoster(String urlPoster) {
        mUrlPoster = urlPoster;
    }

    public int getBudget() {
        return mBudget;
    }

    public void setBudget(int budget) {
        mBudget = budget;
    }

    public int getRevenue() {
        return mRevenue;
    }

    public void setRevenue(int revenue) {
        mRevenue = revenue;
    }

    
    public List<Genre> getGenres() {
        return mGenres;
    }

    public void setGenres(List<Genre> genres) {
        mGenres = genres;
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
