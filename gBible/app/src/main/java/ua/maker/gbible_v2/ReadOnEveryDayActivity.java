package ua.maker.gbible_v2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import ua.maker.gbible_v2.Adapters.ReadEveryDayAdapter;
import ua.maker.gbible_v2.Adapters.SimpleDialogReadAdapter;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.BibleDB;
import ua.maker.gbible_v2.DataBases.UserDB;
import ua.maker.gbible_v2.Models.ItemReadDay;
import ua.maker.gbible_v2.Models.Poem;

public class ReadOnEveryDayActivity extends AppCompatActivity {

    public static final String TAG = "ReadOnEveryDayActivity";

    private StickyListHeadersListView lvData;
    private ReadEveryDayAdapter adapter;
    private BibleDB bibleDB;
    private SharedPreferences pref;
    private AlertDialog dialogChosen;
    private SimpleDialogReadAdapter dialogReadAdapter;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat
            = new SimpleDateFormat("D");
    private int day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_on_every_day);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_header));
        pref = getSharedPreferences(App.Pref.NAME, Context.MODE_PRIVATE);
        setResult(RESULT_CANCELED);

        day = Integer.parseInt(simpleDateFormat.format(new Date())) - 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

        lvData = (StickyListHeadersListView) this.findViewById(R.id.lv_data);

        initData();
        initListeners();
        initDialogChosen();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(
                            getResources().getColor(R.color.action_bar)));
            initToolbar();
        }
    }

    private void initDialogChosen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_chose_link);
        builder.setNegativeButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogReadAdapter = new SimpleDialogReadAdapter(this, new ArrayList<Poem>());
        builder.setAdapter(dialogReadAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openLink(which);
                dialog.cancel();
            }
        });
        dialogChosen = builder.create();
    }

    private void openLink(int position) {
        Poem poem = dialogReadAdapter.getItem(position);
        Intent data = new Intent();
        data.putExtra(App.Extras.DATA, poem);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int lastPosition = pref.getInt(App.Pref.LAST_RED_POSITION, 0);
        if (lastPosition == 0) {
            toPosition(day);
        } else {
            toPosition(lastPosition);
        }
    }

    private void toPosition(int lastPosition) {
        lvData.setSelection(lastPosition);
        lvData.smoothScrollToPosition(lastPosition);
    }

    private void initData() {
        bibleDB = GBApplication.getBibleDB();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<ItemReadDay> data = bibleDB.getListReadForEveryDay();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ReadEveryDayAdapter(ReadOnEveryDayActivity.this, data, bibleDB);
                        lvData.setAdapter(adapter);
                        onResume();
                    }
                });
            }
        }).start();
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

    MenuItem actionItemSeReaded;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read_on_every_day, menu);
        actionItemSeReaded = menu.getItem(1);
        if (pref.contains(App.Pref.FIRST_OPEN_READED)) {
            actionItemSeReaded.setVisible(!pref.getBoolean(App.Pref.FIRST_OPEN_READED, false));
        }
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isReadedToToday()) {
                    actionItemSeReaded.setVisible(false);
                    pref.edit().putBoolean(App.Pref.FIRST_OPEN_READED, true).apply();
                }
            }
        }, 400);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isReadedToToday() {
        if (adapter != null)
            for (int i = 0; i < day; i++) {
                ItemReadDay item = adapter.getItem(i);
                if (!item.isStatusReaded()) {
                    return false;
                }
            }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_clear_rfe:
                dialogAcceptDrop();
                break;
            case R.id.action_set_readed_to_today:
                setReadedStatusToToday();
                pref.edit().putBoolean(App.Pref.FIRST_OPEN_READED, true).apply();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setReadedStatusToToday() {
        final ProgressDialog pd = ProgressDialog.show(this, null, getString(R.string.set_status_readed));

        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < day; i++) {
                    ItemReadDay itemm = adapter.getItem(i);
                    itemm.setStatus(true);
                    bibleDB.setStatusItemReadForEveryDay(i, itemm.getDbxId(), true);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        pd.cancel();
                        if (actionItemSeReaded != null && actionItemSeReaded.isVisible()) {
                            actionItemSeReaded.setVisible(false);
                        }
                    }
                });
            }
        }).start();
    }

    private void dialogAcceptDrop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_accept_drop);
        builder.setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dropList();
                pref.edit().remove(App.Pref.FIRST_OPEN_READED).apply();
                toPosition(0);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void dropList() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                bibleDB.setDefaultStatusItemRead();
            }
        });
        thread.start();
        for (int i = 0; i < adapter.getCount(); i++) {
            adapter.getItem(i).setStatus(false);
        }
        adapter.notifyDataSetChanged();
        pref.edit().remove(App.Pref.LAST_RED_POSITION).apply();
    }

    private void showDialogChosen(int position) {
        if (dialogReadAdapter != null) {
            final ItemReadDay itemReadDay = adapter.getItem(position);
            updateListLinks(itemReadDay);
        }
    }

    private void updateListLinks(ItemReadDay readDay) {
        dialogReadAdapter.clear();
        dialogReadAdapter.addAll(readDay.getListPoemOld());
        dialogReadAdapter.addAll(readDay.getListPoemNew());
        dialogChosen.show();
    }
}
