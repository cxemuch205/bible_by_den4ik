package ua.maker.gbible.utils;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class BibleContentProvider extends ContentProvider {
	
	private DataBase db;
	private static final UriMatcher uriMather;
	
	static final String AUTHORITY = "ua.maker.gbible.provider.BibleContent";
	static final String BOOKS_PATH = "books";
	
	static final int URI_BOOKS = 1;
	static final int URI_CHAPTER_IN_BOOK = 2;
	static final int URI_CHAPTER_CONTENT = 3;
	
	static{
		uriMather = new UriMatcher(UriMatcher.NO_MATCH);
		uriMather.addURI(AUTHORITY, BOOKS_PATH, URI_BOOKS);
		uriMather.addURI(AUTHORITY, BOOKS_PATH+"/#", URI_CHAPTER_IN_BOOK);
		uriMather.addURI(AUTHORITY, BOOKS_PATH+"/#/#", URI_CHAPTER_CONTENT);
	}

	@Override
	public boolean onCreate() {
		db = new DataBase(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if(db != null){			
			switch (uriMather.match(uri)) {
			case URI_BOOKS:				
				return db.getBooksCursor();
			case URI_CHAPTER_IN_BOOK:				
				return db.getChapterCursors(uri.getLastPathSegment());
			case URI_CHAPTER_CONTENT:		
				List<String> listSegm = uri.getPathSegments();
				return db.getContentChapters(listSegm.get(listSegm.size()-2), uri.getLastPathSegment());
			default:
				return db.getCursor(Tools.getTranslateWitchPreferences(getContext()), 
						projection, selection, selectionArgs, null, null, sortOrder);
			}
		}else
			return null;
	}

	@Override
	public String getType(Uri uri) {
		return "text/*";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {		
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
