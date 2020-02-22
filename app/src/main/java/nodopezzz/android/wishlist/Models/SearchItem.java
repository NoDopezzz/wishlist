package nodopezzz.android.wishlist.Models;

public class SearchItem{
    private String mTitle;
    private String mSubtitle;
    private String mOverview;
    private String mThumbnailUrl;
    private String mId;

    public SearchItem(String title, String subtitle, String overview, String urlImage, String id) {
        mTitle = title;
        mSubtitle = subtitle;
        mOverview = overview;
        mThumbnailUrl = urlImage;
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
