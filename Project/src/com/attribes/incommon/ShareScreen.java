package com.attribes.incommon;



import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
//import com.facebook.widget.FacebookDialog.ShareDialogBuilder;
import com.google.android.gms.plus.PlusShare;
/*
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
*/
import com.androidquery.AQuery;

public class ShareScreen extends DrawerScreen{

	AQuery aQuery;
	private UiLifecycleHelper uiHelper;
	
	 private Session.StatusCallback callback = new Session.StatusCallback() {
	        @Override
	        public void call(Session session, SessionState state,
	                Exception exception) {
	            onSessionStateChange(session, state, exception);
	        }
	    };
	    private void onSessionStateChange(Session session, SessionState state,
	            Exception exception) {
	        if (state.isOpened()) {
	             System.out.println("Logged in...");
	        } else if (state.isClosed()) {
	             System.out.println("Logged out...");
	        }
	    }
	

	    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
		getLayoutInflater().inflate(R.layout.activity_share_screen,content,true);
		
		aQuery = new AQuery(this);
		setActionBarStyling();
		setFonts();
		aQuery.id(R.id.shareScreen_mailIcon).clicked(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				openEmailClient();
				Flurry.getInstance().eventShareEmail();
			}

			
		});
		
		aQuery.id(R.id.shareScreen_fbIcon).clicked(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(ShareScreen.this)
			        .setLink("http://www.getincommon.com").setDescription("InCommon is the new way to connect with people who share similar interests")
			        .setCaption("InCommon is the new way to connect with people who share similar interests")
			        .build();
				
				
					uiHelper.trackPendingDialogCall(shareDialog.present());
					Flurry.getInstance().eventShareFB();
					
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Facebook not found", Toast.LENGTH_SHORT).show();
					//publishFeedDialog();
					//openWebView("facebook.com");
				}
				
			}

		});
		
		
		aQuery.id(R.id.shareScreen_twitterIcon).clicked(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				try{
					openTwitter();
					Flurry.getInstance().eventShareTwitter();
				}
				catch(Exception e){
					openWebView("https://www.twitter.com");
				}
				
			}

			
		});
		
		aQuery.id(R.id.shareScreen_googleIcon).clicked(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent shareIntent =new PlusShare.Builder(getActivity())
				.setType("text/plain")
				.setText("InCommon is the new way to connect with people who share similar interests")
				.setContentUrl(Uri.parse("https://developers.google.com/+/"))
				.getIntent();
				
				startActivityForResult(shareIntent, 0);
				
				Flurry.getInstance().eventShareGoogle();

				
			}
		});
	}

	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "InCommon");
	    params.putString("caption", "InCommon is the new way to connect with people who share similar interests");
	    params.putString("description", "InCommon is the new way to connect with people who share similar interests");
	    params.putString("link", "https://developers.facebook.com/android");
	   // params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(getActivity(),
	        		Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

				@Override
				public void onComplete(Bundle values, FacebookException error) {
					if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getActivity(),
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getActivity().getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
					
				}

	        })
	        .build();
	    feedDialog.show();
	}
	
	private void setFonts() {
		aQuery.id(R.id.shareScreen_title).typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		aQuery.id(R.id.shareScreen_description).typeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
		
	}

	private void openTwitter() {
		Intent tweetIntent = new Intent(Intent.ACTION_SEND);
		tweetIntent.putExtra(Intent.EXTRA_TEXT, "InCommon is the new way to connect with people who share similar interests");
		tweetIntent.setType("text/plain");

		PackageManager packManager = getPackageManager();
		List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

		boolean resolved = false;
		for(ResolveInfo resolveInfo: resolvedInfoList){
		    if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
		        tweetIntent.setClassName(
		            resolveInfo.activityInfo.packageName, 
		            resolveInfo.activityInfo.name );
		        resolved = true;
		        break;
		    }
		}
		if(resolved){
		    startActivity(tweetIntent);
		}else{
		    Intent i = new Intent();
		    String message = "InCommon is the new way to connect with people who share similar interests";
		    i.putExtra(Intent.EXTRA_TEXT, message);
		    i.setAction(Intent.ACTION_VIEW);
		    i.setData(Uri.parse("https://twitter.com/intent/tweet?text=InCommon is the new way to connect with people who share similar interests" +
		    		""));
		    startActivity(i);
		}
		
	}
	private void openEmailClient(){
		String subject="",
				body="InCommon is the new way to connect with people who share similar interests",
				mailto="";
		
		String URI="mailto:"+mailto+"?subject=" + subject + "&body=" + body;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		intent.putExtra("flag", true);
		Uri data = Uri.parse(URI);
		intent.setData(data);
		
		startActivityForResult(intent, 54);
	}
	
	@SuppressLint("SetJavaScriptEnabled") private void openWebView(String address){
		WebView webView = (WebView) findViewById(R.id.shareScreen_webView);
		webView.setVisibility(View.VISIBLE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(address);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	           
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	           
	        }
	    });
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

