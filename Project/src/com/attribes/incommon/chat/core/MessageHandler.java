package com.attribes.incommon.chat.core;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;

import android.content.Context;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBIsTypingListener;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;

public class MessageHandler extends QBMessageListenerImpl<QBPrivateChat> implements ChatManager,QBPrivateChatManagerListener, QBIsTypingListener<QBPrivateChat>{

	private MessageListener messageListener;
	
	private  QBPrivateChatManager mPrivateChatManager;
	private QBPrivateChat mPrivateChat;
    private static QBChatService mChatService;
    private int mOpponentID;
    private Context mContext;
    
	
	public MessageHandler(Context context) {
		
		if (!QBChatService.isInitialized()) {
	             
		     QBChatService.init(context);
		            
		 }
		
		mChatService = QBChatService.getInstance();
		//mPrivateChatManager = QBChatService.getInstance().getPrivateChatManager();
		mPrivateChatManager = mChatService.getInstance().getPrivateChatManager();
		
		
//		try {
//			mChatService.startAutoSendPresence(Constants.AUTO_PRESENCE_INTERVAL_IN_SECONDS);
//			
//		} catch (NotLoggedInException e) {
//			
//			
//			//TODO:Have to verify this 
//			Intent intent=new Intent(context, ConnectScreen.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(intent);
//			e.printStackTrace();
//		}
		if(mPrivateChatManager!=null)
			mPrivateChatManager.addPrivateChatManagerListener(this);
		
		
		
	}
	
	public MessageHandler(Context context,int opponentID) {
		if (!QBChatService.isInitialized()) {
            
		     QBChatService.init(context);
		            
		 }
		mChatService = QBChatService.getInstance();
		//mPrivateChatManager = QBChatService.getInstance().getPrivateChatManager();
		mPrivateChatManager=mChatService.getInstance().getPrivateChatManager();
		
		
//		try {
//			mChatService.startAutoSendPresence(Constants.AUTO_PRESENCE_INTERVAL_IN_SECONDS);
//		} catch (NotLoggedInException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(mPrivateChatManager != null)
			mPrivateChatManager.addPrivateChatManagerListener(this);
		this.mOpponentID = opponentID;
		
	}
	
	public void addChatManagerListener(){
		mPrivateChatManager.addPrivateChatManagerListener(this);
	}
	
	public void addListener(MessageListener messageListener){
		this.messageListener = messageListener;
	}
	
	/*Chat Manager*/
	@Override
	public void sendMessage(QBChatMessage message) throws XMPPException,
			NotConnectedException {
		mPrivateChat = QBChatService.getInstance().getPrivateChatManager().getChat(mOpponentID);

		if (mPrivateChat == null) {
            mPrivateChat = QBChatService.getInstance().getPrivateChatManager().createChat(mOpponentID, this);
            
		}
		else{
			mPrivateChat.addMessageListener(this);
		}

		mPrivateChat.sendMessage(message);
		

		
	}
	/*Chat Manager*/
	@Override
	public void release() throws XMPPException {
		
		//this.messageListener = null;
	}
	
	/*Chat QBPrivateChatManagerListener*/
	@Override
	public void chatCreated(QBPrivateChat privatChat, boolean createdLocally) {
		this.mPrivateChat = privatChat;	
		 if(!createdLocally){
			 privatChat.addMessageListener(this);
			 mPrivateChatManager.addPrivateChatManagerListener(this); 
		 }
	}
	
	/*QBMessageListenerImpl*/
	@Override
	public void processError(QBPrivateChat sender, QBChatException exception,
			QBChatMessage message) {
		super.processError(sender, exception, message);
		this.messageListener.chatError(sender, exception, message);
	}
	
	@Override
	public void processMessage(QBPrivateChat sender, QBChatMessage message)  {
		//super.processMessage(sender, message);
		
		this.messageListener.chatDidReceiveMessage(sender, message);
	}
	
	@Override
	public void processMessageDelivered(QBPrivateChat sender, String messageID) {
		super.processMessageDelivered(sender, messageID);
		this.messageListener.chatDidDeliverMessage(sender, messageID);
	}
	
	
	@Override
	public void processMessageRead(QBPrivateChat sender, String messageID) {
		super.processMessageRead(sender, messageID);
		this.messageListener.chatDidReadMessage(sender, messageID);
	}
	
	public void setChatService(QBChatService chatService){
		this.mChatService=chatService;
	}
	public interface MessageListener{
		
		void chatError(QBPrivateChat sender, QBChatException exception,
				QBChatMessage message);
		
		void chatDidReceiveMessage(QBPrivateChat sender, QBChatMessage message) ;
		
		void chatDidDeliverMessage(QBPrivateChat sender, String messageID);
		
		void chatDidReadMessage(QBPrivateChat sender, String messageID);
	
	}
	@Override
	public void processUserIsTyping(QBPrivateChat arg0) {
		
		
	}

	@Override
	public void processUserStopTyping(QBPrivateChat arg0) {
		
		
	}
	
}
