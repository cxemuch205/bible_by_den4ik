package ua.maker.gbible.Fragments;

import ua.maker.gbible.Constants.App;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Interfaces.OnCallBaseActivityAdapter;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;

/**
 * Created by daniil on 11/6/14.
 */
public class ChapterListFragment extends BaseFragment {

    public static String TAG = ChapterListFragment.class.getSimpleName();
    private static ChapterListFragment instance;
    public static ChapterListFragment getInstance(OnCallBaseActivityAdapter adapter) {
        if(instance == null)
            instance = new ChapterListFragment();
        instance.setOnCallBaseActivityListener(adapter);
        return instance;
    }
    public OnCallBaseActivityListener callBaseActivityListener;
    public void setOnCallBaseActivityListener(OnCallBaseActivityAdapter listener) {
        this.callBaseActivityListener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();
        GBApplication.homeBibleLevel = App.BookHomeLevels.CHAPTER;
    }
}
