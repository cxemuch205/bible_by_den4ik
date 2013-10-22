package ua.maker.gbible.utils;

import java.util.ArrayList;
import java.util.List;

import ua.maker.gbible.structs.BookMarksStruct;
import ua.maker.gbible.structs.ItemPlanStruct;
import ua.maker.gbible.structs.PlanStruct;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDB extends SQLiteOpenHelper{
	
	private static final String TAG = "UserDB";
	
	private static final String DB_NAME = "user_data.db";
	private static final int DB_VERSION = 1;
	
	private static final String TABLE_PLAN_LIST = "user_plan_list";
	private static final String TABLE_PLAN_DATA = "item_plan_data";
	private static final String TABLE_BOOKMARKS = "bookmarks_data";
	
	public static final String FIELD_ID = "_id";
	public static final String FIELD_PLAN_ID = "id_plan";
	public static final String FIELD_NAME = "name_plan";
	public static final String FIELD_SUB_DESCRIPTION = "sub_description";
	public static final String FIELD_DATE = "date_create";
	
	public static final String FIELD_BOOK_ID = "bookId";
	public static final String FIELD_BOOK_NAME = "bookName";
	public static final String FIELD_TABLE_NAME = "tableName";
	public static final String FIELD_CHAPTER = "chapter";
	public static final String FIELD_POEM = "poem";
	public static final String FIELD_TO_POEM = "topoem";
	public static final String FIELD_CONTENT = "content";
	
	public static final String FIELD_TYPE_DATA = "type_data";
	public static final String FIELD_PATH_IMG = "path_img";
	
	private static final String SQL_CREATE_TABLE_PLANS_LIST = "CREATE TABLE " + TABLE_PLAN_LIST + " ("
			+ FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FIELD_NAME + " TEXT,"
			+ FIELD_SUB_DESCRIPTION + " TEXT,"
			+ FIELD_DATE + " TEXT);";
	
	private static final String SQL_CREATE_TABLE_PLAN_DATA = "CREATE TABLE " + TABLE_PLAN_DATA + " ("
			+ FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FIELD_PLAN_ID + " INTEGER NOT NULL,"
			+ FIELD_TYPE_DATA +" INTEGER NOT NULL,"
			+ FIELD_PATH_IMG + " TEXT NOT NULL,"
			+ FIELD_CONTENT + " TEXT NOT NULL,"
			+ FIELD_TABLE_NAME + " TEXT NOT NULL,"
			+ FIELD_BOOK_ID + " INTEGER NOT NULL,"
			+ FIELD_BOOK_NAME + " TEXT NOT NULL,"
			+ FIELD_CHAPTER + " INTEGER NOT NULL,"
			+ FIELD_POEM + " INTEGER NOT NULL,"
			+ FIELD_TO_POEM + " INTEGER NOT NULL);";
	
	private static final String SQL_CREATE_TABLE_BOOKMARKS_DATA = "CREATE TABLE " + TABLE_BOOKMARKS + " ("
			+ FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FIELD_TABLE_NAME + " TEXT,"
			+ FIELD_BOOK_ID + " INTEGER,"
			+ FIELD_BOOK_NAME + " TEXT,"
			+ FIELD_CHAPTER + " INTEGER,"
			+ FIELD_POEM + " INTEGER,"
			+ FIELD_CONTENT + " TEXT);";
	
	private Context context = null;
	private SQLiteDatabase db = null;
	
	public UserDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		db = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_PLANS_LIST);
		db.execSQL(SQL_CREATE_TABLE_BOOKMARKS_DATA);
		db.execSQL(SQL_CREATE_TABLE_PLAN_DATA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.delete(TABLE_PLAN_LIST, null, null);
		db.delete(TABLE_BOOKMARKS, null, null);
		db.delete(TABLE_PLAN_DATA, null, null);
		onCreate(db);
	}
	
	public void insertPlan(PlanStruct data){
		Log.d(TAG, "Start insert list plan to db");
		
		if(db.isOpen()){
			ContentValues values = new ContentValues();
			
			values.put(FIELD_NAME, ""+data.getName());
			values.put(FIELD_SUB_DESCRIPTION, ""+data.getSubDescription());
			values.put(FIELD_DATE, ""+data.getDate());
			
			long idINsertData = db.insert(TABLE_PLAN_LIST, null, values);
			Log.d(TAG, "END write list plans to db | long id inser: " + idINsertData);
		}
	}
	
	public void updatePlan(PlanStruct data){
		Log.d(TAG, "insertPlan() - id: " + data.getId());
		
		if(db.isOpen()){
			ContentValues values = new ContentValues();
			
			values.put(FIELD_NAME, data.getName());
			values.put(FIELD_SUB_DESCRIPTION, data.getSubDescription());
			values.put(FIELD_DATE, data.getDate());
			int idUpdateRow = db.update(TABLE_PLAN_LIST, values, FIELD_ID + " = " + data.getId(), null);
			Log.d(TAG, "ROW Update: " + idUpdateRow);
		}
	}
	
	public List<PlanStruct> getPlansList(){
		Log.d(TAG, "START - getPlansList()");
		List<PlanStruct> result = new ArrayList<PlanStruct>();
		
		if(db.isOpen()){
			Cursor c = db.query(TABLE_PLAN_LIST, null, null, null, null, null, null);
			if(c.moveToFirst()){
				int nameId = c.getColumnIndex(FIELD_NAME);
				int subDescriptionId = c.getColumnIndex(FIELD_SUB_DESCRIPTION);
				int dateId = c.getColumnIndex(FIELD_DATE);
				int idId = c.getColumnIndex(FIELD_ID);
				
				do{
					String name = ""+c.getString(nameId);
					String subDescription = ""+c.getString(subDescriptionId);
					String date = ""+c.getString(dateId);
					int id = c.getInt(idId);
					
					result.add(new PlanStruct(name, subDescription, date, id));
				}while(c.moveToNext());
			}
		}
		
		return result;
	}
	
	public PlanStruct getPlanById(int idPlan){
		PlanStruct result = new PlanStruct();
		if(db.isOpen())
		{
			Cursor c = db.rawQuery("SELECT * FROM '"+TABLE_PLAN_LIST+"'", null);
			if(c.moveToFirst()){
				do {
					int idPl = c.getInt(c.getColumnIndex(FIELD_ID));
					if(idPl == idPlan){
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
	
	public void deletePlan(int id){
		Log.d(TAG, "DELETE plan id: " + id);
		if(db.isOpen()){
			db.delete(TABLE_PLAN_LIST, FIELD_ID + " = " + id, null);
			db.delete(TABLE_PLAN_DATA, FIELD_PLAN_ID + " = " + id, null);
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
    		values.put(FIELD_BOOK_NAME, Tools.getBookNameByBookId(bookId, this.context));
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
        		values.put(FIELD_BOOK_NAME, Tools.getBookNameByBookId(item.getBookId(), this.context));
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
    				int id = c.getInt(c.getColumnIndex(FIELD_ID));
    				
    				result.add(new BookMarksStruct(tableName, bookName, content, bookId, chapter, poem, id));
    			}while(c.moveToNext());
    		}
    	}
    	
    	return result;
    }
	
	public void deleteBookmark(int idBookMarks){
    	Log.d(TAG, "DElete bookmark: id: "+idBookMarks);
    	if(db.isOpen()){
    		db.delete(TABLE_BOOKMARKS, FIELD_ID + " = " + idBookMarks, null);
    	}
    }
	
	public void insertItemPlan(ItemPlanStruct data){
		if(db.isOpen()){
			ContentValues cv = new ContentValues();
			
			cv.put(FIELD_PLAN_ID, data.getId());
			cv.put(FIELD_TYPE_DATA, data.getDataType());
			cv.put(FIELD_CONTENT, ""+data.getText());
			cv.put(FIELD_TABLE_NAME, ""+data.getTranslate());
			cv.put(FIELD_BOOK_ID, data.getBookId());
			cv.put(FIELD_BOOK_NAME, ""+data.getBookName());
			cv.put(FIELD_CHAPTER, data.getChapter());
			cv.put(FIELD_POEM, data.getPoem());
			cv.put(FIELD_TO_POEM, data.getToPoem());
			cv.put(FIELD_PATH_IMG, ""+data.getPathImg());
			
			db.insert(TABLE_PLAN_DATA, null, cv);
		}
	}
	
	public void insertPlanData(List<ItemPlanStruct> data){
		if(db.isOpen()){
			for(int i = 0; i < data.size(); i++){
				ItemPlanStruct item = data.get(i);
				ContentValues cv = new ContentValues();
				
				cv.put(FIELD_PLAN_ID, item.getId());
				cv.put(FIELD_TYPE_DATA, item.getDataType());
				cv.put(FIELD_CONTENT, ""+item.getText());
				cv.put(FIELD_TABLE_NAME, ""+item.getTranslate());
				cv.put(FIELD_BOOK_ID, item.getBookId());
				cv.put(FIELD_BOOK_NAME, ""+item.getBookName());
				cv.put(FIELD_CHAPTER, item.getChapter());
				cv.put(FIELD_POEM, item.getPoem());
				cv.put(FIELD_TO_POEM, item.getToPoem());
				cv.put(FIELD_PATH_IMG, ""+item.getPathImg());
				
				db.insert(TABLE_PLAN_DATA, null, cv);
			}
		}
	}
	
	public void deleteItemPlan(int idItemPlan){
		Log.d(TAG, "Id item plan: " + idItemPlan);
		if(db.isOpen()){
			db.delete(TABLE_PLAN_DATA, FIELD_ID + " = " + idItemPlan, null);
		}
	}
	
	public List<ItemPlanStruct> getItemsPlanById(int idPlan_request){
		List<ItemPlanStruct> result = new ArrayList<ItemPlanStruct>();
		Log.d(TAG + " getItemsPlanById()", "Id plan: " + idPlan_request);
		if(db.isOpen()){
			Cursor c = db.rawQuery("SELECT * FROM '" + TABLE_PLAN_DATA + "'", null);
			if(c.moveToFirst()){
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
					if(idPlan == idPlan_request){
						int idItem = 0; idItem = c.getInt(idIdItem);
						int typeData = 0; typeData = c.getInt(idTypeData);
						String text = ""; text = c.getString(idText);
						String translate = ""; translate = c.getString(idTranslate);
						int bookId = 0; bookId = c.getInt(idBookId);
						String bookName = ""; bookName = c.getString(idBookName);
						int chapter = 0; chapter = c.getInt(idChapter);
						int poem = 0, toPoem = 0; poem = c.getInt(idPoem);
						toPoem = c.getInt(idToPoem);
						String pathImg = ""; pathImg = c.getString(idPathImg);
						
						ItemPlanStruct plan = new ItemPlanStruct();
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
}
