package nodopezzz.android.wishlist.Models;

public class Episode {
    private String mTitle;
    private String mUrlImage;
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

    @Override
    public String toString(){
        return mTitle + " - " + mUrlImage + " - " + mOverview;
    }
}
