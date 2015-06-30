package com.attribes.incommon.groups;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.attribes.incommon.ChatHandler;
import com.attribes.incommon.QBChatLoginListener;
import com.attribes.incommon.R;
import com.attribes.incommon.chat.core.GroupChatManagerImpl;
import com.attribes.incommon.models.FriendAllResponse;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.GroupChatList;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.*;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONObject;

public class GroupChatScreen extends ActionBarActivity implements QBChatLoginListener{
	private ArrayList<Integer> occupantIdsList;
	private QBChatService chatService;
	private String groupName;

	private ListView messagesContainer;
    private ImageView sendButton;
    private EditText chatText;
    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
    private GroupChatManagerImpl groupChatManager;
    private ProgressBar progressBar;
    private ArrayList<QBChatHistoryMessage> history;
    private GroupChatAdapter groupChatAdapter;
    private Bundle extras;
    private TextView participantsTextView;
    private ViewGroup actionBarLayout;
    private ActionBar actionBar;
    private TextView actionBarTitle;
    private TextView actionBarStatus;
    private QBDialog QBDialog;
    private ActionBar bar;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chat_screen);
		initContents();

        loginQBChat();

	}

    private void initContents() {
        progressBar=(ProgressBar)findViewById(R.id.group_chat_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        groupChatManager = new GroupChatManagerImpl(this);
        extras = getIntent().getExtras();
        QBDialog = (com.quickblox.chat.model.QBDialog) extras.getSerializable(Constants.EXTRA_QBDIALOG);
        styleActionBar();
        occupantIdsList = new ArrayList<>();
        participantsTextView = (TextView)findViewById(R.id.group_chat_screen_particpants);
        progressBar = (ProgressBar) findViewById(R.id.group_chat_progressBar);
        messagesContainer = (ListView) findViewById(R.id.groupChat_messagesContainer);
        chatText=(EditText)findViewById(R.id.messageEdit);

        sendButton=(ImageView)findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(new SendButtonClick());

        String occupantNames = "";
        ArrayList<String> occupantNameList=new ArrayList<>();
        for(FriendAllResponse.Response iterator: GroupChatList.getInstance().getList()){

            occupantIdsList.add(Integer.parseInt(iterator.qb_id));
            occupantNameList.add(iterator.name);

        }

        occupantNames= TextUtils.join(",",occupantNameList);
        participantsTextView.setText(getResources().getString(R.string.group_participants) + "\n " + occupantNames);

        chatService = QBChatService.getInstance();

        if (!QBChatService.isInitialized()) {
            QBChatService.init(this);
            chatService = QBChatService.getInstance();
        }

        groupName = getIntent().getStringExtra("groupName");
        if(groupName == null){
            groupName = "New Group";
        }

        ChatHandler.getInstance().addQbChatLoginListener(this);

    }
    public Typeface setCustomFont(String fontName) {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/"+fontName);

        return custom_font;
    }

    private void styleActionBar() {
         bar = getActionBar();


        bar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.ab_solid_background));
//		bar.setHomeAsUpIndicator(R.drawable.ic_ab_back_holo_light);
        bar.setIcon(R.drawable.logo);
        bar.setLogo(R.drawable.logo);
//		bar.setDisplayUseLogoEnabled(true);
//		bar.setDisplayShowHomeEnabled(true);
//
//		bar.setDisplayHomeAsUpEnabled(true);
        int actionBarTitleId = Resources.getSystem().getIdentifier(
                "action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
                title.setTextSize(20);
                if(extras.getString(Constants.EXTRA_INTENT_GROUP) == null){
                    title.setText(QBDialog.getName());
                }
                else{
                    title.setText(extras.getString(Constants.EXTRA_INTENT_GROUP));
                }

                title.setTextColor(getResources().getColor(R.color.actionBar));

            }
        }

//        if(extras.getString(Constants.EXTRA_INTENT_GROUP) == null){
//            actionBarTitle.setText(QBDialog.getName());
//        }
//        else{
//            actionBarTitle.setText(extras.getString(Constants.EXTRA_INTENT_GROUP));
//        }
//
//        actionBarTitle.setTextSize(20);
//        actionBarStatus = (TextView) findViewById(R.id.actionBarChat_status);
//        actionBarStatus.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
    }

    private void createGroupChatDialog(ArrayList<Integer> occupantIdsList) {
        QBDialog dialog = new QBDialog();
        dialog.setName(groupName);
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);



        QBChatService.getInstance().getGroupChatManager().createDialog(dialog, new QBEntityCallbackImpl<QBDialog>() {
            @Override
            public void onSuccess(QBDialog dialog, Bundle args) {

                for (Integer id : dialog.getOccupants()) {

                    sendGroupCreationNotification(dialog);
                    //QBChatMessage groupNotificationMessage = createChatNotificationForGroupChatCreation(dialog);

//                    try {
//                        groupChatManager.sendMessage(groupNotificationMessage);
//                    } catch (XMPPException e) {
//                        e.printStackTrace();
//                    } catch (SmackException.NotConnectedException e) {
//                        e.printStackTrace();
//                    }
                }
                joinGroupChat(dialog);


            }

            @Override
            public void onError(List<String> errors) {

            }
        });




    }

    private void joinGroupChat(QBDialog dialog) {
        ((GroupChatManagerImpl) groupChatManager).joinGroupChat(dialog, new QBEntityCallbackImpl() {


            @Override
            public void onSuccess() {


                loadChatHistory(dialog);
            }

            @Override
            public void onError(List list) {

            }
        });

    }

    private void loadChatHistory(QBDialog dialog) {

        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatHistoryMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatHistoryMessage> messages, Bundle args) {
                progressBar.setVisibility(View.GONE);
                history = messages;

                groupChatAdapter = new GroupChatAdapter(GroupChatScreen.this, new ArrayList<QBMessage>());
                messagesContainer.setAdapter(groupChatAdapter);

                for (QBMessage msg : messages) {
                    showMessage(msg);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GroupChatScreen.this);
                dialog.setMessage("load chat history errors: " + errors).create().show();
            }
        });
    }


    public QBChatMessage createChatNotificationForGroupChatCreation(QBDialog dialog) {
	    String dialogId = String.valueOf(dialog.getDialogId());
	    String roomJid = dialog.getRoomJid();

        ArrayList<Integer> occupants = new ArrayList<>();
        String occupantsIds = "";
        for(int i = 0; i < dialog.getOccupants().size(); i++){

            if(dialog.getOccupants().get(i).equals(Integer.parseInt(MasterUser.getInstance().getUserQbId()))){
                continue;
            }
            else{
                occupants.add(dialog.getOccupants().get(i));

                occupantsIds = TextUtils.join(",", occupants);
            }
        }


	    String dialogName = dialog.getName();
	    String dialogTypeCode = String.valueOf(dialog.getType().ordinal());

	    QBChatMessage chatMessage = new QBChatMessage();
	    chatMessage.setBody("You have been added to "+getGroupName());
	 
	    // Add notification_type=1 to extra params when you created a group chat 
	    //
	    chatMessage.setProperty("notification_type", "1");
	 
	    chatMessage.setProperty("_id", dialogId);
	    if (!TextUtils.isEmpty(roomJid)) {
	        chatMessage.setProperty("room_jid", roomJid);
	    }
	    chatMessage.setProperty("occupants_ids", occupantsIds);
	    if (!TextUtils.isEmpty(dialogName)) {
	        chatMessage.setProperty("name", dialogName);
	    }
	    chatMessage.setProperty("type", dialogTypeCode);


	    return chatMessage;
	}


    public void sendGroupCreationNotification(QBDialog dialog){
        String dialogId = String.valueOf(dialog.getDialogId());
        String roomJid = dialog.getRoomJid();

        ArrayList<Integer> occupants = new ArrayList<>();
        StringifyArrayList<Integer> stringifyOccupantList=new StringifyArrayList<>();
        String occupantsIds = "";
        for(int i = 0; i < dialog.getOccupants().size(); i++){

            if(dialog.getOccupants().get(i).equals(Integer.parseInt(MasterUser.getInstance().getUserQbId()))){
                continue;
            }
            else{
                stringifyOccupantList.add(dialog.getOccupants().get(i));
                occupants.add(dialog.getOccupants().get(i));

                occupantsIds = TextUtils.join(",", occupants);
            }
        }


        String dialogName = dialog.getName();
        String dialogTypeCode = String.valueOf(dialog.getType().ordinal());

        QBEvent event = new QBEvent();
        event.setUserId(Integer.parseInt(MasterUser.getInstance().getUserQbId()));
        event.setUserIds(stringifyOccupantList);
        event.setEnvironment(QBEnvironment.PRODUCTION);
        event.setNotificationType(QBNotificationType.PUSH);
        JSONObject json = new JSONObject();
        try {
            json.put("dialogName", dialogName);
            json.put("dialogId", dialogId);
        } catch (Exception e) {
            e.printStackTrace();
        }


        event.setMessage(json.toString());

        QBMessages.createEvent(event, new QBEntityCallbackImpl<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
            }

            @Override
            public void onError(List<String> errors) {

            }
        });
    }
	public void showMessage(QBMessage chatMessage) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(groupChatAdapter!=null){
                    groupChatAdapter.add(chatMessage);
                    groupChatAdapter.notifyDataSetChanged();
                    scrollDown();
                }

            }
        });	
	}


	private void scrollDown() {
		messagesContainer.setSelection(messagesContainer.getCount() - 1);
	    
		
	}

    private void loginQBChat() {

        if(!(ChatHandler.getInstance().getQBSessionFlag(this))){
            ChatHandler.getInstance().createSession(this);
        }

        else{

            ChatHandler.getInstance().loginChat(this);
        }

    }


    @Override
    public void loggedInQbChatSuccessfully() {
        QBDialog dialog = (QBDialog) extras.getSerializable(Constants.EXTRA_QBDIALOG);
        if(dialog == null){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createGroupChatDialog(occupantIdsList);
                }
            });
        }

        else{

//            ArrayList<Integer> allOccupants = dialog.getOccupants();
//            ArrayList<Integer> occupants=new ArrayList<>();
//
//            for(Integer val:allOccupants){
//
//                if(val==Integer.parseInt(MasterUser.getInstance().getUserQbId())){
//                    continue;
//                }
//                else{
//                    occupants.add(val);
//                }
//
//            }
//
//            dialog.setOccupantsIds(occupants);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    joinGroupChat(dialog);
                }
            });

        }


    }

    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }

    private ArrayList<Integer> getOccupantIds() {
        for(FriendAllResponse.Response iterator: GroupChatList.getInstance().getList()){

            occupantIdsList.add(Integer.parseInt(iterator.qb_id));
        }

        return occupantIdsList;
    }

    private String getGroupName() {
        String groupName;
        if(extras.containsKey(Constants.EXTRA_INTENT_GROUP)){
            groupName = extras.getString(Constants.EXTRA_INTENT_GROUP);
        }

        else{
            groupName="New Group";
        }

        return groupName;
    }

    private class SendButtonClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if(! chatText.getText().toString().isEmpty()){

                String messageText=chatText.getText().toString();
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(messageText);
                chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                String encodedImageUrlString = URLEncoder.encode(MasterUser.getInstance().getUserImageUri());
                chatMessage.setProperty("image_url",encodedImageUrlString );
                chatMessage.setProperty("name",MasterUser.getInstance().getUserName());
                chatMessage.setProperty("type","2");




                try {
                    groupChatManager.sendMessage(chatMessage);
                    chatText.setText("");
                    messagesContainer.setAdapter(groupChatAdapter);
                    showMessage(chatMessage);
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    public void onBackPressed() {
        ChatHandler.getInstance().signOut(this);
//        try {
//
//            groupChatManager.release();
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
        super.onBackPressed();
    }
}
