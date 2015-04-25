package com.attribes.incommon.DAO;

import java.util.ArrayList;

public abstract class User{

	
	
	public User(){
		
	}
	

	private String id;
	private String uuid;
	private String installation_id;
	private String device_type;
	private String sm_type;
	private String name;
	private String description;
	private String age;
	private String latitude;
	private String longitude;
	private String qb_id;
	private String image_uri;
	private ArrayList<Interest> interests;
	
	public String getId() {
		return id;
	}

	public String getUuid() {
		return uuid;
	}

	public String getInstallation_id() {
		return installation_id;
	}


	public String getDevice_type() {
		return device_type;
	}






	public String getSm_type() {
		return sm_type;
	}






	public String getName() {
		return name;
	}






	public String getDescription() {
		return description;
	}






	public String getAge() {
		return age;
	}






	public String getLatitude() {
		return latitude;
	}






	public String getLongitude() {
		return longitude;
	}






	public String getQb_id() {
		return qb_id;
	}






	public String getImage_uri() {
		return image_uri;
	}






	public ArrayList<Interest> getInterests() {
		return interests;
	}



	


	public void setId(String id) {
		this.id = id;
	}






	public void setUuid(String uuid) {
		this.uuid = uuid;
	}






	public void setInstallation_id(String installation_id) {
		this.installation_id = installation_id;
	}






	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}






	public void setSm_type(String sm_type) {
		this.sm_type = sm_type;
	}






	public void setName(String name) {
		this.name = name;
	}






	public void setDescription(String description) {
		this.description = description;
	}






	public void setAge(String age) {
		this.age = age;
	}






	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}






	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}






	public void setQb_id(String qb_id) {
		this.qb_id = qb_id;
	}






	public void setImage_uri(String image_uri) {
		this.image_uri = image_uri;
	}






	public void setInterests(ArrayList<Interest> interests) {
		this.interests = interests;
	}






	public class Interest{
		String id;
		String title;
		
	}






	
}
