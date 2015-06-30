package com.attribes.incommon.models;

import java.util.ArrayList;

public class FriendAllResponse {
	
	
	public Meta meta = new Meta();
	public ArrayList<Response> response = new ArrayList<Response>();
	
	public class Meta{
		public String status;
		public String message;
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
		public String latitude;
		public String longitude;
		public String distance;
		public String qb_id;
		public String image_uri;
		public String is_login;
		public String date_added;
        public boolean selected;
		public ArrayList<Interests> interests;


        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
	
	
	
	public class Interests{
		public String id;
		public String title;
	}
	
	
	
	
}
