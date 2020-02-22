package nodopezzz.android.wishlist.Activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import nodopezzz.android.wishlist.Content;
import nodopezzz.android.wishlist.Fragments.SearchFragment;
import nodopezzz.android.wishlist.R;

public class SearchActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 0;
    public static final int RESULT_CODE_ERROR = 1;

    private static final String ARG_CONTENT = "ARG_CONTENT";
    private static final String EXTRA_X = "EXTRA_X";
    private static final String EXTRA_Y = "EXTRA_Y";

    private int revealX;
    private int revealY;
    private View rootLayout;

    public static Intent newInstance(Context context, Content content, int x, int y){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(ARG_CONTENT, content);
        intent.putExtra(EXTRA_X, x);
        intent.putExtra(EXTRA_Y, y);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);

        rootLayout = findViewById(R.id.fragment_frame);
        Content content = (Content)getIntent().getSerializableExtra(ARG_CONTENT);
        Intent intent = getIntent();
        if(savedInstanceState == null && intent.hasExtra(EXTRA_X) && intent.hasExtra(EXTRA_Y)){
            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_X, 0);
            revealY = intent.getIntExtra(EXTRA_Y, 0);

            ViewTreeObserver observer = rootLayout.getViewTreeObserver();
            if(observer.isAlive()){
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else{
            rootLayout.setVisibility(View.VISIBLE);
        }

        Log.i("Retrofit", content.name());
        SearchFragment fragment = SearchFragment.newInstance(content);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment)
                .commit();
    }

    private void revealActivity(int x, int y){
        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
        circularReveal.setDuration(500);
        circularReveal.setInterpolator(new AccelerateInterpolator());

        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_CODE_ERROR){
                Snackbar.make(findViewById(R.id.fragment_frame), R.string.error_open, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
