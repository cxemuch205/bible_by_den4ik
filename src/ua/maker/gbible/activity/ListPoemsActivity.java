package ua.maker.gbible.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.ListPoemsFragment;

public class ListPoemsActivity extends SinglePanelActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected Fragment onCreatePane() {
		// TODO Auto-generated method stub
		return new ListPoemsFragment();
	}

}
