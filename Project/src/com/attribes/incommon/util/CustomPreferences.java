package com.attribes.incommon.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.attribes.incommon.models.QBUserModel.QBUser;
import com.google.gson.Gson;
import com.quickblox.chat.model.QBDialog;
/** This class puts and retrieves custom object in/from prefs
 * 
 * @author Sabih Ahmed
 * Date : 7th May 2015
 */
public class CustomPreferences {

	private Activity activity;
	
	public CustomPreferences(Activity activity){
		this.activity = activity;
		
	}
	public void putObjectInPrefs(Object object, String key) {
		SharedPreferences activityPreferences = activity.getPreferences(
				Activity.MODE_PRIVATE);
		Editor editor = activityPreferences.edit();

		Gson gson = new Gson();

		editor.putString(key, gson.toJson(object));
		
		editor.commit();
	}

	public <T> T getObjectFromPrefs(String key, Class<T> objectClass) {
		Gson gson = new Gson();
		SharedPreferences activityPreferences = activity.getPreferences(
				Activity.MODE_PRIVATE);

		String stringObject = activityPreferences.getString(key, null);

		T object = gson.fromJson(stringObject, objectClass);

		return object;
	}
	
	public <T> void putObjectListInPrefs(ArrayList<T> filteredDialogs,String key){
		Gson gson = new Gson();
		Set<String> objectStringSet = new HashSet<String>();
		SharedPreferences activityPreferences = activity.getPreferences(
				Activity.MODE_PRIVATE);
		Editor editor = activityPreferences.edit();
		
		if(filteredDialogs != null){
			int size = filteredDialogs.size();	
			for(int i = 0; i < size; i++){
				String objectString = gson.toJson(filteredDialogs.get(i));
				objectStringSet.add(objectString);
			}
			
			editor.putStringSet(key, objectStringSet);
			editor.commit();
		}
		
		
		
		
	}
	
	public <T> ArrayList<T> getObjectListFromPrefs(String key,Class<T> objectClass){
		Gson gson = new Gson();
		ArrayList<T> objectList=new ArrayList<T>();
		SharedPreferences activityPreferences=activity.getPreferences(Activity.MODE_PRIVATE);
		
		Set<String> objectStringSet = activityPreferences.getStringSet(key, null);
		
		if(objectStringSet != null){
			for(String s:objectStringSet){
				
				objectList.add(gson.fromJson(s, objectClass));
			}	
		}
		
		return objectList;
	}
	
	
}
