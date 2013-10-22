package ua.maker.gbible.structs;

public class PlanStruct {
	
	private String name = "";
	private String subDescription = "";
	private String date = "";
	private int id = 1;
	
	public PlanStruct() {}
	
	public PlanStruct(String name, String date){
		this.name = name;
		this.date = date;
	}
	
	public PlanStruct(String name, String subDescription, String date){
		this.name = name;
		this.date = date;
		this.subDescription = subDescription;
	}
	
	public PlanStruct(String name, String subDescription, String date, int id){
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
