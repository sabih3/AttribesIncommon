package com.attribes.incommon.models;

public class NavDrawerItem {

	private String title;
    private int icon;
    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
	
    public NavDrawerItem(String title ){
        this.title = title;
        
    }
     
    public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }
    
    public String getTitle() {
		return title;
	}
	public int getIcon() {
		return icon;
	}
	public String getCount() {
		return count;
	}
	public boolean isCounterVisible() {
		return isCounterVisible;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public void setCounterVisible(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}
    
	
	
}
