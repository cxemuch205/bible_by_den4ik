package ua.maker.gbible.Adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ua.maker.gbible.Fragments.PoemListFragment;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;

/**
 * Created by daniil on 11/7/14.
 */
public class PoemSlidePagerAdapter extends FragmentStatePagerAdapter {

    public OnCallBaseActivityListener callBaseActivityListener;
    private Activity activity;

    public PoemSlidePagerAdapter(Activity activity, FragmentManager fragmentManager, OnCallBaseActivityListener listener) {
        super(fragmentManager);
        this.callBaseActivityListener = listener;
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int i) {
        PoemListFragment fragment = PoemListFragment
                .getInstance(callBaseActivityListener);
        int chapterId = i + 1;
        GBApplication.getInstance().setChapterId(chapterId);
        return fragment;
    }

    @Override
    public int getCount() {
        return GBApplication.countChapters;
    }
}
