package ua.maker.gbible.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.SelectBookFragment;

public class StartActivity extends SinglePanelActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	protected Fragment onCreatePane() {
		// TODO Auto-generated method stub
		return new SelectBookFragment();
	}

}
