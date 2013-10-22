package ua.maker.gbible.structs;

public class BookMarksStruct {
	
	private String tableName = "";
	private String bookName = "";
	private String content = "";
	private int bookId = 1;
	private int chapter = 1;
	private int poem = 1;
	private int id = 0;
	
	
	public BookMarksStruct(){}
	
	public BookMarksStruct(String tableName, String bookName, String content, int bookId, int chapter, int poem, int id){
		this.tableName = tableName;
		this.bookName = bookName;
		this.content = content;
		this.bookId = bookId;
		this.chapter = chapter;
		this.poem = poem;
		this.id = id;
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
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
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
	
	public String getTableName() {
		return tableName;
	}
	
	public String getContent() {
		return content;
	}
}
