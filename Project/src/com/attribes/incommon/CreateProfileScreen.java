package com.attribes.incommon;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.facebook.widget.ProfilePictureView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewpagerindicator.CirclePageIndicator;


public class CreateProfileScreen extends DrawerScreen {

	public CreateProfileScreen() {

	}

	private EditText profileDescription;
	private TextView profileDescriptionTextView;
	private TextView maxDescriptionCharsTextView;
	private Button chooseInterestButton;
	private String userDescription;
	private String userImageUri;
	private Intent sourceIntent;
	private ProgressBar progressDialog;
	private DrawerLayout mDrawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.fragment_create_profile_screen);
		ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
		getLayoutInflater().inflate(R.layout.fragment_create_profile_screen,content,true);
		
		mDrawer = (DrawerLayout) findViewById(R.id.fragment_drawer_layout);
		mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		chooseInterestButton = (Button) findViewById(R.id.createProfile_chooseInterests);
		chooseInterestButton.setEnabled(false);
		progressDialog = (ProgressBar) findViewById(R.id.createProfile_progressBar);
		setActionBarStyling();
		
		getActionBar().setDisplayHomeAsUpEnabled(false);
		
		if(isUserRegistered()){
			mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			initializeDrawer();
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		initializeImageLoader(this);
		
		if(getIntent().getStringExtra("dateOfBirth") != null){
		
			String dateOfBirth = getIntent().getStringExtra("dateOfBirth");
			
			setUserAge(getAge(segregateBirthdayInfo(dateOfBirth)));
		}
		
		CircularImageView circularImageView = (CircularImageView) findViewById(R.id.createProfile_circle);
	
			circularImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					
					Intent intent=new Intent(CreateProfileScreen.this, ProfileAlbumListScreen.class);
					if (getIntent().getBooleanExtra(Constants.IS_EDIT, false)){
						intent.putExtra("is_edit_mode", true);
					}
					startActivity(intent);
					
				}
			});
		
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		profileDescription = (EditText) findViewById(R.id.createProfile_description);
		
		TextView changeProfilePic = (TextView) findViewById(R.id.change_profile_pic);
		
		setCustomFont(chooseInterestButton, Constants.FONT_PROXI_REGULAR);
		setCustomFont(changeProfilePic, Constants.FONT_PROXI_REGULAR);
		profileDescription.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				profileDescription.setFocusable(true);

			}
		});
		profileDescription.setOnEditorActionListener(new OnEditorActionListener() {

	        @Override
	        public boolean onEditorAction(TextView v, int actionId,
	                KeyEvent event) {
	            if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
	                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                in.hideSoftInputFromWindow(profileDescription
	                        .getApplicationWindowToken(),
	                        InputMethodManager.HIDE_NOT_ALWAYS);
	               return true;

	            }
	            return false;
	        }
	    });
		
//		if (getIntent() != null)
//			if (getIntent().getBooleanExtra(Constants.IS_EDIT, false))
				if(getDescription() !="")
				profileDescription.setText(getDescription());
		
		maxDescriptionCharsTextView = (TextView) findViewById(R.id.createProfile_maxCharacters);
		setCustomFont(maxDescriptionCharsTextView,
				Constants.FONT_PROXI_LIGHT);
		setCustomFont(profileDescription, Constants.FONT_PROXI_LIGHT);

		profileDescriptionTextView = (TextView) findViewById(R.id.createProfile_descriptionView);

//		BaseActivity.imageLoader.init(ImageLoaderConfiguration
//				.createDefault(getApplicationContext()));

		sourceIntent = getIntent();
		String str = sourceIntent.getStringExtra("dateOfBirth");
		if(str == null){
			str= "";
		}
		// Very First time Creation of profile
		if (sourceIntent.getExtras() != null && str.isEmpty()){
			
			displayDescriptionInEditMode(MasterUser.getInstance().getUserDescription());
			
			 if (!facebookUserRegistsred()) {
				showView(progressDialog);
				setProfileImage();
				setProfileNameAndAge();


			} else {
				showView(progressDialog);
				
				userImageUri = MasterUser.getInstance().getUserImageUri();
				if (userImageUri.isEmpty()) {
					makeMeRequest();
				} else {
					setProfileImage();
				}

				setFacebookNameAndAge();

			}
			
		}
		
		else if (!facebookUserRegistsred()) {
			showView(progressDialog);
			setProfileImage();
			setProfileNameAndAge();
			

		} else {
			showView(progressDialog);
			userImageUri = MasterUser.getInstance().getUserImageUri();
			if (userImageUri.isEmpty()) {
				makeMeRequest();
			} else {
				setProfileImage();
				
			}

			setFacebookNameAndAge();
			
		}

		profileDescription.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

				int max = 430 - arg0.toString().length();
				maxDescriptionCharsTextView.setText("" + max + " characters");
			}
		});

		profileDescription
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							userDescription = v.getText().toString();
							setDescription(v.getText().toString());
							hideEditor();
							displayDescription(v.getText().toString());
						}
						return false;
					}

				});
	}

	private void hideEditor() {
		profileDescription.setVisibility(EditText.GONE);

	}

	private void displayDescription(String description) {
		maxDescriptionCharsTextView.setVisibility(View.GONE);
		profileDescriptionTextView.setVisibility(TextView.VISIBLE);
		setCustomFont(profileDescriptionTextView, Constants.FONT_PROXI_LIGHT);
		profileDescriptionTextView.setText(description);
	}

	private void registerDescription(String personDescription) {
		SharedPreferences registrationPrefs = getSharedPreferences(
				Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = registrationPrefs.edit();
		editor.putString(Constants.PERSON_DESCRIPTION, personDescription);
		editor.commit();
		//TODO: Master user object populated
		MasterUser.getInstance().setUserDescription(personDescription);
	}

	private void displayDescriptionInEditMode(String description) {
		profileDescription.setText(description);
		if(description.length() > 0)
		maxDescriptionCharsTextView.setText(430 - description.length() +   " characters");
	}

	protected void setFacebookNameAndAge() {
		String name;
		String[] firstName;
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		currentUser.get("profile");
		JSONObject userProfile = currentUser.getJSONObject("profile");
		try {
			
			name = userProfile.get("name").toString();
			if(!(name.isEmpty()))
				{
					firstName = name.split(" ");
					name = firstName[0];
						}
			
			
			setUserName(name);
			
			int userAge = Integer.parseInt(MasterUser.getInstance().getUserAge());
			TextView nameAndAgeTextView = (TextView) findViewById(R.id.createProfile_name);
			setCustomFont(nameAndAgeTextView, Constants.FONT_PROXI_REGULAR);
			
			
			int computedAge = getAge(segregateBirthdayInfo(userProfile.get(
					"birthday").toString()));
			nameAndAgeTextView.setText(MasterUser.getInstance().getUserName() + ", " + computedAge);
			setUserAge(computedAge);
			
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	protected void setProfileNameAndAge() {
		
		String name = MasterUser.getInstance().getUserName();
		String age=MasterUser.getInstance().getUserAge();
		TextView nameAndAge = (TextView) findViewById(R.id.createProfile_name);
		Typeface custom_font = Typeface.createFromAsset(getAssets(),
				"fonts/Mark Simonson - Proxima Nova Regular.ttf");
		
		nameAndAge.setText(name + "," + " " + age);
		nameAndAge.setTypeface(custom_font);
	}

	protected void setProfileImage() {
		
		hideView(progressDialog);
		CircularImageView circularImageView = (CircularImageView) findViewById(R.id.createProfile_circle);
		Picasso.with(this).load(getUserImageString()).into(circularImageView);
		chooseInterestButton.setEnabled(true);
		
		Picasso.with(this).load(getUserImageString()).into(target);
		
		if (facebookUserRegistsred()) {
			circularImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					
					Intent intent=new Intent(CreateProfileScreen.this, ProfileAlbumListScreen.class);
					startActivity(intent);
					
				}
			});
		}
		
	} 

	/**
	 * set profile to target function. to get blur images
	 */

	private Target target = new Target() {
		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
			if(bitmap!=null){
				Bitmap temp = bitmap.copy(bitmap.getConfig(), true);
				final RenderScript rs = RenderScript
						.create(CreateProfileScreen.this);
				final Allocation input = Allocation.createFromBitmap(rs, temp,
						Allocation.MipmapControl.MIPMAP_FULL,
						Allocation.USAGE_GRAPHICS_TEXTURE);
				final Allocation output = Allocation.createTyped(rs,
						input.getType());
				final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,
						Element.U8_4(rs));
				script.setRadius(25.0f);
				script.setInput(input);
				script.forEach(output);
				output.copyTo(temp);
				ImageView profilePicView = (ImageView) findViewById(R.id.createProfile_profilePic);
				profilePicView.setImageBitmap(temp);
			}
		}

		@Override
		public void onBitmapFailed(Drawable drawable) {

		}

		@Override
		public void onPrepareLoad(Drawable drawable) {

		}
	};


	private Uri getUserImageString() {

		String userPhotoUrl = "";

		if (!facebookUserRegistsred()) {
			userPhotoUrl=MasterUser.getInstance().getUserImageUri();
		}

		else {
			userPhotoUrl=MasterUser.getInstance().getUserImageUri();
		}

		Uri userPhotoUri = Uri.parse(userPhotoUrl);

		return userPhotoUri;
	}

	public void showInterestScreen(View view) {
		profileDescription = (EditText) findViewById(R.id.createProfile_description);
		userDescription = profileDescriptionTextView.getText().toString();
		if (userDescription.equals(""))
			userDescription = profileDescription.getText().toString();

		registerDescription(profileDescription.getText().toString());

		setDescription(profileDescription.getText().toString());

		Intent selectInterestScreenIntent = new Intent(CreateProfileScreen.this, SelectInterestScreen.class);
		
		if (getIntent().getExtras() == null) {
			selectInterestScreenIntent.putExtra("is_edit_mode", false);
			startActivity(selectInterestScreenIntent);
			this.finish();
		}
		
		else if (getIntent().getExtras().containsKey(Constants.IS_EDIT)) {
			selectInterestScreenIntent.putExtra("is_edit_mode", true);
		}
		
		else{
			selectInterestScreenIntent.putExtra("is_edit_mode", false);
		}
		startActivity(selectInterestScreenIntent);
		this.finish();

	}

	private void showView(View view){
		view.setVisibility(View.VISIBLE);
	}
	
	private void hideView(View view){
		view.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(MasterUser.getInstance().getUserInterests() != null)
		getMenuInflater().inflate(R.menu.create_profile_screen, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if(id == R.id.action_done){
			profileDescription = (EditText) findViewById(R.id.createProfile_description);
			userDescription = profileDescriptionTextView.getText().toString();
			if (userDescription.equals(""))
				userDescription = profileDescription.getText().toString();

			registerDescription(profileDescription.getText().toString());

			setDescription(profileDescription.getText().toString());
			Intent i = new Intent(CreateProfileScreen.this,CompleteProfileScreen.class);
			this.startActivity(i);
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
