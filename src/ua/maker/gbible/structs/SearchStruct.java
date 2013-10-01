package ua.maker.gbible.structs;

public class SearchStruct {
	
	private int idBook = 1;
	private String bookName = "";
	private int chapter = 1;
	private int poem = 1;
	private String content = "";
	
	public SearchStruct(){}
	
	public SearchStruct(int idBook, String bookName, int chapter, int poem, String content){
		this.idBook = idBook;
		this.bookName = bookName;
		this.chapter = chapter;
		this.poem = poem;
		this.content = content;
	}
	
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
	
	public void setIdBook(int idBook) {
		this.idBook = idBook;
	}
	
	public void setPoem(int poem) {
		this.poem = poem;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getBookName() {
		return bookName;
	}
	
	public int getChapter() {
		return chapter;
	}
	
	public int getIdBook() {
		return idBook;
	}
	
	public int getPoem() {
		return poem;
	}
	
	public String getContent() {
		return content;
	}
}
