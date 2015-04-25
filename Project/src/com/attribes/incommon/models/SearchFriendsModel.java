package com.attribes.incommon.models;

import java.util.ArrayList;





public class SearchFriendsModel {
	
	private static SearchFriendsModel _obj = null;

	public SearchFriendsModel() {

	}

	public static SearchFriendsModel getInstance() {
		if (_obj == null) {
			_obj = new SearchFriendsModel();
		}
		return _obj;
	}

	public void setList(SearchFriendsModel obj) {
		_obj = obj;
	}
	
	public Meta meta = new Meta();
	public Response response = new Response();
	
	public class Meta{
		public int status;
		public String message;
		
	}
	
	
	public class Response{
		public ArrayList<SearchFriendsModel.User> users;
		public String interest;
		
	}
	
	public class User{

		public String id;
		public String uuid;
		public String installation_id;
		public String device_type;
		public String sm_type;
		public String name;
		public String description;
		public String age;
		public String latitude;
		public String longitude;
		public String qb_id;
		public String image_uri;
		public String date_added;
		public String distance;
		public String sm_token;
		public String authorization;
		public String is_login;
		public ArrayList<Interest> interests;
		
		public class Interest{
			String id;
			String title;
			
		}
	}
}
