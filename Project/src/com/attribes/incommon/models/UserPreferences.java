package com.attribes.incommon.models;

public class UserPreferences {
	
	private int minAge;
	private int maxAge;
	private long distance;
	private boolean showMen;
	private boolean shoWomen;
	private boolean showAlerts;
	private boolean showNewMatches;
	private boolean showMessages;
	
	
	public int getMinAge() {
		return minAge;
	}

	public int getMaxAge() {
		return maxAge;
	}
	
	public long getDistance(){
		return distance;
	}
	
	public boolean isShowMen() {
		return showMen;
	}
	
	public boolean isShoWomen() {
		return shoWomen;
	}
	
	public boolean isShowAlerts() {
		return showAlerts;
	}
	
	public boolean isShowNewMatches() {
		return showNewMatches;
	}
	
	public boolean isShowMessages() {
		return showMessages;
	}
		
	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public void setDistance(long distance){
		this.distance = distance;
	}
	public void setShowMen(boolean showMen) {
		this.showMen = showMen;
	}
	
	public void setShoWomen(boolean shoWomen) {
		this.shoWomen = shoWomen;
	}
	
	public void setShowAlerts(boolean showAlerts) {
		this.showAlerts = showAlerts;
	}
	
	public void setShowNewMatches(boolean showNewMatches) {
		this.showNewMatches = showNewMatches;
	}
	
	public void setShowMessages(boolean showMessages) {
		this.showMessages = showMessages;
	}
	
	
	
	
}
