package com.attribes.incommon.models;

import java.util.ArrayList;



public class NotificationResponse {

//	public String id;
//	public String notification_type;
//	public SourceUser  source_user;
//	String target_user;
//	String is_read;
//	String time_logged;
	
	public Meta meta=new Meta();
	public ArrayList<Response> response = new ArrayList<Response>();
	//public SourceUser source_user = new SourceUser();
	
	
	public class Response{
		public String id;
		public String notification_type;
		public String action;
		public SourceUser source_user;
		public String target_user;
		public String is_read;
		public String time_logged;
	}
	
	public class SourceUser{
		public String id;
		public String installation_id;
		public String date_added;
		public String image_uri;
		public String description;
		public String age;
		public String name;
		public String longitude;
		public String latitude;
		public String sm_type;
		public String uuid;
		public ArrayList<Interest> interests;
		
	 class Interest{
		String id;
		String title;
	}
}

	
	public class Meta{
		String message;
		String status;
	}
	
	
}


