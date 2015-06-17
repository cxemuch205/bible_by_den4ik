package ua.maker.gbible_v2.DataBases;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.Helpers.DataConverter;
import ua.maker.gbible_v2.Helpers.DropBoxTools;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Models.Book;
import ua.maker.gbible_v2.Models.BookMark;
import ua.maker.gbible_v2.Models.History;
import ua.maker.gbible_v2.Models.ItemColor;
import ua.maker.gbible_v2.Models.ItemPlan;
import ua.maker.gbible_v2.Models.ItemReadDay;
import ua.maker.gbible_v2.Models.Plan;

public class UserDB extends SQLiteOpenHelper {

    private static final String TAG = "UserDB";

    private static final String DB_NAME = "user_data";
    private static final int DB_VERSION = 1;

    private static final String TABLE_PLAN_LIST = "user_plan_list";
    private static final String TABLE_PLAN_DATA = "item_plan_data";
    private static final String TABLE_BOOKMARKS = "bookmarks_data";
    private static final String TABLE_HISTORY = "history_link";
    private static final String TABLE_MARKER = "markars_on_poem";
    private static final String TABLE_COLOR_SELECT_HITORY = "color_select_history";
    private static final String TABLE_CONFIGS = "configs";

    public static final String FIELD_ID = "_id";
    public static final String FIELD_PLAN_ID = "id_plan";
    public static final String FIELD_NAME = "name_plan";
    public static final String FIELD_SUB_DESCRIPTION = "sub_description";
    public static final String FIELD_DATE = "date_create";
    public static final String FIELD_DATA = "data_configs";
    public static final String FIELD_KEY = "key_configs";
    public static final String FIELD_COLOR_HEX = "color_hex_marker";

    public static final String FIELD_BOOK_ID = "bookId";
    public static final String FIELD_BOOK_NAME = "bookName";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_CHAPTER = "chapter";
    public static final String FIELD_POEM = "poem";
    public static final String FIELD_TO_POEM = "topoem";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_TRANSLATE = "translate";
    public static final String FIELD_COMMENT_BOOKMARK = "comment_bookmark";
    public static final String FIELD_NEXT_LINK = "next_link_bookmark";
    public static final String FIELD_DBX_ID = "dbx_id";

    public static final String FIELD_TYPE_DATA = "type_data";
    public static final String FIELD_PATH_IMG = "path_img";
    public static final String FIELD_CREATED_MILLIS = "created_millis";
    public static final String FIELD_UPDATED_MILLIS = "updated_millis";
    public static final String FIELD_LOCAL_DB_ID = "local_raw_id";

    private static final String SQL_CREATE_TABLE_PLANS_LIST = "CREATE TABLE " + TABLE_PLAN_LIST + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + FIELD_NAME + " TEXT,"
            + FIELD_DBX_ID + " TEXT,"
            + FIELD_SUB_DESCRIPTION + " TEXT,"
            + FIELD_DATE + " TEXT);";

    private static final String SQL_CREATE_TABLE_MARKER = "CREATE TABLE " + TABLE_MARKER + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + FIELD_COLOR_HEX + " TEXT,"
            + FIELD_DBX_ID + " TEXT,"
            + FIELD_BOOK_ID + " INTEGER,"
            + FIELD_CHAPTER + " INTEGER,"
            + FIELD_POEM + " INTEGER);";

    private static final String SQL_CREATE_TABLE_PLAN_DATA = "CREATE TABLE " + TABLE_PLAN_DATA + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FIELD_PLAN_ID + " INTEGER NOT NULL,"
            + FIELD_TYPE_DATA + " INTEGER NOT NULL,"
            + FIELD_PATH_IMG + " TEXT NOT NULL,"
            + FIELD_CONTENT + " TEXT NOT NULL,"
            + FIELD_TABLE_NAME + " TEXT NOT NULL,"
            + FIELD_BOOK_ID + " INTEGER NOT NULL,"
            + FIELD_BOOK_NAME + " TEXT NOT NULL,"
            + FIELD_DBX_ID + " TEXT,"
            + FIELD_CHAPTER + " INTEGER NOT NULL,"
            + FIELD_POEM + " INTEGER NOT NULL,"
            + FIELD_TO_POEM + " INTEGER NOT NULL);";

    private static final String SQL_CREATE_TABLE_BOOKMARKS_DATA = "CREATE TABLE " + TABLE_BOOKMARKS + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FIELD_TABLE_NAME + " TEXT,"
            + FIELD_BOOK_ID + " INTEGER,"
            + FIELD_BOOK_NAME + " TEXT,"
            + FIELD_COMMENT_BOOKMARK + " TEXT,"
            + FIELD_NEXT_LINK + " TEXT,"
            + FIELD_DBX_ID + " TEXT,"
            + FIELD_CHAPTER + " INTEGER,"
            + FIELD_POEM + " INTEGER,"
            + FIELD_CONTENT + " TEXT);";

    private static final String SQL_CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FIELD_TRANSLATE + " TEXT,"
            + FIELD_BOOK_ID + " INTEGER,"
            + FIELD_BOOK_NAME + " TEXT,"
            + FIELD_CHAPTER + " INTEGER,"
            + FIELD_DBX_ID + " TEXT,"
            + FIELD_POEM + " INTEGER,"
            + FIELD_DATE + " TEXT);";

    private static final String SQL_COLOR_SELECT_HISTORY = "CREATE TABLE " + TABLE_COLOR_SELECT_HITORY + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FIELD_DBX_ID + " TEXT,"
            + FIELD_COLOR_HEX + " TEXT)";

    private static final String SQL_STATUS_READED = "CREATE TABLE " + BibleDB.TABLE_READ_FOR_EVERY_DAY + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FIELD_DBX_ID + " TEXT,"
            + BibleDB.FIELD_STATUS_READED + " TEXT);";

    private static final String SQL_CONFIGS = "CREATE TABLE " + TABLE_CONFIGS + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FIELD_DATA + " TEXT,"
            + FIELD_DBX_ID + " TEXT,"
            + FIELD_KEY + " TEXT);";

    private Context context = null;
    private SQLiteDatabase db = null;
    private SharedPreferences pref;
    private static DbxDatastore dbxDatastore;

    public UserDB(Context context) {
        super(context, DB_NAME + ".db", null, DB_VERSION);
        this.context = context;
        db = getWritableDatabase();
        pref = context.getSharedPreferences(App.Pref.NAME, 0);
        if (pref.getBoolean(App.Pref.SYNC_WITH_DBX, false) && dbxDatastore == null) {
            try {
                dbxDatastore = DropBoxTools.getDbxDatastoreManager().openOrCreateDatastore(DB_NAME);
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate()");
        db.execSQL(SQL_CREATE_TABLE_PLANS_LIST);
        db.execSQL(SQL_CREATE_TABLE_BOOKMARKS_DATA);
        db.execSQL(SQL_CREATE_TABLE_PLAN_DATA);
        db.execSQL(SQL_CREATE_TABLE_HISTORY);
        db.execSQL(SQL_CREATE_TABLE_MARKER);
        db.execSQL(SQL_COLOR_SELECT_HISTORY);
        db.execSQL(SQL_STATUS_READED);
        db.execSQL(SQL_CONFIGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade()");
        switch (oldVersion) {
            case 1:
            case 2:

        }
    }

    public int insertPlan(Plan data) {
        Log.d(TAG, "Start insert list plan to db");
        long idInsertData = 1;

        if (db.isOpen()) {
            ContentValues values = new ContentValues();

            values.put(FIELD_NAME, "" + data.getName());
            values.put(FIELD_SUB_DESCRIPTION, "" + data.getSubDescription());
            values.put(FIELD_DATE, "" + data.getDate());

            idInsertData = db.insert(TABLE_PLAN_LIST, null, values);
            Log.d(TAG, "END write list plans to db | long id inser: " + idInsertData);

        }
        return Integer.parseInt("" + idInsertData);
    }

    public void updatePlan(Plan data) {
        Log.d(TAG, "insertPlan() - id: " + data.getId());

        if (db.isOpen()) {
            ContentValues values = new ContentValues();

            values.put(FIELD_NAME, data.getName());
            values.put(FIELD_SUB_DESCRIPTION, data.getSubDescription());
            values.put(FIELD_DATE, data.getDate());
            int idUpdateRow = db.update(TABLE_PLAN_LIST, values, FIELD_ID + " = " + data.getId(), null);
            Log.d(TAG, "ROW Update: " + idUpdateRow);
        }
    }

    public List<Plan> getPlansList() {
        Log.d(TAG, "START - getPlansList()");
        List<Plan> result = new ArrayList<Plan>();

        if (db.isOpen()) {
            Cursor c = db.query(TABLE_PLAN_LIST, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                int nameId = c.getColumnIndex(FIELD_NAME);
                int subDescriptionId = c.getColumnIndex(FIELD_SUB_DESCRIPTION);
                int dateId = c.getColumnIndex(FIELD_DATE);
                int idId = c.getColumnIndex(FIELD_ID);

                do {
                    String name = "" + c.getString(nameId);
                    String subDescription = "" + c.getString(subDescriptionId);
                    String date = "" + c.getString(dateId);
                    int id = c.getInt(idId);

                    result.add(new Plan(name, subDescription, date, id));
                } while (c.moveToNext());
            }
        }

        return result;
    }

    public Plan getPlanById(int idPlan) {
        Plan result = new Plan();
        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_PLAN_LIST + "'", null);
            if (c.moveToFirst()) {
                do {
                    int idPl = c.getInt(c.getColumnIndex(FIELD_ID));
                    if (idPl == idPlan) {
                        String nm = c.getString(c.getColumnIndex(FIELD_NAME));
                        String sub = c.getString(c.getColumnIndex(FIELD_SUB_DESCRIPTION));
                        String dt = c.getString(c.getColumnIndex(FIELD_DATE));

                        result.setId(idPl);
                        result.setName(nm);
                        result.setDate(dt);
                        result.setSubDescription(sub);
                        break;
                    }
                } while (c.moveToNext());
            }
        }
        return result;
    }

    public void deletePlan(int id) {
        Log.d(TAG, "DELETE plan id: " + id);
        if (db.isOpen()) {
            db.delete(TABLE_PLAN_LIST, FIELD_ID + " = " + id, null);
            db.delete(TABLE_PLAN_DATA, FIELD_PLAN_ID + " = " + id, null);
        }
    }

    public void insertBookMark(BookMark bookmarks) {
        String dbxIdRecord = null;
        DbxRecord record = null;
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable dbxTable = dbxDatastore.getTable(TABLE_BOOKMARKS);
            if (dbxTable != null) {
                DbxFields fields = new DbxFields();
                fields.set(FIELD_TABLE_NAME, bookmarks.getTableName());
                fields.set(FIELD_BOOK_ID, bookmarks.getBookId());
                fields.set(FIELD_BOOK_NAME, Tools.getBookNameByBookId(bookmarks.getBookId(), this.context));
                fields.set(FIELD_CHAPTER, bookmarks.getChapter());
                fields.set(FIELD_POEM, bookmarks.getPoem());
                fields.set(FIELD_CONTENT, bookmarks.getContent());
                fields.set(FIELD_COMMENT_BOOKMARK, "" + bookmarks.getComment());
                fields.set(FIELD_NEXT_LINK, "" + bookmarks.getLinkNext());
                fields.set(FIELD_CREATED_MILLIS, String.valueOf(System.currentTimeMillis()));
                fields.set(FIELD_UPDATED_MILLIS, String.valueOf(System.currentTimeMillis()));

                record = dbxTable.insert(fields);
                dbxIdRecord = record.getId();
                /*try {
                    dbxDatastore.sync();
                } catch (DbxException e) {
                    e.printStackTrace();
                }*/
            }
        }

        if (db.isOpen()) {
            Log.d(TAG, "WRITE - WORK");
            ContentValues values = new ContentValues();

            values.put(FIELD_TABLE_NAME, bookmarks.getTableName());
            values.put(FIELD_BOOK_ID, bookmarks.getBookId());
            values.put(FIELD_BOOK_NAME, Tools.getBookNameByBookId(bookmarks.getBookId(), this.context));
            values.put(FIELD_CHAPTER, bookmarks.getChapter());
            values.put(FIELD_POEM, bookmarks.getPoem());
            values.put(FIELD_CONTENT, bookmarks.getContent());
            values.put(FIELD_COMMENT_BOOKMARK, "" + bookmarks.getComment());
            values.put(FIELD_NEXT_LINK, "" + bookmarks.getLinkNext());
            if (dbxIdRecord != null) {
                values.put(FIELD_DBX_ID, dbxIdRecord);
            }

            long localRawId = db.insert(TABLE_BOOKMARKS, null, values);
            if (dbxIdRecord != null) {
                try {
                    record.set(FIELD_LOCAL_DB_ID, localRawId);
                    dbxDatastore.sync();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void insertBookMarks(ArrayList<BookMark> listBookMarks) {
        if (db.isOpen()) {
            for (BookMark item : listBookMarks) {
                insertBookMark(item);
            }
        }
    }

    public ArrayList<BookMark> getBookMarks() {
        ArrayList<BookMark> result = new ArrayList<BookMark>();

        //TODO: make get from dbx data

        if (db.isOpen()) {
            Log.d(TAG, "start get BookMarks()");
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_BOOKMARKS + "'", null);
            if (c.moveToFirst()) {
                int idTableName = c.getColumnIndex(FIELD_TABLE_NAME);
                int idBookName = c.getColumnIndex(FIELD_BOOK_NAME);
                int idBookId = c.getColumnIndex(FIELD_BOOK_ID);
                int idChapter = c.getColumnIndex(FIELD_CHAPTER);
                int idPoem = c.getColumnIndex(FIELD_POEM);
                int idContent = c.getColumnIndex(FIELD_CONTENT);
                int idId = c.getColumnIndex(FIELD_ID);
                int idComment = c.getColumnIndex(FIELD_COMMENT_BOOKMARK);
                int idLinkNext = c.getColumnIndex(FIELD_NEXT_LINK);
                int indexDbxId = c.getColumnIndex(FIELD_DBX_ID);

                do {
                    String tableName = c.getString(idTableName);
                    String bookName = c.getString(idBookName);
                    int bookId = c.getInt(idBookId);
                    int chapter = c.getInt(idChapter);
                    int poem = c.getInt(idPoem);
                    String content = c.getString(idContent);
                    int id = c.getInt(idId);
                    String comment = c.getString(idComment);
                    String linkNext = c.getString(idLinkNext);
                    String dbxId = c.getString(indexDbxId);

                    BookMark item = new BookMark(
                            tableName,
                            bookName,
                            content,
                            bookId,
                            chapter,
                            poem,
                            id,
                            comment,
                            linkNext);
                    if (dbxId != null) {
                        item.setDbxId(dbxId);
                    }

                    result.add(item);
                } while (c.moveToNext());
            }
        }

        return result;
    }

    public void deleteBookmark(BookMark bookMark) {
        Log.d(TAG, "DElete bookmark: id: " + bookMark.getId());
        if (db.isOpen()) {
            if (dbxDatastore != null) {
                DbxTable table = dbxDatastore.getTable(TABLE_BOOKMARKS);
                try {
                    DbxRecord record = table.get(bookMark.getDbxId());
                    if (record != null) {
                        record.deleteRecord();
                        dbxDatastore.sync();
                    }
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
            db.delete(TABLE_BOOKMARKS, FIELD_ID + " = " + bookMark.getId(), null);
        }
    }

    public void insertItemPlan(ItemPlan data) {
        if (db.isOpen()) {
            ContentValues cv = new ContentValues();

            cv.put(FIELD_PLAN_ID, data.getId());
            cv.put(FIELD_TYPE_DATA, data.getDataType());
            cv.put(FIELD_CONTENT, "" + data.getText());
            cv.put(FIELD_TABLE_NAME, "" + data.getTranslate());
            cv.put(FIELD_BOOK_ID, data.getBookId());
            cv.put(FIELD_BOOK_NAME, "" + data.getBookName());
            cv.put(FIELD_CHAPTER, data.getChapter());
            cv.put(FIELD_POEM, data.getPoem());
            cv.put(FIELD_TO_POEM, data.getToPoem());
            cv.put(FIELD_PATH_IMG, "" + data.getPathImg());

            db.insert(TABLE_PLAN_DATA, null, cv);
        }
    }

    public void insertPlanData(List<ItemPlan> data) {
        if (db.isOpen()) {
            for (int i = 0; i < data.size(); i++) {
                ItemPlan item = data.get(i);
                ContentValues cv = new ContentValues();

                cv.put(FIELD_PLAN_ID, item.getId());
                cv.put(FIELD_TYPE_DATA, item.getDataType());
                cv.put(FIELD_CONTENT, "" + item.getText());
                cv.put(FIELD_TABLE_NAME, "" + item.getTranslate());
                cv.put(FIELD_BOOK_ID, item.getBookId());
                cv.put(FIELD_BOOK_NAME, "" + item.getBookName());
                cv.put(FIELD_CHAPTER, item.getChapter());
                cv.put(FIELD_POEM, item.getPoem());
                cv.put(FIELD_TO_POEM, item.getToPoem());
                cv.put(FIELD_PATH_IMG, "" + item.getPathImg());

                db.insert(TABLE_PLAN_DATA, null, cv);
            }
        }
    }

    public void deleteItemPlan(int idItemPlan) {
        Log.d(TAG, "Id item plan: " + idItemPlan);
        if (db.isOpen()) {
            db.delete(TABLE_PLAN_DATA, FIELD_ID + " = " + idItemPlan, null);
        }
    }

    public List<ItemPlan> getItemsPlanById(int idPlan_request) {
        List<ItemPlan> result = new ArrayList<ItemPlan>();
        Log.d(TAG + " getItemsPlanById()", "Id plan: " + idPlan_request);
        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_PLAN_DATA + "'", null);
            if (c.moveToFirst()) {
                int idIdItem = c.getColumnIndex(FIELD_ID);
                int ididPlan = c.getColumnIndex(FIELD_PLAN_ID);
                int idTypeData = c.getColumnIndex(FIELD_TYPE_DATA);
                int idText = c.getColumnIndex(FIELD_CONTENT);
                int idTranslate = c.getColumnIndex(FIELD_TABLE_NAME);
                int idBookId = c.getColumnIndex(FIELD_BOOK_ID);
                int idBookName = c.getColumnIndex(FIELD_BOOK_NAME);
                int idChapter = c.getColumnIndex(FIELD_CHAPTER);
                int idPoem = c.getColumnIndex(FIELD_POEM);
                int idToPoem = c.getColumnIndex(FIELD_TO_POEM);
                int idPathImg = c.getColumnIndex(FIELD_PATH_IMG);

                do {
                    int idPlan = c.getInt(ididPlan);
                    if (idPlan == idPlan_request) {
                        int idItem = 0;
                        idItem = c.getInt(idIdItem);
                        int typeData = 0;
                        typeData = c.getInt(idTypeData);
                        String text = "";
                        text = c.getString(idText);
                        String translate = "";
                        translate = c.getString(idTranslate);
                        int bookId = 0;
                        bookId = c.getInt(idBookId);
                        String bookName = "";
                        bookName = c.getString(idBookName);
                        int chapter = 0;
                        chapter = c.getInt(idChapter);
                        int poem = 0, toPoem = 0;
                        poem = c.getInt(idPoem);
                        toPoem = c.getInt(idToPoem);
                        String pathImg = "";
                        pathImg = c.getString(idPathImg);

                        ItemPlan plan = new ItemPlan();
                        plan.setBookId(bookId);
                        plan.setBookName(bookName);
                        plan.setChapter(chapter);
                        plan.setDataType(typeData);
                        plan.setId(idPlan);
                        plan.setPathImg(pathImg);
                        plan.setPoem(poem);
                        plan.setToPoem(toPoem);
                        plan.setText(text);
                        plan.setTranslate(translate);
                        plan.setIdItem(idItem);

                        result.add(plan);
                    }
                } while (c.moveToNext());
            }
        }
        return result;
    }

    public void insertHistory(History historyItem) {
        Log.d(TAG, "insert to db History: bookId " + historyItem.getBookId()
                + " chapter " + historyItem.getChapter()
                + " poem " + historyItem.getPoem());

        if (db.isOpen()) {
            if (isNoLastHistory(historyItem)) {
                ContentValues values = new ContentValues();

                values.put(FIELD_BOOK_NAME, Tools.getBookNameByBookId(historyItem.getBookId(), context));
                values.put(FIELD_BOOK_ID, historyItem.getBookId());
                values.put(FIELD_CHAPTER, historyItem.getChapter());
                values.put(FIELD_POEM, historyItem.getPoem());
                values.put(FIELD_TRANSLATE, historyItem.getTranslate());
                values.put(FIELD_DATE, historyItem.getDateCreated());

                db.insert(TABLE_HISTORY, null, values);
            }
        }
    }

    private boolean isNoLastHistory(History historyItem) {
        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT "
                    + FIELD_BOOK_ID
                    + " FROM '" + TABLE_HISTORY + "'"
                    + " WHERE "
                    + FIELD_BOOK_ID + " = '" + historyItem.getBookId() + "' AND "
                    + FIELD_CHAPTER + " = '" + historyItem.getChapter() + "' AND "
                    + FIELD_DATE + " = '" + historyItem.getDateCreated() + "'", null);
            if (c != null) {
                return false;
            }
        }
        return true;
    }

    public List<History> getHistory() {
        List<History> result = new ArrayList<History>();

        if (db.isOpen()) {
            Log.d(TAG, "getHistory() - start");
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_HISTORY + "'", null);
            if (c.moveToFirst()) {
                int idBookId = c.getColumnIndex(FIELD_BOOK_ID);
                int idChapter = c.getColumnIndex(FIELD_CHAPTER);
                int idPoem = c.getColumnIndex(FIELD_POEM);
                int idBookName = c.getColumnIndex(FIELD_BOOK_NAME);
                int idTranslate = c.getColumnIndex(FIELD_TRANSLATE);
                int idDateCreate = c.getColumnIndex(FIELD_DATE);
                do {
                    int bookId = c.getInt(idBookId);
                    int chapter = c.getInt(idChapter);
                    int poem = c.getInt(idPoem);
                    String bookName = c.getString(idBookName);
                    String translate = c.getString(idTranslate);
                    String dateCreate = c.getString(idDateCreate);

                    result.add(0, new History(dateCreate, bookName, translate, bookId, chapter, poem));

                } while (c.moveToNext());
            }
        }

        return result;
    }

    public void clearHistory() {
        if (db.isOpen()) {
            db.delete(TABLE_HISTORY, null, null);
        }
    }

    public int insertMarker(int bookId, int chapter, int poem, String colorHEX) {
        int row = 0;
        if (db.isOpen()) {
            boolean first = true;
            Cursor c;
            try {
                c = db.rawQuery("SELECT * FROM '" + TABLE_MARKER + "'", null);
                if (c.moveToFirst()) {
                    int bookIndex = c.getColumnIndex(FIELD_BOOK_ID);
                    int chapterIndex = c.getColumnIndex(FIELD_CHAPTER);
                    int poemIndex = c.getColumnIndex(FIELD_POEM);
                    int colorIndex = c.getColumnIndex(FIELD_COLOR_HEX);
                    do {
                        int book = c.getInt(bookIndex);
                        int cha = c.getInt(chapterIndex);
                        int pm = c.getInt(poemIndex);
                        String color = c.getString(colorIndex);
                        if (book == bookId & cha == chapter & pm == poem & color.equals(colorHEX)) {
                            first = false;
                            break;
                        }
                    } while (c.moveToNext());
                }
                if (first) {
                    ContentValues cv = new ContentValues();

                    cv.put(FIELD_BOOK_ID, bookId);
                    cv.put(FIELD_CHAPTER, chapter);
                    cv.put(FIELD_POEM, poem);
                    cv.put(FIELD_COLOR_HEX, colorHEX);

                    row = (int) db.insert(TABLE_MARKER, null, cv);
                    Log.i(TAG, " INSERT - Color row: " + row);
                }
            } catch (Exception e) {
            }
        }
        return row;
    }

    public UserDB updateMarker(int bookId, int chapter, int poem, String colorHEX, int pos) {
        if (db.isOpen()) {
            ContentValues cv = new ContentValues();

            cv.put(FIELD_BOOK_ID, bookId);
            cv.put(FIELD_CHAPTER, chapter);
            cv.put(FIELD_POEM, poem);
            cv.put(FIELD_COLOR_HEX, colorHEX);

            long posColor = db.update(TABLE_MARKER, cv, FIELD_ID + "=" + String.valueOf(pos), null);
            Log.i(TAG, "UPDATE Color pos: " + posColor);
        }
        return this;
    }

    public ItemColor getPoemMarkerColor(int bookId, int chapter, int poem) {
        ItemColor result = new ItemColor();
        try {
            if (db.isOpen()) {
                Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_MARKER + "'", null);
                if (c.moveToFirst()) {
                    int bookIndex = c.getColumnIndex(FIELD_BOOK_ID);
                    int chapterIndex = c.getColumnIndex(FIELD_CHAPTER);
                    int poemIndex = c.getColumnIndex(FIELD_POEM);
                    int colorIndex = c.getColumnIndex(FIELD_COLOR_HEX);
                    int positionIndex = c.getColumnIndex(FIELD_ID);
                    do {
                        int book = c.getInt(bookIndex);
                        int cha = c.getInt(chapterIndex);
                        int pm = c.getInt(poemIndex);
                        int position = c.getInt(positionIndex);
                        if (book == bookId & cha == chapter & pm == poem) {
                            result.setHex(c.getString(colorIndex));
                            result.setPosition(position);
                            break;
                        }

                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    public void insertColorStruct(ItemColor data) {
        if (db.isOpen()) {
            if (isUniqColorHEX(data)) {
                insertCS(data);
            } else {
                if (data.getIdDB() != 0) {
                    deleteColorItemHistory(data);
                    insertCS(data);
                }
            }
        }
    }

    private void insertCS(ItemColor data) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_COLOR_HEX, data.getHex());
        db.insert(TABLE_COLOR_SELECT_HITORY, null, cv);
    }

    private void deleteColorItemHistory(ItemColor data) {
        if (db.isOpen()) {
            db.delete(TABLE_COLOR_SELECT_HITORY, FIELD_ID + "=" + data.getIdDB(), null);
        }
    }

    private boolean isUniqColorHEX(ItemColor data) {
        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_COLOR_SELECT_HITORY + "'", null);
            if (c.moveToFirst()) {
                int idIndex = c.getColumnIndex(FIELD_ID);
                int colorIndex = c.getColumnIndex(FIELD_COLOR_HEX);
                do {
                    int id = c.getInt(idIndex);
                    String color = c.getString(colorIndex);
                    if (color.equals(data.getHex())) {
                        data.setIdDB(id);
                        return false;
                    }
                } while (c.moveToNext());
            }
        }
        return true;
    }

    public List<ItemColor> getListHistoryColorsSelect() {
        List<ItemColor> result = new ArrayList<ItemColor>();
        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_COLOR_SELECT_HITORY + "'", null);
            if (c.moveToFirst()) {
                int idINdex = c.getColumnIndex(FIELD_ID);
                int colorIndex = c.getColumnIndex(FIELD_COLOR_HEX);
                do {
                    int id = c.getInt(idINdex);
                    String color = c.getString(colorIndex);
                    ItemColor item = new ItemColor();
                    item.setHex(color);
                    item.setIdDB(id);
                    result.add(item);
                } while (c.moveToNext());
            }
        }
        return result;
    }

    public void insertStatusesReaded(List<ItemReadDay> data) {
        if (db.isOpen()) {
            for (int i = 0; i < data.size(); i++) {
                ContentValues cv = new ContentValues();

                cv.put(BibleDB.FIELD_STATUS_READED, data.get(i).isStatusReaded());

                db.insert(BibleDB.TABLE_READ_FOR_EVERY_DAY, null, cv);
            }
        }
    }

    public List<ItemReadDay> getStatusReaded(List<ItemReadDay> data) {
        if (!pref.contains("inser_list_status")) {
            insertStatusesReaded(data);
            pref.edit().putBoolean("insert_list_status", true).apply();
        }
        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT * FROM '" + BibleDB.TABLE_READ_FOR_EVERY_DAY + "'", null);
            if (c.moveToFirst()) {
                int indexStatus = c.getColumnIndex(BibleDB.FIELD_STATUS_READED);
                int i = 0;
                do {
                    boolean status = Boolean.parseBoolean(c.getString(indexStatus));
                    data.get(i).setStatus(status);
                    i++;
                } while (c.moveToNext());
            }
        }

        return data;
    }

    public int setStatusReadedByPosition(int index, boolean status) {
        if (db.isOpen()) {
            ContentValues values = new ContentValues();

            values.put(BibleDB.FIELD_STATUS_READED, String.valueOf(status));
            int idUpdateRow = db.update(BibleDB.TABLE_READ_FOR_EVERY_DAY, values, FIELD_ID + " = " + (index + 1), null);
            Log.d(TAG, "ROW Update: " + idUpdateRow);
            return idUpdateRow;
        } else
            return -1;
    }

    public ArrayList<Book> getBooksFromJSON(ArrayList<Book> staticBookList) {
        if (db.isOpen()) {
            Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_CONFIGS + "'"
                    + " WHERE "
                    + FIELD_KEY + " = '" + App.KeysConfig.BOOKS + "'", null);
            if (c != null && c.moveToFirst()) {
                int indexData = c.getColumnIndex(FIELD_DATA);
                do {
                    String data = c.getString(indexData);
                    staticBookList = DataConverter.getGson().fromJson(data, Book.getTypeTokenForArray());
                    return staticBookList;
                } while (c.moveToNext());
            }
        }
        return null;
    }

    public void setBooksToJSON(ArrayList<Book> books) {
        if (db.isOpen()) {
            ContentValues cv = new ContentValues();

            cv.put(FIELD_KEY, App.KeysConfig.BOOKS);
            cv.put(FIELD_DATA, DataConverter.getGson().toJson(books, Book.getTypeTokenForArray()));

            db.insert(TABLE_CONFIGS, null, cv);
        }
    }
}