package ua.maker.gbible.widget.setting;

import ua.maker.gbible.activity.DonateActivity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class DonatePreferences extends Preference implements Preference.OnPreferenceClickListener{

	public DonatePreferences(Context context) {
		super(context);
		init();
	}
	
	public DonatePreferences(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public DonatePreferences(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		setOnPreferenceClickListener(this);		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		Intent startActivityDonate = new Intent(getContext(), DonateActivity.class);
		getContext().startActivity(startActivityDonate);
		return false;
	}
}
