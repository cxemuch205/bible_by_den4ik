package ua.maker.gbible.activity;

import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.ListChaptersFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ListChaptersActivity extends SinglePanelActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected Fragment onCreatePane() {
		// TODO Auto-generated method stub
		return new ListChaptersFragment();
	}
}
