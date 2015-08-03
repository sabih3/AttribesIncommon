package com.attribes.incommon.groups;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.attribes.incommon.views.CustomTextView;
import com.quickblox.chat.model.QBChatMessage;
import retrofit.Callback;
import retrofit.RetrofitError;
import com.devsmart.android.ui.HorizontalListView;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupCreateScreen extends BaseActivity implements OnItemClickListener,OnOpponentSelectedListener,
OnInterestSelectedListener{

	private AQuery mAquery;
	private ArrayList<Response> friendAllResponseList;
	private FriendForGroupAdapter adapter;
	private ListView mFriendList;
	private TextView participantCountView;
	private TextView groupNameTextView;
    private EditText groupNameEditText;
	private ArrayList<String> groupChatOpponentIds;
    private TextView selectInterestView;
    private LinearLayout participantImageContainer;
    private ArrayList<FriendAllResponse.Response> opponentSelectedList;
    private ImageView image;
    private LinearLayout participantParent;
    private HorizontalListView horizontalListView;
    private ArrayList<String> imageUrlList;
    private HorizontalListAdapter imageAdapter;
    private ExpandableListView expandableListView;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_create_screen);

        initContents();
        setActionBarStyling();
        getAllFriends();

	
	}

    private void initContents() {
        groupNameTextView = (CustomTextView)findViewById(R.id.create_group_GroupNameText);
        groupNameEditText = (EditText)findViewById(R.id.createGroup_groupName);
        selectInterestView = (TextView)findViewById(R.id.createGroup_selectInterest);
        selectInterestView.setOnClickListener(new ChooseInterestClick());


        mFriendList = (ListView) findViewById(R.id.createGroup_friendsList);
        mFriendList.setOnItemClickListener(this);

        groupChatOpponentIds = new ArrayList<String>();
        imageUrlList = new ArrayList<String>();
        imageAdapter=new HorizontalListAdapter(this, imageUrlList);

        participantCountView = (TextView) findViewById(R.id.createGroup_participantCount);

        participantParent = (LinearLayout)findViewById(R.id.createGroup_participantContainer);
        participantImageContainer = (LinearLayout) findViewById(R.id.createGroup_imageParent);
        horizontalListView = (HorizontalListView)findViewById(R.id.createGroup_list);
        horizontalListView.setAdapter(imageAdapter);


        mAquery = new AQuery(this);

        ChooseInterestScreen chooseInterestScreen=new ChooseInterestScreen();
        chooseInterestScreen.setOnInterestSelectedListener(this);
        opponentSelectedList=new ArrayList<>();

        groupNameTextView.setOnClickListener(new GroupNametTextClickListener());
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
            if(groupNameEditText.getText().toString().isEmpty()){
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

		int checkedItems = mFriendList.getCheckedItemCount();
		if(checkedItems >=1 ){
			participantCountView.setVisibility(View.VISIBLE);
			participantCountView.setText(checkedItems+" participant(s)");
			groupChatOpponentIds.add(friendAllResponseList.get(position).qb_id);
		}


	}
	
	private void showGroupChatScreen(ArrayList<String> groupChatOpponentIds) {
		ArrayList<Integer>opponentIds = getIntegerOpponentsIds(groupChatOpponentIds);
		String groupName = "" ;
		Intent intent = new Intent(this, GroupChatScreen.class);
		if(!(groupNameEditText.getText().toString().isEmpty())){
			groupName = groupNameEditText.getText().toString();
			intent.putExtra(Constants.EXTRA_INTENT_GROUP,groupName);
		}

		intent.putExtra(Constants.EXTRA_CHAT_MODE,Constants.Mode.GROUP);
        intent.putIntegerArrayListExtra(Constants.EXTRA_GROUP_OPPONENT_IDS, opponentIds);
		//intent.putExtra(Constants.EXTRA_WELCOME_MESSAGE,"Welcome to "+groupName);
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

        participantCountView.setText(Integer.toString(GroupChatList.getInstance().getList().size()) + " participant(s)");
    }

    @Override
    public void OnOpponentAdded(int position) {

        imageUrlList.add(friendAllResponseList.get(position).image_uri);
        imageAdapter.notifyDataSetChanged();

        int participantCount = imageUrlList.size();
        if(participantCount >=1 ){
            participantCountView.setVisibility(View.VISIBLE);
            participantCountView.setText(participantCount + " participant(s)");
            groupChatOpponentIds.add(friendAllResponseList.get(position).qb_id);
            horizontalListView.setVisibility(View.VISIBLE);


//            ArrayList<String> headerList=new ArrayList<>();
//            HashMap<String,ArrayList<String>> childData=new HashMap<>();
//            headerList.add("");
//            childData.put(headerList.get(0), imageUrlList);
            //expandableListView.setAdapter(new ExpandedListAdapter(GroupCreateScreen.this,headerList,childData));
            //expandableListView.expandGroup(0);
        }




    }

    @Override
    public void OnOpponentRemoved(int position) {

        String imageUri = friendAllResponseList.get(position).image_uri;

        imageUrlList.remove(imageUri);
        imageAdapter.notifyDataSetChanged();

        int participantCount = imageUrlList.size();
        if(participantCount == 0 ){
            participantCountView.setVisibility(View.GONE);
            horizontalListView.setVisibility(View.GONE);
        }
        participantCountView.setText(participantCount+" participant(s)");

    }

    @Override
    public void OnInterestSelected() {
        selectInterestView.setTextColor(getResources().getColor(R.color.selected_interest_color_group));
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

    @Override
    protected void onStop() {
        super.onStop();
        if(!GroupChatList.getInstance().getList().isEmpty()){
            GroupChatList.getInstance().getList().clear();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class GroupNametTextClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            groupNameEditText.setVisibility(View.VISIBLE);
            groupNameEditText.setTypeface(Typeface.createFromAsset(GroupCreateScreen.this.getAssets(),
                    "fonts/Mark Simonson - Proxima Nova Regular.ttf"));
            groupNameTextView.setText("");
            groupNameEditText.requestFocus();
            showSoftKeyBoard();

        }

        private void showSoftKeyBoard() {
            EditText myEditText = (EditText) findViewById(R.id.messageEdit);
            // Check if no view has focus:
            View view = GroupCreateScreen.this.getCurrentFocus();

            if (view != null) {

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        GroupCreateScreen.this.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);



            }


        }

    }
}
