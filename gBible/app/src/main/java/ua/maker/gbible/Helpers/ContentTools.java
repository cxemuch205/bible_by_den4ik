package ua.maker.gbible.Helpers;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.maker.gbible.DataBases.BibleDB;
import ua.maker.gbible.GBApplication;
import ua.maker.gbible.Interfaces.OnGetContentListener;
import ua.maker.gbible.Models.BibleLink;
import ua.maker.gbible.Models.Book;
import ua.maker.gbible.Models.Poem;
import ua.maker.gbible.R;

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
//                for (int i = 0; i < 20; i++) {
//                    result.add(new BibleLink("Bitie " + (i+1), i+1));
//                }
                //###############################

                BibleDB bibleDB = new BibleDB(activity);
                bibleDB.startupDB();

                ArrayList<Book> books = bibleDB.getBooks();
                String chaptersTitle = activity.getString(R.string.chapters);
                for (Book book : books) {
                    BibleLink link = new BibleLink();

                    link.bookId = book.id;
                    link.name = book.name;
                    link.info = chaptersTitle + ": " + String.valueOf(book.chaptersCount);

                    result.add(link);
                }

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
//                for (int i = 0; i < 20; i++) {
//                    result.add(new BibleLink(String.valueOf(i + 1), i, bookId));
//                }
                //###############################

                BibleDB bibleDB = new BibleDB(activity);
                bibleDB.startupDB();

                int countChapter = bibleDB.getNumberOfChapterInBook(bookId, BibleDB.TABLE_NAME_RST);
                for (int i = 0; i < countChapter; i++) {
                    result.add(new BibleLink(String.valueOf(i + 1), i, bookId));
                }

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
                int bookId = GBApplication.bookId;
                int chapterId = GBApplication.chapterId;
                final ArrayList<Poem> result = new ArrayList<Poem>();

                //######################## TESTS
//                Random random = new Random();
//                for (int i = 0; i < 75; i++) {
//                    result.add(new Poem((i + 1), chapterId, (i + 1), "Content " + random.nextInt()));
//                }
                //###############################

                BibleDB bibleDB = new BibleDB(activity);
                bibleDB.startupDB();

                int chapter = chapterId + 1;

                result.addAll(bibleDB.getPoemsInChapter(bookId, chapter, Tools.getTranslateWitchPreferences(activity)));

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterProgress.onEndGet(result, tag);
                    }
                });
            }
        }).start();
    }

    public static ArrayList<Poem> parseValueChapterBookIdString(Context context, String contentChapter, String bookName, String bookId) {
        ArrayList<Poem> result = new ArrayList<Poem>();
        int countBook = 0;
        ArrayList<String> bookNames = new ArrayList<String>();
        ArrayList<Integer> bookIds = new ArrayList<Integer>();

        int t = 0;
        for (int i = 0; i <= bookId.length(); i++) {
            if ((i == bookId.length()
                    && (i >= 3 && (bookId.substring((i - 2), (i - 1)).equals("|")
                    || bookId.substring((i - 3), (i - 2)).equals("|"))))
                    || (i != bookId.length() && bookId.substring(i, (i + 1)).equals("|"))) {
                int bI = Integer.parseInt(bookId.substring(t, i));
                bookIds.add(bI);
                String name = Tools.getBookNameByBookId(bI, context);
                bookNames.add(name);
                t = i + 1;
                countBook++;
            }
        }
        if (countBook == 0) {
            int r = 0;
            for (int i = 0; i <= contentChapter.length(); i++) {
                if (i == (contentChapter.length())
                        || contentChapter.substring(i, (i + 1)).equals(":")
                        || contentChapter.substring(i, (i + 1)).equals(",")) {
                    Poem item = new Poem();
                    String chapterStr = contentChapter.substring(r, i);
                    item.chapter = Integer.parseInt(chapterStr);
                    item.bookName = bookName;
                    item.bookId = Integer.parseInt(bookId);
                    boolean isNoStandart = false;
                    if (i != contentChapter.length() && contentChapter.substring(i, (i + 1)).equals(":")) {
                        for (int j = 0; j < contentChapter.length(); j++) {
                            if (contentChapter.substring(j, (j + 1)).equals("-")) {
                                item.poem = Integer.parseInt(contentChapter.substring((i + 1), j));
                                item.poemTo = Integer.parseInt(contentChapter.substring((j + 1), contentChapter.length()));
                            }
                        }
                        isNoStandart = true;
                    }
                    result.add(item);
                    r = i + 1;
                    if (isNoStandart)
                        break;
                }
            }
        } else
            for (int k = 0; k < countBook; k++) {
                int r = 0;
                for (int i = 0; i <= contentChapter.length(); i++) {
                    if (i == (contentChapter.length())
                            || contentChapter.substring(i, (i + 1)).equals(":")
                            || contentChapter.substring(i, (i + 1)).equals("|")
                            || contentChapter.substring(i, (i + 1)).equals(",")) {
                        Poem item = new Poem();
                        item.chapter = Integer.parseInt(contentChapter.substring(r, i));
                        item.bookName = bookNames.get(k);
                        item.bookId = bookIds.get(k);
                        boolean isNoStandart = false;
                        if (i != contentChapter.length() && contentChapter.substring(i, (i + 1)).equals(":")) {
                            for (int j = 0; j < contentChapter.length(); j++) {
                                if (contentChapter.substring(j, (j + 1)).equals("-")) {
                                    item.poem = Integer.parseInt(contentChapter.substring((i + 1), j));
                                    item.poemTo = Integer.parseInt(contentChapter.substring((j + 1), contentChapter.length()));
                                }
                            }
                            isNoStandart = true;
                        }
                        result.add(item);
                        r = i + 1;
                        if (isNoStandart)
                            break;
                    }
                    if (i != (contentChapter.length()) && contentChapter.substring(i, (i + 1)).equals("|")) {
                        contentChapter = contentChapter.substring((i + 1), contentChapter.length());
                        break;
                    }
                }
            }

        return result;
    }
}
