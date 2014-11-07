package ua.maker.gbible.Fragments;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.maker.gbible.Adapters.PoemSlidePagerAdapter;
import ua.maker.gbible.Constants.App;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible.R;


public class PagerPoemFragment extends BaseFragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_poem_fragment, null);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        // TODO: add FragmentActivity to base activity

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewPager();
    }

    @Override
    public void onResume() {
        super.onResume();
        GBApplication.getInstance().setHomeBibleLevel(App.BookHomeLevels.POEM);
    }

    private void initViewPager() {
        pagerAdapter = new PoemSlidePagerAdapter(getActivity(), getActivity().getSupportFragmentManager(), callBaseActivityListener);
        viewPager.setAdapter(pagerAdapter);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(GBApplication.chapterId);
            }
        });
    }
}
