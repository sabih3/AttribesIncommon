package com.attribes.incommon.groups;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.widget.ListView;

import com.attribes.incommon.R;
import com.attribes.incommon.adapters.ChatAdapter;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;

public class GroupChatScreen extends ActionBarActivity{
	private ArrayList<Integer> occupantIdsList;
	private QBChatService chatService;
	private String groupName;
	private ChatAdapter chatAdapter;
	private ListView messagesContainer;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_chat_screen);
		messagesContainer = (ListView) findViewById(R.id.groupChat_messagesContainer);
		chatService = QBChatService.getInstance();
		//chatAdapter=new ChatAdapter(this, chatMessages, opponentID);
		if (!QBChatService.isInitialized()) {
		    QBChatService.init(this);
		    chatService = QBChatService.getInstance();
		}
		
		occupantIdsList = getIntent().getIntegerArrayListExtra("groupChatOpponentIds");
		groupName = getIntent().getStringExtra("groupName"); 
		if(groupName == null){
			groupName = "New Group";
		}
		
		QBDialog dialog = new QBDialog();
		dialog.setName(groupName);
		dialog.setType(QBDialogType.GROUP);
		dialog.setOccupantsIds(occupantIdsList);
		 
		QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
		groupChatManager.createDialog(dialog, new QBEntityCallbackImpl<QBDialog>() {
		    @Override
		    public void onSuccess(QBDialog dialog, Bundle args) {
		    	for (Integer userID : dialog.getOccupants()) {
		    		QBChatMessage chatMessage = createChatNotificationForGroupChatCreation(dialog);
		    		//long time = DateUtils.
		    	    //chatMessage.setProperty("date_sent", time + "");
		    	 
		    	    QBPrivateChat chat = QBChatService.getInstance().getPrivateChatManager().getChat(userID);
		    	   
		    	    if (chat == null) {
		    	        chat = chatService.getPrivateChatManager().createChat(userID, null);
		    	    }
		    	 
		    	    try {
		    	        chat.sendMessage(chatMessage);
		    	    } catch (Exception e) {
		    	    	
		    	        // error
		    	    }
		    	}
		    }
		 
		    @Override
		    public void onError(List<String> errors) {
		 
		    }
		});
		
	}
	
	
	public static QBChatMessage createChatNotificationForGroupChatCreation(QBDialog dialog) {
	    String dialogId = String.valueOf(dialog.getDialogId());
	    String roomJid = dialog.getRoomJid();
	    String occupantsIds = TextUtils.join(",", dialog.getOccupants());
	    String dialogName = dialog.getName();
	    String dialogTypeCode = String.valueOf(dialog.getType().ordinal());
	 
	    QBChatMessage chatMessage = new QBChatMessage();
	    chatMessage.setBody("optional text");
	 
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


	public void showMessage(QBChatMessage chatMessage) {
		chatAdapter.add(chatMessage);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter.notifyDataSetChanged();
                scrollDown();
            }
        });	
	}


	private void scrollDown() {
		messagesContainer.setSelection(messagesContainer.getCount() - 1);
	    
		
	}
	
	
	

	
	
	
	
}
