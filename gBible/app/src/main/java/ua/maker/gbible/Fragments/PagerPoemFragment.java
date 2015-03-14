package ua.maker.gbible.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.maker.gbible.Adapters.PoemSlidePagerAdapter;
import ua.maker.gbible.Constants.App;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible.R;


public class PagerPoemFragment extends Fragment {

    public static final String TAG = "ListPoemFragment";
    private static PagerPoemFragment instance;

    public static PagerPoemFragment getInstance(OnCallBaseActivityListener adapter) {
        if (instance == null) {
            instance = new PagerPoemFragment();
        }
        instance.setOnCallBaseActivityListener(adapter);
        return instance;
    }

    public OnCallBaseActivityListener callBaseActivityListener;

    public void setOnCallBaseActivityListener(OnCallBaseActivityListener listener) {
        this.callBaseActivityListener = listener;
    }

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private ActionBarActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActionBarActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_list_poem_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        initViewPager();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        GBApplication.getInstance().setHomeBibleLevel(App.BookHomeLevels.POEM);

        viewPager.setCurrentItem(GBApplication.chapterId);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                GBApplication.setChapterId(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void initViewPager() {
        Log.d(TAG, "initViewPager()");
        pagerAdapter = new PoemSlidePagerAdapter(activity,
                getChildFragmentManager(),
                callBaseActivityListener);
        viewPager.setAdapter(pagerAdapter);
    }
}
