package nodopezzz.android.wishlist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import nodopezzz.android.wishlist.Content;
import nodopezzz.android.wishlist.Fragments.ContentMovieFragment;
import nodopezzz.android.wishlist.R;

public class ContentMovieActivity extends AppCompatActivity {

    private static final String ARG_ID = "ARG_ID";
    private static final String ARG_TITLE = "ARG_TITLE";

    public static Intent newInstance(Context context, String id, String title){
        Intent data = new Intent(context, ContentMovieActivity.class);
        data.putExtra(ARG_ID, id);
        data.putExtra(ARG_TITLE, title);
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        Intent data = getIntent();
        String id;
        String title;

        if(data != null){
            id = data.getStringExtra(ARG_ID);
            title = data.getStringExtra(ARG_TITLE);

            ContentMovieFragment fragment = ContentMovieFragment.newInstance(id, title);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment)
                    .commit();
        }
    }
}
