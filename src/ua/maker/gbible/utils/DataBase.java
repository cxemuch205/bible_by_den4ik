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
	private static final int DB_VERSION = 1;	
	@SuppressLint("SdCardPath")
	private static final String DB_PATH = "/data/data/ua.maker.gbible/databases/";
	private static final String DB_NAME = "bible_android.db";
	
	public static final String TABLE_NAME_RST = "rus_st";
	public static final String TABLE_NAME_MT = "rus_mt";
	public static final String TABLE_NAME_UAT = "ukr_t";
	
	public static final int TRANSLATE_RST_ID = 0;
	public static final int TRANSLATE_MT_ID = 1;
	public static final int TRANSLATE_UA_ID = 2;
	
	public static final String TABLE_SEARCH_RESULT = "search_history";
	
	public static final String FIELD_BOOK_ID = "bookId";
	public static final String FIELD_TRANSLATE = "translate";
	public static final String FIELD_BOOK_NAME = "bookName";
	public static final String FIELD_TABLE_NAME = "tableName";
	public static final String FIELD_CHAPTER = "chapter";
	public static final String FIELD_POEM = "poem";
	public static final String FIELD_CONTENT = "content";
	public static final String KEY_ROWID = "_id";
	
	public static final String[] TABLE_NAMES = {"rus_st",
												 "rus_mt"//,
												 /*"ukr_t"*/};
	
	private SQLiteDatabase db = null;
	private final Context mContext;

	public DataBase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
	}

	public void createDataBase() throws IOException{
    	boolean dbExist = checkDataBase();

    	if(dbExist){
    		//ничего не делать - база уже есть
    	}else{
    		//вызывая этот метод создаем пустую базу, позже она будет перезаписана
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
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
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
}
