package nodopezzz.android.wishlist.Activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import nodopezzz.android.wishlist.Fragments.SearchFragment;
import nodopezzz.android.wishlist.R;

public class SearchActivity extends AppCompatActivity {

    private static final String ARG_CONTENT = "ARG_CONTENT";
    private static final String EXTRA_X = "EXTRA_X";
    private static final String EXTRA_Y = "EXTRA_Y";

    private int revealX;
    private int revealY;
    private View rootLayout;

    public static Intent newInstance(Context context, String content, int x, int y){
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
        String content = getIntent().getStringExtra(ARG_CONTENT);
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
}
