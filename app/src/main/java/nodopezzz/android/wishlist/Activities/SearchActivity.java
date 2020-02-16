package nodopezzz.android.wishlist.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nodopezzz.android.wishlist.Fragments.SearchFragment;
import nodopezzz.android.wishlist.R;

public class SearchActivity extends AppCompatActivity {

    private static final String ARG_CONTENT = "ARG_CONTENT";

    public static Intent newInstance(Context context, String content){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(ARG_CONTENT, content);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        String content = getIntent().getStringExtra(ARG_CONTENT);

        SearchFragment fragment = SearchFragment.newInstance(content);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment)
                .commit();
    }
}
