package ua.maker.gbible.structs;

public class PoemStruct {
	
	private String text = "";
	private boolean checked = false;
	
	public PoemStruct(){};
	
	public PoemStruct(String text){
		this.text = text;
	};
	
	public PoemStruct(String text, boolean checked){
		this.text = text;
		this.checked = checked;
	};
	
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
