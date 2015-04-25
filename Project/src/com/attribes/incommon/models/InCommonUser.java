package com.attribes.incommon.models;

import java.util.ArrayList;

public class InCommonUser {

	public Meta meta = new Meta();
	public Response response = new Response();
	
	public class Meta{
		public String status;
		public String message;
	}
	
	public class Response{
	public String id;
	public String installation_id;
	public String name;
	public String description;
	public String age;
	public String gender;
	public String latitude;
	public String longitude;
	public String qb_id;
	public String image_uri;
	public ArrayList<Interest> interests;
	public Preferences preferences;
	
	}
	
	public class Interest{
		public String id;
		public String title;
	}
	
	public class Preferences{
		public String search_age;
		public String search_distance;
		public String search_gender;
		public String show_alerts;
		public String show_matches;
		public String show_messages;
		public String search_min_age;
		public String search_max_age;
		
	}
}
