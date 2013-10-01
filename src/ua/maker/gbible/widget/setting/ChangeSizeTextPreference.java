package ua.maker.gbible.widget.setting;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.App;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ChangeSizeTextPreference extends Preference implements Preference.OnPreferenceClickListener{
	
	private SharedPreferences sp = null;
	
	private AlertDialog dialog = null;
	private View view = null;
	private TextView tvCurrentTextSize = null;
	private TextView tvSampleTextSize = null;
	private SeekBar sbSetSize = null;
	
	private int sizeText = App.DEFAULT_SIZE_TEXT;
	
	public ChangeSizeTextPreference(Context context) {
		super(context);
		init();
	}

	public ChangeSizeTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ChangeSizeTextPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		setOnPreferenceClickListener(this);
		sp = PreferenceManager.getDefaultSharedPreferences(getContext());
		if(sp.contains(getContext().getString(R.string.pref_size_text_poem)))
			sizeText = sp.getInt(getContext().getString(R.string.pref_size_text_poem), App.DEFAULT_SIZE_TEXT);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(getContext().getString(R.string.title_size_text_poem));
		
		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
		view = inflater.inflate(R.layout.dialog_pref_text_size, null);
		tvCurrentTextSize = (TextView)view.findViewById(R.id.tv_text_size_dialog);
		tvSampleTextSize = (TextView)view.findViewById(R.id.tv_sample_size);
		sbSetSize = (SeekBar)view.findViewById(R.id.seekBar_text_size);
		
		
		sbSetSize.setMax(App.MAX_SIZE_TEXT);
		
		sbSetSize.setProgress(sizeText);
		tvCurrentTextSize.setText(""+sizeText);
		tvSampleTextSize.setTextSize(sizeText);
		
		sbSetSize.setOnSeekBarChangeListener(seekBarChangeProgressListener);
		
		builder.setView(view);
		builder.setPositiveButton(getContext().getString(R.string.dialog_yes), dialogOkClickListener);
		builder.setNegativeButton(getContext().getString(R.string.dialog_cancel), dialogCancelClickListener);
		dialog = builder.create();
	}
	
	private OnSeekBarChangeListener seekBarChangeProgressListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if(progress <= App.MIN_SIZE_TEXT){
				sizeText = App.MIN_SIZE_TEXT;
				sbSetSize.setProgress(sizeText);
			}
			else
				sizeText = progress;
			
			tvSampleTextSize.setTextSize(sizeText);
			tvCurrentTextSize.setText(""+sizeText);
		}
	};
	
	private OnClickListener dialogOkClickListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			sp.edit().putInt(getContext().getString(R.string.pref_size_text_poem), sizeText).commit();
			
			dialog.cancel();
		}
	};
	
	private OnClickListener dialogCancelClickListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
		}
	};

	@Override
	public boolean onPreferenceClick(Preference preference) {
		dialog.show();
		return false;
	}

}
