package ua.maker.gbible_v2;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;


import com.google.inject.Inject;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import ua.maker.gbible_v2.Helpers.DropBoxTools;
import ua.maker.gbible_v2.Managers.PreferenceManager;

@ContentView(R.layout.activity_settings)
public class SettingsActivity extends RoboActionBarActivity {

    @Inject PreferenceManager preferenceManager;
    @Inject DropBoxTools dropBoxTools;

    private CheckBox cbEnableSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                if (!dropBoxTools.getDbxAccountManager().hasLinkedAccount()) {
                    dropBoxTools.startLink(SettingsActivity.this);
                }
                dropBoxTools.toggleSyncWithDropBox(cbEnableSync.isChecked());
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
        boolean isConnect = dropBoxTools.onActivityResult(
                this.findViewById(android.R.id.content),
                requestCode,
                resultCode,
                data);
        if (isConnect) {
            executeLinkWithDropBox();
            dropBoxTools.sync();
        } else {
            cbEnableSync.setChecked(!cbEnableSync.isChecked());
            executeLinkWithDropBox();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
