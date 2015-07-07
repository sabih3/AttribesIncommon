package com.attribes.incommon.groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.androidquery.AQuery;
import com.attribes.incommon.BaseActivity;
import com.attribes.incommon.R;
import com.attribes.incommon.interfaces.OnOpponentSelectedListener;
import com.attribes.incommon.models.FriendAllResponse;
import com.attribes.incommon.models.FriendAllResponse.Response;
import com.attribes.incommon.network.RestClient;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.GroupChatList;
import com.attribes.incommon.util.UserDevicePreferences;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import retrofit.Callback;
import retrofit.RetrofitError;
import com.devsmart.android.ui.HorizontalListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GroupCreateScreen extends BaseActivity implements OnItemClickListener,OnOpponentSelectedListener,
OnInterestSelectedListener{

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
    private TextView selectInterestView;
    private LinearLayout participantImageContainer;
    private ArrayList<FriendAllResponse.Response> opponentSelectedList;
    private ImageView image;
    private RelativeLayout participantParent;
    private HorizontalListView horizontalListView;
    private ArrayList<String> imageUrlList;
    private HorizontalListAdapter imageAdapter;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_create_screen);

        initContents();
        setActionBarStyling();
        getAllFriends();

	
	}

    private void initContents() {
        groupNameTextView = (EditText)findViewById(R.id.createGroup_groupName);
        selectInterestView = (TextView)findViewById(R.id.createGroup_selectInterest);
        selectInterestView.setOnClickListener(new ChooseInterestClick());

        mFriendList = (ListView) findViewById(R.id.createGroup_friendsList);
        mFriendList.setOnItemClickListener(this);

        groupChatOpponentIds = new ArrayList<String>();
        imageUrlList = new ArrayList<String>();
        imageAdapter=new HorizontalListAdapter(this, imageUrlList);

        participantCountView = (TextView) findViewById(R.id.createGroup_participantCount);

        participantParent = (RelativeLayout)findViewById(R.id.createGroup_participantContainer);
        participantImageContainer = (LinearLayout) findViewById(R.id.createGroup_imageParent);
        horizontalListView = (HorizontalListView)findViewById(R.id.createGroup_list);
        horizontalListView.setAdapter(imageAdapter);


        mAquery = new AQuery(this);

        ChooseInterestScreen chooseInterestScreen=new ChooseInterestScreen();
        chooseInterestScreen.setOnInterestSelectedListener(this);
        opponentSelectedList=new ArrayList<>();

    }

    private void getAllFriends() {
        RestClient.getAdapter().getFriends(Constants.AUTHORIZATION, UserDevicePreferences.getInstance().getSmToken(), new Callback<FriendAllResponse>() {

            @Override
            public void success(FriendAllResponse friendAllResponse, retrofit.client.Response response) {
                friendAllResponseList = friendAllResponse.response;
                setList(friendAllResponseList);

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    private void setList(ArrayList<Response> friendAllResponseList) {
        adapter = new FriendForGroupAdapter(this,friendAllResponseList);
        adapter.setOnOpponentSelectedListener(this);
        mFriendList.setAdapter(adapter);

    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.group_create, menu);
		
		return true;
	}

//	@Override
//	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//		MenuInflater inflater=mode.getMenuInflater();
//		inflater.inflate(R.menu.group_create, menu);
//		menu.findItem(R.id.group_next).setVisible(false);
//
//		return true;
//	}
//
//	@Override
//	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//		menu.findItem(R.id.group_next).setVisible(true);
//		return true;
//	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		if(id==R.id.group_next){
            if(groupNameTextView.getText().toString().isEmpty()){
                Toast.makeText(this,getResources().getString(R.string.blank_group_name),Toast.LENGTH_SHORT).show();
            }
            else{
                showGroupChatScreen(groupChatOpponentIds);
            }

		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//mFriendList.setItemChecked(position, true);

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
			intent.putExtra(Constants.EXTRA_INTENT_GROUP,groupName);
		}

		intent.putExtra(Constants.EXTRA_CHAT_MODE,Constants.Mode.GROUP);
        intent.putIntegerArrayListExtra("groupChatOpponentIds", opponentIds);
		startActivity(intent);
        overridePendingTransition(R.anim.anim_left_in,R.anim.anim_right_out);
        finish();
		
	}
	
	private ArrayList<Integer> getIntegerOpponentsIds(ArrayList<String> stringOpponentIds){
	ArrayList<Integer> opponentIds = new ArrayList<Integer>();
		
		for(String stringval:stringOpponentIds){
			opponentIds.add(Integer.parseInt(stringval));
		}
		
	
	return  opponentIds;
	}

    @Override
    public void OnOpponentSelectionChange() {

        participantCountView.setText(Integer.toString(GroupChatList.getInstance().getList().size())+" participant(s)");
    }

    @Override
    public void OnOpponentAdded(int position) {

        imageUrlList.add(friendAllResponseList.get(position).image_uri);
        imageAdapter.notifyDataSetChanged();


    }

    @Override
    public void OnOpponentRemoved(int position) {

        String imageUri = friendAllResponseList.get(position).image_uri;

        imageUrlList.remove(imageUri);
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnInterestSelected() {
        selectInterestView.setText(GroupInterest.GROUP_INTEREST.toString());
    }

    private class ChooseInterestClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            showChooseInterestScreen();
        }

        private void showChooseInterestScreen() {
            Intent intent = new Intent(GroupCreateScreen.this, ChooseInterestScreen.class);
            startActivity(intent);
        }
    }
}
