package nodopezzz.android.wishlist.Models;

import java.util.List;

public class Season {
    private String mId;
    private String mTitle;
    private String mDate;
    private String mUrlImage;
    private String mSeasonNumber;
    private String mTVShowId;

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

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getSeasonNumber() {
        return mSeasonNumber;
    }

    public void setSeasonNumber(String seasonNumber) {
        mSeasonNumber = seasonNumber;
    }

    public String getTVShowId() {
        return mTVShowId;
    }

    public void setTVShowId(String TVShowId) {
        mTVShowId = TVShowId;
    }
}
