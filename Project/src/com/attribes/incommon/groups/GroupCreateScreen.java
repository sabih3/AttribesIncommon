package com.attribes.incommon.groups;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.BaseActivity;
import com.attribes.incommon.R;
import com.attribes.incommon.models.FriendAllResponse;
import com.attribes.incommon.models.FriendAllResponse.Response;
import com.attribes.incommon.util.Constants;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class GroupCreateScreen extends BaseActivity implements OnItemClickListener, MultiChoiceModeListener{

	private AQuery mAquery;
	private ArrayList<Response> friendAllResponseList;
	private FriendForGroupAdapter adapter;
	private ListView mFriendList;
	private TextView participantCountView;
	private EditText groupNameTextView;
	private ArrayList<String> groupChatOpponentIds;
	private CircularImageView friendImage1;
	private CircularImageView friendImage2;
	private CircularImageView friendImage3;
	private CircularImageView friendImage4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_create_screen);
		
		groupNameTextView=(EditText)findViewById(R.id.createGroup_groupName);
		mFriendList = (ListView) findViewById(R.id.createGroup_friendsList);
		groupChatOpponentIds = new ArrayList<String>();
		participantCountView = (TextView) findViewById(R.id.createGroup_participantCount);
		
		friendImage1 = (CircularImageView) findViewById(R.id.createGroup_image1);
		friendImage2 = (CircularImageView) findViewById(R.id.createGroup_image2);
		friendImage3 = (CircularImageView) findViewById(R.id.createGroup_image3);
		friendImage4 = (CircularImageView) findViewById(R.id.createGroup_image4);
		
		mAquery = new AQuery(this);
		
		mFriendList.setOnItemClickListener(this);
		mFriendList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mFriendList.setMultiChoiceModeListener(this);
		callFriendsApiForAll();
	
	}

	public void callFriendsApiForAll(){
		
		BaseActivity baseActivity=new BaseActivity();
		baseActivity.context = this;
		String url = Constants.BaseUrl + Constants.friendAll;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(GroupCreateScreen.this, "friendResult");
		
		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", baseActivity.getSmToken());
		
		mAquery.ajax(cb);
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
			
			adapter = new FriendForGroupAdapter(this,friendAllResponseList);
			mAquery.id(R.id.createGroup_friendsList).getListView().setAdapter(adapter);

			if(friendAllResponseObject.response.size() == 0){
				//mAquery.id(R.id.no_friends).visible();
				mAquery.id(R.id.createGroup_friendsList).invisible();
				adapter.notifyDataSetChanged();
			}
				
		}

	}
	
	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.group_create, menu);
		
		return true;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater=mode.getMenuInflater();
		inflater.inflate(R.menu.group_create, menu);
		menu.findItem(R.id.group_next).setVisible(false);
		
		return true;	
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		menu.findItem(R.id.group_next).setVisible(true);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		if(id==R.id.group_next){
			showGroupChatScreen(groupChatOpponentIds);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if(item.getItemId()==R.id.group_next){
			//showGroupChatScreen();
			mode.finish();
		}
		return false;
	}

	

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		
		
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		
//		if(checked == true){
//			mFriendList.setItemChecked(position, true);
//			Picasso.with(this).load(friendAllResponseList.get(position).image_uri).into(friendImage1);
//			Picasso.with(this).load(friendAllResponseList.get(position).image_uri).into(friendImage2);
//			Picasso.with(this).load(friendAllResponseList.get(position).image_uri).into(friendImage3);
//			Picasso.with(this).load(friendAllResponseList.get(position).image_uri).into(friendImage4);
//			int checkedItems = mFriendList.getCheckedItemCount();
//			if(checkedItems >=1 ){
//				participantCountView.setVisibility(View.VISIBLE);
//				participantCountView.setText(checkedItems+" participant(s)");
//				groupChatOpponentIds.add(friendAllResponseList.get(position).qb_id);
//			
//			
//			}
//		}
		
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mFriendList.setItemChecked(position, true);
		Picasso.with(this).load(friendAllResponseList.get(position).image_uri).into(friendImage1);
		Picasso.with(this).load(friendAllResponseList.get(position).image_uri).into(friendImage2);
		Picasso.with(this).load(friendAllResponseList.get(position).image_uri).into(friendImage3);
		Picasso.with(this).load(friendAllResponseList.get(position).image_uri).into(friendImage4);
		int checkedItems = mFriendList.getCheckedItemCount();
		if(checkedItems >=1 ){
			participantCountView.setVisibility(View.VISIBLE);
			participantCountView.setText(checkedItems+" participant(s)");
			groupChatOpponentIds.add(friendAllResponseList.get(position).qb_id);
		}
	}
	
	private void showGroupChatScreen(ArrayList<String> groupChatOpponentIds) {
		ArrayList<Integer>opponentIds = getIntegerOpponentsIds(groupChatOpponentIds);
		String groupName;
		Intent intent = new Intent(this, GroupChatScreen.class);
		if(!(groupNameTextView.getText().toString().isEmpty())){
			groupName = groupNameTextView.getText().toString();
			intent.putExtra("groupName",groupName);
		} 
		intent.putIntegerArrayListExtra("groupChatOpponentIds", opponentIds);
		startActivity(intent);
		
	}
	
	private ArrayList<Integer> getIntegerOpponentsIds(ArrayList<String> stringOpponentIds){
	ArrayList<Integer> opponentIds=new ArrayList<Integer>();
		
		for(String stringval:stringOpponentIds){
			opponentIds.add(Integer.parseInt(stringval));
		}
		
	
	return  opponentIds;
	}
}
