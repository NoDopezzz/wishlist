package nodopezzz.android.wishlist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

abstract public class OnScrolled extends RecyclerView.OnScrollListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 3;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    public OnScrolled(RecyclerView recyclerView, LinearLayoutManager layoutManager){
        mRecyclerView = recyclerView;
        mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if(dy > 0){
            visibleItemCount = mRecyclerView.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem/* + visibleThreshold*/)) {
                execute();
                loading = true;
            }
        }
    }

    public abstract void execute();

}
