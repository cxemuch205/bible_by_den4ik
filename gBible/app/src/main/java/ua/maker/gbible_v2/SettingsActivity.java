package ua.maker.gbible_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;


import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Helpers.DropBoxTools;
import ua.maker.gbible_v2.Helpers.PreferenceManager;


public class SettingsActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    private CheckBox cbEnableSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_header));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.action_bar)));
            initToolbar();
        }
        preferenceManager = new PreferenceManager(this);

        initSyncWithDropBox();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        }
    }

    private void initSyncWithDropBox() {
        LinearLayout llBlockSync = (LinearLayout) findViewById(R.id.ll_enable_sync);
        cbEnableSync = (CheckBox) llBlockSync.findViewById(R.id.cb_enable_sync);

        cbEnableSync.setChecked(preferenceManager.isEnableSyncDbx());

        llBlockSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbEnableSync.setChecked(!cbEnableSync.isChecked());
                if (!DropBoxTools.getDbxAccountManager().hasLinkedAccount()) {
                    DropBoxTools.startLink(SettingsActivity.this);
                }
                DropBoxTools.toggleSyncWithDropBox(cbEnableSync.isChecked());
                executeLinkWithDropBox();
            }
        });
    }

    private void executeLinkWithDropBox() {
        preferenceManager.setEnableSyncDbx(cbEnableSync.isChecked());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean isConnect = DropBoxTools.onActivityResult(
                this.findViewById(android.R.id.content),
                requestCode,
                resultCode,
                data);
        if (isConnect) {
            executeLinkWithDropBox();
            DropBoxTools.getInstance().sync();
        } else {
            cbEnableSync.setChecked(!cbEnableSync.isChecked());
            executeLinkWithDropBox();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
