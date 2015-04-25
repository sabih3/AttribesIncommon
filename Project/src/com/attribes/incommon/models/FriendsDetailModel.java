package com.attribes.incommon.models;

import java.util.ArrayList;


public class FriendsDetailModel {
	
	private static FriendsDetailModel _obj = null;

	public FriendsDetailModel() {

	}

	public static FriendsDetailModel getInstance() {
		if (_obj == null) {
			_obj = new FriendsDetailModel();
		}
		return _obj;
	}

	public void setList(FriendsDetailModel obj) { // new FriendsDetailModel();
		_obj = obj;
	}
	
	public Meta meta = new Meta();
	public Response response = new Response();
	
	public class Meta{
		public int status;
		public String message;
		
	}
	
	public class Response{
		public String id;
		public String sm_type;
		public String description;
		public String latitude;
		public String longitude;
		public String image_uri;
		public String name;
		public String distance;
		public String age;
		public String qb_id;
		public String relationship;
		public String is_login;
		public String liked;
		public String chat_login;
		
		public ArrayList<Interests> interests = new ArrayList<Interests>();
		public ArrayList<MatchedInterests> matched_interests = new ArrayList<MatchedInterests>();
		
		public ArrayList<MatchedInterests> getMatchedInterests(){
			return this.matched_interests;
		}
	}
	
	public class Interests{
		public String id;
		public String title;
	}
	public class MatchedInterests{
		public String id;
		public String title;
	}
}
