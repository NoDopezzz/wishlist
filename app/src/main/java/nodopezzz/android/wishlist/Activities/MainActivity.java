package nodopezzz.android.wishlist.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import nodopezzz.android.wishlist.APIs.TMDBApi;
import nodopezzz.android.wishlist.Content;
import nodopezzz.android.wishlist.Fragments.ListFragment;
import nodopezzz.android.wishlist.APIs.GoogleBooksAPI;
import nodopezzz.android.wishlist.R;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_ERROR_OPEN = 2;

    private BottomNavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = findViewById(R.id.navigation_activity_navigation_view);
        mNavigationView.setSelectedItemId(R.id.navigation_item_movie);
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Content content;
                switch(menuItem.getItemId()){
                    case R.id.navigation_item_movie:
                        content = Content.MOVIE;
                        break;
                    case R.id.navigation_item_tv:
                        content = Content.TV;
                        break;
                    case R.id.navigation_item_book:
                        content = Content.BOOK;
                        break;
                    default:
                        return false;
                }

                createFragment(content);

                return true;
            }
        });
        createFragment(Content.MOVIE);
    }

    private void createFragment(Content content){
        ListFragment fragment = ListFragment.newInstance(content);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_activity_frame, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_ERROR_OPEN){
                Log.i("ContentMovieFragment", "result");
                Snackbar.make(findViewById(R.id.navigation_activity_frame), R.string.error_open, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}

