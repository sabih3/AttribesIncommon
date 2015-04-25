package com.attribes.incommon;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.attribes.incommon.util.Constants;


public class TermsOfServiceScreen extends DrawerScreen{

	private AQuery aquery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_terms_service);
		
		ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
		getLayoutInflater().inflate(R.layout.activity_terms_service,content,true);
		
		aquery = new AQuery(this);
		
		setActionBarStyling();
		setFonts();
		
	}
	
	private void setFonts() {
		aquery.id(R.id.termsOfService_appName).typeface(setCustomFont(Constants.FONT_GINGER_REGULAR));
		aquery.id(R.id.termsOfService_headingText).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aquery.id(R.id.termsOfService_update).typeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
		aquery.id(R.id.termsOfService_policyText).typeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
		
	}

	@Override
	protected void setActionBarStyling(){
		ActionBar bar = getSupportActionBar();
		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ab_solid_background));
		bar.setHomeAsUpIndicator(R.drawable.ic_ab_back_holo_light);
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
		
}

