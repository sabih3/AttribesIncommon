package com.attribes.incommon.chat.core;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.attribes.incommon.ChatScreen;
import com.attribes.incommon.R;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBIsTypingListener;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;

public class PrivateChatManagerImpl extends QBMessageListenerImpl<QBPrivateChat> implements ChatManager, QBPrivateChatManagerListener,
QBIsTypingListener<QBPrivateChat>{

    private static final String TAG = "PrivateChatManagerImpl";
    public static final int NOTIFICATION_ID = 1;
    private ChatScreen chatActivity;
    private QBChatService chatService;
    private QBPrivateChatManager privateChatManager;
    private QBPrivateChat privateChat;
	private NotificationManager mNotificationManager;

    public PrivateChatManagerImpl(ChatScreen chatActivity, Integer opponentID) {
        this.chatActivity = chatActivity;
        
        if (!QBChatService.isInitialized()) {
            
    		QBChatService.init(chatActivity);
            chatService = QBChatService.getInstance();
    	}
        
        privateChatManager = QBChatService.getInstance().getPrivateChatManager();
        
        privateChatManager.addPrivateChatManagerListener(this);
        
        try {
        	privateChat = privateChatManager.getChat(opponentID);	
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
        if (privateChat == null) {
            privateChat = privateChatManager.createChat(opponentID, this);
        	
        }
        else{
        	privateChat.addMessageListener(this);	
        }
    
        try {
			privateChat.sendIsTypingNotification();
			privateChat.sendStopTypingNotification();
			
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        privateChat.addIsTypingListener(this);
        
    }

    @Override
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        privateChat.sendMessage(message);
    }

    @Override
    public void release() {
        Log.w(TAG, "release private chat");
        privateChat.removeMessageListener(this);
        privateChatManager.removePrivateChatManagerListener(this);
    }

    @Override
    public void processMessage(QBPrivateChat chat, QBChatMessage message) {
        Log.w(TAG, "new incoming message: " + message);
        chatActivity.showMessage(message);
       
    }

    private void notifyUser(QBChatMessage message) {
    	
    	Intent intent = new Intent(chatActivity, ChatScreen.class);
    	intent.putExtra("opponentQbId", message.getSenderId());
		
		mNotificationManager = (NotificationManager)
                chatActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent chatScreenIntent = PendingIntent.getActivity(chatActivity, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 
		NotificationCompat.Builder mBuilder =
		            new NotificationCompat.Builder(chatActivity)
		    .setSmallIcon(R.drawable.launcher_incommon)
		    .setContentTitle("InCommon")
		    .setStyle(new NotificationCompat.BigTextStyle()
		    .bigText("Somebody has sent you a message"))
		    .setAutoCancel(true);
		
		mBuilder.setContentIntent(chatScreenIntent);
		
	    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
	}

	@Override
    public void processError(QBPrivateChat chat, QBChatException error, QBChatMessage originChatMessage){

    }

    @Override
    public void chatCreated(QBPrivateChat incomingPrivateChat, boolean createdLocally) {
        if(!createdLocally){
            privateChat = incomingPrivateChat;
            privateChat.addMessageListener(PrivateChatManagerImpl.this);
        }

        Log.w(TAG, "private chat created: " + incomingPrivateChat.getParticipant() + ", createdLocally:" + createdLocally);
    }

	@Override
	public void processUserIsTyping(QBPrivateChat privateChat) {
		chatActivity.showIsTypingView();
		
	}

	@Override
	public void processUserStopTyping(QBPrivateChat privateChat) {
		chatActivity.hideisTypingView();
		
	}
}
