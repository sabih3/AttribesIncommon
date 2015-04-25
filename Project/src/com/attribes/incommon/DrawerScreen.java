package com.attribes.incommon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.adapters.NavDrawerAdapter;
import com.attribes.incommon.api.ApiRequest;
import com.attribes.incommon.models.NavDrawerItem;
import com.attribes.incommon.util.Constants;

public class DrawerScreen extends BaseActivity{
	protected DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;
    protected NavDrawerAdapter adapter;
    private ApiRequest apiRequest;
	
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_screen_layout);
		initializeDrawer();
		apiRequest = new ApiRequest(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	protected void initializeDrawer() {
		navMenuTitles=getResources().getStringArray(R.array.nav_drawer_items);
		drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		drawerList=(ListView)findViewById(R.id.list_slidermenu);
		
		mTitle = drawerTitle = getTitle();
		ArrayList<NavDrawerItem>navDrawerItems = new ArrayList<NavDrawerItem>();
		
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8]));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9]));
		
		
		getUserName();
		adapter = new NavDrawerAdapter(getApplicationContext(), navDrawerItems, getUserFullName(),getUserImageUri());
		drawerList.setAdapter(adapter);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                getActionBar().show();
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                getActionBar().hide();
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
		
        
        drawerList.setOnItemClickListener(new SlideMenuClickListener());
        
//        if(isUserRegistered()){
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//        }
//        else{
//        	 drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        }
	}
    
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
	
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		// display view for selected nav drawer item
		displayView(position);
		
		}

		
}
	
	private void displayView(int position) {
		Intent intent;
		switch (position) {
		
		case 1:
			intent = new Intent(this, FriendScreen.class);
	        startActivity(intent);
			break;
		
		case 2:
			
			    SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
				SharedPreferences.Editor editor = registrationPrefs.edit();
				editor.putInt("notificationCount", 0);
				editor.commit();
				drawerList.invalidateViews();
			intent = new Intent(this, ActivityScreen.class);
			startActivity(intent);
			break;
			
		case 3:
//			intent = new Intent(this,GroupMainScreen.class);
//			startActivity(intent);
			
			if(this.getClass().getSimpleName().equals("SearchFriends"))
				drawerLayout.closeDrawers();
				else{
					intent = new Intent(this, SearchFriends.class);
					startActivity(intent);
				}
			break;
			
			
		case 4:
			intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		
		case 5:
			intent = new Intent(this,PrivacyPolicyScreen.class);
			startActivity(intent);
			break;
			
		case 6:
			intent = new Intent(this, TermsOfServiceScreen.class);
			startActivity(intent);
			break;
		
		case 7:
			startContactIntent();
			break;
		
		case 8:
			intent = new Intent(this, ShareScreen.class);
			startActivity(intent);
			break;
			
		case 9:
			showLogOutDialog();
			break;
		
		default:
			break;
		}
		
	}
	
	private void startContactIntent() {
		String subject=Constants.CONTACT_US_SUBJECT,
				body="",
				mailto=Constants.CONTACT_US_EMAIL;
				
		String URI="mailto:"+mailto+"?subject=" + subject + "&body=" + body;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//intent.putExtra("flag", true);
		Uri data = Uri.parse(URI);
		intent.setData(data);
		startActivity(intent);
//		startActivityForResult(intent, 54);
		
	}



	private void showLogOutDialog() {
		new AlertDialog.Builder(this)
			.setTitle("Confirm Logout")
			.setMessage(Constants.LOG_OUT_MESSAGE)
			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {

					apiRequest.setUserStatus(Constants.USER_STATUS_LOG_OUT, DrawerScreen.this, "responseSetUserStatus");
					
				}
				
			})
			.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})

			.show(); 
		
	}

	
	public void responseSetUserStatus(String url, String json, AjaxStatus status){
		if(json !=null ){
			boolean response = json.contains("response");
			if(response){
				
				saveSmToken("");
				setUserImageUri("");
				setUserFullName("");
				setUserName("");
				updateUserPreferences(false);
				ShowLoginScreen();
							
			}
			else{
				ShowToast(this, Constants.GENERAL_ERROR_MESSAGE);
			}
		}
	}
	
	private void ShowLoginScreen() {
		Intent intent=new Intent(this, MainActivity.class);
		if(!facebookUserRegistsred()){
			intent.putExtra("logOut", true);
		}
		startActivity(intent);
		overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
		finish();
		
		
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
	
	public void setCustomFont(TextView textView, String fontName){
		Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/"+fontName);
		if(textView!=null)
			textView.setTypeface(custom_font);
	}
}
