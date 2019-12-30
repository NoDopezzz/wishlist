package nodopezzz.android.wishlist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    private static final String ARG_CONTENT = "ARG_CONTENT";

    public static SearchFragment newInstance(String content){
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    private SearchView mSearchView;
    private RecyclerView mRecyclerView;

    private String mContent;

    private int mPage = 1;
    private List<SearchItem> mSearchItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchView = v.findViewById(R.id.search_view);
        mRecyclerView = v.findViewById(R.id.search_items_recycler_view);

        if(getArguments() != null && getArguments().getString(ARG_CONTENT) != null){
            mContent = getArguments().getString(ARG_CONTENT);
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchItems.clear();
                new GetSearchResult().execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return v;
    }

    private class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchItemHolder>{

        @NonNull
        @Override
        public SearchItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SearchItemHolder(inflater.inflate(R.layout.item_search, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SearchItemHolder holder, int position) {
            holder.bindView(position);
        }

        @Override
        public int getItemCount() {
            return mSearchItems.size();
        }

        private class SearchItemHolder extends RecyclerView.ViewHolder{

            private ImageView mImagePoster;
            private TextView mTextTitle;
            private TextView mTextDate;
            private TextView mTextOverview;

            public SearchItemHolder(@NonNull View itemView) {
                super(itemView);

                mImagePoster = itemView.findViewById(R.id.search_item_poster);
                mTextTitle = itemView.findViewById(R.id.search_item_title);
                mTextDate = itemView.findViewById(R.id.search_item_date);
                mTextOverview = itemView.findViewById(R.id.search_item_overview);
            }

            public void bindView(int position){
                SearchItem item = mSearchItems.get(position);

                //TODO setImageView

                mTextTitle.setText(item.getTitle());
                mTextDate.setText(item.getDate());
                mTextOverview.setText(item.getOverview());

            }
        }
    }

    private void updateUI(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new SearchListAdapter());
    }

    private class GetSearchResult extends AsyncTask<String, Void, List<SearchItem>>{

        @Override
        protected List<SearchItem> doInBackground(String... strings) {
            List<SearchItem> result = TMDBAPI.search(mContent, strings[0], mPage);
            if(isCancelled()) {
                return null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<SearchItem> searchItems) {
            mSearchItems.addAll(searchItems);
            updateUI();
        }
    }
}
