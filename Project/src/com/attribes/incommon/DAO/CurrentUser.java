package com.attribes.incommon.DAO;


public class CurrentUser extends User{

	private String sm_token;
	private CurrentUser user;
	private CurrentUser(){
		//TODO:needs to be populated using id or during creation
	
	}
	
	public CurrentUser getInstance(){
		
		if(user == null){
			
			user = new CurrentUser();
		}
		
	return user;
	}
	
	public String getSm_token() {
		return sm_token;
	}

	public void setSm_token(String sm_token) {
		this.sm_token = sm_token;
	}
	
	public CommonUser getAllFriends(){
		
		return null;
	}
	
	public boolean addFriend(CommonUser commonUser){
		//TODO:Logic need to be implemented
		
		
		return true;
	}
	
	public boolean isFriend(CommonUser commonUser){
		//TODO:Logic need to be implemented
		
		return true;
	}
	
	
}
