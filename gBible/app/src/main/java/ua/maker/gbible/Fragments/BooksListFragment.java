package ua.maker.gbible.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ua.maker.gbible.Adapters.BibleLinkAdapter;
import ua.maker.gbible.Constants.App;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Helpers.ContentTools;
import ua.maker.gbible.Helpers.Tools;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible.Interfaces.OnGetContentAdapter;
import ua.maker.gbible.Interfaces.OnGetContentListener;
import ua.maker.gbible.Models.BibleLink;
import ua.maker.gbible.R;

/**
 * Created by daniil on 11/6/14.
 */
public class BooksListFragment extends BaseFragment {

    public static String TAG = BooksListFragment.class.getSimpleName();
    private static BooksListFragment instance;
    public static BooksListFragment getInstance(OnCallBaseActivityListener adapter) {
        if(instance == null)
            instance = new BooksListFragment();
        instance.setOnCallBaseActivityListener(adapter);
        return instance;
    }
    public OnCallBaseActivityListener callBaseActivityListener;
    public void setOnCallBaseActivityListener(OnCallBaseActivityListener listener) {
        this.callBaseActivityListener = listener;
    }

    private ListView lvData;
    private BibleLinkAdapter adapter;
    private ProgressBar pb;

    boolean mScrolling = false;
    private int lastFirstVisibleItemPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, null);
        lvData = (ListView) view.findViewById(R.id.lv_data);
        pb = (ProgressBar) view.findViewById(R.id.pb_load);
        Tools.initProgressBar(pb);

        initListData();
        initListeners();

        return view;
    }

    private void initListData() {
        if (adapter == null) {
            adapter = new BibleLinkAdapter(getActivity(), new ArrayList<BibleLink>());
        }

        adapter.clear();
        ContentTools.getListBooks(getActivity(), TAG, getListBooksAdapter);

        if (lvData != null && lvData.getAdapter() == null) {
            lvData.setAdapter(adapter);
        }
    }

    private void initListeners() {
        lvData.setOnItemClickListener(clickOnBookItemListener);
        lvData.setOnScrollListener(scrollDataListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        GBApplication.getInstance().setHomeBibleLevel(App.BookHomeLevels.BOOK);
        setToTopBook();
    }

    private void setToTopBook() {
        for (int i = 0; i < adapter.getCount(); i++) {
            BibleLink item = adapter.getItem(i);
            if (item.id == GBApplication.topBookId) {
                lvData.smoothScrollToPosition(i);
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GBApplication.getInstance().setTopBookId(adapter.getItem(lvData.getFirstVisiblePosition()).id);
    }

    private OnGetContentListener getListBooksAdapter = new OnGetContentAdapter() {
        @Override
        public void onStartGet() {
            super.onStartGet();
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        public void onEndGet(ArrayList<? extends Object> result, String tag) {
            super.onEndGet(result, tag);
            pb.setVisibility(ProgressBar.GONE);
            if (result != null) {
                ArrayList<BibleLink> data = (ArrayList<BibleLink>) result;
                adapter.addAll(data);
            }
        }
    };

    private AdapterView.OnItemClickListener clickOnBookItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(callBaseActivityListener != null) {
                BibleLink book = adapter.getItem(position);
                GBApplication.getInstance().setBookId(book.id);
                callBaseActivityListener.switchFragment(ChapterListFragment.TAG,
                        ChapterListFragment.getInstance(null));
            }
            else
                Log.e(TAG, "LISTENER CALL BASE ACTIVITY ## NULL");
        }
    };

    private AbsListView.OnScrollListener scrollDataListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    mScrolling = false;
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    mScrolling = true;
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mScrolling
                    && firstVisibleItem > lastFirstVisibleItemPosition
                    && callBaseActivityListener != null) {
                callBaseActivityListener.callShowHideBottomToolBar(false);
            } else if(callBaseActivityListener == null) {
                Log.e(TAG, "CALL BASE ACTIVITY LISTENER ## NULL");
            }
        }
    };
}
