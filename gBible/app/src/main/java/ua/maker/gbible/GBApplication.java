package ua.maker.gbible;

import android.app.Application;
import android.content.SharedPreferences;

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
                      deviceType;

    private SharedPreferences pref;

    @Override
    public void onCreate() {
        super.onCreate();
        deviceType = getResources().getInteger(R.integer.device_type);
        pref = getSharedPreferences(App.Pref.NAME, 0);
        bookId = pref.getInt(App.Pref.BOOK_ID, 0);
        chapterId = pref.getInt(App.Pref.CHAPTER_ID, 0);
        poem = pref.getInt(App.Pref.POEM, 0);
    }
}
