package ua.maker.gbible.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import ua.maker.gbible.R;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.SelectBookFragment;

public class StartActivity extends SinglePanelActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
	}

	@Override
	protected Fragment onCreatePane() {
		return new SelectBookFragment();
	}
}
