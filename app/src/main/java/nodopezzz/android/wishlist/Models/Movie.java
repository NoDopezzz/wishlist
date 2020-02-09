package nodopezzz.android.wishlist.Models;

public class Movie extends MediaContent {
    private String mBudget;
    private String mRevenue;

    public String getBudget() {
        return mBudget;
    }

    public void setBudget(String budget) {
        mBudget = budget;
    }

    public String getRevenue() {
        return mRevenue;
    }

    public void setRevenue(String revenue) {
        mRevenue = revenue;
    }
}
