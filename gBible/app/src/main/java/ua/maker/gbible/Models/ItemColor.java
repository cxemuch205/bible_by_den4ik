package ua.maker.gbible.Models;

public class ItemColor {
	
	private String hex = "";
	private int position = 1;
	private int idDB = 0;
	
	public ItemColor(){};
	
	public ItemColor(String color){
		this.hex = color;
	};
	
	public ItemColor(String hex, int position){
		this.hex = hex;
		this.position = position;
	};
	
	public void setHex(String hex) {
		this.hex = hex;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public String getHex() {
		return hex;
	}
	
	public int getPosition() {
		return position;
	}

	public int getIdDB() {
		return idDB;
	}

	public void setIdDB(int idDB) {
		this.idDB = idDB;
	}
}
