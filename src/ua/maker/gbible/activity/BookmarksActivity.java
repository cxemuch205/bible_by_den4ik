package ua.maker.gbible.activity;

import android.support.v4.app.Fragment;
import ua.maker.gbible.SinglePanelActivity;
import ua.maker.gbible.fragment.BookmarksFragment;

public class BookmarksActivity extends SinglePanelActivity {

	@Override
	protected Fragment onCreatePane() {
		return new BookmarksFragment();
	}

}
