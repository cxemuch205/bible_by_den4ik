package ua.maker.gbible.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.SelectBookFragment;

public class StartActivity extends SinglePanelActivity {
	
	private static final String TAG = "StartActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
	}

	@Override
	protected Fragment onCreatePane() {
		return SelectBookFragment.getInstance();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart()");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "onStop()");
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState()");
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.i(TAG, "onRestoreInstanceState()");
	}
}
