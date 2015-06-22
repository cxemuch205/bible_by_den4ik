package ua.maker.gbible_v2.Models;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.Helpers.Tools;

public class History {
	
	private String bookName = "";
	private String translate = "";
	private String dateCreate = "";
	private int bookId = 1;
	private int chapter = 1;
	private int poem = 1;
	private String createdMillis;

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat
            dateFormat = new SimpleDateFormat("dd/MMM/yyyy");

    public History() {
        bookName = GBApplication.bookName;
        bookId = GBApplication.bookId;
        chapter = GBApplication.chapterId + 1;
        poem = GBApplication.poem;
        translate = Tools.getTranslateIdWitchPreferences(GBApplication.getInstance());
        dateCreate = dateFormat.format(new Date());
    }

    public History(String dateCreate, String bookName, String translate, int bookId, int chapter, int poem){
		this.dateCreate = dateCreate;
		this.bookId = bookId;
		this.translate = translate;
		this.chapter = chapter;
		this.poem = poem;
		this.bookName = bookName;
	}
	
	public void setDateCreated(String dateCreate) {
		this.dateCreate = dateCreate;
	}
	
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
	
	public void setPoem(int poem) {
		this.poem = poem;
	}
	
	public void setTranslate(String translate) {
		this.translate = translate;
	}
	
	public int getBookId() {
		return bookId;
	}
	
	public String getBookName() {
		return bookName;
	}
	
	public int getChapter() {
		return chapter;
	}
	
	public int getPoem() {
		return poem;
	}
	
	public String getTranslate() {
		return translate;
	}
	
	public String getDateCreated() {
		return dateCreate;
	}

	public String getCreatedMillis() {
		return createdMillis;
	}

	public History setCreatedMillis(String createdMillis) {
		this.createdMillis = createdMillis;
		return this;
	}
}
