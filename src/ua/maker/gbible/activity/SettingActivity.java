package ua.maker.gbible.activity;

import ua.maker.gbible.R;
import ua.maker.gbible.fragment.SettingFragment;
import ua.maker.gbible.widget.setting.ColorPickerPreference;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;

public class SettingActivity extends ActionBarActivity {
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(savedInstanceState == null){
			getSupportFragmentManager().beginTransaction()
				.add(R.id.fl_setting, SettingFragment.getInstance(), SettingFragment.TAG).commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    protected void onStart() {
    	super.onStart();
    	EasyTracker.getInstance().activityStart(this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	EasyTracker.getInstance().activityStop(this);
    }
}