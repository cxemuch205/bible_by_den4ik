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
import ua.maker.gbible_v2.Views.HeaderGridView;

/**
 * Created by daniil on 11/6/14.
 */
public class ChapterListFragment extends RoboFragment {

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

    @InjectView(R.id.gv_data) HeaderGridView gvData;
    @InjectView(R.id.pb_load) ProgressBar pb;
    @InjectView(R.id.toolbar_header) Toolbar toolbar;

    @Inject ContentManager contentManager;

    private BibleLinkAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chapter, container, false);

        /*rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom)
            {
                v.removeOnLayoutChangeListener(this);
                int cx = activity.getResources().getDisplayMetrics().widthPixels / 2;
                int cy = activity.getResources().getDisplayMetrics().widthPixels / 2;

                // get the hypothenuse so the radius is from one corner to the other
                int radius = (int)Math.hypot(right, bottom);

                Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                reveal.setInterpolator(new DecelerateInterpolator(2f));
                reveal.setDuration(1000);
                reveal.start();
            }
        });*/

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Tools.initProgressBar(pb);
        initQRToolbar();
        initToolbar();
        gvData.setNumColumns(NUMB_OF_COLUMNS);
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBaseActivityListener != null) {
                    callBaseActivityListener.switchFragment(BooksListFragment.TAG,
                            BooksListFragment.getInstance(callBaseActivityListener));
                }
            }
        });
        toolbar.inflateMenu(R.menu.menu_base);
        toolbar.setOnMenuItemClickListener(((BaseActivity) getActivity())
                .getOptionMenuItemListener());
    }

    private void initQRToolbar() {
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        if (Build.VERSION.SDK_INT >= 16) {
            headerHeight = toolbar.getMinimumHeight();
        }

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.chose_chapter);

        View headerView = new View(getContext());
        AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
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
        contentManager.getListChapters(TAG, getListBooksAdapter);

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
