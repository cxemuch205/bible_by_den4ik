package ua.maker.gbible.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.StartFragment;

public class StartActivity extends SinglePanelActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Fragment onCreatePane() {
		// TODO Auto-generated method stub
		return new StartFragment();
	}

}
