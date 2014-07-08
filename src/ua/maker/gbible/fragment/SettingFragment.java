package ua.maker.gbible.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import ua.maker.gbible.R;
import ua.maker.gbible.utils.PreferenceFragment;
import ua.maker.gbible.widget.setting.ColorPickerPreference;

@SuppressLint("ValidFragment")
public class SettingFragment extends PreferenceFragment {
	
	public static final String TAG = "SettingFragment";
	
	private static SettingFragment instance;
	
	private SettingFragment(){};
	
	public static SettingFragment getInstance(){
		if(instance == null){
			instance = new SettingFragment();
		}
		return instance;
	}

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setRetainInstance(true);
		
		((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(
				getActivity().getResources().getDrawable(R.drawable.background_action_bar));
		addPreferencesFromResource(R.xml.preference_bible);

		((ColorPickerPreference) findPreference(getString(R.string.pref_background_poem)))
				.setAlphaSliderEnabled(true);
		((ColorPickerPreference) findPreference(getString(R.string.pref_backg_control_panel)))
				.setAlphaSliderEnabled(true);
	}
}
