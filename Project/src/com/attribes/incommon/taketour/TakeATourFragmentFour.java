package com.attribes.incommon.taketour;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.attribes.incommon.BaseActivity;
import com.attribes.incommon.R;
import com.attribes.incommon.util.Constants;
import com.parse.ParseFacebookUtils;

public class TakeATourFragmentFour extends Fragment{

	private Button facebookLoginButton;
	private Dialog progressDialog;
	private Context context;
	private BaseActivity baseActivity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		BaseActivity baseActivity = new BaseActivity();
		baseActivity.context = getActivity().getApplicationContext();
		View fragmentFour = inflater.inflate(R.layout.fragment_tour_four, null);
		
		TextView fragmentFourTitle = (TextView) fragmentFour.findViewById(R.id.tourFour_title);
		Button fragmentFourFacebook = (Button)fragmentFour.findViewById(R.id.fragmentFour_fbLoginButton);
		Button fragmentFourGoogle = (Button)fragmentFour.findViewById(R.id.fragmentFour_googleLoginButton);

		baseActivity.setCustomFont(fragmentFourTitle, Constants.FONT_PROXI_LIGHT);
		baseActivity.setCustomFont(fragmentFourFacebook, Constants.FONT_PROXI_REGULAR);
		baseActivity.setCustomFont(fragmentFourGoogle, Constants.FONT_PROXI_REGULAR);
		
		//		aq.id(R.id.tourFour_title).typeface(setCustomFont(MainActivity.FONT_PROXI_LIGHT));
//		
//		aq.id(R.id.fragmentFour_fbLoginButton).typeface(setCustomFont(MainActivity.FONT_PROXI_REGULAR));
//		aq.id(R.id.fragmentFour_googleLoginButton).typeface(setCustomFont(MainActivity.FONT_PROXI_REGULAR));
		
		
		return fragmentFour;
	}
	
	View.OnClickListener faceBookLoginListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			progressDialog=new Dialog(context);
//			progressDialog = ProgressDialog.show(context, "", "Logging in...", true);
//			progressDialog.setCanceledOnTouchOutside(false);
			BaseActivity baseActivity = new BaseActivity();
			baseActivity.setActivity(getActivity());
			baseActivity.onLoginClick(v);
			
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	public static TakeATourFragmentFour newInstance(){
		TakeATourFragmentFour fragmentFour = new TakeATourFragmentFour();
		
		return fragmentFour;
		
	}

	
}
