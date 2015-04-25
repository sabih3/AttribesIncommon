package com.attribes.incommon;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.DatePicker;
import android.widget.Toast;

public class DateOfBirthDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener, OnDateChangeListener{

	private StringBuilder stringBuilder;
	private static String dobString;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		
	final DatePickerDialog dobDialog = new DatePickerDialog(getActivity(), this ,1999, 1, 1);
	
	
	dobDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Set",new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String st = getDate().toString();
			if(st.isEmpty()){
				Toast.makeText(getActivity(), "Please select your birthdate", Toast.LENGTH_SHORT).show();
			}
			else{
				Intent intent = new Intent(getActivity(), CreateProfileScreen.class);
				intent.putExtra("dateOfBirth",getDate().toString()); 
				startActivity(intent);
				getActivity().finish();
			}
		}
	} );
	
	dobDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(getActivity(), WelcomeScreen.class);
			
			startActivity(intent);
			
		}
	});
	
	dobDialog.getDatePicker().getCalendarView().setOnDateChangeListener(this);
	 
	return dobDialog;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		setDate(year, month + 1, day);
		
	}
	
	private void setDate(int year, int month, int day) {
		
		stringBuilder = new StringBuilder();
		dobString= month +1 + "/" + day+"/" + year;
		stringBuilder.append(month ).append("/").append(day).append("/").append(year);
		
	}

	private StringBuilder getDate(){
		StringBuilder dateString;
		
		dateString = stringBuilder;
		if(dateString == null){
			dateString = new StringBuilder();
			dateString = dateString.append(1 ).append("/").append(0).append("/").append(1999);
		}
		return dateString;
		
		
		
	}

	@Override
	public void onSelectedDayChange(CalendarView view, int year, int month,
			int day) {
		setDate(year, month+1, day);
		
	}
	
	
}
