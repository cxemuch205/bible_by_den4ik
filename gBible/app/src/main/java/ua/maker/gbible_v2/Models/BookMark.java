package ua.maker.gbible_v2.Models;

public class BookMark {
	
	private String tableName = "";
	private String bookName = "";
	private String content = "";
	private String comment = "";
	private String linkNext = "";
	private String dbxId = "";
	private int bookId = 1;
	private int chapter = 1;
	private int poem = 1;
	private int id = 0;
	
	
	public BookMark(){}
	
	public BookMark(
            String tableName,
            String bookName,
            String content,
            int bookId,
            int chapter,
            int poem,
            int id){
		this.tableName = tableName;
		this.bookName = bookName;
		this.content = content;
		this.bookId = bookId;
		this.chapter = chapter;
		this.poem = poem;
		this.id = id;
	}
	
	public BookMark(
            String tableName,
            String bookName,
            String content,
            int bookId,
            int chapter,
            int poem,
            int id,
            String comment,
            String linkNext){
		this.tableName = tableName;
		this.bookName = bookName;
		this.content = content;
		this.bookId = bookId;
		this.chapter = chapter;
		this.poem = poem;
		this.id = id;
		this.comment = comment;
		this.linkNext = linkNext;
	}

    public String getDbxId() {
        return dbxId;
    }

    public void setDbxId(String dbxId) {
        this.dbxId = dbxId;
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
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void setLinkNext(String linkNext) {
		this.linkNext = linkNext;
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
	
	public String getComment() {
		return comment;
	}
	
	public String getLinkNext() {
		return linkNext;
	}
}
