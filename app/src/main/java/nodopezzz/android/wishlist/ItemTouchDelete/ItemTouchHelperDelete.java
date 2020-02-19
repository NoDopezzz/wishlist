package nodopezzz.android.wishlist.ItemTouchDelete;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import nodopezzz.android.wishlist.Adapters.SavedListAdapter;
import nodopezzz.android.wishlist.R;

public class ItemTouchHelperDelete extends ItemTouchHelper.Callback {

    private static final String TAG = "ItemTouchHelperDelete";

    private ItemTouchHelperAdapter mAdapter;
    private boolean mIsDragging = false;
    private boolean mIsOverlapping = false;

    public ItemTouchHelperDelete(ItemTouchHelperAdapter adapter){
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if(!mIsOverlapping){
            return mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder instanceof SavedListAdapter.SavedItemHolder){
            mAdapter.onItemSelected(viewHolder.getAdapterPosition(), viewHolder);
            mIsDragging = true;
        } else{
            mIsDragging = false;
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof SavedListAdapter.SavedItemHolder) {
            mAdapter.onClearView(viewHolder.getAdapterPosition(), viewHolder);
            mIsDragging = false;
        }
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if(mIsDragging){
            boolean previous = mIsOverlapping;
            mIsOverlapping = mAdapter.isOverlapping(viewHolder.itemView.findViewById(R.id.saved_image)) && viewHolder instanceof SavedListAdapter.SavedItemHolder;
            if(mIsOverlapping && !previous){
                mAdapter.onOverlapping();
            } else if(!mIsOverlapping && previous){
                mAdapter.onOverlappingClear();
            }
        }
        if(mIsOverlapping && !mIsDragging && viewHolder instanceof SavedListAdapter.SavedItemHolder){
            if(viewHolder.getAdapterPosition() == -1) return;
            mAdapter.onItemDismiss(viewHolder, viewHolder.getAdapterPosition());
            mIsOverlapping = false;
        }
    }


}
