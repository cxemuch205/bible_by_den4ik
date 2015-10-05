package ua.maker.gbible_v2.DataBases;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Helpers.ContentTools;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Models.Book;
import ua.maker.gbible_v2.Models.ItemReadDay;
import ua.maker.gbible_v2.Models.Poem;
import ua.maker.gbible_v2.R;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class BibleDB extends SQLiteOpenHelper {

    private static final String TAG = "DataBase";
    private static final int DB_VERSION = 1;
    @SuppressLint("SdCardPath")
    private static final String DB_PATH = "/data/data/ua.maker.gbible_v2/databases/";
    private static final String DB_NAME = "bible_android.db";

    public static final String TABLE_NAME_RST = "rus_st";
    public static final String TABLE_NAME_MT = "rus_mt";
    public static final String TABLE_NAME_UAT = "ukr_t";
    public static final String TABLE_NAME_ENT = "en_t";

    public static final int TRANSLATE_RST_ID = 0;
    public static final int TRANSLATE_MT_ID = 1;
    public static final int TRANSLATE_UA_ID = 2;
    public static final int TRANSLATE_EN_ID = 3;

    public static final String TABLE_SEARCH_RESULT = "search_history";
    public static final String TABLE_READ_FOR_EVERY_DAY = "read_for_every_day";

    public static final String FIELD_BOOK_ID = "bookId";
    public static final String FIELD_TRANSLATE = "translate";
    public static final String FIELD_BOOK_NAME = "bookName";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_CHAPTER = "chapter";
    public static final String FIELD_POEM = "poem";
    public static final String FIELD_CONTENT = "content";
    public static final String KEY_ROWID = "_id";
    public static final String FIELD_MONTH = "month";
    public static final String FIELD_DAY = "day";
    public static final String FIELD_RESULT = "result_select";
    public static final String FIELD_BOOK_NAME_OLD_T = "book_name_old_testament";
    public static final String FIELD_BOOK_NAME_NEW_T = "book_name_new_testament";
    public static final String FIELD_CHAPTERS_OLD_T = "chapters_old_testament";
    public static final String FIELD_CHAPTERS_NEW_T = "chapters_new_testament";
    public static final String FIELD_BOOK_ID_NEW_T = "book_id_new_testament";
    public static final String FIELD_BOOK_ID_OLD_T = "book_id_old_testament";
    public static final String FIELD_STATUS_READED = "status_readed";

    public static final String[] TABLE_NAMES = {"rus_st",
            "rus_mt"//,
                                                 /*"ukr_t",
												  *"en_t"*/};

    private SQLiteDatabase db = null;
    private UserDB dbUser;
    private final Context mContext;

    private SharedPreferences pref = null;

    public BibleDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        pref = context.getSharedPreferences(App.Pref.NAME, 0);
        dbUser = new UserDB(context);
    }

    private void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            int oldVer = pref.getInt(App.Pref.DATA_BASE_VER, 1);
            if (oldVer < DB_VERSION) {
                try {
                    SQLiteDatabase.releaseMemory();
                } catch (Exception e) {
                }
                this.getWritableDatabase();

                try {
                    copyDataBase();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }
        } else {

            this.getWritableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        if (pref != null) {
            pref.edit().putInt(App.Pref.DATA_BASE_VER, DB_VERSION).apply();
        }
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void startupDB() {
        try {
            createDataBase();
        } catch (IOException e) {}
        openDataBase();
    }

    @Override
    public synchronized void close() {
        if (db != null)
            db.close();
        super.close();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getCursor(String table, String[] columns, String selection,
                            String[] selectionArgs, String groupBy, String having, String orderBy) {
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public Cursor getBooksCursor() {
        return db.rawQuery("SELECT "
                + FIELD_BOOK_ID + " FROM '" + Tools.getTranslateWitchPreferences(mContext) + "'"
                + " WHERE "
                + FIELD_CHAPTER + " = '1' AND "
                + FIELD_POEM + " = '1'", null);
    }

    public Cursor getChaptersCursors(String bookId) {
        return db.query(Tools.getTranslateWitchPreferences(mContext),
                new String[]{FIELD_BOOK_ID, FIELD_POEM}, BibleDB.FIELD_BOOK_ID + " = " + bookId, null, null, null, null);
    }

    public Cursor getContentChapters(String bookId, String chapter) {
        return db.query(Tools.getTranslateWitchPreferences(mContext),
                new String[]{FIELD_BOOK_ID, FIELD_CHAPTER, FIELD_CONTENT},
                FIELD_BOOK_ID + " = " + bookId + " AND " + FIELD_CHAPTER + " = " + chapter, null, null, null, null);
    }

    public Cursor getReadEDCursor() {
        return db.rawQuery("SELECT * FROM '" + TABLE_READ_FOR_EVERY_DAY + "'", null);
    }

    private static ArrayList<Book> staticBookList;

    public ArrayList<Book> getBooks() {
        staticBookList = dbUser.getBooksFromJSON(staticBookList);
        if (staticBookList == null) {
            ArrayList<Book> books = new ArrayList<>();
            if (db.isOpen()) {
                Cursor c = db.rawQuery("SELECT " + FIELD_BOOK_ID
                        + " FROM '" + TABLE_NAME_RST + "'"
                        + " WHERE "
                        + FIELD_CHAPTER + " = '1' AND "
                        + FIELD_POEM + " = '1'", null);
                if (c.moveToFirst()) {
                    int indexBookId = c.getColumnIndex(FIELD_BOOK_ID);
                    do {
                        int bookId = c.getInt(indexBookId);
                        Book book = new Book();
                        book.id = bookId;
                        book.name = Tools.getBookNameByBookId(bookId, mContext);
                        book.chaptersCount = getNumberOfChapterInBook(bookId, TABLE_NAME_RST);
                        books.add(book);
                    } while (c.moveToNext());
                }
                c.close();
            }
            Log.d(TAG, "END getting books");
            dbUser.setBooksToJSON(books);
            return staticBookList = books;
        } else {
            return staticBookList;
        }
    }

    public List<Poem> getPoemsInChapter(int bookId, int chapter, String tableName) {
        List<Poem> poems = new ArrayList<>();
        Log.d(TAG, "Start getting poem in " + Tools.getBookNameByBookId(bookId, mContext) + "chapter " + chapter);
        if (db.isOpen()) {
            Log.d(TAG, "BaseOpen");
            Cursor c = db.rawQuery("SELECT "
                    + FIELD_CONTENT + ", "
                    + FIELD_POEM
                    + " FROM '" + tableName + "'"
                    + " WHERE "
                    + FIELD_BOOK_ID + " = '" + bookId +"' AND "
                    + FIELD_CHAPTER + " = '" + chapter +"'", null);
            if (c.moveToFirst()) {
                int contentIndex = c.getColumnIndex(FIELD_CONTENT);
                int poemIndex = c.getColumnIndex(FIELD_POEM);
                do {
                    String content = c.getString(contentIndex);
                    int poem = c.getInt(poemIndex);

                    Poem item = new Poem();
                    item.bookId = bookId;
                    item.chapterId = chapter - 1;
                    item.chapter = chapter;
                    item.content = content;
                    item.poem = poem;

                    poems.add(item);
                } while (c.moveToNext());
            }
            c.close();
        }

        return poems;
    }

    public List<Poem> getPoemCompare(int bookId, int chapter, int poem) {
        List<Poem> poems = new ArrayList<>();
        Log.d(TAG, "Starting get poem in all translated");
        if (db.isOpen()) {
            for (int i = 0; i < TABLE_NAMES.length; i++) {
                Cursor c = db.rawQuery("SELECT "
                        + FIELD_CONTENT
                        + " FROM '" + TABLE_NAMES[i] + "'"
                        + " WHERE "
                        + FIELD_BOOK_ID + " = '" + bookId +"' AND "
                        + FIELD_CHAPTER + " = '" + chapter +"' AND "
                        + FIELD_POEM + " = '" + poem +"'", null);
                if (c.moveToFirst()) {
                    int contentIndex = c.getColumnIndex(FIELD_CONTENT);
                    do {
                        String content = c.getString(contentIndex);

                        Poem pm = new Poem();
                        pm.poem = poem;
                        pm.chapter = chapter;
                        pm.bookId = bookId;
                        pm.content = content;
                        pm.translateName = TABLE_NAMES[i];

                        poems.add(pm);
                    } while (c.moveToNext());
                }
                c.close();
            }
        }

        Log.d(TAG, "END getting COMPARE POEMS");
        return poems;
    }

    public int getNumberOfPoemInChapter(int bookId, int chapter, String tableName) {
        int count = 0;

        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT COUNT(" + FIELD_POEM + ") AS " + FIELD_RESULT
                    + " FROM '" + tableName + "'"
                    + " WHERE "
                    + FIELD_BOOK_ID + " = '" + bookId +"' AND "
                    + FIELD_CHAPTER + " = '" + chapter +"'", null);
            c.moveToLast();
            count = c.getInt(c.getColumnIndex(FIELD_RESULT));
            c.close();
        }

        return count;
    }

    public int getNumberOfChapterInBook(int bookId, String tableName) {
        int count = 0;

        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT COUNT(" + FIELD_CHAPTER + ") AS " + FIELD_RESULT
                    + " FROM '" + tableName + "'"
                    + " WHERE "
                    + FIELD_BOOK_ID + " = '" + bookId +"' AND "
                    + FIELD_POEM + " = '1'", null);
            c.moveToLast();
            count = c.getInt(c.getColumnIndex(FIELD_RESULT));
            c.close();
        }

        return count;
    }

    public String getPoem(int bookId, int chapter, int poem, String tableName) {
        String result = "";

        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT "
                    + FIELD_CONTENT
                    + " FROM '" + tableName + "'"
                    + " WHERE "
                    + FIELD_BOOK_ID + " = '" + bookId +"' AND "
                    + FIELD_CHAPTER + " = '" + chapter +"' AND "
                    + FIELD_POEM + " = '" + poem +"'", null);
            if (c.moveToFirst()) {
                int contentIndex = c.getColumnIndex(FIELD_CONTENT);
                do {
                    return c.getString(contentIndex);
                } while (c.moveToNext());
            }
            c.close();
        }
        return result;
    }

    public void clearSearchResultHistory() {
        if (db.isOpen()) {
            db.delete(TABLE_SEARCH_RESULT, null, null);
        }
    }

    public List<Poem> searchInDataBase(String request, int idBookStart, int idBookEnd) {
        Log.d(TAG, "Start func search in db");
        List<Poem> result = new ArrayList<Poem>();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);

        String translateId = pref.contains(mContext.getString(R.string.pref_default_translate)) ?
                pref.getString(mContext.getString(R.string.pref_default_translate), TABLE_NAME_RST)
                : "0";

        String translate = translateId.equals("" + TRANSLATE_RST_ID) ? TABLE_NAME_RST : TABLE_NAME_MT;

        if (db.isOpen()) {
            Log.d(TAG, "DB_is_open: " + db.isOpen() + " request: " + request);
            Cursor c = db.rawQuery("SELECT * FROM '" + translate + "' WHERE content LIKE '%" + request + "%' " + "ORDER BY `" + FIELD_BOOK_ID + "` ASC", null);

            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndex(BibleDB.FIELD_BOOK_ID));
                    if ((id) >= idBookStart && (id) <= idBookEnd) {
                        String bookName = c.getString(c.getColumnIndex(BibleDB.FIELD_BOOK_NAME)) == null ? "null"
                                : c.getString(c.getColumnIndex(BibleDB.FIELD_BOOK_NAME));
                        int chapter = c.getInt(c.getColumnIndex(BibleDB.FIELD_CHAPTER));
                        int poem = c.getInt(c.getColumnIndex(BibleDB.FIELD_POEM));
                        String content = c.getString(c.getColumnIndex(BibleDB.FIELD_CONTENT)) == null ? "null"
                                : c.getString(c.getColumnIndex(BibleDB.FIELD_CONTENT));
                        Poem response = new Poem();
                        response.bookName = bookName;
                        response.chapter = chapter;
                        response.content = content;
                        response.bookId = id;
                        response.poem = poem;
                        result.add(response);
                    }

                } while (c.moveToNext());
            }
            c.close();
        }

        return result;
    }

    public void insertSearchResult(List<Poem> dataResult) {
        Log.d(TAG, "INSERT search RESULT");

        if (db.isOpen()) {
            for (Poem item : dataResult) {
                ContentValues data = new ContentValues();

                data.put(FIELD_BOOK_ID, item.bookId);
                data.put(FIELD_BOOK_NAME, item.bookName);
                data.put(FIELD_CHAPTER, item.chapter);
                data.put(FIELD_POEM, item.poem);
                data.put(FIELD_CONTENT, item.content);

                db.insert(TABLE_SEARCH_RESULT, null, data);
            }
        }
    }

    public List<Poem> getSearchResult() {
        List<Poem> result = new ArrayList<Poem>();

        if (db.isOpen()) {
            Log.d(TAG, "START - getSearchResult");
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_SEARCH_RESULT + "'", null);
            if (c.moveToFirst()) {
                do {
                    String bookName = c.getString(c.getColumnIndex(FIELD_BOOK_NAME));
                    int bookId = c.getInt(c.getColumnIndex(FIELD_BOOK_ID));
                    int chapter = c.getInt(c.getColumnIndex(FIELD_CHAPTER));
                    int poem = c.getInt(c.getColumnIndex(FIELD_POEM));
                    String content = c.getString(c.getColumnIndex(FIELD_CONTENT));

                    Poem item = new Poem();
                    item.bookId = bookId;
                    item.bookName = bookName;
                    item.chapter = chapter;
                    item.poem = poem;
                    item.content = content;

                    result.add(item);
                } while (c.moveToNext());
            }
        }

        return result;
    }

    public void setStatusItemReadForEveryDay(int position, String dbxId, boolean status) {
        dbUser.setStatusReadedByPosition(position, dbxId, status);
    }

    public void setDefaultStatusItemRead() {
        dbUser.setStatusReadedDefault();
    }

    public ArrayList<ItemReadDay> getListReadForEveryDay() {
        ArrayList<ItemReadDay> result = new ArrayList<ItemReadDay>();

        if (db.isOpen()) {
            Log.d(TAG, "START - getListReadForEveryDay()");
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_READ_FOR_EVERY_DAY + "'", null);
            if (c.moveToFirst()) {
                int monthIndex = c.getColumnIndex(FIELD_MONTH);
                int dayIndex = c.getColumnIndex(FIELD_DAY);
                int bookNameOldTestamentIndex = c.getColumnIndex(FIELD_BOOK_NAME_OLD_T);
                int bookNameNewTestamentIndex = c.getColumnIndex(FIELD_BOOK_NAME_NEW_T);
                int bookIdOldTestamentIndex = c.getColumnIndex(FIELD_BOOK_ID_OLD_T);
                int bookIdNewTestamentIndex = c.getColumnIndex(FIELD_BOOK_ID_NEW_T);
                int chaptersOldTestamentIndex = c.getColumnIndex(FIELD_CHAPTERS_OLD_T);
                int chaptersNewTestamentIndex = c.getColumnIndex(FIELD_CHAPTERS_NEW_T);
                int statusIndex = c.getColumnIndex(FIELD_STATUS_READED);

                do {
                    String month = c.getString(monthIndex);
                    int day = c.getInt(dayIndex);
                    String bookNameOldTestament = c.getString(bookNameOldTestamentIndex);
                    String bookNameNewTestament = c.getString(bookNameNewTestamentIndex);
                    String bookIdOldTestament = c.getString(bookIdOldTestamentIndex);
                    String bookIdNewTestament = c.getString(bookIdNewTestamentIndex);
                    String chaptersOldTestament = c.getString(chaptersOldTestamentIndex);
                    String chaptersNewTestament = c.getString(chaptersNewTestamentIndex);
                    boolean status = Boolean.parseBoolean(c.getString(statusIndex));

                    ItemReadDay item = new ItemReadDay();
                    item.setDay(day);
                    item.setMonth(month);
                    item.setListPoemOld(ContentTools.parseValueChapterBookIdString(mContext, chaptersOldTestament, bookNameOldTestament, bookIdOldTestament));
                    item.setListPoemNew(ContentTools.parseValueChapterBookIdString(mContext, chaptersNewTestament, bookNameNewTestament, bookIdNewTestament));
                    item.setContentChapterOldTFull(chaptersOldTestament);
                    item.setContentChapterNewTFull(chaptersNewTestament);
                    item.setStatus(status);

                    result.add(item);

                } while (c.moveToNext());
            }

            result = dbUser.getStatusReaded(result);
        }

        return result;
    }

    public UserDB getUserDB() {
        return dbUser;
    }
}
