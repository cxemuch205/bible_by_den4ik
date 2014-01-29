package ua.maker.gbible.structs;

import java.util.List;

public class ItemReadDay {
	
	private int day = 1;
	private String month = "";
	private int year = 2014;
	private List<PoemStruct> listPoemOld = null;
	private List<PoemStruct> listPoemNew = null;
	private boolean status = false; //false - no readed, true - readed
	private int row = 0;
	private int section = 0;
	private String contentChapterOldTFull = "";
	private String contentChapterNewTFull = "";
	
	public ItemReadDay(){};
	
	public ItemReadDay(int day, String month, int year, List<PoemStruct> dataOld, List<PoemStruct> dataNew){
		this.day = day;
		this.month = month;
		this.year = year;
		this.listPoemOld = dataOld;
		this.listPoemNew = dataNew;
	}
	
	public String getContentChapterNewTFull() {
		return contentChapterNewTFull;
	}
	
	public String getContentChapterOldTFull() {
		return contentChapterOldTFull;
	}
	
	public void setContentChapterNewTFull(String contentChapterNewTFull) {
		this.contentChapterNewTFull = contentChapterNewTFull;
	}
	
	public void setContentChapterOldTFull(String contentChapterOldTFull) {
		this.contentChapterOldTFull = contentChapterOldTFull;
	}
	
	public int getSection() {
		return section;
	}
	
	public void setSection(int section) {
		this.section = section;
	}
	
	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public int getDay() {
		return day;
	}
	
	public boolean isStatusReaded() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public List<PoemStruct> getListPoemNew() {
		return listPoemNew;
	}
	
	public List<PoemStruct> getListPoemOld() {
		return listPoemOld;
	}
	
	public String getMonth() {
		return month;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public void setListPoemNew(List<PoemStruct> listPoemNew) {
		this.listPoemNew = listPoemNew;
	}
	
	public void setListPoemOld(List<PoemStruct> listPoemOld) {
		this.listPoemOld = listPoemOld;
	}
	
	public void setMonth(String month) {
		this.month = month;
	}
	
	public void setYear(int year) {
		this.year = year;
	}

}
