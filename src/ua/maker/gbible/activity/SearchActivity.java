package ua.maker.gbible.activity;

import android.support.v4.app.Fragment;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.SearchFragment;

public class SearchActivity extends SinglePanelActivity {

	@Override
	protected Fragment onCreatePane() {
		return new SearchFragment();
	}

}
