package ua.maker.gbible.structs;

import ua.maker.gbible.constant.PlanData;
import android.graphics.Bitmap;

public class ItemPlanStruct {
	
	private int id = 0;
	private int idItem = 1;
	private String text = "";
	private String bookName = "";
	private String translate = "";
	private int bookId = 1;
	private int chapter = 1;
	private int poem = 1;
	private int toPoem = 1;
	private String pathImg = "";
	private Bitmap imgBitMap = null;
	
	private int dataType = 0;
	
	public ItemPlanStruct(){};
	
	public ItemPlanStruct(int id, String text){
		this.id = id;
		this.text = text;
		dataType = PlanData.DATA_TEXT;
	}
	
	public ItemPlanStruct(int id, String text, Bitmap bitMap){
		this.id = id;
		this.text = text;
		this.imgBitMap = bitMap;
		dataType = PlanData.DATA_TEXT_WITH_IMG;
	}
	
	public ItemPlanStruct(int id, String bookName, int bookId, int chapter, int poem, String text){
		this.id = id;
		this.bookId = bookId;
		this.bookName = bookName;
		this.chapter = chapter;
		this.poem = poem;
		this.text = text;
		dataType = PlanData.DATA_LINK_WITH_TEXT;
	}
	
	public ItemPlanStruct(int id, String bookName, int bookId, int chapter, int poem){
		this.id = id;
		this.bookId = bookId;
		this.bookName = bookName;
		this.chapter = chapter;
		this.poem = poem;
		dataType = PlanData.DATA_LINK;
	}
	
	public ItemPlanStruct(int id, String bookName, int bookId, int chapter, int poem, int toPoem){
		this.id = id;
		this.bookId = bookId;
		this.bookName = bookName;
		this.chapter = chapter;
		this.poem = poem;
		this.toPoem = toPoem;
		dataType = PlanData.DATA_LINK;
	}
	
	public ItemPlanStruct(int id, Bitmap bitMap){
		this.id = id;
		this.imgBitMap = bitMap;
		dataType = PlanData.DATA_IMG;
	}
	
	public int getIdItem() {
		return idItem;
	}
	
	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}
	
	public int getToPoem() {
		return toPoem;
	}
	
	public void setToPoem(int toPoem) {
		this.toPoem = toPoem;
	}
	
	public int getDataType() {
		return dataType;
	}
	
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	
	public void setTranslate(String translate) {
		this.translate = translate;
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
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setImgBitMap(Bitmap imgBitMap) {
		this.imgBitMap = imgBitMap;
	}
	
	public void setPathImg(String pathImg) {
		this.pathImg = pathImg;
	}
	
	public void setPoem(int poem) {
		this.poem = poem;
	}
	
	public void setText(String text) {
		this.text = text;
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
	
	public int getId() {
		return id;
	}
	
	public Bitmap getImgBitMap() {
		return imgBitMap;
	}
	
	public String getTranslate() {
		return translate;
	}
	
	public String getPathImg() {
		return pathImg;
	}
	
	public int getPoem() {
		return poem;
	}
	
	public String getText() {
		return text;
	}
}
