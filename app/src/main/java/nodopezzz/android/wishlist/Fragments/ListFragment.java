package nodopezzz.android.wishlist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import nodopezzz.android.wishlist.Activities.SearchActivity;
import nodopezzz.android.wishlist.Adapters.SavedListAdapter;
import nodopezzz.android.wishlist.Database.AsyncDatabaseGetByContent;
import nodopezzz.android.wishlist.Database.DBItem;
import nodopezzz.android.wishlist.R;
import nodopezzz.android.wishlist.TMDBAPI;

public class ListFragment extends Fragment {

    private static final String ARG_CONTENT = "ARG_CONTENT";

    private String mContent;

    private FloatingActionButton mAddButton;

    private RecyclerView mList;
    private List<DBItem> mItems;

    private SavedListAdapter mAdapter;
    private ProgressBar mProgressBar;

    public static ListFragment newInstance(String content){
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
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

        Bundle args = getArguments();
        mContent = TMDBAPI.CONTENT_MOVIE;
        if(args != null && args.getString(ARG_CONTENT) != null){
            mContent = args.getString(ARG_CONTENT);
        }

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SearchActivity.newInstance(getActivity(), mContent));
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    private void initList(){
        mProgressBar.setVisibility(View.VISIBLE);
        new AsyncDatabaseGetByContent(){

            @Override
            public void onPostGet(List<DBItem> argItems) {
                mItems = argItems;
                mAdapter = new SavedListAdapter(getActivity(), mItems);
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                mList.setAdapter(mAdapter);
                mList.setLayoutManager(layoutManager);

                mProgressBar.setVisibility(View.GONE);
            }
        }.execute(mContent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdapter != null){
            mAdapter.clear();
        }
    }
}
