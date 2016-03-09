package ua.maker.gbible_v2;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.inject.Inject;

import java.util.ArrayList;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import ua.maker.gbible_v2.Adapters.ComparePoemAdapter;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.BibleDB;
import ua.maker.gbible_v2.Managers.ContentManager;
import ua.maker.gbible_v2.Managers.PreferenceManager;
import ua.maker.gbible_v2.Models.Poem;

@ContentView(R.layout.activity_compare_poem)
public class ComparePoemActivity extends RoboActionBarActivity {

    public static final String TAG = "ComparePoemActivity";

    @InjectView(R.id.lv_data) ListView lvData;

    @Inject PreferenceManager preferenceManager;
    @Inject BibleDB bibleDB;
    @Inject ContentManager contentManager;

    private ComparePoemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_header));

        bibleDB.startupDB();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

        ArrayList<Poem> poemsCompare = (ArrayList<Poem>)getIntent().getExtras().getSerializable(App.Extras.DATA);
        if (poemsCompare != null) {
            initData(poemsCompare);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(
                            getResources().getColor(R.color.action_bar)));
            initToolbar();
        }
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(
                    getResources().getDrawable(
                            R.drawable.ic_arrow_back_white_24dp));
        }
    }

    private void initData(final ArrayList<Poem> poems) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(contentManager.parseListPoems(poems));
        }
        if (adapter == null || adapter.getCount() == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<Poem> list = new ArrayList<>();
                    for (Poem p : poems) {
                        list.addAll(bibleDB
                                .getPoemCompare
                                        (p.bookId, p.chapter, p.poem));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new ComparePoemAdapter(
                                    ComparePoemActivity.this,
                                    contentManager.sortByTranslate(list),
                                    preferenceManager);
                            lvData.setAdapter(adapter);
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
