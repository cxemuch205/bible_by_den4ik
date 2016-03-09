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

    public int getBibleLevel() {
        return prefs.getInt(App.Pref.HOME_BIBLE_LEVEL, 0);
    }

    public int getLastBookId() {
        return prefs.getInt(App.Pref.BOOK_ID, 0);
    }

    public int getLastChapterId() {
        return prefs.getInt(App.Pref.CHAPTER_ID, 0);
    }

    public int getLastPoem() {
        return prefs.getInt(App.Pref.POEM, 0);
    }

    public int getLastTopBookId() {
        return prefs.getInt(App.Pref.TOP_BOOK_ID, 1);
    }

    public int getLastCountChapters() {
        return prefs.getInt(App.Pref.COUNT_CHAPTERS, 1);
    }

    public String getLastBookName() {
        return prefs.getString(App.Pref.BOOK_NAME, "");
    }

    public void setBibleLevel(int level) {
        prefs.edit().putInt(App.Pref.HOME_BIBLE_LEVEL, level).apply();
    }

    public void setTopBookId(int topBookId) {
        prefs.edit().putInt(App.Pref.TOP_BOOK_ID, topBookId).apply();
    }

    public void setLastBookId(int bookIdd) {
        prefs.edit().putInt(App.Pref.BOOK_ID, bookIdd).apply();
    }

    public void setLastChapterId(int chapterIdd) {
        prefs.edit().putInt(App.Pref.CHAPTER_ID, chapterIdd).apply();
    }

    public void setLastCountChapters(int countT) {
        prefs.edit().putInt(App.Pref.COUNT_CHAPTERS, countT).apply();
    }

    public void setLastBookName(String bookNameT) {
        prefs.edit().putString(App.Pref.BOOK_NAME, bookNameT).apply();
    }

    public void setLastPoem(int poemT) {
        prefs.edit().putInt(App.Pref.POEM, poemT).apply();
    }

    public int getLastReadPosition() {
        return prefs.getInt(App.Pref.LAST_RED_POSITION, 0);
    }

    public boolean isFirstOpenReadD() {
        return prefs.getBoolean(App.Pref.FIRST_OPEN_READED, false);
    }

    public void setIsFirstOpenReadD(boolean isFirstOpen) {
        prefs.edit().putBoolean(App.Pref.FIRST_OPEN_READED, isFirstOpen).apply();
    }

    public void setLastReadPosition(int position) {
        prefs.edit().putInt(App.Pref.LAST_RED_POSITION, position).apply();
    }

    public void removeLastReadPosition() {
        prefs.edit().remove(App.Pref.LAST_RED_POSITION).apply();
    }
}
