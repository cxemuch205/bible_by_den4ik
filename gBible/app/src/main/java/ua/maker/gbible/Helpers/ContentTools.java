package ua.maker.gbible.Helpers;

import android.app.Activity;

import java.util.ArrayList;

import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Interfaces.OnGetContentListener;
import ua.maker.gbible.Models.BibleLink;
import ua.maker.gbible.Models.Poem;

/**
 * Created by daniil on 11/7/14.
 */
public class ContentTools {

    public static void getListBooks(final Activity activity, final String tag, final OnGetContentListener adapterProgress) {
        adapterProgress.onStartGet();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<BibleLink> result = new ArrayList<BibleLink>();

                //######################## TESTS
                for (int i = 0; i < 20; i++) {
                    result.add(new BibleLink("Bitie " + (i+1), i+1));
                }
                //###############################

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterProgress.onEndGet(result, tag);
                    }
                });
            }
        }).start();
    }

    public static void getListChapters(final Activity activity, final String tag, final OnGetContentListener adapterProgress) {
        adapterProgress.onStartGet();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int bookId = GBApplication.bookId;
                final ArrayList<BibleLink> result = new ArrayList<BibleLink>();

                //######################## TESTS
                for (int i = 0; i < 20; i++) {
                    result.add(new BibleLink(String.valueOf(i + 1), i+1, bookId));
                }
                //###############################

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterProgress.onEndGet(result, tag);
                    }
                });
            }
        }).start();
    }

    public static void getListPoemsFromChapter(final Activity activity, final String tag, final OnGetContentListener adapterProgress) {
        adapterProgress.onStartGet();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int chapterId = GBApplication.chapterId;
                final ArrayList<Poem> result = new ArrayList<Poem>();

                //######################## TESTS
                for (int i = 0; i < 20; i++) {
                    result.add(new Poem((i+1), chapterId, (i+1), "Content " + i));
                }
                //###############################

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterProgress.onEndGet(result, tag);
                    }
                });
            }
        }).start();
    }
}
