package ua.maker.gbible.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ua.maker.gbible.R;
import ua.maker.gbible.constant.App;
import ua.maker.gbible.structs.ItemReadDay;
import ua.maker.gbible.structs.PoemStruct;
import ua.maker.gbible.structs.SearchStruct;
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

public class DataBase extends SQLiteOpenHelper {
	
	private static final String TAG = "DataBase";	
	private static final int DB_VERSION = 2;	
	@SuppressLint("SdCardPath")
	private static final String DB_PATH = "/data/data/ua.maker.gbible/databases/";
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
	private final Context mContext;
	
	private SharedPreferences pref = null;

	public DataBase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
		pref = context.getSharedPreferences(App.PREF_APP, 0);
	}

	public void createDataBase() throws IOException{
    	boolean dbExist = checkDataBase();

    	if(dbExist){
    		int oldVer = pref.getInt(App.PREF_DATA_BASE_VER, 1);
    		if(oldVer < DB_VERSION){
    			try {
    				SQLiteDatabase.releaseMemory();
				} catch (Exception e) {}
    			this.getWritableDatabase();

            	try {
        			copyDataBase();
        		} catch (IOException e) {
            		throw new Error("Error copying database");
            	}
    		}
    	}else{
    		
        	this.getWritableDatabase();

        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    	}
    }

    private boolean checkDataBase(){
    	SQLiteDatabase checkDB = null;

    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){
    	}
    	if(checkDB != null){
    		checkDB.close();
    	}
    	return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException{
    	if(pref != null){
    		pref.edit().putInt(App.PREF_DATA_BASE_VER, DB_VERSION).commit();
    	}
    	InputStream myInput = mContext.getAssets().open(DB_NAME);

    	String outFileName = DB_PATH + DB_NAME;

    	OutputStream myOutput = new FileOutputStream(outFileName);

    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}

    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }

    public void openDataBase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
    	db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    
    @Override
	public synchronized void close() {
    	    if(db != null)
    		    db.close();
    	    super.close();
	}
    
    public SQLiteDatabase getDb() {
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	public List<String> getBooks(String tableName){
		Log.d(TAG, "Start getting books");
		List<String> books = new ArrayList<String>();
		int j = 0;
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("SELECT "+FIELD_BOOK_ID+" FROM '"+tableName+"'", null);
			if(cursor.moveToFirst()){
				do{
					if(j != cursor.getInt(cursor.getColumnIndex(FIELD_BOOK_ID))){
						books.add(Tools.getBookNameByBookId(cursor.getInt(cursor.getColumnIndex(FIELD_BOOK_ID)), mContext));
						j = cursor.getInt(cursor.getColumnIndex(FIELD_BOOK_ID));
					}
				}while(cursor.moveToNext());
			}
		}
		Log.d(TAG, "END getting books");
		return books;
	}
	
	public List<Integer> getChapters(String tableName, int bookId){
		List<Integer> chapters = new ArrayList<Integer>();
		int count = 1;
		if(db.isOpen()){
			Cursor c = db.rawQuery("SELECT "+FIELD_BOOK_ID+", "+FIELD_POEM+" FROM '"+tableName+"'", null);
			if(c.moveToFirst()){
				do{
					if(c.getInt(c.getColumnIndex(FIELD_BOOK_ID)) == bookId)
						if(c.getInt(c.getColumnIndex(FIELD_POEM)) == 1){
							chapters.add(count);
							count++;
						}
					
				}while(c.moveToNext());
			}
		}
		
		return chapters;
	}
	
	public List<PoemStruct> getPoemsInChapter(int bookId, int chapter, String tableName){
		List<PoemStruct> poems = new ArrayList<PoemStruct>();
		Log.d(TAG, "Satart getting poem in " + Tools.getBookNameByBookId(bookId, mContext)+"chapter " + chapter);
		if(db.isOpen()){
			Log.d(TAG, "BaseOpen");
			Cursor cursor = db.rawQuery("SELECT "+FIELD_BOOK_ID+", "+FIELD_CHAPTER+", "+FIELD_CONTENT+" FROM '"+tableName+"'", null);
			if(cursor.moveToFirst()){
				do{
					if(cursor.getInt(cursor.getColumnIndex(FIELD_BOOK_ID)) == bookId){
						if(cursor.getInt(cursor.getColumnIndex(FIELD_CHAPTER)) == chapter){
							poems.add(new PoemStruct(cursor.getString(cursor.getColumnIndex(FIELD_CONTENT))));						
						}
					}
				}while(cursor.moveToNext());
			}
		}
    	
    	return poems;
    }
	
	public List<HashMap<String, String>> getPoemCompare(int bookId, int chapter, int poem){
		List<HashMap<String, String>> poems = new ArrayList<HashMap<String, String>>();
		Log.d(TAG, "Starting get poem in all translated");
		if(db.isOpen()){
			for(int i = 0; i < TABLE_NAMES.length; i++){
				Cursor c = db.rawQuery("SELECT * FROM '"+TABLE_NAMES[i]+"'", null);
				if(c.moveToFirst()){
					do{
						if(c.getInt(c.getColumnIndex(FIELD_BOOK_ID)) == bookId)
							if(c.getInt(c.getColumnIndex(FIELD_CHAPTER)) == chapter)
								if(c.getInt(c.getColumnIndex(FIELD_POEM)) == poem){
									HashMap<String, String> hs = new HashMap<String, String>();
									hs.put(App.POEM, c.getString(c.getColumnIndex(FIELD_CONTENT)));
									hs.put(App.TRANSLATE_LABEL, TABLE_NAMES[i]);
									poems.add(hs);
								}									
					}while(c.moveToNext());
				}
			}
		}
		
		Log.d(TAG, "END getting COMPARE POEMS");
		return poems;
	}
	
	public int getNumberOfPoemInChapter(int bookId, int chapter, String tableName){
		int count = 0;
		
		if(db.isOpen()){
			Log.d(TAG, "BaseOpen");
			Cursor cursor = db.rawQuery("SELECT "+FIELD_BOOK_ID+", "+FIELD_CHAPTER+" FROM '"+tableName+"'", null);
			if(cursor.moveToFirst()){
				do{
					if(cursor.getInt(cursor.getColumnIndex(FIELD_BOOK_ID)) == bookId)
						if(cursor.getInt(cursor.getColumnIndex(FIELD_CHAPTER)) == chapter){
							count++;						
						}
					
				}while(cursor.moveToNext());
			}
		}
		
		return count;
	}
	
	public int getNumberOfChapterInBook(int bookId, String tableName){
		int count = 0, chapter = 1;
		
		if(db.isOpen()){
			Log.d(TAG, "BaseOpen");
			Cursor cursor = db.rawQuery("SELECT "+FIELD_BOOK_ID+", "+FIELD_CHAPTER+" FROM '"+tableName+"'", null);
			if(cursor.moveToFirst()){
				do{
					if(cursor.getInt(cursor.getColumnIndex(FIELD_BOOK_ID)) == bookId)
						if(cursor.getInt(cursor.getColumnIndex(FIELD_CHAPTER)) == chapter){
							count++;
							chapter++;
						}
					
				}while(cursor.moveToNext());
			}
		}
		
		return count;
	}

    public String getPoem(int bookId, int chapter, int poem, String tableName){
    	String result = "";
    	
    	if(db.isOpen()){
    		Log.d(TAG, "Start DB for get Poem");
    		Cursor c = db.rawQuery("SELECT * FROM '"+tableName+"'", null);
    		if(c.moveToFirst()){
    			do{
    				if(c.getInt(c.getColumnIndex(FIELD_BOOK_ID)) == bookId)
						if(c.getInt(c.getColumnIndex(FIELD_CHAPTER)) == chapter)
							if(c.getInt(c.getColumnIndex(FIELD_POEM)) == poem){
							result = c.getString(c.getColumnIndex(FIELD_CONTENT));
						}
    			}while(c.moveToNext());
    		}    		
    	}    	
    	return result;
    }

    public void clearSearchResultHistory(){
    	if(db.isOpen()){
    		db.delete(TABLE_SEARCH_RESULT, null, null);
    	}
    }
    
    public List<SearchStruct> searchInDataBase(String request, int idBookStart, int idBookEnd){
    	Log.d(TAG, "Start func search in db");
    	List<SearchStruct> result = new ArrayList<SearchStruct>();
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
    	
    	String translateId = pref.contains(mContext.getString(R.string.pref_default_translaters))?
    			pref.getString(mContext.getString(R.string.pref_default_translaters), TABLE_NAME_RST)
    			: "0";
    			
    	String translate = translateId.equals(""+TRANSLATE_RST_ID)?TABLE_NAME_RST:TABLE_NAME_MT;
    			
		if(db.isOpen()){
			Log.d(TAG, "DB_is_open: " + db.isOpen() + " request: " + request);
			Cursor c = db.rawQuery("SELECT * FROM '"+translate+"' WHERE content LIKE '%"+request+"%' "+"ORDER BY `"+FIELD_BOOK_ID+"` ASC", null);

			if(c.moveToFirst()){
				do {
					int id = c.getInt(c.getColumnIndex(DataBase.FIELD_BOOK_ID));
					if((id) >= idBookStart && (id) <= idBookEnd){
						String bookName = c.getString(c.getColumnIndex(DataBase.FIELD_BOOK_NAME))==null?"null"
								:c.getString(c.getColumnIndex(DataBase.FIELD_BOOK_NAME));
						int chapter = c.getInt(c.getColumnIndex(DataBase.FIELD_CHAPTER));
						int poem = c.getInt(c.getColumnIndex(DataBase.FIELD_POEM));
						String content = c.getString(c.getColumnIndex(DataBase.FIELD_CONTENT))==null?"null"
								:c.getString(c.getColumnIndex(DataBase.FIELD_CONTENT));
						SearchStruct response = new SearchStruct();
						response.setBookName(bookName);
						response.setChapter(chapter);
						response.setContent(content);
						response.setIdBook(id);
						response.setPoem(poem);
						result.add(response);
					}
					
				} while (c.moveToNext());
			}
		}
    	
    	return result;
    }
    
    public void insertSearchResult(List<SearchStruct> dataResult){
    	Log.d(TAG, "INSERT search RESULT");
    	
    	if(db.isOpen()){
    		for(SearchStruct item : dataResult){
    			ContentValues data = new ContentValues();
    			
    			data.put(FIELD_BOOK_ID, item.getIdBook());
    			data.put(FIELD_BOOK_NAME, item.getBookName());
    			data.put(FIELD_CHAPTER, item.getChapter());
    			data.put(FIELD_POEM, item.getPoem());
    			data.put(FIELD_CONTENT, item.getContent());
    			
    			db.insert(TABLE_SEARCH_RESULT, null, data);
    		}    		
    	}
    }
    
    public List<SearchStruct> getSearchResult(){
    	List<SearchStruct> result = new ArrayList<SearchStruct>();
    	
    	if(db.isOpen()){
    		Log.d(TAG, "START - getSearchResult");
    		Cursor c = db.rawQuery("SELECT * FROM '"+TABLE_SEARCH_RESULT+"'", null);
    		if(c.moveToFirst()){
    			do{
    				String bookName = c.getString(c.getColumnIndex(FIELD_BOOK_NAME));
    				int bookId = c.getInt(c.getColumnIndex(FIELD_BOOK_ID));
    				int chapter = c.getInt(c.getColumnIndex(FIELD_CHAPTER));
    				int poem = c.getInt(c.getColumnIndex(FIELD_POEM));
    				String content = c.getString(c.getColumnIndex(FIELD_CONTENT));
    				
    				result.add(new SearchStruct(bookId, bookName, chapter, poem, content));
    			}while(c.moveToNext());
    		}
    	}
    	
    	return result;
    }
    
    public void setStatusItemReadForEveryDay(int index, boolean status){
    	if(db.isOpen()){
    		ContentValues values = new ContentValues();
			
			values.put(FIELD_STATUS_READED, String.valueOf(status));
			int idUpdateRow = db.update(TABLE_READ_FOR_EVERY_DAY, values, KEY_ROWID + " = " + (index+1), null);
			Log.d(TAG, "ROW Update: " + idUpdateRow);
    	}
    }
    
    public void setDefaultStatusItemRead(int countItems){
    	if(db.isOpen()){
    		ContentValues v = new ContentValues();
    		v.put(FIELD_STATUS_READED, String.valueOf(false));
    		for(int i = 0; i < countItems; i++){
    			db.update(TABLE_READ_FOR_EVERY_DAY, v, KEY_ROWID + " = " + (i+1), null);
    		}
    	}
    }
    
    public List<ItemReadDay> getListReadForEveryDay(){
    	List<ItemReadDay> result = new ArrayList<ItemReadDay>();
    	
    	if(db.isOpen()){
    		Log.d(TAG, "START - getListReadForEveryDay()");
    		Cursor c = db.rawQuery("SELECT * FROM '"+TABLE_READ_FOR_EVERY_DAY+"'", null);
    		if(c.moveToFirst()){
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
    				item.setListPoemOld(parseValueChapterBookIdString(chaptersOldTestament, bookNameOldTestament, bookIdOldTestament));
    				item.setListPoemNew(parseValueChapterBookIdString(chaptersNewTestament, bookNameNewTestament, bookIdNewTestament));
    				item.setContentChapterOldTFull(chaptersOldTestament);
    				item.setContentChapterNewTFull(chaptersNewTestament);
    				item.setStatus(status);
    				
    				result.add(item);
					
				} while (c.moveToNext());
    		}
    	}
    	
    	return result;
    }
    
    private List<PoemStruct> parseValueChapterBookIdString(String contentChapter, String bookName, String bookId){
    	List<PoemStruct> result = new ArrayList<PoemStruct>();
    	int countBook = 0;
    	List<String> bookNames = new ArrayList<String>();
    	List<Integer> bookIds = new ArrayList<Integer>();
    	
    	int t = 0;
    	for(int i = 0; i <= bookId.length(); i++){
    		if((i == bookId.length() 
    				&& ( i>=3 && (bookId.substring((i-2), (i-1)).equals("|") 
    						|| bookId.substring((i-3), (i-2)).equals("|")))) 
    				|| ( i != bookId.length() && bookId.substring(i, (i+1)).equals("|"))){
    			int bI = Integer.parseInt(bookId.substring(t, i));
    			bookIds.add(bI);
    			String name = Tools.getBookNameByBookId(bI, mContext);
    			bookNames.add(name);
    			t = i+1;
    			countBook++;
    		}
    		
    	}
    	if(countBook == 0){
    		int r = 0;
        	for(int i = 0; i <= contentChapter.length(); i++){
        		if(i == (contentChapter.length()) 
        				|| contentChapter.substring(i, (i+1)).equals(":")
        				|| contentChapter.substring(i, (i+1)).equals(",")){
        			PoemStruct item = new PoemStruct();
        			int chapter = Integer.parseInt(contentChapter.substring(r, i));
        			item.setChapter(chapter);
        			item.setBookName(bookName);
        			item.setBookId(Integer.parseInt(bookId));
        			boolean isNoStandart = false;
        			if(i != contentChapter.length() && contentChapter.substring(i, (i+1)).equals(":")){
        				for(int j = 0; j < contentChapter.length(); j++){
        					if(contentChapter.substring(j, (j+1)).equals("-")){
        						int poem = Integer.parseInt(contentChapter.substring((i+1), j));
        						item.setPoem(poem);
        						int poemTo = Integer.parseInt(contentChapter.substring((j+1), contentChapter.length()));
        						item.setPoemTo(poemTo);
        					}
        				}
        				isNoStandart = true;
        			}
        			result.add(item);
        			r = i+1;
        			if(isNoStandart)
        				break;
        		}
        	}
    	}
    	else
    	for(int k = 0; k < countBook; k++){
    		int r = 0;
        	for(int i = 0; i <= contentChapter.length(); i++){
        		if(i == (contentChapter.length()) 
        				|| contentChapter.substring(i, (i+1)).equals(":")
        				|| contentChapter.substring(i, (i+1)).equals("|")
        				|| contentChapter.substring(i, (i+1)).equals(",")){
        			PoemStruct item = new PoemStruct();
        			int chapter = Integer.parseInt(contentChapter.substring(r, i));
        			item.setChapter(chapter);
        			String name = bookNames.get(k);
        			item.setBookName(name);
        			int id = bookIds.get(k);
        			item.setBookId(id);
        			boolean isNoStandart = false;
        			if(i != contentChapter.length() && contentChapter.substring(i, (i+1)).equals(":")){
        				for(int j = 0; j < contentChapter.length(); j++){
        					if(contentChapter.substring(j, (j+1)).equals("-")){
        						int poem = Integer.parseInt(contentChapter.substring((i+1), j));
        						item.setPoem(poem);
        					}
        				}
        				isNoStandart = true;
        			}
        			result.add(item);
        			r = i+1;
        			if(isNoStandart)
        				break;
        		}
        		if(i != (contentChapter.length()) && contentChapter.substring(i, (i+1)).equals("|")){
        			contentChapter = contentChapter.substring((i+1), contentChapter.length());
        			break;
        		}
        	}
    	}
    	
    	return result;
    }
}
