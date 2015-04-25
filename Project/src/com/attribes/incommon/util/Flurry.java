package com.attribes.incommon.util;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;

public class Flurry {
	
	private static Flurry flurryInstance;
	
	private Flurry(){
		
	}
	
	public static Flurry getInstance(){
		if(flurryInstance == null){
			flurryInstance = new Flurry();
		}
		return flurryInstance;
	}
	

	public void eventSearchInterest(String interest){
		Map<String, String> searchParams = new HashMap<String, String>();
	
		searchParams.put("Searched Interest", interest);
		
		FlurryAgent.logEvent("Searched_Interest",searchParams);
	}
	
	public void eventMessageRead(){
		FlurryAgent.logEvent("Message_Read",true);
	}
	
	public void eventMessageSend(){
		FlurryAgent.logEvent("Message_Send",true);
	}
	
	public void eventLiked(){
		FlurryAgent.logEvent("User_Likes",true);
	}
	
	public void eventAddFriend(){
		FlurryAgent.logEvent("Friend_Add",true);
	}
	
	public void eventProfileEdit(){
		FlurryAgent.logEvent("Profile_Edited",true);
	}
	
	public void eventPreferencesUpdate(){
		FlurryAgent.logEvent("Preferences_Update",true);
	}
	
	public void eventShareEmail(){
		FlurryAgent.logEvent("Share_Email",true);
	}
	
	public void eventShareFB(){
		FlurryAgent.logEvent("Share_FB",true);
	}
	
	public void eventShareTwitter(){
		FlurryAgent.logEvent("Share_Twitter",true);
	}
	
	public void eventShareGoogle(){
		FlurryAgent.logEvent("Share_Google",true);
	}

	public void eventMatchReceived() {
		FlurryAgent.logEvent("Match_Received",true);
		
	}

	public void eventStarMatchReceived() {
		FlurryAgent.logEvent("StarMatch_Received",true);
		
	}
	
}
