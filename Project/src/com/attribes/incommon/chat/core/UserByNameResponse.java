package com.attribes.incommon.chat.core;

import java.util.ArrayList;

public class UserByNameResponse {

	
	public String current_page;
	public String per_page;
	public String total_entries;
	public ArrayList<Items> items;
	
	public class Items{
		public User user;
	}
	
	public class User {
		public String id;
	}
}
