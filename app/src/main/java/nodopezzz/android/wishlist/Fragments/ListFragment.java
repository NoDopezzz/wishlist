package nodopezzz.android.wishlist.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import nodopezzz.android.wishlist.Activities.SearchActivity;
import nodopezzz.android.wishlist.Adapters.SavedListAdapter;
import nodopezzz.android.wishlist.Content;
import nodopezzz.android.wishlist.Database.AsyncDatabaseGetByContent;
import nodopezzz.android.wishlist.Database.DBItem;
import nodopezzz.android.wishlist.ItemTouchDelete.ItemTouchHelperDelete;
import nodopezzz.android.wishlist.MemoryUtils.DimensionsCalculator;
import nodopezzz.android.wishlist.R;
import nodopezzz.android.wishlist.APIs.TMDBApi;
import nodopezzz.android.wishlist.Vibration;

public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";

    private static final String ARG_CONTENT = "ARG_CONTENT";

    private Content mContent;

    private FloatingActionButton mAddButton;

    private RecyclerView mList;
    private List<DBItem> mItems;

    private SavedListAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ImageView mDeleteView;
    private View mDeleteCircle;
    private ImageView mImagePlaceholder;

    private AnimatorSet mAnimatorSetTrash;
    private AnimatorSet mAnimatorSetCircle;

    public static ListFragment newInstance(Content content){
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        mAddButton = v.findViewById(R.id.fab_item_add);
        mList = v.findViewById(R.id.list_recyclerview);
        mProgressBar = v.findViewById(R.id.list_progressbar);
        mDeleteView = v.findViewById(R.id.list_delete_image);
        mDeleteCircle = v.findViewById(R.id.list_delete_circle);
        mImagePlaceholder = v.findViewById(R.id.list_image_placeholder);

        mList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy<-4 && !mAddButton.isShown())
                    mAddButton.show();
                else if(dy>4 && mAddButton.isShown())
                    mAddButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        Bundle args = getArguments();
        mContent = Content.MOVIE;
        if(args != null && args.getSerializable(ARG_CONTENT) != null){
            mContent = (Content)args.getSerializable(ARG_CONTENT);
        }

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentActivity(v);
            }
        });
        mAnimatorSetTrash = new AnimatorSet();
        mAnimatorSetCircle = new AnimatorSet();

        return v;
    }

    private void presentActivity(View v){
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "transition");
        int x = (int) (v.getX() + v.getWidth() / 2);
        int y = (int) (v.getY() + v.getHeight() / 2);

        Intent intent = SearchActivity.newInstance(getActivity(), mContent, x, y);
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    private void initList(){
        mImagePlaceholder.setVisibility(View.GONE);
        if(mItems == null) {
            mList.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        new AsyncDatabaseGetByContent(){

            @Override
            public void onPostGet(List<DBItem> argItems) {
                mItems = argItems;

                if(argItems.isEmpty()){
                    if(mContent == Content.MOVIE){
                        mImagePlaceholder.setImageResource(R.drawable.movie_placeholder);
                    } else if(mContent == Content.TV){
                        mImagePlaceholder.setImageResource(R.drawable.tv_placeholder);
                    } else if(mContent == Content.BOOK){
                        mImagePlaceholder.setImageResource(R.drawable.book_placeholder);
                    }
                    mImagePlaceholder.setVisibility(View.VISIBLE);
                }

                if(mAdapter == null) {
                    mAdapter = new SavedListAdapter(getActivity(), mItems, mDeleteView);

                    mAdapter.setOnStateChangedListener(new SavedListAdapter.onStateChangedListener() {
                        @Override
                        public void onSelected() {
                            animateShowingDeleteButton();
                            Vibration.vibrate(getActivity());
                            mAddButton.hide();
                        }

                        @Override
                        public void onClear() {
                            animateClosingDeleteButton();
                            animateCircleDeleteClosing();
                            mAddButton.show();
                        }

                        @Override
                        public void onDeleted() {
                            if(mItems.isEmpty()){
                                mImagePlaceholder.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onOverlapped() {
                            animateCircleDeleteShowing();
                        }

                        @Override
                        public void onOverlappedClear() {
                            animateCircleDeleteClosing();
                        }
                    });
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                    mList.setAdapter(mAdapter);
                    mList.setLayoutManager(layoutManager);

                    ItemTouchHelper.Callback callback = new ItemTouchHelperDelete(mAdapter);
                    ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                    touchHelper.attachToRecyclerView(mList);
                } else{
                    mAdapter.setItems(mItems);
                    mAdapter.notifyDataSetChanged();
                }
                mList.setVisibility(View.VISIBLE);

                mProgressBar.setVisibility(View.GONE);
            }
        }.execute(mContent);

    }

    private void animateCircleDeleteShowing(){
        if(mAnimatorSetCircle.isRunning()){
            mAnimatorSetCircle.cancel();
        }

        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(mDeleteCircle, "scaleX", 200.0f)
                .setDuration(200);
        animatorScaleX.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(mDeleteCircle, "scaleY", 200.0f)
                .setDuration(200);
        animatorScaleY.setInterpolator(new AccelerateInterpolator());

        mAnimatorSetCircle = new AnimatorSet();
        mAnimatorSetCircle.play(animatorScaleX).with(animatorScaleY);
        mAnimatorSetCircle.start();
    }

    private void animateCircleDeleteClosing(){
        if(mAnimatorSetCircle.isRunning()){
            mAnimatorSetCircle.cancel();
        }

        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(mDeleteCircle, "scaleX", 1.0f/200.0f)
                .setDuration(200);
        animatorScaleX.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(mDeleteCircle, "scaleY", 1.0f/200.0f)
                .setDuration(200);
        animatorScaleY.setInterpolator(new AccelerateInterpolator());

        mAnimatorSetCircle = new AnimatorSet();
        mAnimatorSetCircle.play(animatorScaleX).with(animatorScaleY);
        mAnimatorSetCircle.start();
    }

    private void animateShowingDeleteButton(){
        if(mAnimatorSetTrash.isRunning()){
            mAnimatorSetTrash.cancel();
        }

        float px = DimensionsCalculator.calculateDipToPx(getActivity(), 60f);

        float startY = mDeleteView.getY();
        float endY = mDeleteView.getY() - px;

        ObjectAnimator animator = ObjectAnimator.ofFloat(mDeleteView, "y", startY, endY)
                .setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        mAnimatorSetTrash = new AnimatorSet();
        mAnimatorSetTrash.play(animator);
        mAnimatorSetTrash.start();
    }

    private void animateClosingDeleteButton(){
        if(mAnimatorSetTrash.isRunning()){
            mAnimatorSetTrash.cancel();
        }

        float startY = mDeleteView.getY();
        float endY = getView().getHeight();

        ObjectAnimator animator = ObjectAnimator.ofFloat(mDeleteView, "y", startY, endY)
                .setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        mAnimatorSetTrash = new AnimatorSet();
        mAnimatorSetTrash.play(animator);
        mAnimatorSetTrash.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter.clear();
        }
    }
}
