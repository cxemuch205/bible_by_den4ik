package ua.maker.gbible_v2.Fragments;

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
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;

import java.util.ArrayList;

import ua.maker.gbible_v2.Adapters.BibleLinkAdapter;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.Helpers.ContentTools;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible_v2.Interfaces.OnGetContentAdapter;
import ua.maker.gbible_v2.Interfaces.OnGetContentListener;
import ua.maker.gbible_v2.Models.BibleLink;
import ua.maker.gbible_v2.R;
import ua.maker.gbible_v2.Views.HeaderGridView;

/**
 * Created by daniil on 11/6/14.
 */
public class ChapterListFragment extends Fragment {

    public static String TAG = ChapterListFragment.class.getSimpleName();
    private static final int NUMB_OF_COLUMNS = 6;
    private static ChapterListFragment instance;

    public static ChapterListFragment getInstance(OnCallBaseActivityListener adapter) {
        if(instance == null)
            instance = new ChapterListFragment();
        instance.setOnCallBaseActivityListener(adapter);
        return instance;
    }

    public OnCallBaseActivityListener callBaseActivityListener;

    public void setOnCallBaseActivityListener(OnCallBaseActivityListener listener) {
        this.callBaseActivityListener = listener;
    }

    private HeaderGridView gvData;
    private BibleLinkAdapter adapter;
    private ProgressBar pb;
    private Toolbar toolbar;
    private ActionBarActivity activity;
    private View headerView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setRetainInstance(true);
        this.activity = (ActionBarActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chapter, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gvData = (HeaderGridView) view.findViewById(R.id.gv_data);
        pb = (ProgressBar) view.findViewById(R.id.pb_load);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_header);
        Tools.initProgressBar(pb);
        initQRToolbar();
        initToolbar();
        gvData.setNumColumns(NUMB_OF_COLUMNS);
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.icon_back_navigation);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBaseActivityListener != null) {
                    callBaseActivityListener.switchFragment(BooksListFragment.TAG,
                            BooksListFragment.getInstance(callBaseActivityListener));
                }
            }
        });
    }

    private void initQRToolbar() {
        int headerHeight = activity.getResources().getDimensionPixelSize(R.dimen.header_height);
        int headerHeight2 = getActivity().getResources().getDimensionPixelSize(R.dimen.header_height2);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.chose_chapter);

        headerView = new View(activity);
        AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight2);
        headerView.setLayoutParams(headerParams);
        gvData.addHeaderView(headerView);

        gvData.setOnScrollListener(new QuickReturnListViewOnScrollListener
                .Builder(QuickReturnViewType.HEADER)
                .header(toolbar)
                .minHeaderTranslation(-headerHeight)
                .build());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListeners();
    }

    private void initData() {
        if (adapter == null) {
            adapter = new BibleLinkAdapter(getActivity(), new ArrayList<BibleLink>());
        }

        adapter.clear();
        ContentTools.getListChapters(getActivity(), TAG, getListBooksAdapter);

        if (gvData != null && gvData.getAdapter() == null) {
            gvData.setAdapter(adapter);
        }
    }

    private void initListeners() {
        gvData.setOnItemClickListener(clickOnChapterItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        GBApplication.getInstance().setHomeBibleLevel(App.BookHomeLevels.CHAPTER);
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

    private AdapterView.OnItemClickListener clickOnChapterItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(callBaseActivityListener != null) {
                BibleLink chapter = adapter.getItem(position - gvData.getNumColumns());
                GBApplication.setChapterId(chapter.id);
                GBApplication.setCountChapters(adapter.getCount());
                callBaseActivityListener.switchFragment(PagerPoemFragment.TAG,
                        PagerPoemFragment.getInstance(callBaseActivityListener));
            }
            else
                Log.e(TAG, "LISTENER CALL BASE ACTIVITY ## NULL");
        }
    };
}
