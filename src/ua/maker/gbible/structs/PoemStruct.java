package ua.maker.gbible.structs;

public class PoemStruct {
	
	private String content = "";
	private boolean checked = false;
	private String bookName = "";
	private int bookId = 1;
	private int chapter = 1;
	private int poem = 1;
	private int poemTo = 0;
	private String colorHEX = "";
	private int posColor;
	private String translateSource = "";
	
	public PoemStruct(){};
	
	public PoemStruct(String text){
		this.content = text;
	};
	
	public PoemStruct(String text, boolean checked){
		this.content = text;
		this.checked = checked;
	};
	
	public PoemStruct(String content, String bookName, int chapter){
		this.content = content;
		this.bookName = bookName;
		this.chapter = chapter;
	};
	
	public PoemStruct(String content, String bookName, int chapter, int poem){
		this.content = content;
		this.bookName = bookName;
		this.chapter = chapter;
		this.poem = poem;
	};
	
	public void setPosColor(int posColor) {
		this.posColor = posColor;
	}
	
	public int getPosColor() {
		return posColor;
	}
	
	public String getColorHEX() {
		return colorHEX;
	}
	
	public PoemStruct setColorHEX(String colorHEX) {
		this.colorHEX = colorHEX;
		return this;
	}
	
	public int getPoemTo() {
		return poemTo;
	}
	
	public PoemStruct setPoemTo(int poemTo) {
		this.poemTo = poemTo;
		return this;
	}
	
	public int getBookId() {
		return bookId;
	}
	
	public PoemStruct setBookId(int bookId) {
		this.bookId = bookId;
		return this;
	}
	
	public int getPoem() {
		return poem;
	}
	
	public PoemStruct setPoem(int poem) {
		this.poem = poem;
		return this;
	}
	
	public String getBookName() {
		return bookName;
	}
	
	public int getChapter() {
		return chapter;
	}
	
	public PoemStruct setBookName(String bookName) {
		this.bookName = bookName;
		return this;
	}
	
	public PoemStruct setChapter(int chapter) {
		this.chapter = chapter;
		return this;
	}
	
	public String getContent() {
		return content;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public PoemStruct setContent(String text) {
		this.content = text;
		return this;
	}
	
	public PoemStruct setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}

	public String getTranslateSource() {
		return translateSource;
	}

	public void setTranslateSource(String translateSource) {
		this.translateSource = translateSource;
	}
}
