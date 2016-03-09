package ua.maker.gbible_v2.DataBases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
import ua.maker.gbible_v2.Models.Poem;

@Singleton
public class UserDB extends SQLiteOpenHelper {

    private static final String TAG = "UserDB";

    private static final String DB_NAME = "user_data";
    private static final int DB_VERSION = 1;

    @Inject private DropBoxTools dropBoxTools;

    @Inject
    public UserDB(Context context, DropBoxTools dropBoxTools) {
        super(context, DB_NAME + ".db", null, DB_VERSION);
        this.context = context;
        this.dropBoxTools = dropBoxTools;
        db = getWritableDatabase();
        if (dbxDatastore == null) {
            try {
                dbxDatastore = dropBoxTools.getDbxDatastoreManager().openOrCreateDatastore(DB_NAME);
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

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

    /*private static final String SQL_CREATE_TABLE_BOOKMARKS_DATA = "CREATE TABLE " + TABLE_BOOKMARKS + " ("
            + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FIELD_LOCAL_DB_ID + " INTEGER,"
            + FIELD_TABLE_NAME + " TEXT,"
            + FIELD_BOOK_ID + " INTEGER,"
            + FIELD_BOOK_NAME + " TEXT,"
            + FIELD_COMMENT_BOOKMARK + " TEXT,"
            + FIELD_NEXT_LINK + " TEXT,"
            + FIELD_DBX_ID + " TEXT,"
            + FIELD_CHAPTER + " INTEGER,"
            + FIELD_POEM + " INTEGER,"
            + FIELD_CONTENT + " TEXT);";*/

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
    private DbxDatastore dbxDatastore = null;

    public DbxDatastore getDbxDatastore() {
        return dbxDatastore;
    }

    public void openDbxDatastore() {
        try {
            dbxDatastore = dropBoxTools.getDbxDatastoreManager().openOrCreateDatastore(DB_NAME);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate()");
        db.execSQL(SQL_CREATE_TABLE_PLANS_LIST);
        //db.execSQL(SQL_CREATE_TABLE_BOOKMARKS_DATA);
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
                if (isUniqueBookmark(fields)) {
                    fields.set(FIELD_CREATED_MILLIS, String.valueOf(System.currentTimeMillis()));
                    fields.set(FIELD_UPDATED_MILLIS, String.valueOf(System.currentTimeMillis()));

                    dbxTable.insert(fields);
                    try {
                        dbxDatastore.sync();
                    } catch (DbxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void updateBookmark(BookMark bookMark) {
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable dbxTable = dbxDatastore.getTable(TABLE_BOOKMARKS);
            if (dbxTable != null) {
                DbxFields fields = new DbxFields();
                fields.set(FIELD_TABLE_NAME, bookMark.getTableName());
                fields.set(FIELD_BOOK_ID, bookMark.getBookId());
                fields.set(FIELD_BOOK_NAME, Tools.getBookNameByBookId(bookMark.getBookId(), this.context));
                fields.set(FIELD_CHAPTER, bookMark.getChapter());
                fields.set(FIELD_POEM, bookMark.getPoem());
                fields.set(FIELD_CONTENT, bookMark.getContent());
                fields.set(FIELD_COMMENT_BOOKMARK, "" + bookMark.getComment());
                fields.set(FIELD_NEXT_LINK, "" + bookMark.getLinkNext());
                fields.set(FIELD_UPDATED_MILLIS, String.valueOf(System.currentTimeMillis()));

                try {
                    DbxRecord dbxRecord = dbxTable.get(bookMark.getDbxId());
                    dbxRecord.setAll(fields);
                } catch (DbxException e) {
                    e.printStackTrace();
                }
                try {
                    dbxDatastore.sync();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isUniqueBookmark(DbxFields fields) {
        DbxTable dbxTable = dbxDatastore.getTable(TABLE_BOOKMARKS);
        try {
            DbxTable.QueryResult queryResult = dbxTable.query(fields);
            if (queryResult != null && queryResult.count() > 0) {
                return false;
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void insertBookMarks(ArrayList<BookMark> listBookMarks) {
        if (db.isOpen()) {
            for (BookMark item : listBookMarks) {
                insertBookMark(item);
            }
        }
    }

    public ArrayList<BookMark> getBookMarks() {
        ArrayList<BookMark> resultDbx = new ArrayList<BookMark>();

        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable dbxTable = dbxDatastore.getTable(TABLE_BOOKMARKS);
            try {
                DbxTable.QueryResult queryResult = dbxTable.query();
                List<DbxRecord> queryList = queryResult.asList();
                for (DbxRecord record : queryList) {

                    String tableName = record.getString(FIELD_TABLE_NAME);
                    int bookId = (int) record.getLong(FIELD_BOOK_ID);
                    String bookName = record.getString(FIELD_BOOK_NAME);
                    int chapter = (int) record.getLong(FIELD_CHAPTER);
                    int poem = (int) record.getLong(FIELD_POEM);
                    String content = record.getString(FIELD_CONTENT);
                    long id = -1;
                    if (record.hasField(FIELD_LOCAL_DB_ID)) {
                        id = record.getLong(FIELD_LOCAL_DB_ID);
                    }
                    String comment = record.getString(FIELD_COMMENT_BOOKMARK);
                    String linkNext = record.getString(FIELD_NEXT_LINK);
                    String dbxId = record.getId();
                    String createdMillis = "", updateMillis = "";
                    if (record.hasField(FIELD_CREATED_MILLIS)) {
                        createdMillis = record.getString(FIELD_CREATED_MILLIS);
                    }
                    if (record.hasField(FIELD_UPDATED_MILLIS)) {
                        updateMillis = record.getString(FIELD_UPDATED_MILLIS);
                    }

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
                    item.setCreatedMillisDbx(createdMillis);
                    item.setUpdatedMillisDbx(updateMillis);
                    item.setDbxId(dbxId);

                    resultDbx.add(item);
                }
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(resultDbx, new Comparator<BookMark>() {
            @Override
            public int compare(BookMark lhs, BookMark rhs) {
                return (int) (Long.parseLong(lhs.getCreatedMillisDbx()) - Long.parseLong(rhs.getCreatedMillisDbx()));
            }
        });

        return resultDbx;
    }

    public boolean deleteBookmark(BookMark bookMark, boolean withSync) {
        Log.d(TAG, "Delete bookmark: id: " + bookMark.getId());
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(TABLE_BOOKMARKS);
            try {
                DbxRecord record = table.get(bookMark.getDbxId());
                if (record != null) {
                    record.deleteRecord();
                    if (withSync) {
                        dbxDatastore.sync();
                    }
                    return true;
                }
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
        return false;
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
        Log.d(TAG, "getItemsPlanById() Id plan: " + idPlan_request);
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

        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(TABLE_HISTORY);
            if (table != null) {
                DbxFields dbxFields = new DbxFields();
                dbxFields.set(FIELD_BOOK_NAME, Tools.getBookNameByBookId(historyItem.getBookId(), context));
                dbxFields.set(FIELD_BOOK_ID, historyItem.getBookId());
                dbxFields.set(FIELD_CHAPTER, historyItem.getChapter());
                dbxFields.set(FIELD_POEM, historyItem.getPoem());
                dbxFields.set(FIELD_TRANSLATE, historyItem.getTranslate());
                dbxFields.set(FIELD_DATE, historyItem.getDateCreated());

                if (isNoLastHistory(dbxFields)) {
                    dbxFields.set(FIELD_CREATED_MILLIS, String.valueOf(System.currentTimeMillis()));
                    table.insert(dbxFields);

                    try {
                        dbxDatastore.sync();
                    } catch (DbxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean isNoLastHistory(DbxFields historyDbx) {
        DbxTable table = dbxDatastore.getTable(TABLE_HISTORY);
        try {
            DbxTable.QueryResult queryResult = table.query(historyDbx);
            if (queryResult.count() > 0) {
                return false;
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return true;
    }

    public ArrayList<History> getHistory() {
        ArrayList<History> result = new ArrayList<History>();

        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(TABLE_HISTORY);
            if (table != null) {
                try {
                    DbxTable.QueryResult historyResults = table.query();
                    if (historyResults != null) {
                        List<DbxRecord> historyDbxRecords = historyResults.asList();
                        for (DbxRecord record : historyDbxRecords) {
                            int bookId = (int) record.getLong(FIELD_BOOK_ID);
                            int chapter = (int) record.getLong(FIELD_CHAPTER);
                            int poem = (int) record.getLong(FIELD_POEM);
                            String bookName = record.getString(FIELD_BOOK_NAME);
                            String translate = record.getString(FIELD_TRANSLATE);
                            String dateCreate = record.getString(FIELD_DATE);
                            String createdMillis = record.getString(FIELD_CREATED_MILLIS);

                            result.add(new History(dateCreate, bookName, translate, bookId, chapter, poem)
                                    .setCreatedMillis(createdMillis));
                        }
                    }
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }

        Collections.sort(result, new Comparator<History>() {
            @Override
            public int compare(History lhs, History rhs) {
                return (int) (Long.parseLong(rhs.getCreatedMillis()) - Long.parseLong(lhs.getCreatedMillis()));
            }
        });

        return result;
    }

    public void clearHistory() {
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(TABLE_HISTORY);
            if (table != null) {
                try {
                    DbxTable.QueryResult historyResults = table.query();
                    if (historyResults != null) {
                        List<DbxRecord> historyDbxRecords = historyResults.asList();
                        for (DbxRecord record : historyDbxRecords) {
                            record.deleteRecord();
                        }
                    }
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
            try {
                dbxDatastore.sync();
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
    }

    public String insertMarker(int bookId, int chapter, int poem, String colorHEX) {
        String id = null;
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(TABLE_MARKER);
            if (table != null) {
                DbxFields fields = new DbxFields();
                fields.set(FIELD_BOOK_ID, bookId);
                fields.set(FIELD_CHAPTER, chapter);
                fields.set(FIELD_POEM, poem);
                fields.set(FIELD_COLOR_HEX, colorHEX);

                id = table.insert(fields).getId();

                try {
                    dbxDatastore.sync();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
        return id;
    }

    public void updateMarker(String id, String colorHEX) {
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(TABLE_MARKER);
            if (table != null) {
                try {
                    DbxRecord record = table.get(id);

                    record.set(FIELD_COLOR_HEX, colorHEX);

                    dbxDatastore.sync();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ItemColor getPoemMarkerColor(int bookId, int chapter, int poem) {
        ItemColor result = new ItemColor();
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(TABLE_MARKER);
            if (table != null) {
                DbxFields fields = new DbxFields();
                fields.set(FIELD_BOOK_ID, bookId);
                fields.set(FIELD_CHAPTER, chapter);
                fields.set(FIELD_POEM, poem);
                try {
                    DbxTable.QueryResult queryResult = table.query(fields);

                    if (queryResult.count() > 0) {
                        DbxRecord record = queryResult.asList().get(0);
                        result.setId(record.getId());
                        result.setHex(record.getString(FIELD_COLOR_HEX));
                    } else {
                        return null;
                    }

                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void deleteMarker(String id) {
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(TABLE_MARKER);
            if (table != null) {
                try {
                    DbxRecord record = table.get(id);

                    if (record != null) {
                        record.deleteRecord();
                    } else {
                        return;
                    }

                    dbxDatastore.sync();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
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

    public ArrayList<ItemReadDay> getStatusReaded(ArrayList<ItemReadDay> data) {
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(BibleDB.TABLE_READ_FOR_EVERY_DAY);
            if (table != null) {
                try {
                    DbxTable.QueryResult statusQuery = table.query();
                    if (statusQuery != null && statusQuery.count() > 0) {
                        List<DbxRecord> statuses = statusQuery.asList();
                        getItemStatusRead(statuses, data);
                    }
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    private static void getItemStatusRead(final List<DbxRecord> statuses, final ArrayList<ItemReadDay> data) {
        for (DbxRecord record : statuses) {
            boolean status = record.getBoolean(BibleDB.FIELD_STATUS_READED);
            int index = (int) record.getLong(BibleDB.KEY_ROWID);
            data.get(index).setStatus(status);
            data.get(index).setDbxId(record.getId());
        }
    }

    public void deleteTableReadForEveryDay() {
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(BibleDB.TABLE_READ_FOR_EVERY_DAY);
            if (table != null) {
                try {
                    DbxTable.QueryResult statusQuery = table.query();
                    if (statusQuery != null) {
                        List<DbxRecord> statuses = statusQuery.asList();
                        for (DbxRecord record : statuses) {
                            record.deleteRecord();
                        }
                        dbxDatastore.sync();
                    }
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setStatusReadedByPosition(int position, String dbxId, boolean status) {
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable table = dbxDatastore.getTable(BibleDB.TABLE_READ_FOR_EVERY_DAY);
            if (table != null) {
                try {
                    if (dbxId != null) {
                        DbxRecord record = table.get(dbxId);
                        if (record != null) {
                            record.set(BibleDB.FIELD_STATUS_READED, status);
                        }
                    } else {
                        DbxFields fields = new DbxFields();
                        fields.set(BibleDB.KEY_ROWID, position);
                        List<DbxRecord> r = table.query(fields).asList();

                        if (r.size() == 0) {
                            fields.set(BibleDB.FIELD_STATUS_READED, status);
                            table.insert(fields);
                        } else {
                            r.get(0).set(BibleDB.FIELD_STATUS_READED, status);
                        }
                    }

                    dbxDatastore.sync();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setStatusReadedDefault() {
        deleteTableReadForEveryDay();
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
                    staticBookList = DataConverter.getGson()
                            .fromJson(data, Book.getTypeTokenForArray());
                    return staticBookList;
                } while (c.moveToNext());
            }
        }
        return staticBookList;
    }

    public void setBooksToJSON(ArrayList<Book> books) {
        if (db.isOpen()) {
            ContentValues cv = new ContentValues();

            cv.put(FIELD_KEY, App.KeysConfig.BOOKS);
            cv.put(FIELD_DATA, DataConverter.getGson().toJson(books, Book.getTypeTokenForArray()));

            db.insert(TABLE_CONFIGS, null, cv);
        }
    }

    public boolean isBookMark(Poem poem) {
        if (dbxDatastore != null && dbxDatastore.isOpen()) {
            DbxTable dbxTable = dbxDatastore.getTable(TABLE_BOOKMARKS);
            if (dbxTable != null) {
                DbxFields fields = new DbxFields();
                if(poem.translateName != null)
                    fields.set(FIELD_TABLE_NAME, poem.translateName);
                fields.set(FIELD_BOOK_ID, poem.bookId);
                fields.set(FIELD_BOOK_NAME, Tools.getBookNameByBookId(poem.bookId, this.context));
                fields.set(FIELD_CHAPTER, poem.chapter);
                fields.set(FIELD_POEM, poem.poem);
                return !isUniqueBookmark(fields);
            }
        }
        return false;
    }
}