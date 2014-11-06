package ua.maker.gbible;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import ua.maker.gbible.Fragments.BaseFragment;


public class BaseActivity extends ActionBarActivity {

    public static final String TAG = "BaseActivity";

    private static final int DURATION_ANIM = 500;

    private Button btnOpenBottomMenu;
    private LinearLayout llBottomToolBar;
    private DisplayMetrics displayMetrics;

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
            initFragments(null);
        }
    }

    private void initFragments(BaseFragment fragment) {
        if (fragment != null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, fragment.TAG)
                    .commit();
    }

    private void initDataActivity() {
        displayMetrics = getResources().getDisplayMetrics();
    }

    private void initUI() {
        btnOpenBottomMenu = (Button) findViewById(R.id.btn_show_menu_bottom);
        llBottomToolBar = (LinearLayout) findViewById(R.id.ll_bottom_toolbar);
    }

    private void initTypefaces() {

    }

    private void initListener() {
        btnOpenBottomMenu.setOnClickListener(clickOpenBottomToolBarListener);
    }

    private void initAnimations() {
        oaBottomToolbarIn = ObjectAnimator.ofFloat(llBottomToolBar, "translationX", ((displayMetrics.widthPixels / 2) * (-1)), 0);
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
        oaBottomToolbarOut = ObjectAnimator.ofFloat(llBottomToolBar, "translationX", 0, ((displayMetrics.widthPixels / 2) * (-1)));
        oaBottomToolbarOut.setDuration(DURATION_ANIM);
        oaBottomToolbarOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                llBottomToolBar.setVisibility(LinearLayout.GONE);
                btnOpenBottomMenu.setText("<");
            }
        });
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
}
