package ua.maker.gbible_v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ua.maker.gbible_v2.Adapters.ReadEveryDayAdapter;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.BibleDB;

public class ReadOnEveryDayActivity extends AppCompatActivity {

    public static final String TAG = "ReadOnEveryDayActivity";

    private ListView lvData;
    private ReadEveryDayAdapter adapter;
    private BibleDB bibleDB;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_on_every_day);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_header));
        pref = getSharedPreferences(App.Pref.NAME, Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

        lvData = (ListView) this.findViewById(R.id.lv_data);

        initData();
        initListeners();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(
                            getResources().getColor(R.color.action_bar)));
            initToolbar();
        }
        int lastPosition = pref.getInt(App.Pref.LAST_RED_POSITION, 0);
        lvData.smoothScrollToPosition(lastPosition);
    }

    private void initData() {
        bibleDB = new BibleDB(this);
        bibleDB.startupDB();

        adapter = new ReadEveryDayAdapter(this, bibleDB.getListReadForEveryDay(), bibleDB);
        lvData.setAdapter(adapter);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(
                    getResources().getDrawable(
                            R.drawable.ic_arrow_back_white_24dp));
        }
    }

    private void initListeners() {
        lvData.setOnItemClickListener(itemReadListener);
    }

    private AdapterView.OnItemClickListener itemReadListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showDialogChosen(position);
            pref.edit().putInt(App.Pref.LAST_RED_POSITION, position).apply();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read_on_every_day, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_clear_rfe:
                dropList();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dropList() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                bibleDB.setDefaultStatusItemRead(adapter.getCount());
            }
        });
        thread.start();
        for(int i = 0; i < adapter.getCount(); i++){
            adapter.getItem(i).setStatus(false);
        }
        adapter.notifyDataSetChanged();
        pref.edit().remove(App.Pref.LAST_RED_POSITION).apply();
    }

    private void showDialogChosen(int position) {
    }
}
