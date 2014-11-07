package ua.maker.gbible;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;

import ua.maker.gbible.Constants.App;

/**
 * Created by daniil on 11/7/14.
 */
public class GBApplication extends Application {

    public static GBApplication instance = null;

    public static GBApplication getInstance() {
        if(instance == null){
            instance = new GBApplication();
        }
        return instance;
    }

    public static int bookId = 0,
                      chapterId = 0,
                      poem = 0,
                      homeBibleLevel = App.BookHomeLevels.BOOK,
                      deviceType,
                      topBookId = 1;

    private SharedPreferences pref;
    private SharedPreferences.Editor editorPref;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        deviceType = getResources().getInteger(R.integer.device_type);
        pref = getSharedPreferences(App.Pref.NAME, 0);
        editorPref = pref.edit();
        bookId = pref.getInt(App.Pref.BOOK_ID, 0);
        chapterId = pref.getInt(App.Pref.CHAPTER_ID, 0);
        poem = pref.getInt(App.Pref.POEM, 0);
        topBookId = pref.getInt(App.Pref.TOP_BOOK_ID, 1);
    }

    public void setHomeBibleLevel(final int level) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                homeBibleLevel = level;
                editorPref.putInt(App.Pref.HOME_BIBLE_LEVEL, level).commit();
            }
        });
    }

    public void setTopBookId(final int topBook) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                topBookId = topBook;
                editorPref.putInt(App.Pref.TOP_BOOK_ID, topBookId).commit();
            }
        });
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
        editorPref.putInt(App.Pref.BOOK_ID, bookId).commit();
    }

    public void setChapterId(int chapter) {
        this.chapterId = chapter;
        editorPref.putInt(App.Pref.CHAPTER_ID, chapter).commit();
    }
}
