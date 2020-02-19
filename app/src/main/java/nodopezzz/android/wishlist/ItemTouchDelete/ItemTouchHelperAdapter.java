package nodopezzz.android.wishlist.ItemTouchDelete;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(RecyclerView.ViewHolder viewHolderm, int position);
    void onItemSelected(int position, RecyclerView.ViewHolder holder);
    void onClearView(int position, RecyclerView.ViewHolder holder);
    boolean isOverlapping(View firstView);
    void onOverlapping();
    void onOverlappingClear();
}
