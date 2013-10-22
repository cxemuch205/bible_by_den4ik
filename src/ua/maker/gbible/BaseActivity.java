package ua.maker.gbible;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BaseActivity extends SherlockFragmentActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getSupportActionBar() != null) {
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}
}
