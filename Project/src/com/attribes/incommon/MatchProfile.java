package com.attribes.incommon;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.api.ApiRequest;
import com.attribes.incommon.models.FriendsDetailModel;
import com.attribes.incommon.models.FriendsDetailModel.Interests;
import com.attribes.incommon.models.FriendsDetailModel.MatchedInterests;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.attribes.incommon.views.InterestLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

@SuppressLint("DefaultLocale")
public class MatchProfile extends ActionBarActivity {

	private FriendsDetailModel profile;
	private String userId, searchedInterst = "none", title, opponentQbUserId;
			
	private boolean showMultipleSearchedItem;
	
	private InterestLayout otherInterestLayout;
	private ApiRequest api;
	private float distance;
	int roundedDistance;
	AQuery aq;
	Typeface lightFont, regularFont;
	Boolean userLiked;
    private ImageView imageView;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_profile_interest);
		api = new ApiRequest(this);
		aq = new AQuery(this);
		imageView=(ImageView)findViewById(R.id.createProfile_profilePic);
		otherInterestLayout = (InterestLayout) findViewById(R.id.matchProfile_otherInterestList);

		setActionBarStyling();
		if (savedInstanceState != null) {

			FriendsDetailModel profile = getProfileFromPrefs();
			if (profile != null) {
				
				updateUI(profile);
			}
			
			userId = savedInstanceState.getString("userId") ;
			searchedInterst = savedInstanceState.getString("searchedInterst");
			showMultipleSearchedItem = savedInstanceState.getBoolean("showMultipleSearchedItem");
			title = savedInstanceState.getString("title");
			getActionBar().setTitle(title);
			
			if (showMultipleSearchedItem) {
				// TODO:Commented here 78
				// aq.id(R.id.other_intersts_grid).gone();
				aq.id(R.id.matchProfile_otherInterestList).gone();
				aq.id(R.id.other_intersts).gone();
			}
			
			
		}

		else {
				
			if (getIntent() != null) {
				if (getIntent().getExtras().getString("user_id") != null) {
					userId = getIntent().getExtras().getString("user_id");
				} else {
					userId = getIntent().getExtras().getString(
							"opponentQbUserId");
				}
				searchedInterst = getIntent().getExtras().getString(
						"searched_interst", "none");
				showMultipleSearchedItem = getIntent().getBooleanExtra(
						"showMultipleSearchedItem", false);
				title = getIntent().getExtras().getString("title");
			}
			

			/* Hiding other interest when opened from match fragment */
			if (showMultipleSearchedItem) {
				// TODO:Commented here 78
				// aq.id(R.id.other_intersts_grid).gone();
				aq.id(R.id.matchProfile_otherInterestList).gone();
				aq.id(R.id.other_intersts).gone();
			}

			if (getIntent().getExtras().getString("user_id") != null) {
				
				api.getUserDetailApi(userId, this, "searchResult",
						MasterUser.getInstance().getUserSmToken());
			} else {
				
				opponentQbUserId = getIntent().getExtras().getString(
						"opponentQbUserId");
				getUserDetail(opponentQbUserId);
			}
		}

	}

	public void searchResult(String url, String json, AjaxStatus status) {
		if (json != null) {
			// populate model class
			Gson gson = new Gson();
			profile = gson.fromJson(json.toString(), FriendsDetailModel.class);
			
			updateUI(profile);
		}
	}

	public void friendAddResponse(String json, AjaxStatus status) {
		if (json != null) {

		}
	}

	public void updateUI(final FriendsDetailModel profile) {
		float distance = 0;
		lightFont = Typeface.createFromAsset(getAssets(),
				"fonts/Mark Simonson - Proxima Nova Light.ttf");
		regularFont = Typeface.createFromAsset(getAssets(),
				"fonts/Mark Simonson - Proxima Nova Regular.ttf");
        Picasso.with(this).load(profile.response.image_uri).placeholder(R.drawable.human_place_holder).into(imageView);
		//aq.id(R.id.createProfile_profilePic).image(profile.response.image_uri);
		aq.id(R.id.name)
				.text(profile.response.name + ", " + profile.response.age)
				.typeface(regularFont);
		if(profile.response.distance != null){
			distance = (Float.valueOf(profile.response.distance));
		}
		

		if (distance >= 1) {
			int dist = Math.round(distance);
			aq.id(R.id.distance).text(dist + " kilometers away")
					.typeface(lightFont);
		} else {
			aq.id(R.id.distance).text("Less then a kilometers away")
					.typeface(lightFont);
		}

		if (profile.response.is_login.equals("1")) {
			aq.id(R.id.matchProfileInterest_isOnline).visibility(
					ImageView.VISIBLE);
		} else {
			aq.id(R.id.matchProfileInterest_isOnline).background(
					R.drawable.grey_circle);
			aq.id(R.id.matchProfileInterest_isOnline).visibility(
					ImageView.VISIBLE);

		}

		if (profile.response.relationship != null) {
			if (profile.response.relationship.equals("pending")) {
				aq.id(R.id.add_friend).image(
						R.drawable.ic_friend_request_pending);
			}

			if (profile.response.relationship.equals("accepted")) {
				aq.id(R.id.add_friend).image(
						R.drawable.ic_friend_request_accepted);
			}
		}

		if (profile.response.liked.equals("false")) {
			aq.id(R.id.matchProfileInterest_like).clicked(
					new OnClickListener() {

						@Override
						public void onClick(View view) {
							aq.id(R.id.matchProfileInterest_like).image(
									R.drawable.ic_liked);
							sendLike();
							Flurry.getInstance().eventLiked();
						}
					});
		}

		else {

			aq.id(R.id.matchProfileInterest_like).image(R.drawable.ic_liked);
		}
		aq.id(R.id.chat).clicked(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MatchProfile.this, ChatScreen.class);
				intent.putExtra("opponentUserId", profile.response.id);
				intent.putExtra("opponentName", profile.response.name);
				intent.putExtra("opponentQbId", profile.response.qb_id);
                intent.putExtra(Constants.EXTRA_CHAT_MODE, Constants.Mode.PRIVATE);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_left_in,
						R.anim.anim_left_out);
				finish();
			}
		});

		if (profile.response.relationship == null
				|| profile.response.relationship.equals("rejected")) {
			aq.id(R.id.add_friend).clicked(new OnClickListener() {

				@Override
				public void onClick(View view) {

					aq.id(R.id.add_friend).image(
							R.drawable.ic_friend_request_pending);

					String url = Constants.BaseUrl + Constants.FriendAdd;
					AjaxCallback<String> cb = new AjaxCallback<String>();
					cb.url(url)
							.type(String.class)
							.weakHandler(MatchProfile.this, "friendAddResponse");
					cb.param("authorization", Constants.AUTHORIZATION);
					cb.param("user_id", profile.response.id);
					cb.param("sm_token", MasterUser.getInstance()
							.getUserSmToken());

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
		ArrayList<String> forOtherIntrest = new ArrayList<String>();
		for (Interests interest : profile.response.interests) {
			if (!searchedInterst.toLowerCase().equals(
					interest.title.toLowerCase()))
				forOtherIntrest.add(interest.title);
		}

		aq.id(R.id.interest_text).text("Interest searched:")
				.typeface(regularFont);

		// aq.id(R.id.searched_intersts).text(Utils.getFirstLetterCapital(searchedInterst));

		aq.id(R.id.other_intersts).text("Other Interests: ")
				.typeface(regularFont);

		populateInterests(forOtherIntrest);

		ArrayList<String> interesrMatched = new ArrayList<String>();
		if (showMultipleSearchedItem) {
			aq.id(R.id.interest_text).text("Interest matched:")
					.typeface(regularFont);
			for (MatchedInterests interest : profile.response.matched_interests) {

				interesrMatched.add(interest.title);
			}

		} else {
			ArrayList<MatchedInterests> matched_interests = profile.response.matched_interests;
			if (matched_interests.size() != 0) {
				interesrMatched.add(searchedInterst);
			} else {
				interesrMatched.add(searchedInterst);
			}
		}

		ExpandableHeightGridView searchedGridView = (ExpandableHeightGridView) findViewById(R.id.searched_intersts);

		searchedGridView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.searchinterest_gridview, interesrMatched));

		searchedGridView.setExpanded(true);
	}

	private void populateInterests(ArrayList<String> forOtherIntrest) {
		LayoutInflater inflater = getLayoutInflater();
		otherInterestLayout.removeAllViews();
		int size = forOtherIntrest.size();
		for (int i = 0; i < size; i++) {
			TextView interestTextView = (TextView) inflater.inflate(
					R.layout.gridview_text, null);
			interestTextView.setText(forOtherIntrest.get(i));
			otherInterestLayout.addView(interestTextView);
		}

	}

	private void toggleInformation() {

		if (aq.id(R.id.intersts_layout).getView().getVisibility() == View.VISIBLE) {
			aq.id(R.id.intersts_layout).gone();
			aq.id(R.id.interest_text).text("Profile Information:")
					.typeface(regularFont);
			aq.id(R.id.interest_text).visible();
			aq.id(R.id.profile_info).text(profile.response.description)
					.visible().typeface(lightFont);
		} else {
			aq.id(R.id.intersts_layout).visible();
			aq.id(R.id.interest_text).visible();
			if (showMultipleSearchedItem) {

				aq.id(R.id.interest_text).text("Interest matched:")
						.typeface(regularFont);
			} else
				aq.id(R.id.interest_text).text("Interest searched:")
						.typeface(regularFont);
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
		cb.url(url).type(String.class)
				.weakHandler(MatchProfile.this, "likeResult");

		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_token", MasterUser.getInstance().getUserSmToken());
		cb.param("user_id", userId);
		aq.ajax(cb);

	}

	public void likeResult(String url, String json, AjaxStatus status) {
		if (json != null) {

		}
	}

	public void sendBlock() {
		String url = Constants.BaseUrl + Constants.FRIEND_BLOCK;

		AjaxCallback<String> cb = new AjaxCallback<String>();
		cb.url(url).type(String.class)
				.weakHandler(MatchProfile.this, "blockResult");

		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_token", MasterUser.getInstance().getUserSmToken());
		cb.param("user_id", userId);
		aq.ajax(cb);
	}

	public void blockResult(String url, String json, AjaxStatus status) {
		if (json != null) {

		}
	}

	public void sendReport() {
		String url = Constants.BaseUrl + Constants.FRIEND_REPORT;

		AjaxCallback<String> cb = new AjaxCallback<String>();
		cb.url(url).type(String.class)
				.weakHandler(MatchProfile.this, "reportResult");

		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_token", MasterUser.getInstance().getUserSmToken());
		cb.param("user_id", userId);
		aq.ajax(cb);
	}

	public void reportResult(String url, String json, AjaxStatus status) {
		if (json != null) {

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		if (id == R.id.action_block) {
			new AlertDialog.Builder(this)
					.setTitle("Block!")
					.setMessage(
							"Are you sure you want to block "
									+ profile.response.name)
					.setPositiveButton("Block",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									sendBlock();
									Toast.makeText(getApplicationContext(),
											"Block Request send",
											Toast.LENGTH_SHORT).show();
								}
							})

					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							}).show();
		}

		if (id == R.id.action_report) {
			sendReport();
		}

		if (id == R.id.action_cancel) {
			// TODO:Dismiss the menu
		}
		return super.onOptionsItemSelected(item);
	}

	public void getUserDetail(String qbId) {
		String url = Constants.BaseUrl + Constants.UserDetail;

		AjaxCallback<String> callBack = new AjaxCallback<String>();
		callBack.url(url).type(String.class).weakHandler(this, "userDetail");

		callBack.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		callBack.param("sm_token", MasterUser.getInstance().getUserSmToken());
		callBack.param("qb_id", qbId);

		aq.ajax(callBack);
	}

	public void userDetail(String url, String json, AjaxStatus status) {
		if (json != null) {
			Gson gson = new Gson();

			profile = gson.fromJson(json.toString(), FriendsDetailModel.class);

			updateUI(profile);
		}
	}

	protected void setActionBarStyling() {
		ActionBar bar = getSupportActionBar();

		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ab_solid_background));
		// bar.setHomeAsUpIndicator(R.drawable.ic_ab_back_holo_light);
		bar.setIcon(R.drawable.logo);
		bar.setLogo(R.drawable.logo);
		// bar.setDisplayUseLogoEnabled(true);
		// bar.setDisplayShowHomeEnabled(true);
		//
		// bar.setDisplayHomeAsUpEnabled(true);
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

	public void setCustomFont(TextView textView, String fontName) {
		Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/"
				+ fontName);
		if (textView != null)
			textView.setTypeface(custom_font);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("userId", userId);
		savedInstanceState.putString("opponentQbUserId", opponentQbUserId);
		savedInstanceState.putString("searchedInterst", searchedInterst);
		savedInstanceState.putString("title", title);
		savedInstanceState.putBoolean("showMultipleSearchedItem", showMultipleSearchedItem);
		
		saveProfileInPrefs(profile);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
	}

	private void saveProfileInPrefs(FriendsDetailModel profile) {
		SharedPreferences activityPreferences = getPreferences(Activity.MODE_PRIVATE);
		Editor editor = activityPreferences.edit();
		Gson gson = new Gson();

		editor.putString("profile", gson.toJson(profile));

		editor.commit();
	}

	private FriendsDetailModel getProfileFromPrefs() {
		Gson gson = new Gson();
		SharedPreferences activityPreferences = getPreferences(Activity.MODE_PRIVATE);

		String stringProfile = activityPreferences.getString("profile", null);

		profile = gson.fromJson(stringProfile, FriendsDetailModel.class);

		return profile;

	}

}
