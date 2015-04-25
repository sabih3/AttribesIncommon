package com.attribes.incommon.models;

import java.util.ArrayList;

public class MatchAllResponse {

	public Meta meta = new Meta();
	public ArrayList<Response> response = new ArrayList<MatchAllResponse.Response>();
	
	public class Meta{
		String status;
		String message;
	}
	
	public class Response{
		public String id;
		public String uuid;
		public String installation_id;
		public String device_type;
		public String sm_type;
		public String name;
		public String description;
		public String age;
		public String is_login;
		public String latitude;
		public String longitude;
		public String distance;
		public String qb_id;
		public String image_uri;
		public String date_added;
		public ArrayList<Interests> interests;
		public String is_read;
	}
	
	public class Interests{
		String id;
		String title;
	}
}
