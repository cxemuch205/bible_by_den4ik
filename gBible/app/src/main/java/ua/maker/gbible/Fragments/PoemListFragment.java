package ua.maker.gbible.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

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
public class PoemListFragment extends BaseFragment {

    public static final String TAG = "PoemListFragment";
    private static PoemListFragment instance;
    public static PoemListFragment getInstance(OnCallBaseActivityListener adapter) {
        if(instance == null)
            instance = new PoemListFragment();
        instance.setOnCallBaseActivityListener(adapter);
        return instance;
    }
    public OnCallBaseActivityListener callBaseActivityListener;
    public void setOnCallBaseActivityListener(OnCallBaseActivityListener listener) {
        this.callBaseActivityListener = listener;
    }

    private ListView lvData;
    private PoemAdapter adapter;
    private ProgressBar pb;

    boolean mScrolling = false;
    private int lastFirstVisibleItemPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poem_list, null);
        lvData = (ListView) view.findViewById(R.id.lv_data);
        pb = (ProgressBar) view.findViewById(R.id.pb_load);
        Tools.initProgressBar(pb);

        initListeners();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initListData(getActivity(), GBApplication.chapterId);
    }

    public void initListData(Activity activity, int chapterId) {
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
        lvData.setOnScrollListener(scrollDataListener);
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
