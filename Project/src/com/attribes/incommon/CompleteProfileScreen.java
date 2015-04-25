package com.attribes.incommon;


import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.adapters.InterestViewAdapter;
import com.attribes.incommon.api.ApiRequest;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;


public class CompleteProfileScreen extends DrawerScreen {
	public CompleteProfileScreen() {
		
		
	}
	private ImageView profilePicView;
	private TextView userNameAndAge;
	private TextView interestHeadingText;
	JSONObject userDetail;
	private String socialMediaType;

	private DrawerLayout mDrawer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.fragment_complete_profile_screen);
		ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
		getLayoutInflater().inflate(R.layout.fragment_complete_profile_screen,content,true);
		setActionBarStyling();
		
		mDrawer = (DrawerLayout) findViewById(R.id.complete_drawer_layout);
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		
		Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Mark Simonson - Proxima Nova Regular.ttf");
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
		if(isUserRegistered()){
			mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			initializeDrawer();
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
        
        interestHeadingText = (TextView) findViewById(R.id.completeProfile_InterestHeading);
        setCustomFont(interestHeadingText, Constants.FONT_PROXI_REGULAR);
        profilePicView =(ImageView) findViewById(R.id.createProfile_profilePic);
 
//        if(!isUserRegistered()){
//        	
//        	ChatHandler.getInstance().signUpQbUser(getSmToken(), getSmToken(),getUserName(), getUserImageUri(), new BaseActivity());
//        }
        
	    if(facebookUserRegistsred()){
			
	    	displayProfileImage(MasterUser.getInstance().getUserImageUri(),profilePicView);
			setProfileNameAndAge();
			
			setProfileDescription(fetchProfileDescription());
			AutoGridView gridview = (AutoGridView) findViewById(R.id.complete_profile_interestView);
			
			//gridview.setExpanded(true);
			if(MasterUser.getInstance().getUserInterests().size() >= 5)
		    gridview.setAdapter(new InterestViewAdapter(this, fetchUserInterests()));
			
		}
		
		else{

			displayProfileImage(MasterUser.getInstance().getUserImageUri(),profilePicView);
					//.replace("?sz=50", "?sz=1000"),profilePicView);
			setProfileNameAndAge();
			setProfileDescription(fetchProfileDescription());
			AutoGridView gridview = (AutoGridView) findViewById(R.id.complete_profile_interestView);
			//gridview.setExpanded(true);
		    gridview.setAdapter(new InterestViewAdapter(this, fetchUserInterests()));
			
		}
	
	TextView interestsTextView=(TextView) findViewById(R.id.completeProfile_InterestHeading);
	interestsTextView.setTypeface(custom_font);
}
	private void setProfileNameAndAge() {
		
		userNameAndAge=(TextView) findViewById(R.id.createProfile_name);
		setCustomFont(userNameAndAge, Constants.FONT_PROXI_REGULAR);
		userNameAndAge.setText(MasterUser.getInstance().getUserName()+", "+MasterUser.getInstance().getUserAge());
		
		
	}
	private void displayProfileImage(String userImageUri, ImageView profilePicView) {
		Picasso.with(this).load(userImageUri).into(profilePicView);
		
	}

	private void setProfileDescription(String profileDescription) {
		
		TextView profileDescriptionTextView=(TextView) findViewById(R.id.profileDescription_TextView);
		if(profileDescription != null && profileDescription.isEmpty())
			profileDescriptionTextView.setVisibility(View.GONE);
		setCustomFont(profileDescriptionTextView, Constants.FONT_PROXI_LIGHT);
		profileDescriptionTextView.setText(profileDescription);
	
	}
	
	private String fetchProfileDescription() {
		String profileDescription = MasterUser.getInstance().getUserDescription();
		
		return profileDescription;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.complete_profile_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		if (id == R.id.action_edit) {
			Intent mIntent = new Intent(CompleteProfileScreen.this, CreateProfileScreen.class);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			mIntent.putExtra(Constants.IS_EDIT, true);
			startActivity(mIntent);

			
		}


		if(id == R.id.action_save){
			if(!isUserRegistered()){
				String gender = getUserGenderPreference();
				
				if(MasterUser.getInstance().getUserInterests().size() >=5 && MasterUser.getInstance().getUserImageUri().length() > 1 
						&& gender.length() > 1 && !Double.valueOf(MasterUser.getInstance().getUserLat()).equals(0) 
						&& !Double.valueOf(MasterUser.getInstance().getUserLon()).equals(0) && !Integer.valueOf(MasterUser.getInstance().getUserAge()).equals(0)){
//				startActivity(new Intent(CompleteProfileScreen.this, ConnectScreen.class));
//				this.finish();
				ApiRequest api = new ApiRequest(this);
				socialMediaType = facebookUserRegistsred() ? "1" : "2";

				
				api.userCreate(socialMediaType, 
						MasterUser.getInstance().getUserSmToken(), 
						MasterUser.getInstance().getUserLat(), 
						MasterUser.getInstance().getUserLon(),
						getDeviceId(), 
						MasterUser.getInstance().getUserDescription(), 
						MasterUser.getInstance().getUserImageUri(),
						MasterUser.getInstance().getUserName(), 
						MasterUser.getInstance().getUserAge(),
						MasterUser.getInstance().getUserInterests(),
						getParseObjectId(),
						getQbUserId(),gender, CompleteProfileScreen.this,"result");
				
				       
//				Intent serviceIntent = new Intent(this,BackgroundService.class);
//				startService(serviceIntent);
				}
				else{
					
					Toast.makeText(this, "Sorry we could'nt get your complete information, please try again",Toast.LENGTH_LONG).show();
					Intent i = new Intent(CompleteProfileScreen.this, CreateProfileScreen.class);
					this.startActivity(i);
					
				}

			}
			
			else{
				
				ApiRequest api = new ApiRequest(this);
				api.requestUpdateUser(MasterUser.getInstance(), this, "responseRequestUpdate");
				
				Flurry.getInstance().eventProfileEdit();
				updateQbUserImage(getUserImageUri());
				startActivity(new Intent(CompleteProfileScreen.this, ConnectScreen.class));
				this.finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void updateQbUserImage(String userImageUri) {
		QBUser user = new QBUser();
		user.setId(Integer.parseInt(MasterUser.getInstance().getUserQbId()));
		user.setLogin(MasterUser.getInstance().getUserQbLogin());
		user.setPassword(MasterUser.getInstance().getUserQbPassword());
		user.setCustomData(userImageUri);
		QBUsers.updateUser(user, new QBEntityCallbackImpl<QBUser>(){
			
			@Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
				
                
            }

            @Override
            public void onError(List<String> strings) {

            }
		});
			
		}
	public void result(String url, String json, AjaxStatus status){
			if(json!=null){
				
				// setUserRegistered();
				startActivity(new Intent(CompleteProfileScreen.this, ConnectScreen.class));
				this.finish();
				//ChatHandler.getInstance().signUpQbUser(getSmToken(), getSmToken(),getUserName(), getUserImageUri(), new BaseActivity());
			}
		}
	private void showSettingsAlert() {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	
			// Setting Dialog Title
			alertDialog.setTitle("Interests");
	
			// Setting Dialog Message
			alertDialog.setMessage("Please select atleast 5 Interests");
	
	
			alertDialog.setNegativeButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
	
			alertDialog.show();
			
		}
	public void responseRequestUpdate(String url, String json, AjaxStatus status){
		if(json!=null){
			
		}
	}

	

}
