package com.attribes.incommon;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
//import android.app.ActivityManager.AppTask;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.attribes.incommon.chat.core.MessageHandler.MessageListener;
import com.attribes.incommon.models.Album;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class BaseActivity extends ActionBarActivity implements MessageListener{

	//public static ImageLoader imageLoader = ImageLoader.getInstance();
	
	public Context context;
	Session facebookSession;

	private Dialog progressDialog;
	private Activity activity;
	String profilePictureIds;
	private ArrayList<String> profileAlbums;
	private NotificationManager mNotificationManager;
	
	public BaseActivity(){
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		context = this;
//		if(this instanceof SearchFriends){
//			
//			ChatHandler.getInstance().addMessageListener(BaseActivity.this, this);
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

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
	
		public void onLoginClick(View v) {
		
		//progressDialog = ProgressDialog.show(activity, "", "Logging in...", true);
	    
	    List<String> permissions = Arrays.asList("public_profile", "email","user_photos","user_birthday","friends_birthday");
	    // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
	    // (https://developers.facebook.com/docs/facebook-login/permissions/)
	    
	    ParseFacebookUtils.logIn(permissions, activity, new LogInCallback() {
	      @Override
	      public void done(ParseUser user, ParseException err) {
	        //progressDialog.dismiss();
	        if (user == null) {
	          Log.d(AppController.TAG, "Uh oh. The user cancelled the Facebook login.");
	        } else if (user.isNew()) {
	        	
	          Log.d(AppController.TAG, "User signed up and logged in through Facebook!");
	          showWelcomeScreen();
	          
	        } else {
	          Log.d(AppController.TAG, "User logged in through Facebook!");
	          showWelcomeScreen();
	        }
	      }
	    });
	  }
		
	private void showWelcomeScreen() {
		Intent intent = new Intent(BaseActivity.this, WelcomeScreen.class);
		startActivity(intent);
		this.finish();
			
		}



	protected void setActionBarStyling() {
		ActionBar bar = getSupportActionBar();


		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ab_solid_background));
//		bar.setHomeAsUpIndicator(R.drawable.ic_ab_back_holo_light);
		bar.setIcon(R.drawable.logo);
		bar.setLogo(R.drawable.logo);
//		bar.setDisplayUseLogoEnabled(true);
//		bar.setDisplayShowHomeEnabled(true);
//
//		bar.setDisplayHomeAsUpEnabled(true);
		int actionBarTitleId = Resources.getSystem().getIdentifier(
				"action_bar_title", "id", "android");
		if (actionBarTitleId > 0) {
			TextView title = (TextView) findViewById(actionBarTitleId);
			if (title != null) {
				setCustomFont(title, Constants.FONT_PROXI_REGULAR);
				title.setTextSize(20);
				
				title.setTextColor(getResources().getColor(R.color.actionBar));
				
			}
		}


		
		
		
	}
	
	protected void initializeImageLoader(Context context){
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
        .taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        .taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
        .threadPoolSize(3) // default
        .threadPriority(Thread.NORM_PRIORITY - 1) // default
        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
        .denyCacheImageMultipleSizesInMemory()
        .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // default
        .memoryCacheSize(2 * 1024 * 1024)
        .imageDownloader(new BaseImageDownloader(context)) // default
        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
        
        .build();
		
	}
	
	protected String getDeviceId(){
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		 
	return mngr.getDeviceId();
		 
	}
	
	protected void saveSmToken(String socialMediaUserId){
		SharedPreferences registrationPreferences = getSharedPreferences(SplashScreen.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPreferences.edit();
		editor.putString(Constants.USER_SM_TOKEN, socialMediaUserId);
		editor.commit();
		MasterUser.getInstance().setUserSmToken(socialMediaUserId);
	}
	
	public String getSmToken(){
		String sMToken= "";
		SharedPreferences registrationPreferences = context.getSharedPreferences(SplashScreen.PREFS_NAME, 0);
		sMToken = registrationPreferences.getString(Constants.USER_SM_TOKEN, "");
		
		return sMToken;
	}
	
	public void saveUserIdFromApi(String userId){
		try {
		
			SharedPreferences registrationPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
			SharedPreferences.Editor editor = registrationPreferences.edit();				
			editor.putString(Constants.USER_ID_API, userId);
			editor.commit();
			MasterUser.getInstance().setUserId(userId);
		} catch (Exception e) {
			Log.e("BASEACTIVITY",e.toString());
		}
				
	}
	
	protected String getUserIdFromApi(){
		String userId= "";
		SharedPreferences registrationPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		userId = registrationPreferences.getString(Constants.USER_ID_API, "");
		
		return userId;		
	}
	
	public void setUserImageUri(String imageUri){
		
		SharedPreferences registrationPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor=registrationPreferences.edit();
		editor.putString(Constants.USER_IMAGE_URI, imageUri);
		editor.commit();
		
		MasterUser.getInstance().setUserImageUri(imageUri);
		
	}
	
	public String getUserImageUri(){
		String userImageUri = "";
		SharedPreferences registrationPreferences=getSharedPreferences(Constants.PREFS_NAME, 0);
		userImageUri = registrationPreferences.getString(Constants.USER_IMAGE_URI, "");
		
		return userImageUri;
	}
	
	protected void setDescription(String personDescription) {
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPrefs.edit();
		editor.putString(Constants.PERSON_DESCRIPTION, personDescription);
		editor.commit();
		//TODO:Master User object populated
		MasterUser.getInstance().setUserDescription(personDescription);
	}
	
	protected String getDescription(){
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		String userDescription = registrationPrefs.getString(Constants.PERSON_DESCRIPTION, "");
		return userDescription;
	}
	
	protected void setUserName(String userName){
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPrefs.edit();
		editor.putString(Constants.PERSON_NAME, userName);
		editor.commit();
		MasterUser.getInstance().setUserName(userName);
		
		
	}
	
	protected String getUserName(){
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		String userName = registrationPrefs.getString(Constants.PERSON_NAME, "");
		return userName;
	}
	
	protected void setUserAge(int userAge){
		
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPrefs.edit();
		editor.putInt(Constants.PERSON_AGE, userAge);
		editor.commit();
		
		//TODO:MasterUser object populated
		MasterUser.getInstance().setUserAge(Integer.toString(userAge));
	}
	
	protected int getUserAge(){
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		int userAge = registrationPrefs.getInt(Constants.PERSON_AGE, 0);
		return userAge;
	}
	protected void saveUserInterests(ArrayList<String> selectedInterests) {
		SharedPreferences userPreferences = getSharedPreferences(
				Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = userPreferences.edit();
		Set<String> selectedInterestSet = new HashSet<String>();
		for (int i = 0; i < selectedInterests.size(); i++) {
			selectedInterestSet.add(selectedInterests.get(i).toString());
		}

		editor.putStringSet(Constants.USER_INTERESTS, selectedInterestSet);
		editor.commit();
		
		//TODO:MasterUser object populated
		MasterUser.getInstance().setUserInterests(selectedInterests);
	}
	
	protected ArrayList<String> fetchUserInterests(){
		
		SharedPreferences userPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
		ArrayList<String> userInterests=new ArrayList<String>();
		Set<String> selectedInterestSet = userPreferences.getStringSet(Constants.USER_INTERESTS, null);
		if(!(selectedInterestSet == null)){
		if(!(selectedInterestSet.isEmpty())){
			for(String s:selectedInterestSet){
				
				userInterests.add(s);
			}
		}
		}
		return userInterests;		
	}
 public void updateUserPreferences(boolean flag) {
		
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPrefs.edit();
		editor.putBoolean(Constants.FACEBOOK_USER, flag);
		editor.commit();		
	
	}
	protected boolean facebookUserRegistsred() {
		Boolean facebookUserRegistered = false;
		SharedPreferences registrationPreferences = getSharedPreferences(SplashScreen.PREFS_NAME, 0);
		Boolean userFlag = registrationPreferences.getBoolean(Constants.FACEBOOK_USER, false);
		if(userFlag)
			facebookUserRegistered = userFlag;
		
		return facebookUserRegistered;
		
	}
	
	public void getFacebookUserInfo(){
		facebookSession = ParseFacebookUtils.getSession();
		
		if(facebookSession!=null && facebookSession.isOpened())
			makeMeRequest();
			
		
	}
	
	public void makeMeRequest() {		
	    Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
	            new Request.GraphUserCallback() {
	                
	    			@Override
	                public void onCompleted(GraphUser user, Response response) {
	    				if (user != null) { 
	                        // Create a JSON object to hold the profile info
	                        JSONObject userProfile = new JSONObject();
	                        try {                   
	                            // Populate the JSON object 
	                            userProfile.put("facebookId", user.getId());
	                            saveSmToken(user.getId());
	                           
	                            userProfile.put("name", user.getName());
	                        	setUserFullName(user.getName());
	                        	setUserName(user.getFirstName());
//	                                                   
//	                            if (user.getProperty("gender") != null) {
//	                                userProfile.put("gender",       
//	                                        (String) user.getProperty("gender"));   
//	                            }                           
	                           
	                            
	                            if(user.getFirstName()!=null){
	                            	
	                            	userProfile.put("firstName", user.getFirstName());
	                            	
	                            }

	                            if(user.getName()!= null){
	                            	userProfile.put("name",user.getName());
	                            }
	                            
//	                            if(user.getProperty("age_range") !=null ){
//	                            	userProfile.put("ageRange", user.getProperty("age_range"));
//	                            }
//	                            
//	                            if (user.getProperty("relationship_status") != null) {
//	                                userProfile.put("relationship_status",                 
//	                                (String) user.getProperty("relationship_status"));                               
//	                            } 
//	                            
//	                            if(user.getProperty("albums")!=null){
//	                            	Log.i("","");
//	                            }
	                            
	                            ParseUser currentUser = ParseUser.getCurrentUser();
	                            currentUser.put("profile", userProfile);
	                            currentUser.saveInBackground();
	                            
	                            getAlbums(user);
	                            
	                            if (user.getBirthday() != null) {
	                                userProfile.put("birthday",     
	                                        user.getBirthday());                          
	                            }
	                            
	                        }catch (JSONException e) {
	                            Log.d(AppController.TAG,
	                                    "Error parsing returned user data.");
	                        }
	    				
	    				}else if (response.getError() != null) {
	                            Log.e(AppController.TAG, response.getError().toString());
	                        }                  

	    			}

					
	    				
	            });
	    request.executeAsync();
	 
	}
	
	public void getAlbums(GraphUser user){
		
		
		RequestAsyncTask albumRequest = 
				Request.newGraphPathRequest(ParseFacebookUtils.getSession(), user.getId()+"/albums", 
						new Request.Callback() {

					JSONArray dataArray=new JSONArray();

					@Override
					public void onCompleted(Response response) {
						if(response.getGraphObject() != null){
							try {
								GraphObject graphObject = response.getGraphObject();
								
								
								JSONObject object = response.getGraphObject().getInnerJSONObject();
								JSONArray jsonArray = object.getJSONArray("data");
								
								profileAlbums = new ArrayList<String>();
								String id = "",name = "",coverPhoto = "";
								String count = "";
								
								for(int i = 0 ; i < jsonArray.length();i++){
	
									JSONObject innerObject = jsonArray.getJSONObject(i);
									if(innerObject.has("id")){
										id = innerObject.getString("id");
									}
									if(innerObject.has("name")){
										name=innerObject.getString("name");
									}
									if(innerObject.has("count")){
										count = innerObject.getString("count");
									}
									
									if(innerObject.has("cover_photo")){
										coverPhoto=innerObject.getString("cover_photo");
									}
									Album albumObject = new Album(id, name, count, coverPhoto);
									Gson gson = new Gson();
									String albumObjectJson = gson.toJson(albumObject);
									profileAlbums.add(albumObjectJson);
									
									if(innerObject.get("type").equals("profile")) {
										profilePictureIds = innerObject.get("id").toString();
	
										//profilePictureAlbumIds.add(innerObject.get("id").toString());							
										
									}
	
								}
	
							} catch (Exception e) {
	
								e.printStackTrace();
							}
							setFacebookAlbums(profileAlbums);
							retrieveProfileImages(profilePictureIds);
						}
				}

					
				
				}).executeAsync();

	}
	

	private void retrieveProfileImages(String profilePictureIds) {
		final ArrayList<ImageVO> profileImagesArray=new ArrayList<ImageVO>();		
		final int width = 1080;

		RequestAsyncTask photoRequest=Request.newGraphPathRequest(ParseFacebookUtils.getSession(), profilePictureIds+"/photos", new Request.Callback() {
			
			@Override
			public void onCompleted(Response response) {
				if(response.getGraphObject()!=null){
					JSONObject object= response.getGraphObject().getInnerJSONObject();
	
					try {
						JSONArray jsonArray = object.getJSONArray("data");
						for(int i = 0; i < jsonArray.length(); i++){
							JSONArray imagesArray = jsonArray.getJSONObject(i).getJSONArray("images");
	
							for(int j = 0; j < imagesArray.length() ; j++){
								ImageVO img = GsonUtility.getObjectFormJsonString(imagesArray.get(0).toString(), ImageVO.class);								
								
								profileImagesArray.add(new ImageVO(img.getHeight(),img.getWidth(),img.getSource()));
								
							}
	
						}
						
						
					} catch (JSONException e) {
	
						e.printStackTrace();
					}
					catch (Exception ex){
						ex.printStackTrace();
					}
					setFacebookProfileImagesArray(profileImagesArray);
					setUserImageUri(profileImagesArray.get(0).getSource());
					setFacebookProfileImage();
					getFacebookAlbums();
				}
			}

			
			
			}).executeAsync();
		
	}
	
	
	public void setFacebookAlbums(ArrayList<String> profileAlbums) {
		SharedPreferences userPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
		SharedPreferences.Editor editor = userPreferences.edit();
		
		Set<String> facebookAlbumSet=new HashSet<String>();
		for(int i = 0 ; i < profileAlbums.size() ; i++){
			facebookAlbumSet.add(profileAlbums.get(i));
		}
		
		editor.putStringSet(Constants.FACEBOOK_ALBUMS, facebookAlbumSet);
		editor.commit();
	}
	
	public ArrayList<Album> getFacebookAlbums(){
		ArrayList<Album>profileAlbums = new ArrayList<Album>();
		Gson gson = new Gson();
		SharedPreferences userPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
		
		Set<String> profileAlbumSet = userPreferences.getStringSet(Constants.FACEBOOK_ALBUMS, null);
		
		if(profileAlbumSet != null){
			for(String s:profileAlbumSet){
				Album albumObject = gson.fromJson(s, Album.class);
				profileAlbums.add(albumObject);
			}
		}
		return profileAlbums;
		
	}
	
	public void setGooglePlusAlbums(ArrayList<String> profileAlbums) {
		SharedPreferences userPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
		SharedPreferences.Editor editor = userPreferences.edit();
		
		Set<String> googleAlbumSet=new HashSet<String>();
		for(int i = 0 ; i < profileAlbums.size() ; i++){
			googleAlbumSet.add(profileAlbums.get(i));
		}
		
		editor.putStringSet("googleAlbums", googleAlbumSet);
		editor.commit();
	}
	
	public ArrayList<Album> getGooglePlusAlbums(){
		ArrayList<Album>profileAlbums = new ArrayList<Album>();
		Gson gson = new Gson();
		SharedPreferences userPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
		
		Set<String> profileAlbumSet = userPreferences.getStringSet("googleAlbums", null);
		
		if(profileAlbumSet != null){
			for(String s:profileAlbumSet){
				Album albumObject = gson.fromJson(s, Album.class);
				profileAlbums.add(albumObject);
			}
		}
		return profileAlbums;
		
	}
	public ArrayList<Album> getGoogleAlbums(){
		ArrayList<Album>profileAlbums = new ArrayList<Album>();
		Gson gson = new Gson();
		SharedPreferences userPreferences = getSharedPreferences(Constants.PREFS_NAME,0);
		
		Set<String> profileAlbumSet = userPreferences.getStringSet("googleAlbums", null);
		
		if(profileAlbumSet != null){
			for(String s:profileAlbumSet){
				Album albumObject = gson.fromJson(s, Album.class);
				profileAlbums.add(albumObject);
			}
		}
		return profileAlbums;
		
	}
	
	public void setFacebookProfileImagesArray(ArrayList<ImageVO> profileImagesArray){
		SharedPreferences userPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = userPreferences.edit();
		
		Set<String> facebookProfileImagesSet = userPreferences.getStringSet(Constants.FACEBOOK_IMAGES, 
				new HashSet<String>());
		
		for (int i = 0; i < profileImagesArray.size(); i++) {
			facebookProfileImagesSet.add(profileImagesArray.get(i).getSource().toString());
		}

		editor.putStringSet(Constants.FACEBOOK_IMAGES, facebookProfileImagesSet);
		editor.commit();
		MasterUser.getInstance().setUserProfileImage(profileImagesArray);
		
	}
	
	public ArrayList<String> getFacebookProfileImages(){
		
		SharedPreferences userPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
		ArrayList<String> profileImagesUriArray=new ArrayList<String>();
		Set<String> facebookImagesUriSet = userPreferences.getStringSet(Constants.FACEBOOK_IMAGES, null);
		for(String s:facebookImagesUriSet){
			
			profileImagesUriArray.add(s);
		}
		
		return profileImagesUriArray;
		
		
	}
	
	public void setFacebookProfileImage(){
		ProgressBar progressBar=(ProgressBar) findViewById(R.id.createProfile_progressBar);
		ImageView facebookImageView = (ImageView) findViewById(R.id.createProfile_profilePic);
		CircularImageView circularImageView = (CircularImageView) findViewById(R.id.createProfile_circle);
		Button chooseInterestButton=(Button)findViewById(R.id.createProfile_chooseInterests);
		
		if(progressBar !=null){
			progressBar.setVisibility(View.GONE);
		}
		
		if(facebookImageView!=null && chooseInterestButton != null){
			Picasso.with(this).load(getUserImageUri()).into(target);
			chooseInterestButton.setEnabled(true);
		}
		
		if(circularImageView!=null){
			Picasso.with(this).load(getUserImageUri()).into(circularImageView);
		}
	}
	public void setCustomFont(TextView textView, String fontName){
		Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontName);
		if(textView!=null)
			textView.setTypeface(custom_font);
	}
	
	public void setCustomFont(EditText editText, String fontName){
		Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/"+fontName);
		editText.setTypeface(custom_font);
	}
	
	public void setCustomFont(Button button, String fontName){
		Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontName);
		button.setTypeface(custom_font);
	}
	
	public Typeface setCustomFont(String fontName) {
		Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/"+fontName);
		
		return custom_font;
	}
	
	public void setActivity(Activity activity){
		this.activity = activity;
	}
	
	public Activity getActivity(){
		return this.activity;
	}
	
	private Target target = new Target() {
		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
			Bitmap temp = bitmap.copy(bitmap.getConfig(), true);
			final RenderScript rs = RenderScript
					.create(BaseActivity.this);
			final Allocation input = Allocation.createFromBitmap(rs, temp,
					Allocation.MipmapControl.MIPMAP_FULL,
					Allocation.USAGE_GRAPHICS_TEXTURE);
			final Allocation output = Allocation.createTyped(rs,
					input.getType());
			final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,
					Element.U8_4(rs));
			script.setRadius(25.0f);
			script.setInput(input);
			script.forEach(output);
			output.copyTo(temp);
			ImageView profilePicView = (ImageView) findViewById(R.id.createProfile_profilePic);
			profilePicView.setImageBitmap(temp);
		}

		@Override
		public void onBitmapFailed(Drawable drawable) {

		}

		@Override
		public void onPrepareLoad(Drawable drawable) {

		}
	};

	
	
 
	public String getParseObjectId(){
		
		return ParseInstallation.getCurrentInstallation().getObjectId();
	}
	
	protected String[] segregateBirthdayInfo(String birthInfo) {
		String[] ageArray;
		ageArray = birthInfo.split("/");
		return ageArray;
	}

	protected int getAge(String[] ageArray) {
		int _year, _month, _day;

		_year = Integer.parseInt(ageArray[2]);
		_month = Integer.parseInt(ageArray[0]);
		_day = Integer.parseInt(ageArray[1]);

		GregorianCalendar cal = new GregorianCalendar();
		int y, m, d, a;

		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH);
		d = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(_year, _month, _day);
		a = y - cal.get(Calendar.YEAR);
		if ((m < cal.get(Calendar.MONTH))
				|| ((m == cal.get(Calendar.MONTH)) && (d < cal
						.get(Calendar.DAY_OF_MONTH)))) {
			--a;
		}
		if (a < 0)
			throw new IllegalArgumentException("Age < 0");
		return a;
	}
	
	public void setQbUserLogin(String qbUserLogin){
		SharedPreferences registrationPreferences = context.getSharedPreferences("registrationPreference", 0);
		SharedPreferences.Editor editor = registrationPreferences.edit();
		editor.putString(Constants.QB_USER_LOGIN, qbUserLogin);
		editor.commit();
		MasterUser.getInstance().setUserQbLogin(qbUserLogin);
	}
	
	public String getQbUserLogin(){
		
		SharedPreferences registrationPreferences = context.getSharedPreferences("registrationPreference", 0);
		String qbUserLogin = registrationPreferences.getString(Constants.QB_USER_LOGIN, null);
		
		return qbUserLogin;
	}
	
	
	public void setQbUserPassword(String qbUserPassword){
		SharedPreferences registrationPreferences = context.getSharedPreferences("registrationPreference", 0);
		SharedPreferences.Editor editor = registrationPreferences.edit();
		editor.putString(Constants.QB_USER_PASSWORD, qbUserPassword);
		editor.commit();
		MasterUser.getInstance().setUserQbPassword(qbUserPassword);
	}
	
	public String getQbUserPassword(){
		
		SharedPreferences registrationPreferences = context.getSharedPreferences("registrationPreference", 0);
		String qbUserPassword = registrationPreferences.getString(Constants.QB_USER_PASSWORD, null);
		
		return qbUserPassword;
	}
	
	
	public void setQbUserId(String qbUserId){
		SharedPreferences registrationPreferences = context.getSharedPreferences("registrationPreference", 0);
		SharedPreferences.Editor editor = registrationPreferences.edit();
		editor.putString(Constants.QB_USER_ID, qbUserId.toString());
		editor.commit();
		MasterUser.getInstance().setUserQbId(qbUserId);
	}
	
	public String getQbUserId(){
		
		SharedPreferences registrationPreferences = context.getSharedPreferences("registrationPreference", 0);
		String qbUserId = registrationPreferences.getString(Constants.QB_USER_ID, "");
		
		return qbUserId;
	}
	
	public void setUserFullName(String fullName) {
		SharedPreferences registraPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registraPreferences.edit();
		editor.putString(Constants.USER_FULL_NAME, fullName);
		editor.commit();
		MasterUser.getInstance().setUserFullName(fullName);
	}
	
	public String getUserFullName(){
		SharedPreferences registraPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		String fullName=registraPreferences.getString(Constants.USER_FULL_NAME, "");
	
	return fullName;
	}
	
	public void setUserGenderPreference(String gender){
		
		SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPrefs.edit();
		editor.putString("Gender", gender);
		editor.commit();		
		
	}
	public String getUserGenderPreference(){
		
		SharedPreferences registraPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		String gender = registraPreferences.getString("Gender", "");
		return gender;
		
	}
	/**This method sets a flag in local preferences, which indicates user has been registered in past
	 * 
	 */
	public void setUserRegistered(){
		
		SharedPreferences registrationPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPreferences.edit();
		editor.putBoolean(Constants.REGISTRATION_KEY, true);
		editor.commit();
	}
	
	/** This method checks whether user has been registered in past or not
	 * 
	 * @return True if User is Registered, False otherwise
	 */
	public Boolean isUserRegistered(){
		Boolean isUserRegistered;
		SharedPreferences registrationPreferences=context.getSharedPreferences(Constants.PREFS_NAME, 0);
		isUserRegistered = registrationPreferences.getBoolean(Constants.REGISTRATION_KEY, false);
		
		return isUserRegistered;
	}
	
	/**This method sets inCommon preferences in local preferences
	 * 
	 * @param userPreferencesMap
	 */
	protected void setPreferencesLocally(HashMap<String,String> userPreferencesMap) {
		SharedPreferences registrationPreferences=context.getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPreferences.edit();
		for(String key:userPreferencesMap.keySet()){
			
			editor.putString(key, userPreferencesMap.get(key));
		}
		
		editor.commit();
	
	}
	
	/**This method sets default inCommon preferences in local preferences
	 * 
	 */
	protected void setDefaultPreferences(){
		HashMap<String, String> userPreferencesMap = new HashMap<String, String>();		
		userPreferencesMap.put("minAge", Integer.toString(18));
		userPreferencesMap.put("maxAge", Integer.toString(72));
		userPreferencesMap.put("distance", Integer.toString(160));
		userPreferencesMap.put("showMen", Boolean.toString(false));
		userPreferencesMap.put("showWomen", Boolean.toString(false));
		userPreferencesMap.put("showBoth", Boolean.toString(true));
		userPreferencesMap.put("showAlerts", Boolean.toString(true));
		userPreferencesMap.put("showMatches", Boolean.toString(true));
		userPreferencesMap.put("showMessages", Boolean.toString(true));
		
		setPreferencesLocally(userPreferencesMap);
		
	}
	
	private void downloadUserImage(String imageUri) {
		Bitmap bitmap = null;
		
		try {
			InputStream input=new java.net.URL(imageUri).openStream();
			bitmap=new BitmapFactory().decodeStream(input);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void chatError(QBPrivateChat sender, QBChatException exception,
			QBChatMessage message) {
		
		
	}

	public void showMessage(QBMessage message){
		//TDOO: this shouldn't be here find appropriate place for it
		//TO do nothing wait for over ride
	}
	
	@Override
	public void chatDidReceiveMessage(QBPrivateChat sender,
			QBChatMessage message) {
		
		
//		//TODO: have to change getRunningTasks method 
		/*
		ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
		Class<? extends ActivityManager> alltasks = am.getClass();
		
		@SuppressWarnings("deprecation")
		List<ActivityManager.RunningTaskInfo> runningTasks = am
                .getRunningTasks(1);
		
		for (ActivityManager.RunningTaskInfo aTask : runningTasks) {
    
            if (!aTask.topActivity.getClassName().equals(ChatScreen.class.getName())){	
            	notifyChatMessage(message);            	                       
            }
		}*/
	}

	@Override
	public void chatDidDeliverMessage(QBPrivateChat sender, String messageID) {
		
		
	}

	@Override
	public void chatDidReadMessage(QBPrivateChat sender, String messageID) {
		
		
	}

	private void notifyChatMessage(QBChatMessage message) {
		Uri defaultNotificationSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		Intent intent = new Intent(this, ChatScreen.class);
		intent.putExtra("opponentQbId", Integer.toString(message.getSenderId()));
		
		mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent chatScreenIntent = PendingIntent.getActivity(this, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 
		NotificationCompat.Builder mBuilder =
		            new NotificationCompat.Builder(this)
		    .setSmallIcon(R.drawable.launcher_incommon)
		    .setContentTitle(getResources().getString(R.string.app_name))
		    .setTicker(message.getBody())
		    .setStyle(new NotificationCompat.BigTextStyle()
		    .bigText(message.getBody()))
		    .setSound(defaultNotificationSound)
		    .setAutoCancel(true);
		
		mBuilder.setContentIntent(chatScreenIntent);
		
	    mNotificationManager.notify(Constants.CHAT_NOTIFICATION_ID, mBuilder.build());
		
	}
	
	public void ShowToast(Context context, String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	
}
