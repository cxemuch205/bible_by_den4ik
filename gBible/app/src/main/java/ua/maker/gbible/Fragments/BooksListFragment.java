package ua.maker.gbible.Fragments;

import ua.maker.gbible.Interfaces.OnCallBaseActivityAdapter;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;

/**
 * Created by daniil on 11/6/14.
 */
public class BooksListFragment extends BaseFragment {

    public static String TAG = BooksListFragment.class.getClass().getSimpleName();

    public OnCallBaseActivityListener callBaseActivityListener;

    public void setOnCallBaseActivityListener(OnCallBaseActivityAdapter listener) {
        this.callBaseActivityListener = listener;
    }
}
