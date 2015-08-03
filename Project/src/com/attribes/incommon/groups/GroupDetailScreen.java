package com.attribes.incommon.groups;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.attribes.incommon.R;
import com.attribes.incommon.chat.core.GroupChatManagerImpl;
import com.attribes.incommon.models.FriendAllResponse;
import com.attribes.incommon.network.RestClient;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.UserDevicePreferences;
import com.attribes.incommon.views.CustomTextView;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabih Ahmed on 02-Jul-15.
 */
public class GroupDetailScreen extends ActionBarActivity implements GroupParticipantChangeListener,GroupDialogUpdateListener{

    private ArrayList<FriendAllResponse.Response> friendList;
    private QBDialog qbDialog;
    private ListView friendListView;
    private GroupParticipantsAdapter adapter;
    private ArrayList<QBUser> QBUsersList;
    private CustomTextView textViewParticpants;
    private CustomTextView textViewParticipantCount;
    private ArrayList<Integer> participantAddList;
    private ArrayList<Integer> participantRemoveList;
    private ArrayList<QBUser> participantAddedNames;
    private ArrayList<String> participantRemovedNames;
    private Menu mMenu;
    private GroupChatManagerImpl groupChatManager;
    private ViewGroup actionBarLayout;
    private ActionBar actionBar;
    private TextView actionBarTitle;
    private ImageView actionBarBack;
    private String groupName;
    private ProgressDialog progressDialog;
    private ArrayList<QBUser> qbUser;
    private ArrayList<String> occupantNameList;
    private String occupantNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail);

        initContents();
        styleActionBar();

    }

    private void initContents() {
        qbDialog = (QBDialog) getIntent().getExtras().getSerializable(Constants.EXTRA_QBDIALOG);
        QBUsersList = (ArrayList<QBUser>) getIntent().getExtras().getSerializable(Constants.EXTRA_QBUSERS);
        friendListView = (ListView)findViewById(R.id.group_detail_list);
        textViewParticpants = (CustomTextView)findViewById(R.id.group_detail_participantNames);
        textViewParticipantCount = (CustomTextView)findViewById(R.id.group_detail_participantCount);
        friendList = new ArrayList<>();
        groupChatManager=new GroupChatManagerImpl(this);
        groupChatManager.setGroupDialogUpdateListener(this);

        populateOccupantDetails(QBUsersList);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        getFriends();

    }

    private void styleActionBar() {
        actionBarLayout=(ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_group_chat, null);

        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_solid_background));
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        actionBarTitle = (TextView) findViewById(R.id.actionBarChat_title);
        actionBarTitle.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
        actionBarTitle.setTextSize(20);


        if (actionBarTitle != null) {
            actionBarTitle.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
            actionBarTitle.setTextSize(20);
            if(qbDialog != null){
                groupName = qbDialog.getName();
                actionBarTitle.setText(groupName);
            }


            actionBarTitle.setTextColor(getResources().getColor(R.color.actionBar));
            actionBarBack = (ImageView)findViewById(R.id.actionBarChat_back);
            actionBarBack.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    showGroupChatScreen();
                    GroupDetailScreen.this.finish();

                }
            });
        }

    }

    public Typeface setCustomFont(String fontName) {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/"+fontName);

        return custom_font;
    }

    private void populateOccupantDetails(ArrayList<QBUser> qbUsersList) {
        String names = "";
        ArrayList<String> namesList=new ArrayList<>();


        try {
            for(QBUser user: qbUsersList){
                namesList.add(user.getFullName());
            }

            textViewParticpants.setText(TextUtils.join(", ", namesList));
            textViewParticipantCount.setText(Integer.toString(qbDialog.getOccupants().size()) + " participants");
        }
        catch (NullPointerException e){
            textViewParticpants.setText("Unable to get participants info this time");
        }




    }


    private void getFriends() {

        RestClient.getAdapter().getFriends(Constants.AUTHORIZATION, UserDevicePreferences.getInstance().getSmToken(),
                new Callback<FriendAllResponse>() {
                    @Override
                    public void success(FriendAllResponse friendAllResponse, Response response) {
                        friendList = friendAllResponse.response;
                        populateListView(friendList);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                    }
                });


    }

    private void populateListView(ArrayList<FriendAllResponse.Response> friendList) {
        adapter = new GroupParticipantsAdapter(this,friendList,qbDialog.getOccupants(),qbDialog);
        friendListView.setAdapter(adapter);
        adapter.setGroupParticipantChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_group_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id ==  R.id.group_detail_done){

            updateGroupChatDialog(qbDialog);
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateGroupChatDialog(QBDialog qbDialog) {

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Group dialog update");
//        progressDialog.setMessage("updating occupants");
//        progressDialog.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        groupChatManager.updateGroupChatOccupants(qbDialog,participantAddList,participantRemoveList,new QBEntityCallbackImpl(){
            @Override
            public void onSuccess(){

                progressDialog.dismiss();
            }


        });
    }

    @Override
    public void OnParticipantChange(ArrayList<Integer> participantAddList, ArrayList<Integer> participantRemoveList,
                                    QBUser participantAddedNames, QBUser participantRemovedNames) {



        this.participantAddList = participantAddList;
        this.participantRemoveList = participantRemoveList;

        if(participantAddedNames != null){

            QBUsersList.add(participantAddedNames);

        }

        if(participantRemovedNames !=null){
            ArrayList<QBUser> tempList = new ArrayList<QBUser>(QBUsersList);


            tempList = (ArrayList<QBUser>) QBUsersList.clone();
            for(QBUser userIterator: QBUsersList){
                if(userIterator.getId().equals(participantRemovedNames.getId())){
                    tempList.remove(userIterator);
                }

            }
            QBUsersList = tempList;
        }



        populateOccupantDetails(QBUsersList);
        textViewParticipantCount.setText(Integer.toString(QBUsersList.size()) + " participants");
        mMenu.findItem(R.id.group_detail_done).setEnabled(true);

    }

    @Override
    public void onBackPressed() {
        showGroupChatScreen();
        this.finish();
    }

    private void showGroupChatScreen() {
        Intent intent = new Intent(this,GroupChatScreen.class);
        intent.putExtra(Constants.EXTRA_QBDIALOG, qbDialog);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_right_out);
    }

    @Override
    public void OnGroupDialogUpdated(QBDialog updatedDialog) {

        qbDialog = updatedDialog;
        showGroupChatScreen();
        this.finish();
        //qbDialog.getOccupants();

        //fetchUsersByIds(qbDialog.getOccupants());

    }

    private void fetchUsersByIds(List<Integer> userIds) {
        occupantNameList=new ArrayList<>();
        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(1);
        requestBuilder.setPerPage(userIds.size());
        //requestBuilder.addParameter("sort_desc", "last_message_date_sent");

        QBUsers.getUsersByIDs(userIds, requestBuilder,
                new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {

                        // Save users
                        //
                        QBUsersList = users;
                        for (QBUser QBUser : users) {
                            occupantNameList.add(QBUser.getFullName());
                        }
                        occupantNames = TextUtils.join(", ", occupantNameList);
                        textViewParticpants.setText(occupantNames);
                        textViewParticipantCount.setText(Integer.toString(qbDialog.getOccupants().size()) + " participants");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(List<String> errors) {
                        progressDialog.dismiss();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                GroupDetailScreen.this);
                        dialog.setMessage("Unable to find occupants: " + errors)
                                .create().show();
                    }

                });


    }
}