package ua.maker.gbible.widget.setting;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.utils.Tools;
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

public class ChangeSpeedSmoothPreference extends Preference implements Preference.OnPreferenceClickListener{
	
	private SharedPreferences pref = null;
	
	private AlertDialog dialog = null;
	
	private TextView tvCurrentValueSpeed = null;
	private SeekBar sbSetValueSpeed = null;
	
	private int valueSpeed = App.DEFAULT_SCROOL_SPEED;
	
	public ChangeSpeedSmoothPreference(Context context) {
		super(context);
		init();
	}

	public ChangeSpeedSmoothPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ChangeSpeedSmoothPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		setOnPreferenceClickListener(this);
		pref = PreferenceManager.getDefaultSharedPreferences(getContext());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(getContext().getString(R.string.dialog_pref_title_speed_smooth_scroll));
		
		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
		View viewDialog = inflater.inflate(R.layout.dialog_pref_speed_scroll, null);
		tvCurrentValueSpeed = (TextView)viewDialog.findViewById(R.id.tv_current_value_speed_scroll);
		sbSetValueSpeed = (SeekBar)viewDialog.findViewById(R.id.seekBar_set_speed_scroll_list_poem);
		sbSetValueSpeed.setMax(App.MAX_SCROOL_SPEED);
		sbSetValueSpeed.setOnSeekBarChangeListener(seekBarChangeListener);
		if(pref.contains(getContext().getString(R.string.pref_smooth_duration))){
			valueSpeed = pref.getInt(getContext().getString(R.string.pref_smooth_duration), 2);
		}
		sbSetValueSpeed.setProgress(valueSpeed);
		tvCurrentValueSpeed.setText(""+sbSetValueSpeed.getProgress());
		
		builder.setView(viewDialog);
		builder.setPositiveButton(getContext().getString(R.string.dialog_save), clickOkListener);
		builder.setNegativeButton(getContext().getString(R.string.dialog_cancel), clickCancelListener);
		
		dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
	}
	
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(progress <= App.MIN_SCROOL_SPEED){
				valueSpeed = App.MIN_SCROOL_SPEED;
				sbSetValueSpeed.setProgress(valueSpeed);
			}
			else
				valueSpeed = progress;
			
			tvCurrentValueSpeed.setText(""+valueSpeed);
		}
	};
	
	private DialogInterface.OnClickListener clickOkListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			pref.edit().putInt(getContext().getString(R.string.pref_smooth_duration), valueSpeed).commit();
			dialog.cancel();
		}
	};
	
	private DialogInterface.OnClickListener clickCancelListener = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
		}
	};

	@Override
	public boolean onPreferenceClick(Preference preference) {
		boolean useVolBtn = pref.getBoolean(getContext().getString(R.string.pref_use_vol_up_down_btn), false);
		if(useVolBtn){
			dialog.show();
		}
		else
		{
			Tools.showToast(getContext(), getContext().getString(R.string.pref_message_select_use_btn_lov));
		}
		return false;
	}

	

}
