package ua.maker.gbible_v2.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;

import java.util.ArrayList;

import ua.maker.gbible_v2.Adapters.PoemAdapter;
import ua.maker.gbible_v2.BaseActivity;
import ua.maker.gbible_v2.ComparePoemActivity;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.UserDB;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.Helpers.ContentTools;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Interfaces.OnCallBaseActivityListener;
import ua.maker.gbible_v2.Interfaces.OnGetContentAdapter;
import ua.maker.gbible_v2.Interfaces.OnGetContentListener;
import ua.maker.gbible_v2.Models.Poem;
import ua.maker.gbible_v2.R;

/**
 * Created by daniil on 11/7/14.
 */
public class PoemListFragment extends Fragment {

    public static final String TAG = "PoemListFragment";
    private static PoemListFragment instance;

    public static PoemListFragment getInstance(OnCallBaseActivityListener adapter) {
        if (instance == null)
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
    private AppCompatActivity activity;
    private ActionMode actionMode;
    private UserDB userDB;

    private View headerView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setRetainInstance(false);
        this.activity = (AppCompatActivity) activity;
        userDB = new UserDB(activity);
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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBaseActivityListener != null) {
                    callBaseActivityListener.switchFragment(ChapterListFragment.TAG,
                            ChapterListFragment.getInstance(callBaseActivityListener));
                }
                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }
            }
        });
        toolbar.inflateMenu(R.menu.menu_base);
        toolbar.setOnMenuItemClickListener(((BaseActivity) activity).getOptionMenuItemListener());
    }

    private void initQRToolbar() {
        int headerHeight = activity.getResources().getDimensionPixelSize(R.dimen.header_height);
        if (Build.VERSION.SDK_INT >= 16) {
            headerHeight = toolbar.getMinimumHeight() + 14;
        }

        toolbar.setTitleTextColor(Color.WHITE);

        headerView = new View(activity);
        AbsListView.LayoutParams headerParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
        headerView.setLayoutParams(headerParams);
        lvData.addHeaderView(headerView);
        lvData.addFooterView(headerView);
        lvData.setSmoothScrollbarEnabled(true);
        lvData.setDivider(new ColorDrawable(Color.TRANSPARENT));

        lvData.setOnScrollListener(new QuickReturnListViewOnScrollListener
                .Builder(QuickReturnViewType.HEADER)
                .header(toolbar)
                .minHeaderTranslation(-headerHeight)
                .build());
    }

    private void setTitleActionBar(int chapter) {
        toolbar.setTitle(GBApplication.bookName);
        toolbar.setSubtitle(activity.getString(R.string.chapter) + " "
                + chapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        initListData(getActivity());
        setTitleActionBar(chapter);
    }

    public void initListData(final Activity activity) {
        if (adapter == null) {
            adapter = new PoemAdapter(activity, new ArrayList<Poem>());
        }

        adapter.clear();
        ContentTools.getListPoemsFromChapter(chapter, activity, TAG, getContentChapterListener);

        if (lvData != null && lvData.getAdapter() == null) {
            lvData.setAdapter(adapter);
        }
    }

    private void initListeners() {
        //lvData.setOnScrollListener(scrollDataListener);
        lvData.setOnItemLongClickListener(itemPoemLongClickListener);
        lvData.setOnItemClickListener(itemPoemClickListener);
    }

    private AdapterView.OnItemLongClickListener itemPoemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            startMultiChoseAction(position);
            return false;
        }
    };

    private void startMultiChoseAction(int position) {
        adapter.toggleSelection(position);
        boolean hasCheckedItems = adapter.getSelectedCount() > 0;

        if (hasCheckedItems && actionMode == null) {
            actionMode = toolbar.startActionMode(new ActionModeSelectItemCallback());
        } else if (!hasCheckedItems && actionMode != null) {
            actionMode.finish();
        }

        if (actionMode != null) {
            actionMode.setTitle(String.format(activity.getString(R.string.chosen_str),
                    adapter.getSelectedCount()));
        }
    }

    private AdapterView.OnItemClickListener itemPoemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (actionMode != null) {
                startMultiChoseAction(position);
            }
        }
    };

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

                if ((GBApplication.chapterId + 1) == chapter) {
                    int positionScroll = GBApplication.poem - 1;
                    lvData.setSelection(positionScroll);
                    lvData.smoothScrollToPosition(positionScroll);
                }
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if ((GBApplication.chapterId + 1) == chapter)
            GBApplication.setPoem(adapter.getItem(lvData.getFirstVisiblePosition()).poem);
    }

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

    private class ActionModeSelectItemCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.multi_poem_select, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            ArrayList<Poem> selectedPoems = adapter.getSelectedItems();

            switch (menuItem.getItemId()) {
                case R.id.action_add_to_bookmarks:
                    addToBookmarks(selectedPoems);
                    break;
                case R.id.action_add_to_plan:

                    break;
                case R.id.action_copy:
                    addToClipboard(selectedPoems);
                    break;
                case R.id.action_share:
                    shareSelectionPoem(selectedPoems);
                    break;
                case R.id.action_compare:
                    comparePoem(selectedPoems);
                    break;
                case R.id.action_highlighter:

                    break;
            }

            actionMode.finish();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            adapter.removeSelection();
            PoemListFragment.this.actionMode = null;
        }
    }

    private void addToBookmarks(ArrayList<Poem> poems) {
        userDB.insertBookMarks(ContentTools.convertPoemToBookmarkArray(activity, poems));
    }

    private void addToClipboard(ArrayList<Poem> poems) {
        Tools.copyToClipBoard(activity, ContentTools.convertForClipboard(activity, poems));
    }

    private void shareSelectionPoem(ArrayList<Poem> poems) {
        Tools.shareData(activity, ContentTools.convertForClipboard(activity, poems));
    }

    private void comparePoem(ArrayList<Poem> poems) {
        Intent compare = new Intent(activity, ComparePoemActivity.class);
        compare.putExtra(App.Extras.DATA, poems);
        activity.startActivity(compare);
    }
}