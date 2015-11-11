package ua.maker.gbible_v2.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;

import ua.maker.gbible_v2.Constants.App;

/**
 * Created by daniilpavenko on 10/2/15.
 */
public class PreferenceManager {

    private SharedPreferences prefs;

    @Inject
    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(App.Pref.NAME, 0);
    }

    public boolean isEnableSyncDbx() {
        return prefs.getBoolean(App.Pref.SYNC_WITH_DBX, false);
    }

    public void setEnableSyncDbx(boolean enable) {
        prefs.edit().putBoolean(
                App.Pref.SYNC_WITH_DBX, enable)
                .apply();
    }

    public float getTextPoemSize() {
        return prefs.getFloat(App.Pref.POEM_TEXT_SIZE, App.DEFAULT_TEXT_SIZE);
    }

    public void setTextPoemSize(float size) {
        prefs.edit().putFloat(App.Pref.POEM_TEXT_SIZE, size).apply();
    }
}
