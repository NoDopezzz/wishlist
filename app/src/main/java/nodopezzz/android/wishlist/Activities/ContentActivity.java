package nodopezzz.android.wishlist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import nodopezzz.android.wishlist.Fragments.ContentFragment;
import nodopezzz.android.wishlist.R;

public class ContentActivity extends AppCompatActivity {

    private static final String ARG_CONTENT = "ARG_CONTENT";
    private static final String ARG_ID = "ARG_ID";
    private static final String ARG_TITLE = "ARG_TITLE";

    public static Intent newInstance(Context context, String content, String id, String title){
        Intent data = new Intent(context, ContentActivity.class);
        data.putExtra(ARG_CONTENT, content);
        data.putExtra(ARG_ID, id);
        data.putExtra(ARG_TITLE, title);
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent data = getIntent();
        String content;
        String id;
        String title;

        if(data != null){
            content = data.getStringExtra(ARG_CONTENT);
            id = data.getStringExtra(ARG_ID);
            title = data.getStringExtra(ARG_TITLE);

            ContentFragment fragment = ContentFragment.newInstance(content, id, title);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }
}
