package com.attribes.incommon.models;

import java.util.ArrayList;

import com.attribes.incommon.ImageVO;
import com.parse.ParseInstallation;

public class MasterUser {

	private String userId;
	private String userFullName;
	private String userName;
	private String userAge;
	private String userImageUri;
	private String userDescription;
	private String userQbId;
	private String userQbLogin;
	private String userQbPassword;
    private String userSmToken;
    private String userLat;
    private String userLon;
    private String userParseObjectId;
    
    private int preference_minAge;
    private int preference_maxAge;
    private int preference_distance;
    private boolean preference_messages;
    private boolean preference_alerts;
    private boolean preference_newMatches;
    private boolean preferences_showMen;
	private boolean preferences_showWomen;
	private boolean preferences_showBoth;
    private boolean qbInitilized = false;
    private ArrayList<String> userInterests;
    private ArrayList<ImageVO> userProfileImage;
    
    private static MasterUser masterUser;
    
    private MasterUser(){
    	
    }
    
    public static MasterUser getInstance(){
		
    	if(masterUser == null){
			
			masterUser = new MasterUser();	
		}
    	
    return masterUser;
    	
    }
    
    public boolean isQbInitilized() {
		return qbInitilized;
	}

	public void setQbInitilized(boolean qbInitilized) {
		this.qbInitilized = qbInitilized;
	}
    
	public String getUserId() {
		return userId;
	}
	public String getUserFullName() {
		return userFullName;
	}
	public String getUserName() {
		return userName;
	}
	public String getUserAge() {
		return userAge;
	}
	public String getUserImageUri() {
		return userImageUri;
	}
	public String getUserDescription() {
		return userDescription;
	}
	public String getUserQbId() {
		return userQbId;
	}
	public String getUserQbLogin() {
		return userQbLogin;
	}
	public String getUserQbPassword() {
		return userQbPassword;
	}
	public String getUserSmToken() {
		return userSmToken;
	}
	public String getUserLat() {
		return userLat;
	}
	public String getUserLon() {
		return userLon;
	}
	
	public int getPreference_minAge() {
		return preference_minAge;
	}

	public int getPreference_maxAge() {
		return preference_maxAge;
	}

	public int getPreference_distance() {
		return preference_distance;
	}

	public boolean isPreference_messages() {
		return preference_messages;
	}

	public boolean isPreference_alerts() {
		return preference_alerts;
	}

	public boolean isPreference_newMatches() {
		return preference_newMatches;
	}
	
	

	public boolean isPreferences_showMen() {
		return preferences_showMen;
	}

	public boolean isPreferences_showWomen() {
		return preferences_showWomen;
	}
	
	public boolean isPreferences_showBoth() {
		return preferences_showBoth;
	}

	public ArrayList<String> getUserInterests() {
		return userInterests;
	}
	

	public ArrayList<ImageVO> getUserProfileImage() {
		return userProfileImage;
	}

	public static MasterUser getMasterUser() {
		return masterUser;
	}

	public static void setMasterUser(MasterUser masterUser) {
		MasterUser.masterUser = masterUser;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}
	
	public void setUserImageUri(String userImageUri) {
		this.userImageUri = userImageUri;
	}
	
	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}
	
	public void setUserQbId(String userQbId) {
		this.userQbId = userQbId;
	}
	
	public void setUserQbLogin(String userQbLogin) {
		this.userQbLogin = userQbLogin;
	}
	
	public void setUserQbPassword(String userQbPassword) {
		this.userQbPassword = userQbPassword;
	}
	
	public void setUserSmToken(String userSmToken) {
		this.userSmToken = userSmToken;
	}
	
	public void setUserLat(String userLat) {
		this.userLat = userLat;
	}
	
	public void setUserLon(String userLon) {
		this.userLon = userLon;
	}

	public void setPreference_minAge(int preference_minAge) {
		this.preference_minAge = preference_minAge;
	}

	public void setPreference_maxAge(int preference_maxAge) {
		this.preference_maxAge = preference_maxAge;
	}

	public void setPreference_distance(int preference_distance) {
		this.preference_distance = preference_distance;
	}

	public void setPreference_messages(boolean preference_messages) {
		this.preference_messages = preference_messages;
	}

	public void setPreference_alerts(boolean preference_alerts) {
		this.preference_alerts = preference_alerts;
	}

	public void setPreference_newMatches(boolean preference_newMatches) {
		this.preference_newMatches = preference_newMatches;
	}

	public void setPreferences_showMen(boolean preferences_showMen) {
		this.preferences_showMen = preferences_showMen;
	}

	public void setPreferences_showWomen(boolean preferences_showWomen) {
		this.preferences_showWomen = preferences_showWomen;
	}
	
	public void setPreferences_showBoth(boolean preferences_showBoth) {
		this.preferences_showBoth = preferences_showBoth;
	}

	public void setUserInterests(ArrayList<String> userInterests) {
		this.userInterests = userInterests;
	}
    
	public void setUserProfileImage(ArrayList<ImageVO> userProfileImage) {
		this.userProfileImage = userProfileImage;
	}
	
	public String getUserParseObjectId(){
		return userParseObjectId;
	}
	
	public void setUserParseObjectId(String parseObjectId){
		this.userParseObjectId = parseObjectId;
	}
	
	public String getParseObjectId(){
		
		return ParseInstallation.getCurrentInstallation().getObjectId();
	}

	
}
