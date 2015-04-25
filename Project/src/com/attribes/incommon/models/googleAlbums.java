package com.attribes.incommon.models;

import java.util.ArrayList;



public class googleAlbums {

	private String id;
	
	public class feed{
		
		public String xmlns;
		public String xmlns$openSearch;
		public String xmlns$gphoto;
		public String xmlns$media;
		
		public ArrayList<entry> albums;
		
		public class entry{
			
			public class gphoto$numphotos{
				public String $t;
				
				public class media$group{
					
					public ArrayList<media$content> albums;
					
					public class media$content{
						
						public String url;
						public String type;
						public String medium;
					}
				}
			}
		}

	}
}
