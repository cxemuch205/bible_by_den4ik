package ua.maker.gbible_v2.Fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible_v2.Adapters.BookmarksAdapter;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.UserDB;
import ua.maker.gbible_v2.Helpers.ContentTools;
import ua.maker.gbible_v2.Helpers.DropBoxTools;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Interfaces.OnGetContentAdapter;
import ua.maker.gbible_v2.Interfaces.OnGetContentListener;
import ua.maker.gbible_v2.Models.BookMark;
import ua.maker.gbible_v2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment {

    public static final String TAG = "BookmarksFragment";

    public BookmarksFragment() {}

    private static BookmarksFragment instance;
    private AppCompatActivity activity;
    private LocalBroadcastManager broadcastManager;

    public static BookmarksFragment getInstance() {
        if (instance == null) {
            instance = new BookmarksFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
        DropBoxTools.getInstance().sync();
        userDB = new UserDB(activity);
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerUpdateBroadcast();
    }

    private void registerUpdateBroadcast() {
        IntentFilter filer = new IntentFilter();
        filer.addAction(App.Actions.UPDATE_BOOKMARKS);
        broadcastManager.registerReceiver(receiverUpdate, filer);
    }

    private BroadcastReceiver receiverUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null) {
                toggleEmptyMessage(adapter.isEmpty());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false);
    }

    private Toolbar toolbar;
    private ListView lvData;
    private BookmarksAdapter adapter;
    private ProgressBar pb;
    private TextView tvMessage;
    private UserDB userDB;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_header);
        lvData = (ListView) view.findViewById(R.id.lv_data);
        pb = (ProgressBar) view.findViewById(R.id.pb_load);
        tvMessage = (TextView) view.findViewById(R.id.tv_message);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initToolbar(toolbar);
        Tools.initProgressBar(pb);
        initListData();
        initListeners();
    }

    private void initToolbar(Toolbar toolbar) {
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.title_bookmarks);
    }

    private void initListData() {
        if (adapter == null) {
            adapter = new BookmarksAdapter(getActivity(), new ArrayList<BookMark>(), userDB);
        }

        adapter.clear();
        ContentTools.getBookmarks(userDB, activity, TAG, getBookmarksAdapter);

        if (lvData != null && lvData.getAdapter() == null) {
            lvData.setAdapter(adapter);
        }

        int headerHeight = activity.getResources().getDimensionPixelSize(R.dimen.header_height);

        View headerView = new View(activity);
        AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
        headerView.setLayoutParams(headerParams);
        lvData.addFooterView(headerView);
    }



    private OnGetContentListener getBookmarksAdapter = new OnGetContentAdapter() {
        @Override
        public void onStartGet() {
            super.onStartGet();
            toggleEmptyMessage(false);
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        public void onEndGet(ArrayList<? extends Object> result, String tag) {
            super.onEndGet(result, tag);
            pb.setVisibility(ProgressBar.GONE);
            if (result != null) {
                ArrayList<BookMark> data = (ArrayList<BookMark>) result;
                if (data.isEmpty()) {
                    toggleEmptyMessage(true);
                } else {
                    adapter.addAll(data);
                }
            } else {
                toggleEmptyMessage(true);
            }
        }
    };

    private void toggleEmptyMessage(final boolean enable) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enable) {
                    tvMessage.setVisibility(TextView.VISIBLE);
                    tvMessage.setText(R.string.empty_bookmark);
                } else {
                    tvMessage.setVisibility(TextView.GONE);
                }
            }
        });
    }

    private void initListeners() {
        lvData.setOnItemClickListener(itemBookmarksClickListener);
        lvData.setOnScrollListener(scrollBookmarksListener);
    }

    private AdapterView.OnItemClickListener itemBookmarksClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.toggleMenuByItem(adapter.getItem(position));
        }
    };

    private AbsListView.OnScrollListener scrollBookmarksListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                adapter.setDefaultOtherItems(null);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(receiverUpdate);
    }
}
