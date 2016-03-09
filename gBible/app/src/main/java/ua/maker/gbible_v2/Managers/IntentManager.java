package ua.maker.gbible_v2.Managers;

import android.app.Activity;
import android.content.Intent;

import com.google.inject.Inject;

import java.util.ArrayList;

import ua.maker.gbible_v2.BaseActivity;
import ua.maker.gbible_v2.ComparePoemActivity;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Constants.IntentData;
import ua.maker.gbible_v2.Models.Poem;
import ua.maker.gbible_v2.ReadOnEveryDayActivity;
import ua.maker.gbible_v2.SettingsActivity;

/**
 * Created by daniilpavenko on 3/9/16.
 */
public class IntentManager {

    private Activity activity;

    @Inject
    public IntentManager(Activity activity) {
        this.activity = activity;
    }

    public void startBaseActivity() {
        Intent data = new Intent(activity, BaseActivity.class);
        activity.startActivity(data);
        activity.finish();
    }

    public void startComparePoemActivity(ArrayList<Poem> poems) {
        Intent compare = new Intent(activity, ComparePoemActivity.class);
        compare.putExtra(App.Extras.DATA, poems);
        activity.startActivity(compare);
    }

    public void startReadForEveryDayActivity() {
        Intent openRfED = new Intent(activity, ReadOnEveryDayActivity.class);
        activity.startActivityForResult(openRfED, IntentData.REQUEST_RED_LINK);
    }

    public void startSettingsActivity() {
        Intent openSettings = new Intent(activity, SettingsActivity.class);
        activity.startActivity(openSettings);
    }
}
