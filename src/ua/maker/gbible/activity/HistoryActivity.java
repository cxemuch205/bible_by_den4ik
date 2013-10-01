package ua.maker.gbible.activity;

import android.support.v4.app.Fragment;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.HistoryFragment;

public class HistoryActivity extends SinglePanelActivity {

	@Override
	protected Fragment onCreatePane() {
		// TODO Auto-generated method stub
		return new HistoryFragment();
	}

}
