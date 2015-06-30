package com.attribes.incommon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
//import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
//import android.widget.Toast;

import com.attribes.incommon.api.ApiRequest;
import com.attribes.incommon.models.MasterUser;
//import com.attribes.incommon.models.UserDevicePreferences;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.yahoo.mobile.client.android.util.RangeSeekBar;
import com.yahoo.mobile.client.android.util.RangeSeekBar.OnRangeSeekBarChangeListener;
//import org.xbill.DNS.Master;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.AQuery;

/** This class is used for user preferences 
 * 
 * @author Sabih Ahmed
 *
 */
public class SettingActivity extends DrawerScreen{

	private CheckBox menCheckBox;
	private CheckBox womenCheckBox;
	private CheckBox bothCheckBox;
	private CheckBox alertCheckBox;
	private CheckBox matchesCheckBox;
	private CheckBox messagesCheckBox;
	private RangeSeekBar<Integer> rangeSeekBar ;
	private SeekBar distanceSeekBar;
	private TextView ageRangeTextView;
	private TextView distanceTextView;
	private AQuery aQuery;
	private Set<String> preferencesKeyMap;
	private View referenceValue; 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.preference_layout);
		
		   ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
			getLayoutInflater().inflate(R.layout.preference_layout,content,true);
			
		initializeContent();
		setActionBarStyling();
		
		setAgeRangeSeekBar();
		setDistanceSeekBar();
		setCheckListenerForBothGender();
		setCheckListenerForMen();
		setCheckListenerForWomen();
		setCheckListenerForAlerts();
		setCheckListenerForMatches();
		setCheckListenerForMessages();

		initializeValues();		

	}

	
	/**Sets Age range and changes the age text label
	 * 
	 */
	private void setAgeRangeSeekBar() {
		rangeSeekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {

			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				
				ageRangeTextView.setText(Integer.toString(minValue)+" - "+Integer.toString(maxValue));
				MasterUser.getInstance().setPreference_minAge(minValue);
				MasterUser.getInstance().setPreference_maxAge(maxValue);
				
				
			}
		});
		
	}
	
	/**Sets distance and changes the distance text label
	 * 
	 */
	private void setDistanceSeekBar() {
		distanceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int distance = seekBar.getProgress();
				distanceSeekBar.setProgress(seekBar.getProgress());
				distanceTextView.setText(Integer.toString(distance)+"km");
				MasterUser.getInstance().setPreference_distance(distance);
				
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				
			}
		});
		
	}
	
	/**Sets check for messages
	 * 
	 */
	private void setCheckListenerForMessages() {
		messagesCheckBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View messageCheckBox) {
				if(((CheckBox)messagesCheckBox).isChecked()){
					messagesCheckBox.setSelected(true);
					MasterUser.getInstance().setPreference_messages(true);
				}
				
				else{
					messagesCheckBox.setSelected(false);
					MasterUser.getInstance().setPreference_messages(false);
				}
				
			}
		});
		
	}

	/**Sets check for matches
	 * 
	 */
	private void setCheckListenerForMatches() {
		matchesCheckBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View matchesCheckBox) {
				if(((CheckBox)matchesCheckBox).isChecked()){
					matchesCheckBox.setSelected(true);
					MasterUser.getInstance().setPreference_newMatches(true);
				}
				
				else{
					matchesCheckBox.setSelected(false);
					MasterUser.getInstance().setPreference_newMatches(false);
				}
				
			}
		});
		
	}

	/**Sets check for alerts
	 * 
	 */
	private void setCheckListenerForAlerts() {
		alertCheckBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View alertCheckBox) {
				if(((CheckBox)alertCheckBox).isChecked()){
					alertCheckBox.setSelected(true);
					MasterUser.getInstance().setPreference_alerts(true);
				}
				else{
					alertCheckBox.setSelected(false);
					MasterUser.getInstance().setPreference_alerts(false);
				}
			}
		});
		
	}

	/**Sets check for women
	 * 
	 */
	private void setCheckListenerForWomen() {
		womenCheckBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View womenCheckBox) {
				
				toggleCheck(womenCheckBox);
				((CheckBox) womenCheckBox).setChecked(true);
				((CheckBox) menCheckBox).setChecked(false);
				((CheckBox) bothCheckBox).setChecked(false);
				MasterUser.getInstance().setPreferences_showWomen(true);
				MasterUser.getInstance().setPreferences_showMen(false);
				MasterUser.getInstance().setPreferences_showBoth(false);
				
			}
		});
		
	}

	/**Sets check for men
	 * 
	 */
	private void setCheckListenerForMen() {
		menCheckBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View menCheckBox) {
				
				toggleCheck(menCheckBox);
				((CheckBox) menCheckBox).setChecked(true);
				((CheckBox) womenCheckBox).setChecked(false);
				((CheckBox) bothCheckBox).setChecked(false);
				MasterUser.getInstance().setPreferences_showMen(true);
				MasterUser.getInstance().setPreferences_showWomen(false);
				MasterUser.getInstance().setPreferences_showBoth(false);

			}
		});
		
	}
	
	
	private void setCheckListenerForBothGender(){
		bothCheckBox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View bothCheckBox) {

				toggleCheck(bothCheckBox);
				((CheckBox) bothCheckBox).setChecked(true);
				((CheckBox) menCheckBox).setChecked(false);
				((CheckBox) womenCheckBox).setChecked(false);
				MasterUser.getInstance().setPreferences_showBoth(true);
				MasterUser.getInstance().setPreferences_showMen(false);
				MasterUser.getInstance().setPreferences_showWomen(false);
				
				
			}
		});
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			updatePreferences();
			finish();
		break;
		
		}
		return true;
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		updatePreferences();
		HashMap<String, String> userPreferencesMap = new HashMap<String, String>();				
		userPreferencesMap.put("minAge", Integer.toString(MasterUser.getInstance().getPreference_minAge()));
		userPreferencesMap.put("maxAge", Integer.toString(MasterUser.getInstance().getPreference_maxAge()));
		userPreferencesMap.put("distance", Integer.toString(MasterUser.getInstance().getPreference_distance()));
		userPreferencesMap.put("showMen", Boolean.toString(MasterUser.getInstance().isPreferences_showMen()));
		userPreferencesMap.put("showWomen", Boolean.toString(MasterUser.getInstance().isPreferences_showWomen()));
		userPreferencesMap.put("showBoth", Boolean.toString(MasterUser.getInstance().isPreferences_showBoth()));
		userPreferencesMap.put("showAlerts", Boolean.toString(MasterUser.getInstance().isPreference_alerts()));
		userPreferencesMap.put("showMatches", Boolean.toString(MasterUser.getInstance().isPreference_newMatches()));
		userPreferencesMap.put("showMessages", Boolean.toString(MasterUser.getInstance().isPreference_messages()));
		
		setPreferencesLocally(userPreferencesMap);
		
		Flurry.getInstance().eventPreferencesUpdate();
	}
	
	


	public void responseUpdatePreferences(String url, String json, AjaxStatus status){
		if(json!=null){
			
		}
	}

	private void updatePreferences() {
		ApiRequest apiRequest=new ApiRequest(this);
		apiRequest.requestUpdatePreferences(MasterUser.getInstance(),this,"responseUpdatePreferences");	
	}

	/**This method initializes all of the UI components used in this view, and also sets the custom
	 * fonts on text labels.
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void initializeContent() {
		aQuery = new AQuery(this);
		rangeSeekBar = new RangeSeekBar<Integer>(this);
		rangeSeekBar = (RangeSeekBar<Integer>) findViewById(R.id.settingScreen_ageRange);
		rangeSeekBar.setRangeValues(18, 75);
		rangeSeekBar.setSelected(true);
		rangeSeekBar.setEnabled(true);
		ageRangeTextView = (TextView) findViewById(R.id.settingScreen_ageRangeText);
		
		
		distanceSeekBar =(SeekBar) findViewById(R.id.settingScreen_distance);
		distanceSeekBar.setMax(160);
		
		distanceTextView = (TextView) findViewById(R.id.settingScreen_distanceText);
		distanceSeekBar.setMax(160);
		
		menCheckBox = (CheckBox) findViewById(R.id.settingScreen_checkBoxMen);
		womenCheckBox = (CheckBox) findViewById(R.id.settingScreen_checkBoxWomen);
		bothCheckBox = (CheckBox) findViewById(R.id.settingScreen_checkBoxBoth);
		alertCheckBox = (CheckBox) findViewById(R.id.settingScreen_checkBoxAlerts);
		matchesCheckBox = (CheckBox) findViewById(R.id.settingScreen_checkBoxMatches);
		messagesCheckBox = (CheckBox) findViewById(R.id.settingScreen_checkBoxMessages);

		aQuery.id(R.id.settingScreen_ageLabel).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.settingScreen_ageRangeText).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.settingScreen_distanceLabel).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.settingScreen_distanceText).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.settingScreen_textViewShow).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		
		aQuery.id(R.id.settingScreen_TextViewMen).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.settingScreen_TextViewWomen).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.settingScreen_TextViewAlerts).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.settingScreen_TextViewMatches).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.settingScreen_TextViewMessages).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		
		preferencesKeyMap = new HashSet<String>();
		preferencesKeyMap.add("minAge");
		preferencesKeyMap.add("maxAge");
		preferencesKeyMap.add("distance");
		preferencesKeyMap.add("showMen");
		preferencesKeyMap.add("showWomen");
		preferencesKeyMap.add("showBoth");
		preferencesKeyMap.add("showAlerts");
		preferencesKeyMap.add("showMatches");
		preferencesKeyMap.add("showMessages");
	}
	
	private void initializeValues() {
		loadUserPreferences();
		
		
		ageRangeTextView.setText(MasterUser.getInstance().getPreference_minAge()+" - "+MasterUser.getInstance().getPreference_maxAge());
		rangeSeekBar.setSelectedMinValue(MasterUser.getInstance().getPreference_minAge());
		rangeSeekBar.setSelectedMaxValue(MasterUser.getInstance().getPreference_maxAge());

		distanceTextView.setText(Integer.toString(MasterUser.getInstance().getPreference_distance()));
		distanceSeekBar.setProgress(MasterUser.getInstance().getPreference_distance());
		
		menCheckBox.setChecked(MasterUser.getInstance().isPreferences_showMen());
		womenCheckBox.setChecked(MasterUser.getInstance().isPreferences_showWomen());
		bothCheckBox.setChecked(MasterUser.getInstance().isPreferences_showBoth());
		
		alertCheckBox.setChecked(MasterUser.getInstance().isPreference_alerts());
		matchesCheckBox.setChecked(MasterUser.getInstance().isPreference_newMatches());
		messagesCheckBox.setChecked(MasterUser.getInstance().isPreference_messages());
	
	}

	private void loadUserPreferences() {
		HashMap<String, String> localPreferencesMap = getPreferencesLocally(preferencesKeyMap);

		MasterUser.getInstance().setPreference_minAge(Integer.parseInt(localPreferencesMap.get("minAge")));
		MasterUser.getInstance().setPreference_maxAge(Integer.parseInt(localPreferencesMap.get("maxAge")));
		MasterUser.getInstance().setPreference_distance(Integer.parseInt(localPreferencesMap.get("distance")));
		MasterUser.getInstance().setPreferences_showMen(Boolean.parseBoolean(localPreferencesMap.get("showMen")));
		MasterUser.getInstance().setPreferences_showWomen(Boolean.parseBoolean(localPreferencesMap.get("showWomen")));
		MasterUser.getInstance().setPreferences_showBoth(Boolean.parseBoolean(localPreferencesMap.get("showBoth")));
		MasterUser.getInstance().setPreference_alerts(Boolean.parseBoolean(localPreferencesMap.get("showAlerts")));
		MasterUser.getInstance().setPreference_newMatches(Boolean.parseBoolean(localPreferencesMap.get("showMatches")));
		MasterUser.getInstance().setPreference_messages(Boolean.parseBoolean(localPreferencesMap.get("showMessages")));
	}

	
	private HashMap<String, String> getPreferencesLocally(Set<String> preferencesKeyMap){
		HashMap<String, String> userPreferencesMap=new HashMap<String, String>();
		
		SharedPreferences registrationPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
		for(String key:preferencesKeyMap){
			userPreferencesMap.put(key, registrationPreferences.getString(key,""));	
		}
		
		return userPreferencesMap;
	}
	

	@Override
	protected void setActionBarStyling(){
		ActionBar bar = getSupportActionBar();


		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ab_solid_background));
//		bar.setHomeAsUpIndicator(R.drawable.ic_ab_back_holo_light);
		bar.setIcon(R.drawable.logo);
		bar.setLogo(R.drawable.logo);
		bar.setDisplayUseLogoEnabled(true);
		bar.setDisplayShowHomeEnabled(true);

		bar.setDisplayHomeAsUpEnabled(true);
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
	
	private void toggleCheck(View view){
		if(referenceValue == null){
			
			((CheckBox) view).setChecked(true);
			
			referenceValue = view;
		}
		
		else{
			
			((CheckBox)referenceValue).setChecked(false);
			((CheckBox) view).setChecked(true);
			
			referenceValue = view;
		}
		
	}
}
