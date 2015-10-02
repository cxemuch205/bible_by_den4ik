package ua.maker.gbible_v2;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.BibleDB;
import ua.maker.gbible_v2.Helpers.DropBoxTools;

/**
 * Created by daniil on 11/7/14.
 */
public class GBApplication extends Application {

    public static GBApplication instance = null;
    public static BibleDB bibleDB;

    public static GBApplication getInstance() {
        if(instance == null){
            instance = new GBApplication();
        }
        return instance;
    }

    public static BibleDB getBibleDB() {
        if (bibleDB == null) {
            bibleDB = new BibleDB(getInstance());
            bibleDB.startupDB();
        }
        return bibleDB;
    }

    public static int bookId = 0,
            chapterId = 0,
            poem = 0,
            homeBibleLevel = App.BookHomeLevels.BOOK,
            deviceType,
            topBookId = 1,
            countChapters = 1;

    public static String bookName = "";

    private SharedPreferences pref;
    private SharedPreferences.Editor editorPref;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        new DropBoxTools(instance);
        deviceType = getResources().getInteger(R.integer.device_type);
        pref = getSharedPreferences(App.Pref.NAME, 0);
        editorPref = pref.edit();
        homeBibleLevel = pref.getInt(App.Pref.HOME_BIBLE_LEVEL, 0);
        bookId = pref.getInt(App.Pref.BOOK_ID, 0);
        chapterId = pref.getInt(App.Pref.CHAPTER_ID, 0);
        poem = pref.getInt(App.Pref.POEM, 0);
        topBookId = pref.getInt(App.Pref.TOP_BOOK_ID, 1);
        countChapters = pref.getInt(App.Pref.COUNT_CHAPTERS, 1);
        bookName = pref.getString(App.Pref.BOOK_NAME, "");
    }

    public void setHomeBibleLevel(final int level) {
        homeBibleLevel = level;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                editorPref.putInt(App.Pref.HOME_BIBLE_LEVEL, level).apply();
            }
        });
    }

    public void setTopBookId(final int topBook) {
        topBookId = topBook;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                editorPref.putInt(App.Pref.TOP_BOOK_ID, topBookId).apply();
            }
        });
    }

    public static void setBookId(final int bookIdd) {
        bookId = bookIdd;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                instance.editorPref.putInt(App.Pref.BOOK_ID, bookIdd).apply();
            }
        });
    }

    public static void setChapterId(final int chapterIdd) {
        chapterId = chapterIdd;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                instance.editorPref.putInt(App.Pref.CHAPTER_ID, chapterIdd).apply();
            }
        });
    }

    public static void setCountChapters(final int countT) {
        countChapters = countT;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                instance.editorPref.putInt(App.Pref.COUNT_CHAPTERS, countT).apply();
            }
        });
    }

    public static void setCurrentBookName(final String bookNameT) {
        bookName = bookNameT;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                instance.editorPref.putString(App.Pref.BOOK_NAME, bookNameT).apply();
            }
        });
    }

    public static void setPoem(final int poemT) {
        poem = poemT;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                instance.editorPref.putInt(App.Pref.POEM, poemT).apply();
            }
        });
    }
}
