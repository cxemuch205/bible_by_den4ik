package ua.maker.gbible.structs;

public class PoemStruct {
	
	private String text = "";
	private boolean checked = false;
	private String bookName = "";
	private int bookId = 1;
	private int chapter = 1;
	private int poem = 1;
	private int poemTo = 0;
	
	public PoemStruct(){};
	
	public PoemStruct(String text){
		this.text = text;
	};
	
	public PoemStruct(String text, boolean checked){
		this.text = text;
		this.checked = checked;
	};
	
	public PoemStruct(String content, String bookName, int chapter){
		this.text = content;
		this.bookName = bookName;
		this.chapter = chapter;
	};
	
	public PoemStruct(String content, String bookName, int chapter, int poem){
		this.text = content;
		this.bookName = bookName;
		this.chapter = chapter;
		this.poem = poem;
	};
	
	public int getPoemTo() {
		return poemTo;
	}
	
	public void setPoemTo(int poemTo) {
		this.poemTo = poemTo;
	}
	
	public int getBookId() {
		return bookId;
	}
	
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	public int getPoem() {
		return poem;
	}
	
	public void setPoem(int poem) {
		this.poem = poem;
	}
	
	public String getBookName() {
		return bookName;
	}
	
	public int getChapter() {
		return chapter;
	}
	
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
