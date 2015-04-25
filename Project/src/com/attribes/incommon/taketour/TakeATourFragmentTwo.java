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

public class TakeATourFragmentTwo extends Fragment{

	BaseActivity baseActivity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		baseActivity = new BaseActivity();
		baseActivity.context = getActivity().getApplicationContext();
		
		View fragmentTwo=inflater.inflate(R.layout.fragment_tour_two, null);
		
		TextView fragmentTwoTitle=(TextView) fragmentTwo.findViewById(R.id.tourTwo_title);
		TextView fragmentTwoDesc=(TextView) fragmentTwo.findViewById(R.id.tourTwo_desc);
		TextView skipText=(TextView) fragmentTwo.findViewById(R.id.tour_skip);
		
		baseActivity.setCustomFont(skipText, Constants.FONT_AILERON_REGULAR);
		baseActivity.setCustomFont(fragmentTwoTitle, Constants.FONT_PROXI_LIGHT);
		baseActivity.setCustomFont(fragmentTwoDesc,Constants.FONT_PROXI_LIGHT);
		
		skipText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().finish();
				
			}
		});
		
		return fragmentTwo;
	}

	public static TakeATourFragmentTwo newInstance(){
		TakeATourFragmentTwo fragmentTwo=new TakeATourFragmentTwo();
		
		return fragmentTwo;
		
	}
	
}
