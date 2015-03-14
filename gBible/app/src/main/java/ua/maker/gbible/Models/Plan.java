package ua.maker.gbible.Models;

public class Plan {
	
	private String name = "";
	private String subDescription = "";
	private String date = "";
	private int id = 1;
	
	public Plan() {}
	
	public Plan(String name, String date){
		this.name = name;
		this.date = date;
	}
	
	public Plan(String name, String subDescription, String date){
		this.name = name;
		this.date = date;
		this.subDescription = subDescription;
	}
	
	public Plan(String name, String subDescription, String date, int id){
		this.name = name;
		this.date = date;
		this.subDescription = subDescription;
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSubDescription(String subDescription) {
		this.subDescription = subDescription;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSubDescription() {
		return subDescription;
	}
	
	public int getId() {
		return id;
	}
}
