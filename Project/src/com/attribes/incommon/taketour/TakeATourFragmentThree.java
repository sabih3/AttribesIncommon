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

public class TakeATourFragmentThree extends Fragment{

	BaseActivity baseActivity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		baseActivity = new BaseActivity();
		baseActivity.context = getActivity().getApplicationContext();
		
		View fragmentThree = inflater.inflate(R.layout.fragment_tour_three, null);
		
		TextView skipText=(TextView) fragmentThree.findViewById(R.id.tour_skip);
		TextView fragmentThreeTitle=(TextView) fragmentThree.findViewById(R.id.tourThree_title);
		TextView fragmentThreeDesc=(TextView) fragmentThree.findViewById(R.id.tourThree_desc);
		
		baseActivity.setCustomFont(skipText, Constants.FONT_AILERON_REGULAR);
		baseActivity.setCustomFont(fragmentThreeTitle, Constants.FONT_PROXI_LIGHT);
		baseActivity.setCustomFont(fragmentThreeDesc,Constants.FONT_PROXI_LIGHT);
		
		skipText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().finish();
				
			}
		});
		return fragmentThree;
	}

	public static TakeATourFragmentThree newInstance(){
		TakeATourFragmentThree fragmentThree=new TakeATourFragmentThree();
		
		return fragmentThree;
		
	}
}
