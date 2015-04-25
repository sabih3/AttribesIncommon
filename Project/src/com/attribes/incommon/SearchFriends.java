package com.attribes.incommon;

import java.util.ArrayList;


//import android.R.bool;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
//import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
//import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
//import android.widget.Toast;


import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
//import com.attribes.incommon.DAO.User.Interest;
import com.attribes.incommon.adapters.NavDrawerAdapter;
import com.attribes.incommon.api.ApiRequest;
import com.attribes.incommon.models.MasterUser;
//import com.attribes.incommon.api.RequestAddFriend;
import com.attribes.incommon.models.NavDrawerItem;
import com.attribes.incommon.models.SearchFriendsModel;
import com.attribes.incommon.models.SearchFriendsModel.User;
//import com.attribes.incommon.models.SearchFriendsModel.Response;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.attribes.incommon.util.Utils;
//import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;



public class SearchFriends extends BaseActivity implements
SearchView.OnQueryTextListener , OnItemClickListener {

	AQuery aq;
	SearchView searchView;
	String searchedText;
	protected DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;
    protected NavDrawerAdapter adapter;
    BaseActivity baseActivity;
    private Dialog progressDialog;
    private ApiRequest apiRequest;
    private boolean drawerIsClosed;

    ArrayList<SearchFriendsModel.User> array;
    private int length;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search_friends);
		baseActivity=new BaseActivity();
		baseActivity.context=this;
		apiRequest = new ApiRequest(this);
		setActionBarStyling();
		drawerIsClosed = true;
		initializeDrawer();
		
		callSearchApiForAll();
		
//		ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
//		getLayoutInflater().inflate(R.layout.activity_search_friends,content,true);
		
		aq = new AQuery(this);
		aq.id(R.id.friendsList).itemClicked(this);
		
		 getActionBar().setTitle("InCommon");
		
//		if (savedInstanceState == null) {
//			// on first time display view for first nav item
//			displayView(1);
//		}
	}
	
	
	
	protected void initializeDrawer() {
		navMenuTitles=getResources().getStringArray(R.array.nav_drawer_items);
		drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		drawerList=(ListView)findViewById(R.id.list_slidermenu);
		
		//mTitle = drawerTitle = getTitle();
		mTitle = drawerTitle = "InCommon";
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
	//	navDrawerItems.add(new NavDrawerItem(navMenuTitles[10]));
		
		
		adapter = new NavDrawerAdapter(getApplicationContext(), navDrawerItems,getUserFullName(),getUserImageUri());
		drawerList.setAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle("InCommon");
                getActionBar().show();
                drawerIsClosed = true;
               
                
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {

            	
            	
            	getActionBar().setTitle(drawerTitle);
            	
            	
            	 getActionBar().hide();
                drawerIsClosed = false;
               
                
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
		
        
        drawerList.setOnItemClickListener(new SlideMenuClickListener());
		
		
		aq = new AQuery(this);
		aq.id(R.id.friendsList).itemClicked(this);

		
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
			
//		case 3:
//			//TODO: have to change this
//			Toast.makeText(getApplicationContext(), "Feature to be implemented ", Toast.LENGTH_SHORT).show();
//			break;
			
		case 3:
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
		
		intent.putExtra("flag", true);
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

					apiRequest.setUserStatus(Constants.USER_STATUS_LOG_OUT, SearchFriends.this, "responseSetUserStatus");
					
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
				updateUserPreferences(false);
				setUserImageUri("");
				setUserFullName("");
				setUserName("");
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



	public void callSearchApi(String searchText){
		
		aq.id(R.id.friendsList).invisible();
		aq.id(R.id.searched_item_layout).visible();
		aq.id(R.id.last_searched).text(Utils.getFirstLetterCapital(searchText));
		
		String url = Constants.BaseUrl + Constants.SearchUser;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(SearchFriends.this, "searchResult");
		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", MasterUser.getInstance().getUserSmToken());
		cb.param("interest", searchText);
		aq.ajax(cb);
		
		Flurry.getInstance().eventSearchInterest(searchText);

	}
	
	public void callSearchApiForAll(){
		//progressDialog = ProgressDialog.show(SearchFriends.this, "", "Preparing the list", true,true);
		
		String url = Constants.BaseUrl + Constants.SearchUser;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(SearchFriends.this, "searchResult");
		
		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", MasterUser.getInstance().getUserSmToken());
		cb.param("user_id", MasterUser.getInstance().getUserId());
		aq.ajax(cb);
	}

	public void searchResult(String url, String json,AjaxStatus status){
		if(json != null){
			//progressDialog.dismiss();
			//populate model class
			Gson gson = new Gson();
			SearchFriendsModel obj = new SearchFriendsModel();
			try {
			
				obj = gson.fromJson(json.toString(), SearchFriendsModel.class);
				SearchFriendsModel.getInstance().setList(obj);
			} catch (Exception e) {
				// TODO: handle exception
				e.toString();
			}
			 length = SearchFriendsModel.getInstance().response.users.size();
			 array  = SearchFriendsModel.getInstance().response.users;
			 if(length != 0){
		        quickSort(0, length - 1);

				SearchFriendsAdapter adapter = new SearchFriendsAdapter(SearchFriends.this,array);
				aq.id(R.id.friendsList).getListView().setAdapter(adapter);
				aq.id(R.id.searched_item_layout).visible();
				aq.id(R.id.no_friends).gone();
				aq.id(R.id.friendsList).visible();
				
				
				aq.id(R.id.last_searched).text(Utils.getFirstLetterCapital(SearchFriendsModel.getInstance().response.interest));
			 }
			 
			if(obj.response.users.size() == 0){
				aq.id(R.id.friendsList).invisible();
				aq.id(R.id.no_friends).visible();
				adapter.notifyDataSetChanged();
			
			}
			
			adapter.notifyDataSetChanged();	
		}

	}

	private void hideSoftKeyboard(SearchView searchView) {
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(drawerIsClosed){
		getMenuInflater().inflate(R.menu.select_interest_screen, menu);
		}
		else{
			getMenuInflater().inflate(R.menu.no_search_bar, menu);
		}
		
		if ( this.getClass().getSimpleName().equals("SearchFriends") && drawerIsClosed) 
		{
			searchView = (SearchView) menu.findItem(R.id.menu_search)
					.getActionView();

			
			searchView.setIconifiedByDefault(false);
			searchView.setOnQueryTextListener(this);
			
			int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
			
			TextView textView = (TextView) searchView.findViewById(id);
			
			textView.setTextColor(Color.GRAY);
			textView.setHintTextColor(Color.GRAY);
			
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	
	
	
	/*@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
//		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
//        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}*/


	@Override
	public boolean onQueryTextChange(String searchText) {
		
		return true;
	}


	@Override
	public boolean onQueryTextSubmit(String searchText) {
		this.searchedText = searchText;
		callSearchApi(searchText);
		aq.id(R.id.searched_item_layout).visible();
		aq.id(R.id.last_searched).text(Utils.getFirstLetterCapital(searchText));
		searchView.setQuery("",false);
		
		hideSoftKeyboard(searchView);
		return true;
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
	
	@Override
	protected void onActivityResult(int requestCode,int responseCode, Intent intent){
		super.onActivityResult(requestCode, responseCode, intent);
		
		if(requestCode==54){
			showDialog();
		}
		
	}
	
	private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle(Constants.CONTACT_US_DIALOG_TITLE)
				.setMessage(
						Constants.CONTACT_US_DIALOG_MESSAGE)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				}).show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long Id) {
		Intent mIntent = new Intent(SearchFriends.this, MatchProfile.class);
		
		mIntent.putExtra("user_id", SearchFriendsModel.getInstance().response.users.get(position).id);
		mIntent.putExtra("searched_interst", Utils.getFirstLetterCapital(SearchFriendsModel.getInstance().response.interest));
		mIntent.putExtra("title", "Search Profile");
		startActivity(mIntent);
		this.finish();
	}

	private void quickSort(int lowerIndex, int higherIndex) {
        
        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        User pivot =array.get(lowerIndex+(higherIndex-lowerIndex)/2) ;
        
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a number
             * from right side which is less then the pivot value. Once the search
             * is done, then we exchange both numbers.
             */
            while (Float.valueOf(array.get(i).distance) < Float.valueOf(pivot.distance)) {
                i++;
            }
            while (Float.valueOf(array.get(j).distance) > Float.valueOf(pivot.distance)) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(lowerIndex, j);
        if (i < higherIndex)
            quickSort(i, higherIndex);
    }
 
    private void exchangeNumbers(int i, int j) {
    	User temp =array.get(i);
        array.set(i, array.get(j));
        array.set(j, temp);
       //array[j] = temp;
    }

	
	
}


