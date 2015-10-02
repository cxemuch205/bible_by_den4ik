package ua.maker.gbible_v2.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ua.maker.gbible_v2.Fragments.PoemListFragment;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityListener;

/**
 * Created by daniil on 11/7/14.
 */
public class PoemSlidePagerAdapter extends FragmentPagerAdapter {

    public OnCallBaseActivityListener callBaseActivityListener;

    public PoemSlidePagerAdapter(FragmentManager fragmentManager, OnCallBaseActivityListener listener) {
        super(fragmentManager);
        this.callBaseActivityListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return PoemListFragment.newInstance(callBaseActivityListener, position + 1);
    }

    @Override
    public int getCount() {
        return GBApplication.countChapters;
    }
}
