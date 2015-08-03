package com.attribes.incommon;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import android.widget.LinearLayout;
import android.widget.Toast;
import com.attribes.incommon.chat.core.PlayServicesHelper;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.NetworkChangeReceiver;
import com.attribes.incommon.views.CustomTextView;
import com.quickblox.users.model.QBUser;


public class SplashScreen extends BaseActivity implements QBSessionListener{

	public static final String PREFS_NAME = "registrationPreference";
	public static final String REGISTRATION_KEY = "registrationKey";
	//TODO: remove time out constant from here
	private static int SPLASH_TIMEOUT = 7000;
	private PlayServicesHelper playServicesHelper;
    private CustomTextView retryToConnect;
    private LinearLayout noInternetLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        
        ChatHandler.getInstance().addQbSessionListener(this);
    	setContentView(R.layout.activity_splash_screen);
        noInternetLayout=(LinearLayout)findViewById(R.id.splash_noInternet);
        retryToConnect= (CustomTextView) findViewById(R.id.splash_noInternetRetryText);
        retryToConnect.setOnClickListener(new RetryToConnectClickListener());
    	if(!isUserRegistered()){
    		
    		setUserImageUri("");
    		setUserFullName("");
    		setUserName("");
    		setUserAge(0);


            createSession();


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

         createSession();


     }


	}



    private void createSession() {
        boolean isInternetAvailable = checkConnectivity(this);

        if(isInternetAvailable){
            ChatHandler.getInstance().createSessionForNewUser();
        }
        else{

            noInternetLayout.setVisibility(View.VISIBLE);
            //showToast(R.string.no_internet_message);
        }

    }

    private void showToast(int string_id) {

        Toast.makeText(this,getResources().getString(string_id),Toast.LENGTH_LONG).show();
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

    private boolean checkConnectivity(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        return isConnected;
    }


	@Override
	protected void onRestart(){
		super.onRestart();
	}


    private class RetryToConnectClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if(noInternetLayout.getVisibility()==View.VISIBLE){
                noInternetLayout.setVisibility(View.GONE);
            }
            createSession();
        }
    }
}
