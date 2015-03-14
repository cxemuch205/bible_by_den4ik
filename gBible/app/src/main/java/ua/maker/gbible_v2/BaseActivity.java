package ua.maker.gbible_v2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Fragments.BooksListFragment;
import ua.maker.gbible_v2.Fragments.ChapterListFragment;
import ua.maker.gbible_v2.Fragments.PagerPoemFragment;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityAdapter;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityListener;


public class BaseActivity extends ActionBarActivity {

    public static final String TAG = "BaseActivity";

    private static final int DURATION_ANIM = 500;

    private Button btnOpenBottomMenu;
    private LinearLayout llBottomToolBar;
    private DisplayMetrics displayMetrics;
    private LinearLayout llBibleHome;

    private ObjectAnimator oaBottomToolbarOut, oaBottomToolbarIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initDataActivity();
        initUI();
        initTypefaces();
        initAnimations();
        initListener();

        if (savedInstanceState == null) {
            attachReadContent();
        }
    }

    private void initFragments(Fragment fragment, String tag) {
        if (fragment != null) {
            Log.d(TAG, "initFragments: " + tag);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .commit();
        }
    }

    private void initDataActivity() {
        displayMetrics = getResources().getDisplayMetrics();
    }

    private void initUI() {
        btnOpenBottomMenu = (Button) findViewById(R.id.btn_show_menu_bottom);
        llBottomToolBar = (LinearLayout) findViewById(R.id.ll_bottom_toolbar);
        llBibleHome = (LinearLayout) findViewById(R.id.ll_home_bible);
    }

    private void initTypefaces() {

    }

    private void initListener() {
        btnOpenBottomMenu.setOnClickListener(clickOpenBottomToolBarListener);
        llBibleHome.setOnClickListener(clickBibleHomeListener);
    }

    private void initAnimations() {
        oaBottomToolbarIn = ObjectAnimator.ofFloat(llBottomToolBar, "translationX", (displayMetrics.widthPixels * 2), 0);
        oaBottomToolbarIn.setDuration(DURATION_ANIM);
        oaBottomToolbarIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                llBottomToolBar.setVisibility(LinearLayout.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btnOpenBottomMenu.setText(">");
            }
        });
        oaBottomToolbarOut = ObjectAnimator.ofFloat(llBottomToolBar, "translationX", 0, (displayMetrics.widthPixels * 2));
        oaBottomToolbarOut.setDuration(DURATION_ANIM);
        oaBottomToolbarOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                llBottomToolBar.setVisibility(LinearLayout.GONE);
                btnOpenBottomMenu.setText("<");
            }
        });
        btnOpenBottomMenu.setAlpha(0.42f);
    }

    private View.OnClickListener clickOpenBottomToolBarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (llBottomToolBar.getVisibility() == LinearLayout.VISIBLE) {
                showHideBottomToolbar(false);
            } else {
                showHideBottomToolbar(true);
            }
        }
    };

    private void showHideBottomToolbar(boolean show) {
        if (show)
            oaBottomToolbarIn.start();
        else
            oaBottomToolbarOut.start();
    }

    private View.OnClickListener clickBibleHomeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            attachReadContent();
        }
    };

    private void attachReadContent() {
        switch (GBApplication.homeBibleLevel) {
            case App.BookHomeLevels.BOOK:
                initFragments(BooksListFragment.getInstance(bibleLinkListener), BooksListFragment.TAG);
                break;
            case App.BookHomeLevels.CHAPTER:
                initFragments(ChapterListFragment.getInstance(bibleLinkListener), ChapterListFragment.TAG);
                break;
            case App.BookHomeLevels.POEM:
                initFragments(PagerPoemFragment.getInstance(bibleLinkListener), PagerPoemFragment.TAG);
                break;
        }
    }

    private OnCallBaseActivityListener bibleLinkListener = new OnCallBaseActivityAdapter() {
        @Override
        public void callShowHideBottomToolBar(boolean show) {
            super.callShowHideBottomToolBar(show);
            showHideBottomToolbar(show);
        }

        @Override
        public void switchFragment(String tag, Fragment fragment) {
            super.switchFragment(tag, fragment);
            initFragments(fragment, tag);
        }
    };
}
