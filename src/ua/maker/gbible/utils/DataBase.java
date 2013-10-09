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
import ua.maker.gbible.structs.BookMarksStruct;
import ua.maker.gbible.structs.HistoryStruct;
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
	
	private static final int TRANSLATE_RST_ID = 0;
	private static final int TRANSLATE_MT_ID = 1;
	
	private static final String TABLE_HISTORY = "history_link";
	private static final String TABLE_BOOKMARKS = "bookmarks";
	private static final String TABLE_SEARCH_RESULT = "search_history";
	
	public static final String FIELD_BOOK_ID = "bookId";
	public static final String FIELD_TRANSLATE = "translate";
	public static final String FIELD_BOOK_NAME = "bookName";
	public static final String FIELD_TABLE_NAME = "tableName";
	public static final String FIELD_CHAPTER = "chapter";
	public static final String FIELD_POEM = "poem";
	public static final String FIELD_CONTENT = "content";
	private static final String KEY_ROWID = "_id";
	
	public static final String[] TABLE_NAMES = {"rus_st",
												 "rus_mt"};
	public static final String TRANSLATE_NAME_RST = "Русский Синодальный Перевод";
	public static final String TRANSLATE_NAME_MT = "Современный Русский Перевод";
	
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
			Cursor cursor = db.rawQuery("SELECT * FROM '"+tableName+"'", null);
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
		if(db.isOpen()){
			Cursor c = db.rawQuery("SELECT * FROM '"+tableName+"'", null);
			if(c.moveToFirst()){
				do{
					if(c.getInt(c.getColumnIndex(FIELD_BOOK_ID)) == bookId)
						if(c.getInt(c.getColumnIndex(FIELD_POEM)) == 1)
								chapters.add(c.getInt(c.getColumnIndex(FIELD_CHAPTER)));
					
				}while(c.moveToNext());
			}
		}
		
		return chapters;
	}
	
	public List<String> getPoemsInChapter(int bookId, int chapter, String tableName){
		List<String> poems = new ArrayList<String>();
		Log.d(TAG, "Satart getting poem in " + Tools.getBookNameByBookId(bookId, mContext)+"chapter " + chapter);
		if(db.isOpen()){
			Log.d(TAG, "BaseOpen");
			Cursor cursor = db.rawQuery("SELECT * FROM '"+tableName+"'", null);
			if(cursor.moveToFirst()){
				do{
					if(cursor.getInt(cursor.getColumnIndex(FIELD_BOOK_ID)) == bookId)
						if(cursor.getInt(cursor.getColumnIndex(FIELD_CHAPTER)) == chapter){
							poems.add(cursor.getString(cursor.getColumnIndex(FIELD_CONTENT)));						
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
			Cursor cursor = db.rawQuery("SELECT * FROM '"+tableName+"'", null);
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
			Cursor cursor = db.rawQuery("SELECT * FROM '"+tableName+"'", null);
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
    
    public void insertHistory(int bookId, int chapter, int poem, String translate){
    	Log.d(TAG, "insert to db History: bookId " + bookId 
    			+ " chapter " + chapter 
    			+ " poem " + poem);
    	
    	if(db.isOpen()){
    		ContentValues values = new ContentValues();
    		
    		values.put(FIELD_BOOK_NAME, Tools.getBookNameByBookId(bookId, mContext));
    		values.put(FIELD_BOOK_ID, bookId);
    		values.put(FIELD_CHAPTER, chapter);
    		values.put(FIELD_POEM, poem);
    		values.put(FIELD_TRANSLATE, translate);
    		
    		db.insert(TABLE_HISTORY, null, values);
    	}
    }
    
    public List<HistoryStruct> getHistory(){
    	List<HistoryStruct> result = new ArrayList<HistoryStruct>();
    	
    	if(db.isOpen()){
    		Log.d(TAG, "getHistory() - start");
    		Cursor c = db.rawQuery("SELECT * FROM '"+TABLE_HISTORY+"'", null);
    		if(c.moveToFirst()){
    			do{
    				int bookId = c.getInt(c.getColumnIndex(FIELD_BOOK_ID));
    				int chapter = c.getInt(c.getColumnIndex(FIELD_CHAPTER));
    				int poem = c.getInt(c.getColumnIndex(FIELD_POEM));
    				String bookName = c.getString(c.getColumnIndex(FIELD_BOOK_NAME));
    				String translate = c.getString(c.getColumnIndex(FIELD_TRANSLATE));
    				
    				result.add(0, new HistoryStruct(bookName, translate, bookId, chapter, poem));
    				
    			}while(c.moveToNext());
    		}
    	}
    	
    	return result;
    }
    
    public void clearHistory(){
    	
    	if(db.isOpen()){
    		db.delete(TABLE_HISTORY, null, null);
    	}
    }
    
    public void insertBookMark(String tableName, int bookId, int chapter, int poem, String content){
    	Log.d(TAG, "insert to db BookMarks: bookId " + bookId 
    			+ " chapter " + chapter 
    			+ " poem " + poem
    			+ " tableName " + tableName);
    	
    	if(db.isOpen()){
    		Log.d(TAG, "WRITE - WORK");
    		ContentValues values = new ContentValues();
    		
    		values.put(FIELD_TABLE_NAME, tableName);
    		values.put(FIELD_BOOK_ID, bookId);
    		values.put(FIELD_BOOK_NAME, Tools.getBookNameByBookId(bookId, mContext));
    		values.put(FIELD_CHAPTER, chapter);
    		values.put(FIELD_POEM, poem);
    		values.put(FIELD_CONTENT, content);
    		
    		db.insert(TABLE_BOOKMARKS, null, values);
    	}
    }
    
    public void insertBookMarks(List<BookMarksStruct> listBookMarks){
    	Log.d(TAG, "insert to db BookMarks: size " + listBookMarks.size());
    	
    	if(db.isOpen()){
    		
    		for(BookMarksStruct item : listBookMarks){
    			ContentValues values = new ContentValues();
        		
        		values.put(FIELD_TABLE_NAME, item.getTableName());
        		values.put(FIELD_BOOK_ID, item.getBookId());
        		values.put(FIELD_BOOK_NAME, Tools.getBookNameByBookId(item.getBookId(), mContext));
        		values.put(FIELD_CHAPTER, item.getChapter());
        		values.put(FIELD_POEM, item.getPoem());
        		values.put(FIELD_CONTENT, item.getContent());
        		
        		db.insert(TABLE_BOOKMARKS, null, values);
    		}    		
    	}
    }
    
    public List<BookMarksStruct> getBookMarks(){
    	List<BookMarksStruct> result = new ArrayList<BookMarksStruct>();
    	
    	if(db.isOpen()){
    		Log.d(TAG, "start get BookMarks()");
    		Cursor c = db.rawQuery("SELECT * FROM '"+TABLE_BOOKMARKS+"'", null);
    		if(c.moveToFirst()){
    			do{
    				String tableName = c.getString(c.getColumnIndex(FIELD_TABLE_NAME));
    				String bookName = c.getString(c.getColumnIndex(FIELD_BOOK_NAME));
    				int bookId = c.getInt(c.getColumnIndex(FIELD_BOOK_ID));
    				int chapter = c.getInt(c.getColumnIndex(FIELD_CHAPTER));
    				int poem = c.getInt(c.getColumnIndex(FIELD_POEM));
    				String content = c.getString(c.getColumnIndex(FIELD_CONTENT));
    				
    				result.add(new BookMarksStruct(tableName, bookName, content, bookId, chapter, poem));
    			}while(c.moveToNext());
    		}
    	}
    	
    	return result;
    }
    
    public void clearBookMarks(){
    	
    	if(db.isOpen()){
    		db.delete(TABLE_BOOKMARKS, null, null);
    	}
    }
    
    public void clearBookMark(int idBookMarks){
    	
    	if(db.isOpen()){
    		db.delete(TABLE_BOOKMARKS, KEY_ROWID + " = " + idBookMarks, null);
    	}
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
    	Log.d(TAG, "TRanslate: " + translate);
    			
		if(db.isOpen()){
			Log.d(TAG, "DB_is_open: " + db.isOpen() + " request: " + request);
			Cursor c = db.rawQuery("SELECT * FROM '"+translate+"' WHERE content LIKE '%"+request+"%' "+"ORDER BY `"+FIELD_BOOK_ID+"` ASC", null);
			
			if(c.moveToFirst()){
				do {
					int id = c.getInt(c.getColumnIndex(DataBase.FIELD_BOOK_ID));
					if((id-1) >= idBookStart && (id-1) <= idBookEnd){
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
