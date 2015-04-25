package com.attribes.incommon.models;

public class Album {

	String id;
	String name;
	String count;
	String cover_photo;
	
	public Album(String id, String name, String count,String cover_photo){
		this.id = id;
		this.name = name;
		this.count = count;
		this.cover_photo = cover_photo;
		
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCount() {
		return count;
	}
	
	public String getCoverPhoto(){
		return cover_photo;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCount(String count) {
		this.count = count;
	}
	
	public void setCoverPhoto(String cover_photo){
		this.cover_photo = cover_photo;
	}
	
}
