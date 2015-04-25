package com.attribes.incommon;

import android.os.Bundle;
import android.widget.TextView;

import com.attribes.incommon.util.Constants;

public class DateOfBirthScreen extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_date_of_birth);
		setActionBarStyling();
		TextView dobDescription=(TextView)findViewById(R.id.dateOfBirth_dobText);
		dobDescription.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		
		showDobDialog();
		
		
	}

	private void showDobDialog() {
		DateOfBirthDialog dateOfBirthDialog = new DateOfBirthDialog();
		
		dateOfBirthDialog.setCancelable(false);
		dateOfBirthDialog.show(getSupportFragmentManager(), "datePicker");
		
	}

	
	
}
