package nodopezzz.android.wishlist.Models;

import java.util.List;

public class Movie {
    private String mId;
    private String mTitle;
    private String mDate;
    private String mOverview;
    private String mUrlBackground;
    private String mVoteAverage;
    private String mVoteCount;
    private String mTime;
    private String mUrlPoster;

    private List<String> mGenres;
    private List<String> mCountries;

    private List<String> mUrlImages;
    private String mYoutubeId;
    private List<Actor> mCast;

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

    public List<String> getUrlImages() {
        return mUrlImages;
    }

    public void setUrlImages(List<String> urlImages) {
        mUrlImages = urlImages;
    }

    public List<String> getGenres() {
        return mGenres;
    }

    public void setGenres(List<String> genres) {
        mGenres = genres;
    }

    public List<String> getCountries() {
        return mCountries;
    }

    public void setCountries(List<String> countries) {
        mCountries = countries;
    }

    public String getYoutubeId() {
        return mYoutubeId;
    }

    public void setYoutubeId(String youtubeId) {
        mYoutubeId = youtubeId;
    }

    public List<Actor> getCast() {
        return mCast;
    }

    public void setCast(List<Actor> cast) {
        mCast = cast;
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

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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
}
