package com.attribes.incommon;

import java.util.ArrayList;
import java.util.Set;

import com.attribes.incommon.util.UserDevicePreferences;
import com.quickblox.core.QBSettings;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import org.xbill.DNS.Master;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.api.ApiRequest;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.facebook.LoginActivity;
import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.quickblox.chat.QBChatService;

@ReportsCrashes(formUri = "https://attribes.cloudant.com/acra-incommon/_design/acra-storage/_update/report", 
				reportType = HttpSender.Type.JSON, 
				httpMethod = HttpSender.Method.POST, 
				formUriBasicAuthLogin = "ersherdsweentrushallyetu", 
				formUriBasicAuthPassword = "eTHkySl8gGxftOSU7s7GJvAq", 
				formKey = "", // This is for backward compatibility and not used
customReportContent = { ReportField.APP_VERSION_CODE,
		ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
		ReportField.PACKAGE_NAME, ReportField.REPORT_ID, ReportField.BUILD,
		ReportField.STACK_TRACE, ReportField.DEVICE_ID,
		ReportField.INSTALLATION_ID, ReportField.TOTAL_MEM_SIZE,
		ReportField.AVAILABLE_MEM_SIZE },

mode = ReportingInteractionMode.TOAST, resToastText = R.string.toast_crash

)
public class AppController extends Application implements
		ActivityLifecycleCallbacks {

	public static final String TAG = "FB";
	public static final String NETWORKTAG = "NETWORK";
    private static final String API_END_POINT = "https://apiincommon.quickblox.com";
    private static final String CHAT_END_POINT = "chatincommon.quickblox.com";
    private static final String TURN_SERVER = "turnserver.quickblox.com";
    private static final String BUCKET = "qb-incommon-s3";
    private RequestQueue requestQueue;
	private static AppController networkInstance;
	private int resumed;
	private int stopped;
	private ApiRequest apiRequest;
	private QBChatService chatService;
	private static Context context;

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();

		AppController.context = getApplicationContext();
        UserDevicePreferences.getInstance().init(this);
		ChatHandler.getInstance().init(this);
        apiRequest = new ApiRequest(this);
		registerActivityLifecycleCallbacks(this);
		if (isUserRegistered()) {

			MasterUser.getInstance().setUserQbId(getQbUserId());
			MasterUser.getInstance().setUserQbLogin(getQbUserLogin());
			MasterUser.getInstance().setUserQbPassword(getQbUserPassword());
		}
        //setQBEndpoints();
		initParse();
		initFlurry();

		networkInstance = this;

	}

    private void setQBEndpoints() {

        QBSettings.getInstance().setServerApiDomain(API_END_POINT);

        QBSettings.getInstance().setChatServerDomain(CHAT_END_POINT);

        QBSettings.getInstance().setTurnServerDomain(TURN_SERVER);

        QBSettings.getInstance().setContentBucketName(BUCKET);
    }

    private void initFlurry() {
		// configure Flurry
		FlurryAgent.setLogEnabled(false);

		// init Flurry
		FlurryAgent.init(this, Constants.FLURRY_KEY);
	}

	private void initParse() {
		Parse.initialize(this, "BzOfdyrukcpzoxFDDWRk4hohbvtc4IMr4DYNYG4i",
				"v8HmYx8YZ4bDZKhG5brK2YDbVP3l889AB5DDW5Q5");

		ParseFacebookUtils.initialize(getString(R.string.app_id));
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);


		ParseACL.setDefaultACL(defaultACL, true);

	}

	public static synchronized AppController getInstance() {

		return networkInstance;
	}

	public RequestQueue getRequestQueue() {

		if (requestQueue == null) {

			requestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return requestQueue;
	}

	public <T> void addToRequestQueue(Request<T> request, String tag) {

		request.setTag(TextUtils.isEmpty(tag) ? NETWORKTAG : tag);

		VolleyLog.d("Adding request to queue: %s", request.getUrl());

		getRequestQueue().add(request);
	}

	public <T> void addToRequestQueue(Request<T> request) {
		request.setTag(NETWORKTAG);

		getRequestQueue().add(request);

	}

	public void cancelPendingRequests(Object tag) {

		if (requestQueue != null) {
			requestQueue.cancelAll(tag);
		}
	}

	public int getAppVersion() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public boolean isUserRegistered() {
		Boolean userRegistered = false;
		SharedPreferences registrationPreferences = context
				.getSharedPreferences(Constants.PREFS_NAME, 0);
		Boolean userFlag = registrationPreferences.getBoolean(
				Constants.REGISTRATION_KEY, false);
		if (userFlag)
			userRegistered = userFlag;

		return userRegistered;

	}

	private void sendRequestLogIn() {
		apiRequest.setUserStatus(Constants.USER_STATUS_LOG_IN,
				AppController.this, "resultRequestLogIn");

	}

	public void resultRequestLogIn(String url, String json, AjaxStatus status) {
		if (json != null) {

		}
	}

	private void sendRequestLogOut() {
		apiRequest.setUserStatus(Constants.USER_STATUS_LOG_OUT,
				AppController.this, "resultRequestLogOut");

	}

	public void resultRequestLogOut(String url, String json, AjaxStatus status) {
		if (json != null) {

		}

	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		if (isUserRegistered()) {

			MasterUser.getInstance().setUserAge(String.valueOf(getUserAge()));
			MasterUser.getInstance().setUserDescription(getDescription());
			MasterUser.getInstance().setUserFullName(getUserFullName());
			MasterUser.getInstance().setUserId(getUserIdFromApi());
			MasterUser.getInstance().setUserImageUri(getUserImageUri());
			MasterUser.getInstance().setUserInterests(getUserInterests());
			// MasterUser.getInstance().setUserLat(userLat);
			// MasterUser.getInstance().setUserLon(userLon);
			MasterUser.getInstance().setUserName(getUserName());
			// MasterUser.getInstance().setUserParseObjectId(parseObjectId);
			// MasterUser.getInstance().setUserProfileImage(userProfileImage);
			MasterUser.getInstance().setUserQbId(getQbUserId());
			MasterUser.getInstance().setUserQbLogin(getQbUserLogin());
			MasterUser.getInstance().setUserQbPassword(getQbUserPassword());
			MasterUser.getInstance().setUserSmToken(getSmToken());

		}
		Log.i("AppController", "onActivityCreated " + isUserRegistered());

		// if( !(activity instanceof SplashScreen || activity instanceof
		// MainActivity || activity instanceof WelcomeScreen ||
		// activity instanceof DateOfBirthScreen ||activity instanceof
		// CreateProfileScreen||
		// activity instanceof SelectInterestScreen || activity instanceof
		// AlbumImageChooserScreen ||
		// activity instanceof AlbumPhotosGridScreen|| activity instanceof
		// CompleteProfileScreen ||
		// activity instanceof LoginActivity)){
		// ++resumed;
		// }

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		// TODO: release all the resource consuming threads here
		Log.i("AppController", "onActivityDestroyed");
		// if(activity.getClass().getSimpleName().equals("ShareScreen")){
		// resumed++;
		// }
		// if(!(activity instanceof SplashScreen || activity instanceof
		// MainActivity || activity instanceof CreateProfileScreen||
		// activity instanceof WelcomeScreen || activity instanceof
		// DateOfBirthScreen || activity instanceof SelectInterestScreen ||
		// activity instanceof AlbumImageChooserScreen || activity instanceof
		// AlbumPhotosGridScreen||
		// activity instanceof CompleteProfileScreen || activity instanceof
		// LoginActivity
		// )){
		// ++stopped;
		// }
		//
	}

	@Override
	public void onActivityPaused(Activity activity) {

		Log.i("AppController", "onActivityPaused");

		// if(activity.getClass().getSimpleName().equals("ShareScreen")){
		// stopped++;
		// }
	}

	@Override
	public void onActivityResumed(Activity activity) {

		if (!(activity instanceof SplashScreen
				|| activity instanceof MainActivity
				|| activity instanceof WelcomeScreen
				|| activity instanceof DateOfBirthScreen
				|| activity instanceof CreateProfileScreen
				|| activity instanceof SelectInterestScreen
				|| activity instanceof AlbumImageChooserScreen
				|| activity instanceof AlbumPhotosGridScreen
				|| activity instanceof CompleteProfileScreen || activity instanceof LoginActivity)) {
			++resumed;
		}

		if (resumed >= 1 && isUserRegistered()) {
			sendRequestLogIn();

			// ChatHandler.QBInit();

		}
		// Log.i("AppController", "Open Splash");

		// if(resumed ==1 && stopped == 0 && isUserRegistered() ){
		// Log.i("AppController", "Opening Splash");
		// Intent Splashactivity = new Intent(this, SplashScreen.class);
		// Splashactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// Splashactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(Splashactivity);
		// }
		//

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {
		// TODO: implement check of GPS and internet over here
		Log.i("AppController", "onActivityStarted");
		String str = activity.getClass().getSimpleName();
		if ((resumed == stopped) && isUserRegistered()
				&& str.equals("SearchFriends")) {

			activity.finish();
			Intent Splashactivity = new Intent(this, SplashScreen.class);
			Splashactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Splashactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(Splashactivity);
		}
	}

	@Override
	public void onActivityStopped(Activity activity) {

		if (!(activity instanceof SplashScreen
				|| activity instanceof MainActivity
				|| activity instanceof CreateProfileScreen
				|| activity instanceof WelcomeScreen
				|| activity instanceof DateOfBirthScreen
				|| activity instanceof SelectInterestScreen
				|| activity instanceof AlbumImageChooserScreen
				|| activity instanceof AlbumPhotosGridScreen
				|| activity instanceof CompleteProfileScreen || activity instanceof LoginActivity)) {
			++stopped;
		}
		if ((resumed == stopped) && isUserRegistered()) {
			sendRequestLogOut();
			MasterUser.getInstance().setQbInitilized(false);
			// Activity parent = activity.getParent();
			// if(!activity.getClass().getSimpleName().equals("SplashScreen") ||
			// (!activity.getClass().getSimpleName().equals("ConnectScreen")))
			// activity.finish();
			// activity.getParent().finishAffinity();

		}

	}

	private int getUserAge() {
		SharedPreferences registrationPrefs = getSharedPreferences(
				Constants.PREFS_NAME, 0);
		int userAge = registrationPrefs.getInt(Constants.PERSON_AGE, 0);
		return userAge;
	}

	private String getDescription() {
		SharedPreferences registrationPrefs = getSharedPreferences(
				Constants.PREFS_NAME, 0);
		String userDescription = registrationPrefs.getString(
				Constants.PERSON_DESCRIPTION, "");
		return userDescription;
	}

	private String getUserFullName() {
		SharedPreferences registraPreferences = context.getSharedPreferences(
				Constants.PREFS_NAME, 0);
		String fullName = registraPreferences.getString(
				Constants.USER_FULL_NAME, "");

		return fullName;
	}

	private String getUserIdFromApi() {
		String userId = "";
		SharedPreferences registrationPreferences = context
				.getSharedPreferences(Constants.PREFS_NAME, 0);
		userId = registrationPreferences.getString(Constants.USER_ID_API, "");

		return userId;
	}

	private String getUserImageUri() {
		String userImageUri = "";
		SharedPreferences registrationPreferences = getSharedPreferences(
				Constants.PREFS_NAME, 0);
		userImageUri = registrationPreferences.getString(
				Constants.USER_IMAGE_URI, "");

		return userImageUri;
	}

	private ArrayList<String> getUserInterests() {

		SharedPreferences userPreferences = getSharedPreferences(
				Constants.PREFS_NAME, 0);
		ArrayList<String> userInterests = new ArrayList<String>();
		Set<String> selectedInterestSet = userPreferences.getStringSet(
				Constants.USER_INTERESTS, null);
		if (!(selectedInterestSet == null)) {
			if (!(selectedInterestSet.isEmpty())) {
				for (String s : selectedInterestSet) {

					userInterests.add(s);
				}
			}
		}
		return userInterests;
	}

	private String getUserName() {
		SharedPreferences registrationPrefs = getSharedPreferences(
				Constants.PREFS_NAME, 0);
		String userName = registrationPrefs
				.getString(Constants.PERSON_NAME, "");
		return userName;
	}

	public String getQbUserId() {

		SharedPreferences registrationPreferences = context
				.getSharedPreferences("registrationPreference", 0);
		String qbUserId = registrationPreferences.getString(
				Constants.QB_USER_ID, "");

		return qbUserId;
	}

	public String getQbUserPassword() {

		SharedPreferences registrationPreferences = context
				.getSharedPreferences("registrationPreference", 0);
		String qbUserPassword = registrationPreferences.getString(
				Constants.QB_USER_PASSWORD, null);

		return qbUserPassword;
	}

	public String getQbUserLogin() {

		SharedPreferences registrationPreferences = context
				.getSharedPreferences("registrationPreference", 0);
		String qbUserLogin = registrationPreferences.getString(
				Constants.QB_USER_LOGIN, null);

		return qbUserLogin;
	}

	private String getSmToken() {
		String sMToken = "";
		SharedPreferences registrationPreferences = context
				.getSharedPreferences(SplashScreen.PREFS_NAME, 0);
		sMToken = registrationPreferences
				.getString(Constants.USER_SM_TOKEN, "");

		return sMToken;
	}

	public Context getContext() {

		return this.context;
	}

}
