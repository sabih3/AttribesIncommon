package com.attribes.incommon.api;

import com.attribes.incommon.util.Constants;

public class RequestAddFriend {

	String user_id;
	String sm_token;
	String authorization = Constants.AUTHORIZATION;
	
	public RequestAddFriend(String user_id,String sm_token,
	String authorization){
		
		this.user_id = user_id;
		this.sm_token = sm_token;
		this.authorization = authorization;
	}
	
	
	public String getUser_id() {
		return user_id;
	}
	public String getSm_token() {
		return sm_token;
	}
	public String getAuthorization() {
		return authorization;
	}
	
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
	public void setSm_token(String sm_token) {
		this.sm_token = sm_token;
	}
	
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	
	
	
}
