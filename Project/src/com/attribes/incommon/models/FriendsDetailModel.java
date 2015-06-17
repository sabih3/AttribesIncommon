package com.attribes.incommon.models;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

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

	public static class Meta {
		public int status;
		public String message;

		public Meta() {

		}

	}

	public static class Response {
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

		public Response() {
			// TODO Auto-generated constructor stub
		}

		public ArrayList<MatchedInterests> getMatchedInterests() {
			return this.matched_interests;
		}

	}

	public static class Interests {
		public String id;
		public String title;

		public String toString(){
			return this.title;
			
		}
	}

	public static class MatchedInterests {
		public String id;
		public String title;

		public MatchedInterests(Parcel source) {
			String[] data = new String[3];

			this.id = data[0];
			this.title = data[1];
		}

	}

}
