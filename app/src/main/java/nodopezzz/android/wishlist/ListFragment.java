package nodopezzz.android.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class ListFragment extends Fragment {

    private static final String ARG_CONTENT = "ARG_CONTENT";

    private String mContent;

    private TextView mTextView;
    private FloatingActionButton mAddButton;

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

        mTextView = v.findViewById(R.id.text_view);
        mAddButton = v.findViewById(R.id.fab_item_add);

        Bundle args = getArguments();
        mContent = TMDBAPI.CONTENT_MOVIE;
        if(args != null && args.getString(ARG_CONTENT) != null){
            mContent = args.getString(ARG_CONTENT);
        }

        mTextView.setText(mContent);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment fragment = SearchFragment.newInstance(mContent);
                if(getFragmentManager() == null) return;
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.navigation_activity_frame, fragment)
                        .commit();
            }
        });
        return v;

    }
}
