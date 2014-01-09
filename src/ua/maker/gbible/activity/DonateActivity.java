package ua.maker.gbible.activity;

import android.support.v4.app.Fragment;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.DonateFragment;

public class DonateActivity extends SinglePanelActivity {

	@Override
	protected Fragment onCreatePane() {
		return new DonateFragment();
	}
}
