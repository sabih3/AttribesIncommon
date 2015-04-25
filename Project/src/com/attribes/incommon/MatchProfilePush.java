 package com.attribes.incommon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.models.FriendsDetailModel;
import com.attribes.incommon.models.FriendsDetailModel.MatchedInterests;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.google.gson.Gson;

public class MatchProfilePush extends BaseActivity {
	
	private FriendsDetailModel profile;
	TextView nameAndAgeTextView;
	TextView distanceTextView;
	ArrayList matchedInterestList;
	String userId;
	AQuery aq;
	private String searchedInterst= "none",title;
	private boolean showMultipleSearchedItem;
	private Typeface lightFont;
	private Typeface regularFont;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_profile);
		setActionBarStyling();
		aq = new AQuery(this);
		
		if(getIntent().getExtras()!=null){
			userId = getIntent().getStringExtra("user_id");
			
			this.searchedInterst = getIntent().getExtras().getString("searched_interst","none");
		}
		
		callDetailApi(userId);
	}

	
	public void callDetailApi(String userId){
		String url = Constants.BaseUrl + Constants.UserDetail;
		
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(MatchProfilePush.this, "searchResult");
		
		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_token", getSmToken());
		cb.param("user_id", userId);
		aq.ajax(cb);

	}
	
	public void searchResult(String url, String json,AjaxStatus status){
		if(json != null){
			
			
			//populate model class
			Gson gson = new Gson();
			FriendsDetailModel obj = new FriendsDetailModel();
			profile = gson.fromJson(json.toString(), FriendsDetailModel.class);
			//profile.setList(obj);
			updateUI();
		}
	}
	
	public void updateUI(){
		lightFont = Typeface.createFromAsset(getAssets(), "fonts/Mark Simonson - Proxima Nova Light.ttf");
		regularFont = Typeface.createFromAsset(getAssets(), "fonts/Mark Simonson - Proxima Nova Regular.ttf");
		
		aq.id(R.id.createProfile_profilePic).image(profile.response.image_uri);
		aq.id(R.id.name).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aq.id(R.id.name).text(profile.response.name+", "+profile.response.age);
		
		aq.id(R.id.distance).typeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
		
		float distance =  (Float.valueOf(profile.response.distance) );
		
		if(distance >= 1 ){
			int dist = Math.round(distance);
			aq.id(R.id.distance).text(dist+" kilometers away").typeface(lightFont);
		}
		else{
			aq.id(R.id.distance).text("Less then a kilometers away").typeface(lightFont);
		}
		
		if(profile.response.is_login.equals("1")){
			aq.id(R.id.matchProfile_isOnline).visibility(ImageView.VISIBLE);
		}
		else{
			aq.id(R.id.matchProfile_isOnline).background(R.drawable.grey_circle);
			aq.id(R.id.matchProfile_isOnline).visibility(ImageView.VISIBLE);
		}
		
		if(profile.response.relationship != null){
			if(profile.response.relationship.equals("pending")){
				aq.id(R.id.add_friend).image(R.drawable.ic_friend_request_pending);
			}
			
			if(profile.response.relationship.equals("accepted")){
				aq.id(R.id.add_friend).image(R.drawable.ic_friend_request_accepted);
			}
		}
		
		if(profile.response.liked.equals("false")){
			aq.id(R.id.matchProfile_like).clicked(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					aq.id(R.id.matchProfile_like).image(R.drawable.ic_liked);
					sendLike();
					
					Flurry.getInstance().eventLiked();
				}	
			});
		}
		
		else{
			
			aq.id(R.id.matchProfile_like).image(R.drawable.ic_liked);
		}
		
		aq.id(R.id.chat).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(MatchProfilePush.this,ChatScreen.class);
				intent.putExtra("opponentName", profile.response.name);
				intent.putExtra("opponentQbId", profile.response.qb_id);
				startActivity(intent);
				finish();
			}
		});
		
		if(profile.response.relationship == null || profile.response.relationship.equals("rejected")){
			aq.id(R.id.add_friend).clicked(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					aq.id(R.id.add_friend).image(R.drawable.ic_friend_request_pending);
					String url = Constants.BaseUrl + Constants.FriendAdd;
					AjaxCallback<String> cb = new AjaxCallback<String>();        
					cb.url(url).type(String.class).weakHandler(MatchProfilePush.this, "friendAddResponse");
					cb.param("authorization", Constants.AUTHORIZATION);
					cb.param("user_id", profile.response.id);
					cb.param("sm_token", getSmToken());
					
					aq.ajax(cb);
					
					Flurry.getInstance().eventAddFriend();
				}
			});
			
		}
		
		aq.id(R.id.info).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleInformation();
			}
		});
		ArrayList<String> matchedIntrest = new ArrayList<String>();
		for(MatchedInterests interest : profile.response.matched_interests){
			matchedIntrest.add(interest.title);
		}
		aq.id(R.id.interest_grid).adapter( new ArrayAdapter<String>(this,
				R.layout.gridview_text, matchedIntrest));
	}
	
	private void toggleInformation() {
		if(aq.id(R.id.interests_layout).getView().getVisibility() == View.VISIBLE){
			aq.id(R.id.interests_layout).gone();
			aq.id(R.id.interest_text).text("Profile Information:").typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
			aq.id(R.id.profile_info).text(profile.response.description).visible();
			aq.id(R.id.profile_info).typeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
		}else{
			aq.id(R.id.interests_layout).visibility(View.VISIBLE);
			aq.id(R.id.interest_text).text("Interest matched:").typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
			aq.id(R.id.profile_info).gone();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.match_profile, menu);
		return true;
	}

	private void sendLike() {
		String url = Constants.BaseUrl + Constants.FRIEND_LIKE;
		
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(MatchProfilePush.this, "likeResult");
		
		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_token", getSmToken());
		cb.param("user_id", userId);
		aq.ajax(cb);
		
	}
	
	public void likeResult(String url, String json, AjaxStatus status){
		if(json != null){
			
		}
	}
	
	public void sendBlock(){
		String url = Constants.BaseUrl + Constants.FRIEND_BLOCK;
		
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(MatchProfilePush.this, "blockResult");
		
		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_token", getSmToken());
		cb.param("user_id", userId);
		aq.ajax(cb);
	}
	
	public void blockResult(String url, String json, AjaxStatus status){
		if(json != null){
			
		}
	}
	
	public void sendReport(){
		String url = Constants.BaseUrl + Constants.FRIEND_REPORT;
		
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(MatchProfilePush.this, "reportResult");
		
		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_token", getSmToken());
		cb.param("user_id", userId);
		aq.ajax(cb);
	}
	
	public void reportResult(String url, String json, AjaxStatus status){
		if(json != null){
			
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		
		if (id == R.id.action_block) {
			new AlertDialog.Builder(this)
			.setTitle("Block!")
			.setMessage(
					"Are you sure you want to block "+profile.response.name)
			.setPositiveButton("Block", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					sendBlock();
					Toast.makeText(getApplicationContext(), "Block Request send", Toast.LENGTH_SHORT).show();
				}
			})
			
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			})
			.show();
		}
		
		if(id == R.id.action_report){
			sendReport();
		}
		
		if(id == R.id.action_cancel){
			//TODO:Dismiss the menu
		}
		return super.onOptionsItemSelected(item);
	}
}
