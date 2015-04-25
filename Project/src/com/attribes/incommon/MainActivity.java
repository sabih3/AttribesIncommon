package com.attribes.incommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.models.Album;
import com.attribes.incommon.models.InCommonUser;
import com.attribes.incommon.models.InCommonUser.Interest;
import com.attribes.incommon.taketour.TakeATourFragmentZero;
import com.attribes.incommon.util.Constants;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


public class MainActivity extends BaseActivity implements com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks,
com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener{

	public GoogleApiClient googleApiClient;
	private TextView startTourText;
	private TextView googleSignIn;
	private ConnectionResult connectionResult;
	private boolean intentInProgress;
	private boolean signInClicked;
	private String[] personName;
	private String personId;
	private static String personPhotoUrl;
	private String personFullName;
	private Dialog progressDialog;
	private AQuery mAquery;
	private String mSmToken;
	private InCommonUser inCommonUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
   		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAquery = new AQuery(this);
		progressDialog=new Dialog(this);
		googleSignIn=(TextView) findViewById(R.id.main_googleConnectTextView);
		startTourText=(TextView) findViewById(R.id.main_takeATourTextView);
		 inCommonUser = null;
		setFonts();
		setOnClickListeners();

		
	}
	
	private void initializeGoogleApiClient() {
		
		googleApiClient=new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API)
		.addScope(Plus.SCOPE_PLUS_LOGIN)
		.build();

		googleApiClient.connect();
		
		
	}


	private void setFonts() {
		
		mAquery.id(R.id.main_title).typeface(setCustomFont(Constants.FONT_GINGER_REGULAR));
		mAquery.id(R.id.main_fbConnectTextView).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		mAquery.id(R.id.main_googleConnectTextView).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		mAquery.id(R.id.main_takeATourTextView).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		
	}


	private void setOnClickListeners() {
		googleSignIn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View view) {
				if(view.getId()==R.id.main_googleConnectTextView /*&& !googleApiClient.isConnected()*/){
					initializeGoogleApiClient();
					progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging in...", true);
					signInClicked = true;
					resolveSignInError();
				}	
			}			
		});
		
		startTourText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(MainActivity.this,TakeATourFragmentZero.class);
				startActivity(intent);
				
			}
		});
		
	}

	
	private void resolveSignInError() {
		if(connectionResult==null){
			initializeGoogleApiClient();
		}
		
		if(!(connectionResult == null)){
			if (connectionResult.hasResolution()) {
			    try {
			      intentInProgress = true;
			      startIntentSenderForResult(connectionResult.getResolution().getIntentSender(),
			          Constants.RC_SIGN_IN, null, 0, 0, 0);
			    } catch (SendIntentException e) {
			      /* The intent was canceled before it was sent.  Return to the default
			       state and attempt to connect to get an updated ConnectionResult.*/
			      intentInProgress = false;
			      googleApiClient.connect();
			    }
			  }
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);		
		
	}
	
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);		
		if (requestCode == Constants.RC_SIGN_IN) {
			  if (responseCode != RESULT_OK) {
			      signInClicked = false;
			    }
			  
			  intentInProgress = false;

		    if (!googleApiClient.isConnecting()) {
		      googleApiClient.connect();
		    }
		  }
		else{
			ParseFacebookUtils.finishAuthentication(requestCode, responseCode, intent);
			
		}
		
	}
	
	private void updateUserPreferences() {
		
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPrefs.edit();
		editor.putBoolean(Constants.FACEBOOK_USER, true);
		editor.commit();		
	
	}
		


	protected void onStart() {
	    super.onStart();
	    if(!(googleApiClient == null))
	    	googleApiClient.connect();
	    
    
	}

	protected void onStop() {
	    super.onStop();
	    if (googleApiClient !=null && googleApiClient.isConnected()) {
	        googleApiClient.disconnect();
	    }
	  }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		 if (!intentInProgress) {
			    /* Store the ConnectionResult so that we can use it later when the user clicks
			     'sign-in'.*/
			    connectionResult = result;

			    if (signInClicked) {
			      /* The user has already clicked 'sign-in' so we attempt to resolve all
			       errors until the user is signed in, or they cancel.*/
			      resolveSignInError();
			    }
			  }
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
		 
		if((getIntent().getBooleanExtra("logOut", false))){
			
			if (googleApiClient.isConnected()) {
				
				Plus.AccountApi.revokeAccessAndDisconnect(googleApiClient).setResultCallback(new ResultCallback<Status>() {
					
					@Override
					public void onResult(Status arg0) {
						 //signInClicked = false;
						getIntent().removeExtra("logOut");
						googleApiClient.disconnect();
						googleApiClient.connect();
						
					}
				});
			}
			else{
				googleApiClient.connect();
			}

		}
		 
		else{ 
			if (googleApiClient.isConnected()) {
				 
			  if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
				  signInClicked = false;
				    Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
				    personFullName = currentPerson.getDisplayName();
				    personName = currentPerson.getDisplayName().split(" ");
				    personId = currentPerson.getId();
				    personPhotoUrl = currentPerson.getImage().getUrl();
				    personPhotoUrl = personPhotoUrl.replace("?sz=50", "");
				    
				   int gender =  currentPerson.getGender();
				   if(gender == 0){
				   setUserGenderPreference("male");  
				   }
				   else{
					   setUserGenderPreference("female");
				   }
				   //getUserProfileImagesFromGoogle(personId);
				   mSmToken = personId;
				  getUser(mSmToken);
				  updateUserPreferences(false);
			  }
			  
			  else{
				  Toast.makeText(this, "Please see if selected accounted is configured in device", Toast.LENGTH_SHORT).show();
				  resolveSignInError();
			  }
			  
			 
			}
			else{
				googleApiClient.connect();
				Toast.makeText(this, "Establishing connection with Google, please wait", Toast.LENGTH_SHORT).show();
			}
		}
	 
	}
	

	private void showWelcomeScreen() {
		progressDialog.dismiss();
		Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
		  startActivity(intent);
		  this.finish();
		
	}

	@Override
	public void onConnectionSuspended(int cause) {
		googleApiClient.connect();
		
	}
	
	
	public void onLoginClick(View v) {
		
		progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging in...", true);
		LoginFacebook();
	 }
	
	public void LoginFacebook(){
	    List<String> permissions = Arrays.asList("public_profile", "email","user_photos","user_birthday","user_about_me");
	    // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
	    // (https://developers.facebook.com/docs/facebook-login/permissions/)
	    
	    ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
	     
		@SuppressWarnings("deprecation")
		@Override
	      public void done(ParseUser user, ParseException err) {

	    	 /* Session mSession = Session.getActiveSession();
	    	  
	    	  Request meRequest=Request.newMeRequest(mSession, new GraphUserCallback()
              {

                      @Override
                      public void onCompleted(GraphUser user, Response response)
                      {
                              if(response.getError()==null)
                              {
                            	  mSmToken = user.getId();
          						saveSmToken(mSmToken);
          						Log.i("id" , user.getId());
          						updateUserPreferences();    //Setting a flag in local preferences which indicates A user as a FB user//
          			        	//setUserGenderPreference(user.getInnerJSONObject("Gender").toString())
          						JSONObject obj = user.getInnerJSONObject();
          						try {
          							Object gender = obj.get("gender");
          							setUserGenderPreference(gender.toString());
          						} catch (JSONException e) {
          							// TODO Auto-generated catch block
          							e.printStackTrace();
          						}
          						getUser(user.getId());
          			        	
          						if(getFacebookAlbums().size() == 0){
          							
          							makeMeRequest();
          						}
          						//updateUserPreferences();
          					}

                              else{
                            	  progressDialog.dismiss();
                            	  Toast.makeText(MainActivity.this,"Unstable Internet Connection", Toast.LENGTH_SHORT).show();
                              }
                              

                      }
              });
	    	  //Execute the request
	    	  meRequest.executeAsync();*/

	    	  Request.executeMeRequestAsync(Session.getActiveSession(), new Request.GraphUserCallback() {
	    		// callback after Graph API response with user object

	

					@Override
					public void onCompleted(GraphUser user, Response response) {
						
						if(user != null){
						mSmToken = user.getId();
						saveSmToken(mSmToken);
						Log.i("id" , user.getId());
						updateUserPreferences();    //Setting a flag in local preferences which indicates A user as a FB user//
			        	//setUserGenderPreference(user.getInnerJSONObject("Gender").toString())
						JSONObject obj = user.getInnerJSONObject();
						try {
							Object gender = obj.get("gender");
							setUserGenderPreference(gender.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						getUser(user.getId());
			        	
						if(getFacebookAlbums().size() == 0){
							
							makeMeRequest();
						}
						//updateUserPreferences();
					}
						else{
							Toast.makeText(MainActivity.this,"Unstable Internet Connection", Toast.LENGTH_SHORT).show();
							//LoginFacebook();
						}
					}
	    		}); 
	    	  
//	        if (user == null) {
//	          Log.d(AppController.TAG, "Uh oh. The user cancelled the Facebook login.");
//	        } 
//	        else if (user.isNew()) {
//	        	Log.i("user ID",user.getObjectId());
//	        	Log.i("userName",user.getUsername());
//	        	updateUserPreferences();  /*Setting a flag in local preferences which indicates A user as a FB user*/
//	        	getUser(retrieveSmToken(user));
//	          
//	        } else {
//	        	Log.i("user ID",user.getObjectId());
//	        	Log.i("userName",user.getUsername());
//	        	
//	         updateUserPreferences();    /*Setting a flag in local preferences which indicates A user as a FB user*/
//        	 getUser(retrieveSmToken(user));
//
//	        }
	      }

		
		
	    });
	}
//	private String retrieveSmToken(ParseUser user) {
//		 try {
//			 
//
//			mSmToken = (user.getJSONObject("profile").get("facebookId")).toString();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return mSmToken;
//	}
	
	/*This method makes an api call to inCommon server, and checks whether the smToken is present in user base or not
	 * @params smToken
	 * 
	 * @return The response of this request comes in 'ResultUserDetail' method in this class
	 */
	public void getUser(String smToken){
		
		String url = Constants.BaseUrl + Constants.USER_GET;
		
		AjaxCallback<String> callBack = new AjaxCallback<String>();        
		callBack.url(url).type(String.class).weakHandler(MainActivity.this, "ResultUserDetail");
		
		callBack.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		callBack.param("sm_token", smToken);
		
		mAquery.ajax(callBack);
	}
	
	public void ResultUserDetail(String Url, String json, AjaxStatus status){
		if(json!=null){		
			
			Gson gson = new Gson();
			
			try {				
				inCommonUser=gson.fromJson(json.toString(), InCommonUser.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			if(! (inCommonUser.response == null))  /*User has already registered as an InCommon user in past*/
			{ 
				
				setUserRegistered();       
				setUserPreferences(inCommonUser);
				if(facebookUserRegistsred()){
					progressDialog.dismiss();
				    showConnectScreen();
				}
				else{
					if(inCommonUser.response.qb_id.length()>1)
					   setUserRegistered();
					
					 getUserProfileImagesFromGoogle(mSmToken);
				}
			}
			
			else{								/*First time user*/
				
				
				if(facebookUserRegistsred()){
					progressDialog.dismiss();/* If it is a fb user, then fetch profile info of fb*/
					getFacebookUserInfo();	    /* This is a async call, inside this method, we set user profile info in local preferences*/ 
					setDefaultPreferences();
					saveSmToken(mSmToken);
					showWelcomeScreen();
				}
				
				else{
					setGoogleUserInfo();      /*if it has a google user, then set profile info accordingly*/
					setDefaultPreferences();
					getUserProfileImagesFromGoogle(mSmToken);
				}
				
				
			}
			
		}
	}


	private void setGoogleUserInfo() {

	 if(personName[0] == null || personName[0].isEmpty()){
		  setUserName(personFullName);  
	  }else{
		  
		  setUserName(personName[0]);
	  }
	  
	  saveSmToken(personId);  
	  setUserImageUri(personPhotoUrl);
	  if(getUserName().equals(""))
	  setUserName(personFullName);
	  
	  setUserFullName(personFullName);
		
	}


	private void showConnectScreen() {
		Intent intent=new Intent(MainActivity.this,ConnectScreen.class);
		startActivity(intent);
		this.finish();
		
	}


	private void setUserPreferences(InCommonUser inCommonUser) {
		ArrayList<String> interestsInString = new ArrayList<String>();
		for(Interest interest : inCommonUser.response.interests){
			interestsInString.add(interest.title);
		}
		 saveUserIdFromApi(inCommonUser.response.id);
		 saveSmToken(mSmToken);
		 setUserName(inCommonUser.response.name);
		 setUserFullName(inCommonUser.response.name);
		 setUserAge(Integer.parseInt(inCommonUser.response.age));
		 setDescription(inCommonUser.response.description);
		 setUserImageUri(inCommonUser.response.image_uri);
		 setQbUserId(inCommonUser.response.qb_id);
		 setQbUserLogin(mSmToken);
		 setQbUserPassword(mSmToken);
		 saveUserInterests(interestsInString);
		 setUserGenderPreference(inCommonUser.response.gender);
		 
		 String age[]= inCommonUser.response.preferences.search_age.split("-");
		 String distance = inCommonUser.response.preferences.search_distance;
		 String gender = inCommonUser.response.preferences.search_gender;
		 String alerts = inCommonUser.response.preferences.show_alerts;
		 String matches = inCommonUser.response.preferences.show_matches;
		 String messages = inCommonUser.response.preferences.show_messages;
		 
		 String minAge = age[0];
		 String maxAge = age[1];
		 String showMen = null  ;
		 String showWomen = null;
		 String showAlerts = null ;
		 String showMatches = null;
		 String showMessages = null;
		 String showBoth = null;
		 
		 if(gender.equals("both")){
			 showBoth= "true";

		 }
		 
		 if(gender.equals("male")){
			 showMen = "true";
			 showWomen = "false";
		 }
		 
		 if(gender.equals("female")){
			 showMen = "false";
			 showWomen = "true";
		 }
		 
		 if(alerts.equals("1")){
			 showAlerts = "true";
		 }
		 
		 if(alerts.equals("0")){
			 showAlerts = "false";
		 }
		 
		 if(matches.equals("1")){
			 showMatches = "true";
		 }
		 if(matches.equals("0")){
			 showMatches = "false";
		 }
		 if(messages.equals("1")){
			 showMessages = "true";
		 }
		 
		 if(messages.equals("0")){
			 showMessages = "false";
		 }
		 
		 HashMap<String, String> userPreferencesMap=new HashMap<String, String>();
		 userPreferencesMap.put("minAge", minAge);
		 userPreferencesMap.put("maxAge", maxAge);
		 userPreferencesMap.put("distance", distance);
		 userPreferencesMap.put("showMen", showMen);
		 userPreferencesMap.put("showWomen", showWomen);
		 userPreferencesMap.put("showBoth", showBoth);
		 userPreferencesMap.put("showAlerts", showAlerts);
		 userPreferencesMap.put("showMatches", showMatches);
		 userPreferencesMap.put("showMessages",showMessages);

		 
		 
		 setPreferencesLocally(userPreferencesMap);
		
	}
	
	public void getUserProfileImagesFromGoogle(String smToken){
		 
        String url = "https://picasaweb.google.com/data/feed/api/user/"+mSmToken+"?alt=json";
		Log.i("url", url);
       
		AQuery mAquery = new AQuery(this);
		AjaxCallback<String> callBack = new AjaxCallback<String>();        
		//callBack.url(url).type(String.class).weakHandler(MainActivity.this, "retrieveGoogleAlbums").method(AQuery.METHOD_GET);
		
		mAquery.ajax(url,null , String.class, new AjaxCallback<String>() {

	        @Override
	        public void callback(String url, String json, AjaxStatus status) {
	            if(status.getCode() == 200) {

	            	retrieveGoogleAlbums(url,  json,  status);
	            	if(inCommonUser.response == null){
	            		showWelcomeScreen();
	            		progressDialog.dismiss();
	            	}
	            	else{
	            		showConnectScreen();
	            		progressDialog.dismiss();
	            	}
	            } else {

	            	Log.i("failed","albums");
	            }

	        }
	    }.method(AQuery.METHOD_GET));
		
	//	mAquery.ajax(callBack);
		
		
	 }
	
public void retrieveGoogleAlbums(String Url, String json, AjaxStatus status){
		
		{
			if(json!=null){
				try {
					ArrayList<String> albums = new ArrayList<String>();
					JSONObject obj = new JSONObject(json);
					JSONObject feed = obj.getJSONObject("feed");
					JSONArray entry = feed.getJSONArray("entry");
					
				
					for(int i= 0 ; i< entry.length(); i++){
						
						JSONObject entryObj = entry.getJSONObject(i);
						JSONArray arrayOfimages = entryObj.getJSONArray("link");
						JSONObject test = arrayOfimages.getJSONObject(0);
						String str = test.getString("href");
						
					JSONObject id = 	entryObj.getJSONObject("id");
					String albumID = id.getString("$t");
					
					JSONObject countObj  = entryObj.getJSONObject("gphoto$numphotos");
					JSONObject nameObj   = entryObj.getJSONObject("gphoto$name");
				//	JSONObject idObj     =  entryObj.getJSONObject("gphoto$id");
				
					JSONObject mediaGroup  =  entryObj.getJSONObject("media$group");
					JSONArray mediaContent =mediaGroup.getJSONArray("media$content");
					
					 JSONObject urlObj = mediaContent.getJSONObject(0);
					String url =  urlObj.getString("url");
					Album albumObject = new Album(str, nameObj.getString("$t"), countObj.getString("$t"), url);
					Gson gson = new Gson();
					String albumObjectJson = gson.toJson(albumObject);
					
					albums.add(albumObjectJson);
					}
					
					setGooglePlusAlbums(albums);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
	
	public void googleDisconnect(){
		if (googleApiClient !=null && googleApiClient.isConnected()) {
	        googleApiClient.disconnect();
	    }
	}
}
