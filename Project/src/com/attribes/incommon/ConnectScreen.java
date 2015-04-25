
package com.attribes.incommon;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.api.ApiRequest;
import com.attribes.incommon.chat.core.PlayServicesHelper;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.GPSManager;
import com.parse.ParseInstallation;
import com.quickblox.users.model.QBUser;

public class ConnectScreen extends DrawerScreen implements QBSignInListener {

	private AQuery aQuery;
	private ProgressBar progressBar;
	private Button connectOtherButton;
	private com.quickblox.users.model.QBUser qbUser;
	private DrawerLayout mDrawer;
	private QBSignInListener qbSignInListener;
	private Boolean locationUpdateFlag = false;
	private double latitude;
	private double longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.activity_connect_screen);
		
		ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
		getLayoutInflater().inflate(R.layout.activity_connect_screen,content,true);
		setActionBarStyling();
		
		mDrawer = (DrawerLayout) findViewById(R.id.connect_drawer_layout);
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		
		initViews();
		
		show(progressBar);
		if(!isUserRegistered()){
			ChatHandler.getInstance().signUpQbUser(getSmToken(), getSmToken(),getUserName(), getUserImageUri(), this);
		}
		
		else{
			 QBUser user=new QBUser();
	    	 user.setId(Integer.parseInt(MasterUser.getInstance().getUserQbId()));
	    	 user.setLogin(MasterUser.getInstance().getUserQbLogin());
	    	 user.setPassword(MasterUser.getInstance().getUserQbPassword());
	    	 ChatHandler.getInstance().signInToQb(user, this);					
		}

		ChatHandler.getInstance().addQbSignInListener(this);
		
	
	
	}
	
	
	private void signInQB() {
		
		qbUser= new com.quickblox.users.model.QBUser();
		
		
		try{
			qbUser.setId(Integer.parseInt(getQbUserId()));
			qbUser.setLogin(getQbUserLogin());
			qbUser.setPassword(getQbUserPassword());
		
			ChatHandler.getInstance().signInToQb(qbUser,this);
		}
		catch(NumberFormatException e){
			
			signInQB();
		}
	}
	private void setCustomFont() {
		Typeface font_ProxiRegular = Typeface.createFromAsset(getAssets(), "fonts/Mark Simonson - Proxima Nova Regular.ttf");
		Typeface font_ProxiLight = Typeface.createFromAsset(getAssets(), "fonts/"+Constants.FONT_PROXI_LIGHT);
		
		aQuery.id(R.id.connect_with_others).typeface(font_ProxiLight);
		aQuery.id(R.id.connect).typeface(font_ProxiLight);
		aQuery.id(R.id.search_button).typeface(font_ProxiRegular);
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connect_screen_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void loggedInQBSuccessfully() {
		setUserRegistered();
		PlayServicesHelper playServicesHelper = new PlayServicesHelper(this);
		//playServicesHelper.checkPlayServices();
		
		ApiRequest api = new ApiRequest(ConnectScreen.this);
		api.requestUpdateUserParseInstallationId(MasterUser.getInstance(), ConnectScreen.this, "responseRequestUpdate");
		
		
		
		ConnectScreen.this.runOnUiThread(new Runnable() {
  		     @Override
		     public void run() {

	    	    progressBar.setVisibility(View.GONE);
 
				mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				initializeDrawer();
				getActionBar().setDisplayHomeAsUpEnabled(true);
  				
  		    	connectOtherButton.setOnClickListener(new OnClickListener() {
  					
  					@Override
  					public void onClick(View v) {
  						startActivity(new Intent(ConnectScreen.this, SearchFriends.class));
  						//finish();
  						
	
  					}
  				});
  		    	
		    }
		});
	
	}
	
	public void responseRequestUpdate(String url, String json, AjaxStatus status){
		if(json!=null){
			
//			startActivity(new Intent(ConnectScreen.this, SearchFriends.class));
//			finish();
//			setUserRegistered();
		}
	}
	
	private void initViews(){
		aQuery = new AQuery(this);
		progressBar = (ProgressBar) findViewById(R.id.connectScreen_progressBar);
		connectOtherButton=(Button)findViewById(R.id.search_button);
		
		setCustomFont();
		setActionBarStyling();
		
		
	}
	
	private void show(View view){
		aQuery = new AQuery(this);
		progressBar = (ProgressBar) findViewById(R.id.connectScreen_progressBar);
		progressBar.setVisibility(View.VISIBLE);
	}
	
	public String getParseObjectId(){
		
		return ParseInstallation.getCurrentInstallation().getObjectId();
	}
	
	private void updateLocation(){
		GPSManager gpsManager = new GPSManager(this);
		Boolean canGetLocation = gpsManager.isLocationServiceEnabled();
		
		if(canGetLocation){
			Location location = gpsManager.getLocation();
			if(location !=null ){
				
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
			
			updateUserLocationOnServer(latitude , longitude);
			gpsManager.stopUsingGPS();
		}
		
		else{
			gpsManager.showSettingsAlert();
			
		}
	}

	public void updateUserLocationOnServer(double latitude, double longitude) {
		aQuery = new AQuery(this);
		
		AjaxCallback<String> callBack = new AjaxCallback<String>();
		callBack.url(Constants.BaseUrl + Constants.USER_UPDATE).type(String.class).
		weakHandler(this, "responseUpdateLocation");
	
		callBack.param(Constants.PARAM_AUTHORIZATION, Constants.AUTHORIZATION);
		callBack.param(Constants.PARAM_SM_TOKEN, MasterUser.getInstance().getUserSmToken());
		callBack.param(Constants.PARAM_USER_ID, MasterUser.getInstance().getUserId());
		callBack.param(Constants.PARAM_LATITUDE, latitude);	
		callBack.param(Constants.PARAM_LONGITUDE, longitude);
		
		aQuery.ajax(callBack);
	}
	
	public void responseUpdateLocation(String url, String json, AjaxStatus status){
		if(json != null){
			locationUpdateFlag = true;
		}
	}

	@Override
	protected void onStart(){
		super.onStart();
		if( !locationUpdateFlag ){
			updateLocation();
		}
		
		
	}
	
	@Override 
	protected void onRestart(){
		super.onRestart();

	}
}
