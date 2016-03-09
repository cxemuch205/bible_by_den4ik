package ua.maker.gbible_v2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.inject.Inject;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Constants.IntentData;
import ua.maker.gbible_v2.DataBases.BibleDB;
import ua.maker.gbible_v2.Fragments.BookmarksFragment;
import ua.maker.gbible_v2.Fragments.BooksListFragment;
import ua.maker.gbible_v2.Fragments.ChapterListFragment;
import ua.maker.gbible_v2.Fragments.HistoryFragment;
import ua.maker.gbible_v2.Fragments.PagerPoemFragment;
import ua.maker.gbible_v2.Fragments.SearchFragment;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityAdapter;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible_v2.Managers.IntentManager;
import ua.maker.gbible_v2.Models.Poem;

@ContentView(R.layout.activity_base)
public class BaseActivity extends RoboActionBarActivity {

    public static final String TAG = "BaseActivity";

    private static final int DURATION_ANIM = 500;

    @InjectView(R.id.ll_home_bible) LinearLayout llBibleHome;
    @InjectView(R.id.ll_bookmarks) LinearLayout llBookmarks;
    @InjectView(R.id.ll_history) LinearLayout llHistory;
    @InjectView(R.id.ll_search) LinearLayout llSearch;
    @InjectView(R.id.btn_show_menu_bottom) Button btnOpenBottomMenu;
    @InjectView(R.id.ll_bottom_toolbar) LinearLayout llBottomToolBar;
    @InjectView(R.id.rl_bottom_container) RelativeLayout rlBottomContainer;

    @Inject BibleDB bibleDB;
    @Inject IntentManager intentManager;

    private DisplayMetrics displayMetrics;
    private ObjectAnimator oaBottomToolbarOut, oaBottomToolbarIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar));
        }
        initDataActivity();
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
        bibleDB.startupDB();
    }

    private void initTypefaces() {

    }

    private void initListener() {
        btnOpenBottomMenu.setOnClickListener(clickOpenBottomToolBarListener);
        llBibleHome.setOnClickListener(clickBibleHomeListener);
        llBookmarks.setOnClickListener(clickBookmarksListener);
        llHistory.setOnClickListener(clickHistoryListener);
        llSearch.setOnClickListener(clickSearchListener);
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

    private View.OnClickListener clickBookmarksListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initFragments(BookmarksFragment.getInstance(), BookmarksFragment.TAG);
        }
    };

    private View.OnClickListener clickHistoryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initFragments(HistoryFragment.getInstance(), HistoryFragment.TAG);
        }
    };

    private View.OnClickListener clickSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initFragments(SearchFragment.getInstance(), SearchFragment.TAG);
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

    public void openReadContent(int bookId, int chapter, int poem) {
        GBApplication.homeBibleLevel = App.BookHomeLevels.POEM;
        GBApplication.setBookId(bookId);
        GBApplication.setChapterId(chapter);
        GBApplication.setCountChapters(bibleDB.getNumberOfChapterInBook(bookId, Tools.getTranslateWitchPreferences(this)));
        GBApplication.setPoem(poem);
        GBApplication.setCurrentBookName(Tools.getBookNameByBookId(bookId, this));
        attachReadContent();
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

    public Toolbar.OnMenuItemClickListener getOptionMenuItemListener() {
        return optionMenuItemListener;
    }

    private Toolbar.OnMenuItemClickListener optionMenuItemListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_settings:
                    intentManager.startSettingsActivity();
                    break;
                case R.id.action_rfe:
                    intentManager.startReadForEveryDayActivity();
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentData.REQUEST_RED_LINK:
                if (resultCode == RESULT_OK) {
                    Bundle info = data.getExtras();
                    Poem poem = (Poem) info.getSerializable(App.Extras.DATA);
                    if (poem != null) {
                        openReadContent(poem.bookId, poem.chapter - 1, poem.poem + 1);
                    }
                }
                break;
        }
    }

    public RelativeLayout getRlBottomContainer() {
        return rlBottomContainer;
    }
}
