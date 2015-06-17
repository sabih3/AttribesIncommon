package com.attribes.incommon;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.adapters.FriendRequestAdapter;
import com.attribes.incommon.adapters.FriendRequestAdapter.CustomInterface;
import com.attribes.incommon.models.NotificationResponse;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.CustomPreferences;
import com.google.gson.Gson;

public class NotificationFragment extends Fragment implements CustomInterface {

	private AQuery aq;
	private NotificationResponse notificationResponse;
	Dialog progressDialog;
	private View view;
	private TextView notificationsCount;
	private int count;
	public static String keyPrefsForNotification = "notifications";
	public static String keyPrefsForCount = "count";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_notifications, null);
		aq = new AQuery(getActivity());
		notificationsCount = (TextView) view
				.findViewById(R.id.notificationFragment_countText);
		notificationsCount
				.setTypeface(setCustomFont(Constants.FONT_PROXI_LIGHT));

		count = 0;
		
		if(savedInstanceState != null){
			CustomPreferences customPreferences = new CustomPreferences(getActivity());
			notificationResponse = customPreferences.getObjectFromPrefs(keyPrefsForNotification, NotificationResponse.class);
			if(notificationResponse != null){
				
				count = savedInstanceState.getInt(keyPrefsForCount);
				updateUI(notificationResponse, count);
				
			}
			
		}
		else{
			getAllNotifications();
		}
		
		return view;
	}

	public static NotificationFragment newInstance() {

		NotificationFragment notificationFragment = new NotificationFragment();

		return notificationFragment;
	}

	public void notificationResult(String url, String json, AjaxStatus status) {
		if (json != null) {

			Gson gson = new Gson();

			notificationResponse = new NotificationResponse();

			notificationResponse = gson.fromJson(json.toString(),
					NotificationResponse.class);
			for (int i = 0; i < notificationResponse.response.size(); i++) {
				if ((notificationResponse.response.get(i).is_read) == "0") {
					count++;
				}
			}
			updateUI(notificationResponse,count);
			

		}

	}

	private void updateUI(NotificationResponse notificationResponse, int count) {
		if(notificationResponse != null){
			
			FriendRequestAdapter adapter = new FriendRequestAdapter(
					getActivity(), this, notificationResponse.response);
			ListView notificationList = (ListView) view.findViewById(R.id.notificationList_friend);
			notificationList.setAdapter(adapter);
			

			Typeface custom_font = Typeface.createFromAsset(this.getActivity()
					.getAssets(),
					"fonts/Mark Simonson - Proxima Nova Light.ttf");
			if (count > 1) {
				notificationsCount.setText(count + "New notifications");
				notificationsCount.setTextColor(getResources().getColor(
						R.color.burnt_orange));
				notificationsCount.setTypeface(custom_font);
			} else if (count == 1) {
				notificationsCount.setTextColor(getResources().getColor(
						R.color.burnt_orange));
				notificationsCount.setText(count + "New notification");
				notificationsCount.setTypeface(custom_font);
			} else {
				notificationsCount.setText("No new notifications");
				notificationsCount.setTextColor(getResources().getColor(
						R.color.black_font));
				notificationsCount.setTypeface(custom_font);
			}
		}
		
		
	}

	private void getAllNotifications() {
		BaseActivity baseActivity = new BaseActivity();
		baseActivity.context = getActivity().getApplicationContext();
		
		String url = Constants.BaseUrl + Constants.NOTIFICATIONSALL;
		AjaxCallback<String> cb = new AjaxCallback<String>();

		cb.timeout(30000);
		cb.url(url).type(String.class)
				.weakHandler(NotificationFragment.this, "notificationResult");
		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("user_id", baseActivity.getUserIdFromApi());
		cb.param("sm_token", baseActivity.getSmToken());

		aq.ajax(cb);

	}

	private Typeface setCustomFont(String fontName) {
		Typeface custom_font = Typeface.createFromAsset(getActivity()
				.getAssets(), "fonts/" + fontName);
		return custom_font;
	}

	@Override
	public void callBack(View view, int position) {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		saveInstance(outState);
		super.onSaveInstanceState(outState);
	}

	private void saveInstance(Bundle outState) {
		CustomPreferences customPreferences = new CustomPreferences(
				getActivity());

		customPreferences.putObjectInPrefs(notificationResponse,
				keyPrefsForNotification);
		outState.putInt(keyPrefsForCount, count);

	}

}
