package com.attribes.incommon;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.models.FriendsDetailModel;
import com.attribes.incommon.models.FriendsDetailModel.MatchedInterests;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.views.InterestLayout;
import com.google.gson.Gson;

public class StarMatchScreen extends BaseActivity implements OnClickListener{

	private ArrayList<MatchedInterests> matchedInterest;
	private static InterestLayout matchedInterestList;
	private AQuery aQuery;
	private String userId;
	private FriendsDetailModel profile;
	private Gson gson ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_star_match);
		
		initContents();
		if(getIntent().getExtras()!=null){
			this.userId = getIntent().getStringExtra("user_id");
		}
		
		getUserDetails(userId);
		
		
	}

	private void getUserDetails(String userId2) {
		
		String url = Constants.BaseUrl + Constants.UserDetail;
		
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(StarMatchScreen.this, "userDetailResult");
		
		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_token", getSmToken());
		cb.param("user_id", userId);
		aQuery.ajax(cb);

		
	}

	public void userDetailResult(String url, String json, AjaxStatus status){
		if(json != null){
			
			
			profile = gson.fromJson(json.toString(), FriendsDetailModel.class);
			
			setUi();
		}
		
	}
	private void setUi() {
		aQuery.id(R.id.starMatch_heading).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.starMatch_viewProfile).typeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
		aQuery.id(R.id.starMatch_interestMatched).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		
		setImage(profile.response.image_uri);
		populateInterests(profile.response.getMatchedInterests());
		aQuery.id(R.id.starMatch_viewProfile).clicked(this);
		
	}

	private void initContents() {
		aQuery = new AQuery(this);
		matchedInterestList = (InterestLayout)findViewById(R.id.starMatch_matchedInterestList);
		matchedInterest = new ArrayList<FriendsDetailModel.MatchedInterests>();
		gson = new Gson();
	}

	private void populateInterests(ArrayList<MatchedInterests>  matchedInterest) {
		LayoutInflater inflater = getLayoutInflater();
		matchedInterestList.removeAllViews();
		for(int i = 0 ; i < matchedInterest.size() ; i++){
			TextView interestTextView = (TextView) inflater.inflate(R.layout.gridview_text, null);
			interestTextView.setText(matchedInterest.get(i).title);
			matchedInterestList.addView(interestTextView);
		}		
	}
	
	private void setImage(String imageUri) {
		aQuery.id(R.id.starMatch_image).image(imageUri);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, MatchProfile.class);
		intent.putExtra("user_id", userId);
		startActivity(intent);
		finish();
		
		
	}
	
}
