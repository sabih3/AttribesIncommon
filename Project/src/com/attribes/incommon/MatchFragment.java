package com.attribes.incommon;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.adapters.MatchAllAdapter;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.models.MatchAllResponse;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.CustomPreferences;
import com.google.gson.Gson;

public class MatchFragment extends Fragment implements OnItemClickListener {

	private AQuery aQuery;
	private Dialog progressDialog;
	private Gson gson;
	private View view;
	private MatchAllResponse matchesAllResponse;
	private TextView matchesCount;
	private int count;
	private Typeface custom_font;
	private static String keyPrefsForMatches = "matches";
	public static String keyPrefsForCount = "count";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_match, null);
		matchesCount = (TextView) view
				.findViewById(R.id.matchFragment_countText);

		custom_font = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/" + Constants.FONT_PROXI_REGULAR);
		count = 0;

		if (savedInstanceState != null) {

			CustomPreferences customPreferences = new CustomPreferences(
					getActivity());

			matchesAllResponse = customPreferences.getObjectFromPrefs(
					keyPrefsForMatches, MatchAllResponse.class);
			count = savedInstanceState.getInt(keyPrefsForCount);
			if (matchesAllResponse != null) {

				updateUI(matchesAllResponse, count);
			}
		}

		else {
			getAllMatches();
		}

		return view;
	}

	public void getAllMatches() {
		aQuery = new AQuery(getActivity());

		String url = Constants.BaseUrl + Constants.MATCHES_ALL;
		AjaxCallback<String> callBack = new AjaxCallback<String>();
		callBack.progress(progressDialog);

		callBack.url(url).type(String.class)
				.weakHandler(MatchFragment.this, "matchesAllResult");
		callBack.param("authorization", Constants.AUTHORIZATION);
		callBack.param("sm_token", MasterUser.getInstance().getUserSmToken());

		aQuery.ajax(callBack);
	}

	public void matchesAllResult(String url, String json, AjaxStatus status) {
		if (json != null) {
			gson = new Gson();

			matchesAllResponse = new MatchAllResponse();

			matchesAllResponse = gson.fromJson(json.toString(),
					MatchAllResponse.class);
			for (int i = 0; i < matchesAllResponse.response.size(); i++) {
				if ((matchesAllResponse.response.get(i).is_read) == "0") {
					count++;
				}
			}

			updateUI(matchesAllResponse, count);

		}
	}

	private void updateUI(MatchAllResponse matchesAllResponse, int count) {
		MatchAllAdapter adapter = new MatchAllAdapter(getActivity(), this,
				matchesAllResponse.response);
		ListView matchesList = (ListView) view
				.findViewById(R.id.matchFragment_list);

		matchesList.setAdapter(adapter);
		matchesList.setOnItemClickListener(this);
		// Typeface custom_font =
		// Typeface.createFromAsset(this.getActivity().getAssets(),
		// "fonts/Mark Simonson - Proxima Nova Light.ttf");
		if (count > 1) {
			matchesCount.setText(count + "new matches");
			matchesCount.setTextColor(getResources().getColor(
					R.color.burnt_orange));
			matchesCount.setTypeface(custom_font);
		} else if (count == 1) {
			matchesCount.setTextColor(getResources().getColor(
					R.color.burnt_orange));
			matchesCount.setText(count + "new match");
			matchesCount.setTypeface(custom_font);
		} else {
			matchesCount.setText("No new matches");
			matchesCount.setTextColor(getResources().getColor(
					R.color.black_font));
			matchesCount.setTypeface(custom_font);
		}
	}

	public static MatchFragment newInstance() {
		MatchFragment matchFragment = new MatchFragment();

		return matchFragment;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), MatchProfile.class);
		intent.putExtra("user_id", matchesAllResponse.response.get(position).id);
		intent.putExtra("showMultipleSearchedItem", true);
		intent.putExtra("title", "Match Profile");
		startActivity(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		saveInstances(outState);

		super.onSaveInstanceState(outState);
	}

	private void saveInstances(Bundle outState) {
		CustomPreferences customPreferences = new CustomPreferences(
				getActivity());

		customPreferences.putObjectInPrefs(matchesAllResponse,
				keyPrefsForMatches);

		outState.putInt(keyPrefsForCount, count);
		

	}

}
