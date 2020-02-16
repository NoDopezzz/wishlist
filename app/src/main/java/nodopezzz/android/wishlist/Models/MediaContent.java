package nodopezzz.android.wishlist.Models;

import java.util.List;

public class MediaContent {
    protected String mId;
    protected String mTitle;
    protected String mDate;
    protected String mOverview;
    protected String mUrlBackground;
    protected String mVoteAverage;
    protected String mVoteCount;
    protected String mTime;
    protected String mUrlPoster;
    protected String mYear;

    protected List<String> mGenres;

    protected List<String> mUrlImages;
    protected String mYoutubeUrl;
    protected List<Actor> mCast;

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

    public String getYoutubeUrl() {
        return mYoutubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        mYoutubeUrl = youtubeUrl;
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

    public String getYear(){
        return mYear;
    }

    public void setYear(String year){
        mYear = year;
    }
}
