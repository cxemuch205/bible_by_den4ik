package ua.maker.gbible_v2.Fragments;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ua.maker.gbible_v2.Adapters.PoemAdapter;
import ua.maker.gbible_v2.Adapters.SearchAdapter;
import ua.maker.gbible_v2.BaseActivity;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.Helpers.ContentTools;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Interfaces.OnGetContentAdapter;
import ua.maker.gbible_v2.Interfaces.OnGetContentListener;
import ua.maker.gbible_v2.Models.Poem;
import ua.maker.gbible_v2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private static SearchFragment instance;

    public SearchFragment() {
    }

    public static SearchFragment getInstance() {
        if (instance == null) {
            instance = new SearchFragment();
        }
        return instance;
    }

    private AppCompatActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    private Toolbar toolbar;
    private ListView lvData;
    private ProgressBar pb;
    private SearchAdapter adapter;
    private EditText etData;
    private Button btnSearch;
    private TextView tvMessage;
    private Spinner sBookStart, sBookEnd;

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
        initSearchTools();
        initListeners();
        initBooksSelection();
    }

    private void initToolbar(Toolbar toolbar) {
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.title_search);
    }

    private void initListData() {
        if (adapter == null) {
            adapter = new SearchAdapter(getActivity(), new ArrayList<Poem>());
        }
        if (lvData.getAdapter() == null) {
            lvData.setAdapter(adapter);
        }
    }

    private void initListeners() {
        lvData.setOnItemClickListener(itemClickListener);
        etData.setOnKeyListener(keyEtDataListener);
        btnSearch.setOnClickListener(clickExecuteSearchListener);
    }

    private void initSearchTools() {
        if (lvData.getHeaderViewsCount() == 0) {
            View searchTools = activity.getLayoutInflater().inflate(R.layout.block_search, null);
            etData = (EditText) searchTools.findViewById(R.id.et_data);
            btnSearch = (Button) searchTools.findViewById(R.id.btn_search);
            sBookStart = (Spinner) searchTools.findViewById(R.id.s_start_book);
            sBookEnd = (Spinner) searchTools.findViewById(R.id.s_end_book);

            lvData.addHeaderView(searchTools);
        }
    }

    private void initBooksSelection() {
        sBookStart.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.array_books)));
        sBookEnd.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.array_books)));
        sBookEnd.setSelection(sBookEnd.getAdapter().getCount() - 1);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Poem poem = adapter.getItem(position - 1);
            GBApplication.chapterId = poem.chapter;
            ((BaseActivity) activity).openReadContent(poem.bookId, poem.chapter - 1, poem.poem + 1);
        }
    };

    private View.OnKeyListener keyEtDataListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP) {
                btnSearch.performClick();
            }
            return false;
        }
    };

    private View.OnClickListener clickExecuteSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentTools.executeSearch(activity,
                    etData.getText().toString(),
                    TAG,
                    sBookStart.getSelectedItemPosition() + 1,
                    sBookEnd.getSelectedItemPosition() + 1,
                    contentAdapterProgress);
            Tools.hideKeyboard(activity);
        }
    };

    private OnGetContentListener contentAdapterProgress = new OnGetContentAdapter() {
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
                ArrayList<Poem> data = (ArrayList<Poem>) result;
                adapter.clear();
                adapter.addAll(data);
            }
        }
    };

    private void toggleEmptyMessage(final boolean enable) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enable) {
                    tvMessage.setVisibility(TextView.VISIBLE);
                    tvMessage.setText(R.string.empty_search);
                } else {
                    tvMessage.setVisibility(TextView.GONE);
                }
            }
        });
    }
}