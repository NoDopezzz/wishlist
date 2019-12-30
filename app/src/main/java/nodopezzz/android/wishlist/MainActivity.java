package nodopezzz.android.wishlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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
                String content;
                switch(menuItem.getItemId()){
                    case R.id.navigation_item_movie:
                        content = TMDBAPI.CONTENT_MOVIE;
                        break;
                    case R.id.navigation_item_tv:
                        content = TMDBAPI.CONTENT_TV;
                        break;
                    case R.id.navigation_item_book:
                        content = TMDBAPI.CONTENT_MOVIE;
                        break;
                    default:
                        return false;
                }

                createFragment(content);

                return true;
            }
        });
        createFragment(TMDBAPI.CONTENT_MOVIE);
    }

    private void createFragment(String content){
        ListFragment fragment = ListFragment.newInstance(content);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_activity_frame, fragment)
                .commit();
    }
}

