package ua.maker.gbible.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
public class ChapterListFragment extends BaseFragment {

    public static String TAG = ChapterListFragment.class.getSimpleName();
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

    private GridView gvData;
    private BibleLinkAdapter adapter;
    private ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter, null);
        gvData = (GridView) view.findViewById(R.id.gv_data);
        pb = (ProgressBar) view.findViewById(R.id.pb_load);
        Tools.initProgressBar(pb);
        gvData.setNumColumns(6);

        return view;
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
                BibleLink chapter = adapter.getItem(position);
                GBApplication.getInstance().setChapterId(chapter.id);
                GBApplication.getInstance().setCountChapters(adapter.getCount());
                callBaseActivityListener.switchFragment(PagerPoemFragment.TAG,
                        PagerPoemFragment.getInstance(callBaseActivityListener));
            }
            else
                Log.e(TAG, "LISTENER CALL BASE ACTIVITY ## NULL");
        }
    };
}
