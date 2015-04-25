package com.attribes.incommon;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.attribes.incommon.chat.core.PlayServicesHelper;
import com.attribes.incommon.models.MasterUser;
import com.quickblox.users.model.QBUser;


public class SplashScreen extends BaseActivity implements QBSessionListener{

	public static final String PREFS_NAME = "registrationPreference";
	public static final String REGISTRATION_KEY = "registrationKey";
	//TODO: remove time out constant from here
	private static int SPLASH_TIMEOUT = 7000;
	private PlayServicesHelper playServicesHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        
        ChatHandler.getInstance().addQbSessionListener(this);
    	setContentView(R.layout.activity_splash_screen);
    	
    	if(!isUserRegistered()){
    		
    		setUserImageUri("");
    		setUserFullName("");
    		setUserName("");
    		setUserAge(0);
    		
    		ChatHandler.getInstance().createSessionForNewUser();
    	}
    	
    	MasterUser.getInstance().setUserSmToken(getSmToken());
    	MasterUser.getInstance().setUserId(getUserIdFromApi());
    	MasterUser.getInstance().setUserFullName(getUserFullName());
    	MasterUser.getInstance().setUserName(getUserName());
    	MasterUser.getInstance().setUserAge(Integer.toString(getUserAge()));
    	MasterUser.getInstance().setUserDescription(getDescription());
    	MasterUser.getInstance().setUserImageUri(getUserImageUri());
//    	MasterUser.getInstance().setUserQbId(getQbUserId());
//    	MasterUser.getInstance().setUserQbLogin(getQbUserLogin());
//    	MasterUser.getInstance().setUserQbPassword(getQbUserPassword());
    	MasterUser.getInstance().setUserParseObjectId(MasterUser.getInstance().getParseObjectId());

    	
     if(isUserRegistered()){
    	 QBUser user=new QBUser();
    	 user.setId(Integer.parseInt(MasterUser.getInstance().getUserQbId()));
    	 user.setLogin(MasterUser.getInstance().getUserQbLogin());
    	 user.setPassword(MasterUser.getInstance().getUserQbPassword());
    	 
    	 ChatHandler.getInstance().createSessionForNewUser();
    	 //ChatHandler.getInstance().createSessionForExistingUser(user, this);
     }
//    if(!MasterUser.getInstance().isQbInitilized()){
//    		//ChatHandler.QBInit();
//    	ChatHandler.getInstance();
//    }

	}
	
	@Override
	public void sessionCreated() {
		if(isUserRegistered() && !(getSmToken().isEmpty())){
			MasterUser.getInstance().setUserInterests(fetchUserInterests());

			Intent connectScreen=new Intent(SplashScreen.this,ConnectScreen.class);
			startActivity(connectScreen);
			overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_in);
			finish();
			
		}
		
		else{
			Intent loginIntent = new Intent(SplashScreen.this,MainActivity.class);
			startActivity(loginIntent);
			overridePendingTransition(R.anim.anim_left_in, R.anim.anim_left_out);
			finish();
			
					
		}
		
	}
	
//public void userDetail(String url, String json, AjaxStatus status) {
//	
//	if(json != null){
//		//populate model class
//		Gson gson = new Gson();
//		InCommonUser obj = new InCommonUser();
//		try {
//		
//			obj = gson.fromJson(json.toString(), InCommonUser.class);
//			ArrayList<String> myInterests = new ArrayList<String>();
//			for(Interest interest : obj.response.interests){
//				myInterests.add(interest.title);
//			}
////			MasterUser.getInstance().setUserInterests(myInterests);
////			Intent connectScreen=new Intent(SplashScreen.this,ConnectScreen.class);
////			startActivity(connectScreen);
////			overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_in);
////			finish();
////			
////			MasterUser.getInstance().setUserInterests(fetchUserInterests());
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.toString();
//		}
//	}
	
	
	@Override
	protected void onRestart(){
		super.onRestart();
	}
}
