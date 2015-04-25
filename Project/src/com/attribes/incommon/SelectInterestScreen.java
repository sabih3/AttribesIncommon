package com.attribes.incommon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.models.InterestsModel;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.views.InterestLayout;
import com.google.gson.Gson;

public class SelectInterestScreen extends BaseActivity implements
		SearchView.OnQueryTextListener, View.OnClickListener {

	private Boolean flag = false;
	private SparseBooleanArray selectedIndexesArray;
	public static InterestLayout interestsList;
	private View notFoundView;
	private Button addNewInterest;
	private ArrayList<String> selectedInterests;
	SearchView searchView;
	private Boolean searchFlag = false;
	private String socialMediaType;
	private List<String> interests;
	private Button submitInterestButton;
	private AQuery mAquery;
	private Dialog progressDialog;
	private Typeface font_ProxiLight;
	private Typeface font_ProxiRegular;
	String[] interestListContent;

	private InterestsModel allInterests;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_select_interest_screen);

	
		
		setActionBarStyling();
		setCustomFonts();
		selectedInterests = new ArrayList<String>();
		
		//selectedInterests = fetchUserInterests();
		
		submitInterestButton = (Button) findViewById(R.id.select_interest_add);
		interestsList = (InterestLayout) findViewById(R.id.selectInterest_list);
		addNewInterest = (Button) findViewById(R.id.select_interest_add);
		addNewInterest.setOnClickListener(this);
		notFoundView = findViewById(R.id.select_interest_not_found);
		mAquery = new AQuery(this);
		font_ProxiLight = Typeface.createFromAsset(getAssets(),
				"fonts/"+Constants.FONT_PROXI_LIGHT);
		font_ProxiRegular =Typeface.createFromAsset(getAssets(), "fonts/"+Constants.FONT_PROXI_REGULAR);
		
		getAllInterests();
		
    	 if(MasterUser.getInstance().getUserInterests() != null){
    		 selectedInterests = MasterUser.getInstance().getUserInterests();
    	 }

	}

	public void getAllInterests(){
		notFoundView.setVisibility(View.GONE);
		progressDialog = ProgressDialog.show(SelectInterestScreen.this, "", "Preparing the list", true,true);
        String url = com.attribes.incommon.util.Constants.BaseUrl + "/interests/all";
		
		AjaxCallback<String> callBack = new AjaxCallback<String>();        
		callBack.url(url).type(String.class).weakHandler(SelectInterestScreen.this, "interestsList");
		
		callBack.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		callBack.param("sm_token", getSmToken());
		
		mAquery.ajax(callBack);
	}
	
	public void interestsList(String url, String json, AjaxStatus status){
		if(!(json.isEmpty())){
			
			
			Gson gson = new Gson();
			InterestsModel obj = new InterestsModel();
			allInterests = gson.fromJson(json.toString(), InterestsModel.class);
			
			interestsList.setOnClickListener(this);
			interestListContent = new String[allInterests.response.size()];
			for(int i = 0;i< allInterests.response.size(); i++){
				
				interestListContent[i] = allInterests.response.get(i).title;
			}
               for(int i = 0;i< allInterests.response.size(); i++){
				
				interestListContent[i] = allInterests.response.get(i).title;
			}
               
			populateInterests(interestListContent);
		}
	}
	
	public void populateInterests(String[] interests) {
		progressDialog.dismiss();
		LayoutInflater inflater = getLayoutInflater();
		interestsList.setVisibility(View.VISIBLE);
		interestsList.removeAllViews();
		
		for (int i = 0; i < interests.length; i++) {
			TextView interest = (TextView) inflater.inflate(
					R.layout.interest_txt_item, null);
			interest.setText(interests[i]);

			
			interest.setTextSize(14);
			
			

			interest.setTypeface(font_ProxiLight);
			interest.setTextSize(18);
			interest.setOnClickListener(this);
			interestsList.addView(interest);
			if (!selectedInterests.isEmpty()) {
				for (int k = 0; k < selectedInterests.size(); k++) {
					if (interests[i].equals(selectedInterests
							.get(k))) {
						
						interest.setSelected(true);
						interest.setTextColor(Color.WHITE);
					}
				}
			}
		}
	}

	Menu mMenu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.mMenu = menu;

		// SearchManager searchManager = (SearchManager)
		// getSystemService(Context.SEARCH_SERVICE);
		// SearchView searchView = (SearchView)
		// menu.findItem(R.id.menu_search).getActionView();
		//
		// searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		// searchView.setIconifiedByDefault(false);

		getMenuInflater().inflate(R.menu.select_interest_screen, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if(MasterUser.getInstance().getUserInterests() != null){
   		 selectedInterests = MasterUser.getInstance().getUserInterests();
   	 }
		if (searchView == null) {
			searchView = (SearchView) menu.findItem(R.id.menu_search)
					.getActionView();

			ComponentName cn = new ComponentName(this,
					SelectInterestScreen.class);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

			searchView.setIconifiedByDefault(false);
			searchView.setOnQueryTextListener(this);
			

			int id = searchView.getContext().getResources()
					.getIdentifier("android:id/search_src_text", null, null);
			TextView textView = (TextView) searchView.findViewById(id);
			textView.setTextColor(Color.GRAY);
			textView.setHintTextColor(Color.GRAY);

			// searchView.setOnQueryTextListener(this);

			// searchView.setOnClickListener(this);

		}

		getMenuInflater().inflate(R.menu.interest_menu_done, menu);

//		if (getIntent() != null)
//			if (getIntent().getBooleanExtra("is_edit_mode", false)) {
//				mMenu.findItem(R.id.done_button).setEnabled(true);
//				//TODO:Refactored code
////				selectedInterests = fetchUserInterests();
//				selectedInterests = MasterUser.getInstance().getUserInterests();
//				//populateInterests(interestListContent);
//				
//			}

		if(!selectedInterests.isEmpty()){
		if(selectedInterests.size()>=5){
			
			mMenu.findItem(R.id.done_button).setEnabled(true);
		}
		}
		return true;
	}

	private Boolean searchInterests(final String newText) {
		Boolean matched = false;
		
		if (!(newText.isEmpty())){
			interests = new ArrayList<String>();
			for (String s : interestListContent) {

				if(s != null){
				if (s.toLowerCase().startsWith((newText.toLowerCase()))) {
					interests.add(s);
				}
				}

			}
			if (interests.isEmpty()) {
				// no interest found
				notFoundView.setVisibility(View.VISIBLE);
				interestsList.setVisibility(View.GONE);
				interestsList.clearFocus();
				interestsList.setClickable(false);
				
				//submitInterestButton.setOnClickListener(this);
				//submitInterestButton.requestFocus();
				//hideSoftKeyBoard();
				//searchView.clearFocus();
				//notFoundView.requestFocus();
				
				// Get the intent, verify the action and get the query
				submitInterestButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						openEmailClient(newText);
						
					}
				});
				
				
			} else {
				notFoundView.setVisibility(View.GONE);
				interestsList.setVisibility(View.VISIBLE);
			}

			String[] finalInterets = new String[interests.size()];
			populateInterests(interests.toArray(finalInterets));
			matched = true;
		}

		else {
			if (notFoundView.getVisibility() == View.VISIBLE) {
				notFoundView.setVisibility(View.GONE);
				interestsList.setVisibility(View.VISIBLE);
			}
			populateInterests(interestListContent);
			matched = false;
		}

		return matched;
	}

	private void hideSoftKeyBoard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		
	}

	private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle("Thank You!")
				.setMessage(
						"InCommon has received your suggested interest and if suitable it will be added to the list.")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						notFoundView.setVisibility(View.GONE);
						interestsList.setVisibility(View.VISIBLE);
						getAllInterests();
					}
				}).show();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

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

		if (id == R.id.done_button) {
			interestsList.setSelectedChildPositions();
			saveUserInterests(selectedInterests);

			Intent completeprofileScreen = new Intent(
					SelectInterestScreen.this, CompleteProfileScreen.class);

			startActivity(completeprofileScreen);
			SelectInterestScreen.this.finish();
		}

		return super.onOptionsItemSelected(item);
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		//View view = findViewById(R.id.selectInterest_list);
//		View currentFocus = getCurrentFocus();
//		boolean ret = super.dispatchTouchEvent(event);
//
//		if (currentFocus instanceof Button) {
//			
//			openEmailClient("query");
////			
////			View w = getCurrentFocus();
////			int scrcoords[] = new int[2];
////			w.getLocationOnScreen(scrcoords);
////			float x = event.getRawX() + w.getLeft() - scrcoords[0];
////			float y = event.getRawY() + w.getTop() - scrcoords[1];
////
////			if (event.getAction() == MotionEvent.ACTION_UP
////					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
////							.getBottom())) {
////				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
////						.getWindowToken(), 0);
////			}
//		}
//		return ret;

	//}

	@Override
	public boolean onQueryTextChange(String queryText) {
		
		if(queryText.isEmpty()){
			getAllInterests();
		}
		
		if(!queryText.isEmpty()){
			searchInterests(queryText);	
		}
//		else{
//			//populateInterests(interestListContent);
//			getAllInterests();
//		}
//		
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String queryText) {
//		if(!queryText.isEmpty()){
//			searchInterests(queryText);	
//		}
//		else{
//			//populateInterests(interestListContent);
//			getAllInterests();
//		}
		
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v instanceof TextView) {
			// safe check
			TextView tv = (TextView) v;
			tv.setSelected(!tv.isSelected());
			toggleTextColor(tv);
			if (!(selectedInterests.contains(tv.getText().toString()))) {
					selectedInterests.add(tv.getText().toString());
			} else {
				selectedInterests.remove(tv.getText().toString());
			}

			if (selectedInterests.size() >= 5) {
				flag = true;
				mMenu.findItem(R.id.done_button).setEnabled(true);
				// invalidateOptionsMenu();
			} else {
				flag = false;
				mMenu.findItem(R.id.done_button).setEnabled(false);
				// invalidateOptionsMenu();
			}
		}

		if (v.getId() == R.id.select_interest_add) {
			//showDialog();
			
		}

	}

	private void openEmailClient(String newText) {
		String subject="Request for new interest ",
				body="Please add this  new interest "+"\""+newText+"\"",
				mailto="marketing@getincommon.com ";
		
//		String URI="mailto:?subject=" + subject + "&body=" + body;
		
		String URI="mailto:"+mailto+"?subject=" + subject + "&body=" + body;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		intent.putExtra("flag", true);
		Uri data = Uri.parse(URI);
		intent.setData(data);
		//startActivity(intent);
		
		startActivityForResult(intent, 54);
		
//		Intent mailer = new Intent(Intent.Action);
//		mailer.setType("text/plain");
//		mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{"kashif.y.saeed@gmail.com"});
//		mailer.putExtra(Intent.EXTRA_SUBJECT, subject);
//		mailer.putExtra(Intent.EXTRA_TEXT, );
//		
//		startActivity(Intent.createChooser(mailer, "Send email..."));
		
	}

	private void toggleTextColor(TextView tx) {
		if (tx.isSelected())
			tx.setTextColor(Color.WHITE);
		else
			tx.setTextColor(Color.BLACK);
	}

	private void populateSelectedItems() {
		if (InterestLayout.selectedItemPositions.size() != 0) {
			for (int i = 0; i < InterestLayout.selectedItemPositions.size(); i++) {
				View view = interestsList
						.getChildAt(InterestLayout.selectedItemPositions.get(i));
				view.setSelected(true);
				toggleTextColor((TextView) view);
			}
		}
	}

	private void setCustomFonts() {
		Typeface custom_font = Typeface.createFromAsset(getAssets(),
				"fonts/Mark Simonson - Proxima Nova Regular.ttf");
		((TextView) findViewById(R.id.interest_not_found))
				.setTypeface(custom_font);
		((TextView) findViewById(R.id.select_interest_add))
				.setTypeface(custom_font);
	}

	
	@Override
	protected void onActivityResult(int requestCode,int responseCode, Intent intent){
		super.onActivityResult(requestCode, responseCode, intent);
		
		if(requestCode==54){
			notFoundView.setVisibility(View.GONE);
			interestsList.setVisibility(View.VISIBLE);
			showDialog();
			
		}
		
	}
	
//	public void submitInterest(View view){
//		
//	}
	
	
}
