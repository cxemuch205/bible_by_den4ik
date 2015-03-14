package ua.maker.gbible_v2.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import ua.maker.gbible_v2.Fragments.PoemListFragment;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityListener;

/**
 * Created by daniil on 11/7/14.
 */
public class PoemSlidePagerAdapter extends FragmentPagerAdapter {

    public OnCallBaseActivityListener callBaseActivityListener;
    private Context context;
    private ArrayList<PoemListFragment> listPoemsByChapterFragment;

    public PoemSlidePagerAdapter(Context context, FragmentManager fragmentManager, OnCallBaseActivityListener listener) {
        super(fragmentManager);
        this.callBaseActivityListener = listener;
        this.context = context;
        listPoemsByChapterFragment = new ArrayList<PoemListFragment>();
        for (int i = 1; i <= GBApplication.countChapters; i++) {
            listPoemsByChapterFragment.add(PoemListFragment.newInstance(callBaseActivityListener, i));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return listPoemsByChapterFragment.get(position);
    }

    @Override
    public int getCount() {
        return GBApplication.countChapters;
    }
}
