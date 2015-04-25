package com.attribes.incommon; 

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * This utility class is used for JSON handling
 * @author arsalan.ahmed
 *
 */
public class GsonUtility {

	
	/**
	 * Get object of specified 'type' against the provided 'jsonString' string
	 * @param <T> Object of T
	 * @param 
	 * @param type 
	 * @return Object 
	 * @throws Exception
	 */
	public static <T> T getObjectFormJsonString(String jsonString, Class<T> type) throws Exception{
		try {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.serializeNulls();
			Gson gson = gsonBuilder.create();
			return (T) gson.fromJson(jsonString, type);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	
	 
	/**
	 * This method json string of the provided object
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static String getJsonStringOfObject(Object object)throws Exception{
		String str="";
		try{
			Gson gson = new Gson();
			String json = gson.toJson(object);
			str = json.toString();
		}
		catch(Exception e)
		{
			throw e;
		}
		return str;
	}
}
