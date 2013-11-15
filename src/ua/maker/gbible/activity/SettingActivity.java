package ua.maker.gbible.activity;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import ua.maker.gbible.R;
import ua.maker.gbible.widget.setting.ColorPickerPreference;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
public class SettingActivity extends SherlockPreferenceActivity {
	
	private static final String TAG = "Setting Fragment";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Start onCreate");
		super.onCreate(savedInstanceState);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_action_bar));
		addPreferencesFromResource(R.xml.preference_bible);
		
		((ColorPickerPreference)findPreference(getString(R.string.pref_background_poem))).setAlphaSliderEnabled(true);
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
}
