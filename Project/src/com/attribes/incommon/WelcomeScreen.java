package com.attribes.incommon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.GPSManager;

public class WelcomeScreen extends BaseActivity implements GPSLocationListener {
	
	
	private GPSManager locationManager;
	private Dialog progressDialog;
	private LocationManager manager;
	
	public WelcomeScreen() {
		
				
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_welcome);
		setActionBarStyling();
		locationManager = new GPSManager(this);
		progressDialog=new Dialog(this);
		
		TextView tx = (TextView)findViewById(R.id.welcome_greeting);
		TextView welcomeText=(TextView) findViewById(R.id.welcome_welcomeText);
		Button createProfileButton=(Button) findViewById(R.id.welcome_createProfileButton);
		Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Mark Simonson - Proxima Nova Regular.ttf");
		tx.setTypeface(custom_font);
		welcomeText.setTypeface(custom_font);
		createProfileButton.setTypeface(custom_font);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void startCreateProfile(View view){
		
		  locationManager = new GPSManager(this);
		  locationManager.setLocationListener(this);
		  manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
		  boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		 
		locationManager.isLocationServiceEnabled();
		
		 if(statusOfGPS){
		     
			 locationManager.getLocation();
			 progressDialog = ProgressDialog.show(WelcomeScreen.this, "Getting Location", "Please wait this may take a while", true);
		 }
		 
		 else{
			 showSettingsAlert();
			 
		 }


	}

	private void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle(R.string.GPSAlertDialogTitle);

		// Setting Dialog Message
		alertDialog.setMessage(R.string.GPSAlertDialogMessage);

		// On Pressing Setting button
		alertDialog.setPositiveButton(R.string.Button_YES,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						
						startActivityForResult(intent, 200);
					}
				});

		// On pressing cancel button
		alertDialog.setNegativeButton(R.string.Button_NO,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.show();
		
	}
	
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if(requestCode == 200){
			
			boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			locationManager.isLocationServiceEnabled();
			 if(statusOfGPS){
				 locationManager.getLocation();
				 progressDialog = ProgressDialog.show(WelcomeScreen.this, "Getting Location", "Please wait this may take a while", true);
				
			 }
			 
			 else{
				 showSettingsAlert();
				 
			 }
		}
		
	}

	@Override
	public void gpsCoordinatesRecieved(Location loc) {
		
		progressDialog.dismiss();
		locationManager.setLocationListener(null);
		locationManager.stopUsingGPS();
		MasterUser.getInstance().setUserLat(Double.toString(loc.getLatitude()));
		 MasterUser.getInstance().setUserLon(Double.toString(loc.getLongitude()));
		 
		 if(!facebookUserRegistsred()){
				Intent intent = new Intent(this, DateOfBirthScreen.class);
				startActivity(intent);
			}
			else{
				Intent createProfile = new Intent(WelcomeScreen.this, CreateProfileScreen.class);
				startActivity(createProfile);
				
			}
		 
		 this.finish();
		
	}
	
	
	
	
}
