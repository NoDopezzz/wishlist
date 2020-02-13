package nodopezzz.android.wishlist.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nodopezzz.android.wishlist.Fragments.ContentBookFragment;
import nodopezzz.android.wishlist.Models.Book;
import nodopezzz.android.wishlist.R;

public class ContentBookActivity extends AppCompatActivity{
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_ID = "ARG_ID";

    public static Intent newInstance(Context activity,
                                     String id,
                                     String title){
        Intent intent = new Intent(activity, ContentBookActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent = getIntent();
        String title = intent.getStringExtra(ARG_TITLE);
        String id = intent.getStringExtra(ARG_ID);

        ContentBookFragment fragment = ContentBookFragment.newInstance(title, id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_book_menu, menu);
        return true;
    }
}
