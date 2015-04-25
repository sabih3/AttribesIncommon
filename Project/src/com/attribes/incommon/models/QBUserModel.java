package com.attribes.incommon.models;

public class QBUserModel {


	public QBUser qbuser=new QBUser();
	
	public class QBUser{
		String id; 
		String createdAt; 
		String updatedAt; 
		String fullName; 
		String  email; 
		String  login; 
		String phone; 
		String website;
		String lastRequestAt; 
		String externalId; 
		String facebookId; 
		String twitterId; 
		String blobId; 
		String tags; 
		String password; 
		String oldPassword; 
		String customData;
	}
}
