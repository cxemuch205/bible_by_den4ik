package ua.maker.gbible.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;

import java.util.ArrayList;

import ua.maker.gbible.Adapters.PoemAdapter;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Helpers.ContentTools;
import ua.maker.gbible.Helpers.Tools;
import ua.maker.gbible.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible.Interfaces.OnGetContentAdapter;
import ua.maker.gbible.Interfaces.OnGetContentListener;
import ua.maker.gbible.Models.Poem;
import ua.maker.gbible.R;

/**
 * Created by daniil on 11/7/14.
 */
public class PoemListFragment extends Fragment {

    public static final String TAG = "PoemListFragment";
    private static PoemListFragment instance;
    public static PoemListFragment getInstance(OnCallBaseActivityListener adapter) {
        if(instance == null)
            instance = new PoemListFragment();
        instance.setOnCallBaseActivityListener(adapter);
        return instance;
    }

    public static PoemListFragment newInstance(OnCallBaseActivityListener activityListener, int chapter) {
        PoemListFragment fragment = new PoemListFragment();
        fragment.setOnCallBaseActivityListener(activityListener);
        fragment.chapter = chapter;
        return fragment;
    }

    public OnCallBaseActivityListener callBaseActivityListener;

    public void setOnCallBaseActivityListener(OnCallBaseActivityListener listener) {
        this.callBaseActivityListener = listener;
    }
    private ListView lvData;
    private PoemAdapter adapter;
    private ProgressBar pb;

    private Toolbar toolbar;
    boolean mScrolling = false;
    private int lastFirstVisibleItemPosition = 0, chapter = 1;
    private ActionBarActivity activity;

    private View headerView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setRetainInstance(false);
        this.activity = (ActionBarActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poem_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvData = (ListView) view.findViewById(R.id.lv_data);
        pb = (ProgressBar) view.findViewById(R.id.pb_load);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_header);
        initQRToolbar();
        initToolbar();
        Tools.initProgressBar(pb);

        initListeners();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.icon_back_navigation);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBaseActivityListener != null) {
                    callBaseActivityListener.switchFragment(ChapterListFragment.TAG,
                            ChapterListFragment.getInstance(callBaseActivityListener));
                }
            }
        });
    }

    private void initQRToolbar() {
        int headerHeight = activity.getResources().getDimensionPixelSize(R.dimen.header_height);
        int headerHeight2 = getActivity().getResources().getDimensionPixelSize(R.dimen.header_height2);

        toolbar.setTitleTextColor(Color.WHITE);

        headerView = new View(activity);
        AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight2);
        headerView.setLayoutParams(headerParams);
        lvData.addHeaderView(headerView);

        lvData.setOnScrollListener(new QuickReturnListViewOnScrollListener
                .Builder(QuickReturnViewType.HEADER)
                .header(toolbar)
                .minHeaderTranslation(-headerHeight)
                .build());
    }

    private void setTitleActionBar(int chapter) {
        toolbar.setTitle(GBApplication.bookName + " | "
                + activity.getString(R.string.chapter) + " "
                + chapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        initListData(getActivity());
        setTitleActionBar(chapter);
    }

    public void initListData(Activity activity) {
        if (adapter == null) {
            adapter = new PoemAdapter(activity, new ArrayList<Poem>());
        }

        adapter.clear();
        ContentTools.getListPoemsFromChapter(activity, TAG, getContentChapterListener);

        if (lvData != null && lvData.getAdapter() == null) {
            lvData.setAdapter(adapter);
        }
    }

    private void initListeners() {
        //lvData.setOnScrollListener(scrollDataListener);
    }

    private OnGetContentListener getContentChapterListener = new OnGetContentAdapter() {
        @Override
        public void onStartGet() {
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        public void onEndGet(ArrayList<? extends Object> result, String tag) {
            pb.setVisibility(ProgressBar.GONE);
            if (result != null) {
                ArrayList<Poem> data = (ArrayList<Poem>) result;
                adapter.addAll(data);
            }
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
