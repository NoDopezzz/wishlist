package nodopezzz.android.wishlist.Models;

import java.util.List;

public class TVShow extends MediaContent {

    private List<Season> mSeasons;
    private String mStatus;
    private String mNumberOfSeasons;

    public List<Season> getSeasons() {
        return mSeasons;
    }

    public void setSeasons(List<Season> seasons) {
        mSeasons = seasons;
    }

    public String toString(){
        return mDate;
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
}
