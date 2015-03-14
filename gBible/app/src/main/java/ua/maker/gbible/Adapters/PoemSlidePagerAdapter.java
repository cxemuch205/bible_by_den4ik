package ua.maker.gbible.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import ua.maker.gbible.Fragments.PoemListFragment;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible.R;

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
