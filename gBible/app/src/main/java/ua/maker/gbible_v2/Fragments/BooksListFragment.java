package ua.maker.gbible_v2.Fragments;

import android.os.Build;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.google.inject.Inject;

import java.util.ArrayList;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import ua.maker.gbible_v2.Adapters.BibleLinkAdapter;
import ua.maker.gbible_v2.BaseActivity;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.Managers.ContentManager;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible_v2.Interfaces.OnGetContentAdapter;
import ua.maker.gbible_v2.Interfaces.OnGetContentListener;
import ua.maker.gbible_v2.Models.BibleLink;
import ua.maker.gbible_v2.R;

/**
 * Created by daniil on 11/6/14.
 */
public class BooksListFragment extends RoboFragment {

    public static String TAG = BooksListFragment.class.getSimpleName();
    private static BooksListFragment instance;

    public static BooksListFragment getInstance(OnCallBaseActivityListener adapter) {
        if (instance == null)
            instance = new BooksListFragment();
        instance.setOnCallBaseActivityListener(adapter);
        return instance;
    }

    public OnCallBaseActivityListener callBaseActivityListener;

    public void setOnCallBaseActivityListener(OnCallBaseActivityListener listener) {
        this.callBaseActivityListener = listener;
    }

    @InjectView(R.id.toolbar_header) Toolbar toolbar;
    @InjectView(R.id.lv_data) ListView lvData;
    @InjectView(R.id.pb_load) ProgressBar pb;

    @Inject ContentManager contentManager;

    private BibleLinkAdapter adapter;

    boolean mScrolling = false;
    private int lastFirstVisibleItemPosition = 0;
    private View headerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Tools.initProgressBar(pb);
        initListData();
        initToolbar();
        initActionbar();

        initListeners();
    }

    private void initActionbar() {
        toolbar.inflateMenu(R.menu.menu_base);
        toolbar.setOnMenuItemClickListener(((BaseActivity) getActivity())
                .getOptionMenuItemListener());
    }

    private void initToolbar() {
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        if (Build.VERSION.SDK_INT >= 16) {
            headerHeight = toolbar.getMinimumHeight();
        }

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.chose_book);

        headerView = new View(getContext());
        AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
        headerView.setLayoutParams(headerParams);
        lvData.addHeaderView(headerView);
        lvData.addFooterView(headerView);

        lvData.setOnScrollListener(new QuickReturnListViewOnScrollListener
                .Builder(QuickReturnViewType.HEADER)
                .header(toolbar)
                .minHeaderTranslation(-headerHeight)
                .build());
    }

    private void initListData() {
        if (adapter == null) {
            adapter = new BibleLinkAdapter(getActivity(), new ArrayList<BibleLink>());
        }

        adapter.clear();
        contentManager.getListBooks(TAG, getListBooksAdapter);

        if (lvData != null && lvData.getAdapter() == null) {
            lvData.setAdapter(adapter);
        }
    }

    private void initListeners() {
        lvData.setOnItemClickListener(clickOnBookItemListener);
        //lvData.setOnScrollListener(scrollDataListener);
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
        if (adapter != null && adapter.getCount() > 0) {
            GBApplication.getInstance().setTopBookId(adapter.getItem(lvData.getFirstVisiblePosition()).id);
        }
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
            if (callBaseActivityListener != null) {
                BibleLink book = adapter.getItem(position - lvData.getHeaderViewsCount());
                GBApplication.setBookId(book.bookId);
                GBApplication.setCurrentBookName(book.name);
                callBaseActivityListener.switchFragment(ChapterListFragment.TAG,
                        ChapterListFragment.getInstance(callBaseActivityListener));
            } else
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
            } else if (callBaseActivityListener == null) {
                Log.e(TAG, "CALL BASE ACTIVITY LISTENER ## NULL");
            }
        }
    };
}
