package com.attribes.incommon.models;

import java.util.ArrayList;



public class InterestsModel {

	public Meta meta=new Meta();
	public ArrayList<Response> response = new ArrayList<Response>();
	//public SourceUser source_user = new SourceUser();
	
	
	public class Response{
		public String id;
		public String title;

        @Override
        public String toString() {
            return this.title;
        }
    }



    public class Meta{
		String message;
		String status;
	}
	
	
}
