package ua.maker.gbible_v2;

import android.app.Application;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.inject.Inject;

import io.fabric.sdk.android.Fabric;
import roboguice.RoboGuice;
import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Helpers.DropBoxTools;
import ua.maker.gbible_v2.Managers.PreferenceManager;

/**
 * Created by daniil on 11/7/14.
 */
public class GBApplication extends Application {

    public static GBApplication instance = null;

    @Inject PreferenceManager preferenceManager;

    public static GBApplication getInstance() {
        if (instance == null) {
            instance = new GBApplication();
        }
        return instance;
    }

    public static int bookId = 0,
            chapterId = 0,
            poem = 0,
            homeBibleLevel = App.BookHomeLevels.BOOK,
            deviceType,
            topBookId = 1,
            countChapters = 1;

    public static String bookName = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        instance = this;
        DropBoxTools.initDbxAccountManager(instance);
        RoboGuice.injectMembers(this, this);
        deviceType = getResources().getInteger(R.integer.device_type);
        homeBibleLevel = preferenceManager.getBibleLevel();
        bookId = preferenceManager.getLastBookId();
        chapterId = preferenceManager.getLastChapterId();
        poem = preferenceManager.getLastPoem();
        topBookId = preferenceManager.getLastTopBookId();
        countChapters = preferenceManager.getLastCountChapters();
        bookName = preferenceManager.getLastBookName();
    }

    public void setHomeBibleLevel(final int level) {
        homeBibleLevel = level;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                preferenceManager.setBibleLevel(level);
            }
        });
    }

    public void setTopBookId(final int topBook) {
        topBookId = topBook;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                preferenceManager.setTopBookId(topBookId);
            }
        });
    }

    public static void setBookId(final int bookIdd) {
        bookId = bookIdd;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getInstance().preferenceManager.setLastBookId(bookIdd);
            }
        });
    }

    public static void setChapterId(final int chapterIdd) {
        chapterId = chapterIdd;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getInstance().preferenceManager.setLastChapterId(chapterIdd);
            }
        });
    }

    public static void setCountChapters(final int countT) {
        countChapters = countT;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getInstance().preferenceManager.setLastCountChapters(countT);
            }
        });
    }

    public static void setCurrentBookName(final String bookNameT) {
        bookName = bookNameT;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getInstance().preferenceManager.setLastBookName(bookNameT);
            }
        });
    }

    public static void setPoem(final int poemT) {
        poem = poemT;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getInstance().preferenceManager.setLastPoem(poemT);
            }
        });
    }
}
