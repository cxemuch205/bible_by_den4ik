package ua.maker.gbible.structs;

public class HistoryStruct {
	
	private String bookName = "";
	private String translate = "";
	private int bookId = 1;
	private int chapter = 1;
	private int poem = 1;
	
	public HistoryStruct(){}
	
	public HistoryStruct(String bookName, String translate, int bookId, int chapter, int poem){
		this.bookId = bookId;
		this.translate = translate;
		this.chapter = chapter;
		this.poem = poem;
		this.bookName = bookName;
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
}
