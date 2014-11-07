package ua.maker.gbible.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.maker.gbible.Constants.App;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Interfaces.OnCallBaseActivityAdapter;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible.R;

/**
 * Created by daniil on 11/6/14.
 */
public class BooksListFragment extends BaseFragment {

    public static String TAG = BooksListFragment.class.getSimpleName();
    private static BooksListFragment instance;
    public static BooksListFragment getInstance(OnCallBaseActivityAdapter adapter) {
        if(instance == null)
            instance = new BooksListFragment();
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
        GBApplication.homeBibleLevel = App.BookHomeLevels.BOOK;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, null);
        return view;
    }
}
