package ua.maker.gbible.utils;

public class ColorStruct {
	
	private String hex = "";
	private int position = 1;
	
	public ColorStruct(){};
	
	public ColorStruct(String hex, int position){
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
}
