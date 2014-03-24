package ua.maker.gbible.widget.setting;

import ua.maker.gbible.activity.InstructionUpdateActivity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class InstructionUpdatesPreferences extends Preference implements Preference.OnPreferenceClickListener{

	public InstructionUpdatesPreferences(Context context) {
		super(context);
		init();
	}
	
	public InstructionUpdatesPreferences(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public InstructionUpdatesPreferences(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		setOnPreferenceClickListener(this);		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		Intent startActivity = new Intent(getContext(), InstructionUpdateActivity.class);
		getContext().startActivity(startActivity);
		return false;
	}
}
