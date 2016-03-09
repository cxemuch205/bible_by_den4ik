package ua.maker.gbible_v2.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.ArrayList;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import ua.maker.gbible_v2.Adapters.HistoryAdapter;
import ua.maker.gbible_v2.BaseActivity;
import ua.maker.gbible_v2.DataBases.UserDB;
import ua.maker.gbible_v2.Managers.ContentManager;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Interfaces.OnGetContentAdapter;
import ua.maker.gbible_v2.Interfaces.OnGetContentListener;
import ua.maker.gbible_v2.Models.History;
import ua.maker.gbible_v2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends RoboFragment {

    public static final String TAG = "HistoryFragment";

    private static HistoryFragment instance;

    public static HistoryFragment getInstance() {
        if (instance == null) {
            instance = new HistoryFragment();
        }
        return instance;
    }

    public HistoryFragment() {}

    @Inject UserDB userDB;
    @Inject ContentManager contentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @InjectView(R.id.toolbar_header) Toolbar toolbar;
    @InjectView(R.id.lv_data) ListView lvData;
    @InjectView(R.id.pb_load) ProgressBar pb;
    @InjectView(R.id.tv_message) TextView tvMessage;

    private HistoryAdapter adapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(toolbar);
        Tools.initProgressBar(pb);
        initListData();
        initListeners();
    }

    private void initToolbar(Toolbar toolbar) {
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.title_history);
        toolbar.inflateMenu(R.menu.menu_history);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_clear:
                        userDB.clearHistory();
                        adapter.clear();
                        toggleEmptyMessage(true);
                        return true;
                }
                return false;
            }
        });
    }

    private void initListData() {
        if (adapter == null) {
            adapter = new HistoryAdapter(getActivity(), new ArrayList<History>());
        }

        adapter.clear();
        contentManager.getHistory(TAG, getBookmarksAdapter);

        if (lvData != null && lvData.getAdapter() == null) {
            lvData.setAdapter(adapter);
        }

        int headerHeight = getResources().getDimensionPixelSize(R.dimen.header_height);

        View headerView = new View(getContext());
        AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
        headerView.setLayoutParams(headerParams);
        lvData.addFooterView(headerView);
    }



    private OnGetContentListener getBookmarksAdapter = new OnGetContentAdapter() {
        @Override
        public void onStartGet() {
            super.onStartGet();
            pb.setVisibility(ProgressBar.VISIBLE);
            toggleEmptyMessage(false);
        }

        @Override
        public void onEndGet(ArrayList<? extends Object> result, String tag) {
            super.onEndGet(result, tag);
            pb.setVisibility(ProgressBar.GONE);
            if (result != null) {
                ArrayList<History> data = (ArrayList<History>) result;
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enable) {
                    tvMessage.setVisibility(TextView.VISIBLE);
                    tvMessage.setText(R.string.empty_history);
                } else {
                    tvMessage.setVisibility(TextView.GONE);
                }
            }
        });
    }

    private void initListeners() {
        lvData.setOnItemClickListener(itemBookmarksClickListener);
    }

    private AdapterView.OnItemClickListener itemBookmarksClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            History history = adapter.getItem(position);
            ((BaseActivity) getActivity()).openReadContent(history.getBookId(), history.getChapter() - 1, 1);
        }
    };
}
