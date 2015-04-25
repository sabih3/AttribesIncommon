package com.attribes.incommon.taketour;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.attribes.incommon.BaseActivity;
import com.attribes.incommon.R;
import com.attribes.incommon.util.Constants;

public class TakeATourFragmentOne extends Fragment{

	BaseActivity baseActivity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		baseActivity = new BaseActivity();
		baseActivity.context = getActivity().getApplicationContext();
		View fragmentOne = inflater.inflate(R.layout.fragment_tour_one, null);
		
		TextView skipText=(TextView) fragmentOne.findViewById(R.id.tour_skip);
		
		
		TextView fragmentOneTitle=(TextView) fragmentOne.findViewById(R.id.tourOne_chooseInterest);
		TextView fragmentOneDesc=(TextView) fragmentOne.findViewById(R.id.tourOne_pickInterests);
		
		baseActivity.setCustomFont(skipText, Constants.FONT_AILERON_REGULAR);
		baseActivity.setCustomFont(fragmentOneTitle, Constants.FONT_PROXI_LIGHT);
		baseActivity.setCustomFont(fragmentOneDesc, Constants.FONT_PROXI_LIGHT);
		
		
		
		skipText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().finish();
				
			}
		});
		
		return fragmentOne;
	}

	public static TakeATourFragmentOne newInstance(){
		TakeATourFragmentOne fragmentOne=new TakeATourFragmentOne();
		
		return fragmentOne;
		
	}
	
	
}
