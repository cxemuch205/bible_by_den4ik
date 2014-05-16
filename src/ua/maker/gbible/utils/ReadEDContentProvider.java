package ua.maker.gbible.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class ReadEDContentProvider extends ContentProvider {
	
	static final String AUTHORITY = "ua.maker.gbible.provider.RfEDContent";
	static final String RED_PATH = "readforeveryday";
	
	public static final Uri RED_CONTENT_URI = Uri.parse("content://"
		      + AUTHORITY + "/" + RED_PATH);

	private DataBase db;	
	
	@Override
	public boolean onCreate() {
		db = new DataBase(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return db.getReadEDCursor();
	}

	@Override
	public String getType(Uri uri) {
		return "text/*";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if(values.containsKey(DataBase.FIELD_STATUS_READED)){
			try {
				int item = Integer.parseInt(uri.getLastPathSegment());
				if(item < 365){
					long res = db.setStatusItemReadForEveryDay(item, values.getAsBoolean(DataBase.FIELD_STATUS_READED));
					Uri result = ContentUris.withAppendedId(RED_CONTENT_URI, res);
					return result;
				}else
					return null;
			} catch (Exception e) {return null;}
		}else
			return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {		
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {		
		return 0;
	}

}
