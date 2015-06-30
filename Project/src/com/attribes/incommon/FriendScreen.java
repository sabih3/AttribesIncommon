package com.attribes.incommon;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
//import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
//import android.view.ContextMenu;
//import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.adapters.FriendAllAdapter;
import com.attribes.incommon.models.FriendAllResponse;
import com.attribes.incommon.models.FriendAllResponse.Response;
import com.attribes.incommon.network.RestClient;
import com.attribes.incommon.util.Constants;
//import com.attribes.incommon.models.SearchFriendsModel;
import com.google.gson.Gson;
import retrofit.Callback;
import retrofit.RetrofitError;

public class FriendScreen extends DrawerScreen implements OnItemClickListener,MultiChoiceModeListener,SearchView.OnQueryTextListener{
	
	//was extended from DrawerScreen
	AQuery aq;
	private ListView friendList;
	private ArrayList<Response> friendAllResponseList;
	private FriendAllAdapter adapter;
	private Context context;
	private SearchView searchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarStyling();
		//setContentView(R.layout.activity_friend_screen);
		
		context = this;
		ViewGroup content=(ViewGroup) findViewById(R.id.frame_container);
		getLayoutInflater().inflate(R.layout.activity_friend_screen,content,true);
		
		callFriendsApiForAll();
		
		friendList = (ListView) findViewById(R.id.friendsList);
		friendList.setOnItemClickListener(this);
		friendList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		friendList.setMultiChoiceModeListener(this);
		
		aq = new AQuery(this);
		
		
		
	}
	
	public void callFriendsApiForAll(){
		aq = new AQuery(this);
		BaseActivity baseActivity=new BaseActivity();
		baseActivity.context = this;
		String url = Constants.BaseUrl + Constants.friendAll;
		AjaxCallback<String> cb = new AjaxCallback<String>();
		cb.url(url).type(String.class).weakHandler(FriendScreen.this, "friendResult");

		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", baseActivity.getSmToken());

		aq.ajax(cb);

//        RestClient.getAdapter().getFriends(Constants.AUTHORIZATION, baseActivity.getSmToken(), new Callback<FriendAllResponse>() {
//
//            @Override
//            public void success(FriendAllResponse friendAllResponse, retrofit.client.Response response) {
//
//            }
//
//            @Override
//            public void failure(RetrofitError retrofitError) {
//
//            }
//        });



	}

	public void friendResult(String url, String json,AjaxStatus status){
		if(json != null){
			
			Gson gson = new Gson();
			FriendAllResponse friendAllResponseObject = new FriendAllResponse();
			try {
				friendAllResponseObject = gson.fromJson(json.toString(), FriendAllResponse.class);	
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			
			friendAllResponseList = friendAllResponseObject.response;
			
			adapter = new FriendAllAdapter(this,friendAllResponseObject.response);
			aq.id(R.id.friendsList).getListView().setAdapter(adapter);

			aq.id(R.id.friendsList).getListView().setTextFilterEnabled(true);
			if(friendAllResponseObject.response.size() == 0){
				aq.id(R.id.no_friends).visible();
				aq.id(R.id.friendsList).invisible();
				adapter.notifyDataSetChanged();
			}
				
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Intent intent = new Intent(this, MatchProfile.class);
		intent.putExtra("user_id", friendAllResponseList.get(position).id);
		intent.putExtra("showMultipleSearchedItem", true);
		intent.putExtra("title", "Friends Profile");
		
		startActivity(intent);
	}

	@Override
	public boolean onCreateActionMode (ActionMode mode, Menu menu){
		MenuInflater inflater=mode.getMenuInflater();
		inflater.inflate(R.menu.menu_friend_list, menu);
		menu.findItem(R.id.delete).setVisible(false);
		
		return true;		
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch(item.getItemId()){
		
		case R.id.delete:
			deleteItem();
			mode.finish();
		}
		return true;
	}

	

	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		menu.findItem(R.id.delete).setVisible(true);
		
		return true;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		//int checkedItemCount = friendList.getCheckedItemCount();
		//mode.setTitle(friendList.getCheckedItemCount());
		
	}

	private void deleteItem() {
		
		SparseBooleanArray checkedItemPosition = friendList.getCheckedItemPositions();
		for(int i = 0 ; i < checkedItemPosition.size() ; i++){
			if(checkedItemPosition.valueAt(i)==true){
				RequestDeleteFriend(friendAllResponseList.get(i).id);
				friendAllResponseList.remove(i);
				adapter.notifyDataSetChanged();
			}
			
		}
		
	}

	private void RequestDeleteFriend(String id) {
		BaseActivity baseActivity=new BaseActivity();
		baseActivity.context = context;
		AQuery aq = new AQuery(context);
		String url = Constants.BaseUrl + Constants.FRIEND_DELETE;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(context, "RequestFriendDeleteResponse");
		
		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", baseActivity.getSmToken());
		cb.param("user_id", id);
		aq.ajax(cb);
	}
	
	public void RequestFriendDeleteResponse(String url, String json, AjaxStatus status){
		if(json!=null){
			
		}
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.search_friends, menu);
		searchView = (SearchView) menu.findItem(R.id.friend_search).getActionView();
		
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);
		
		int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
		TextView textView = (TextView) searchView.findViewById(id);
		textView.setTextColor(Color.GRAY);
		textView.setHintTextColor(Color.GRAY);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if(!newText.isEmpty()){
			adapter.getFilter().filter(newText.toString().toLowerCase());
		}
		
		if(newText.isEmpty()){
			callFriendsApiForAll();
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		
		return false;
	}
	
}

