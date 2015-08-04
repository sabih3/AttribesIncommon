package com.attribes.incommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.attribes.incommon.util.GroupChatList;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.api.ApiRequest;
import com.attribes.incommon.chat.core.MessageHandler;
import com.attribes.incommon.chat.core.MessageHandler.MessageListener;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class ChatHandler {


    private static final String LOGGEDIN = "You have already logged in chat";

    private static String QbUserLogin;
	private static String QbUserPassword;
	private static QBUser currentUser ;
	private static ChatHandler chatManager;
	private static QBChatService chatService;
	private MessageHandler messageHandler;
	private static QBSessionListener qbSessionListener;
	private static QBSignInListener qbSignInListener;
	private static QBChatLoginListener qbChatLoginListener;
	private Map<Integer, QBUser> dialogsUsers =new HashMap<Integer, QBUser>();
	private static Context mContext;
	private static Context appContext;
    private Activity mActivity;
	private  QBUser loginUser;
	
	private ChatHandler(){
			
	}

    public static void init(Context context){
        appContext = context;
    }
	public static ChatHandler getInstance(){
		
		if(chatManager == null){
			chatManager = new ChatHandler();
			}
	
	return chatManager;
		
		
	}

    public void initializeChat(){

        if(!QBChatService.isInitialized()){

            QBChatService.init(appContext);

        }

    }
	
	public void createSessionForNewUser(){
		QBInit();
	}
	
	public  void createSessionForExistingUser(QBUser qbUser , Context context){
		
		createSession(qbUser, context);
	}
	
	
	public void setQbSessionListener(QBSessionListener qbSessionListener) {
		this.qbSessionListener = qbSessionListener;
	}

	
	
	public void addQbSessionListener(QBSessionListener qbSessionListener){
		this.setQbSessionListener(qbSessionListener);
	}
	
	public void addQbSignInListener(QBSignInListener qbSignInListener){
		this.qbSignInListener = qbSignInListener;
	}
	
	public void addQbChatLoginListener(QBChatLoginListener qbChatLoginListener){
		this.qbChatLoginListener = qbChatLoginListener;
	}
	
	public static void QBInit(){
        QBSettings.getInstance().setServerApiDomain(Constants.API_END_POINT);

        QBSettings.getInstance().setChatServerDomain(Constants.CHAT_END_POINT);

        QBSettings.getInstance().setTurnServerDomain(Constants.TURN_SERVER);

        QBSettings.getInstance().setContentBucketName(Constants.BUCKET);

		QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
		
		QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
 
			@Override
            public void onSuccess(QBSession session, Bundle params) {
				
				
				if(!(qbSessionListener == null)){
					qbSessionListener.sessionCreated();
                    GroupChatList.getInstance().setQBSessionFlag(true);
				}
					
				
				
            }
         
			@Override
            public void onError(List<String> errors) {

//                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                appContext.startActivity(intent);
                QBInit();

            }
        });
		
		

	}
	

	
	public void createSession(final Context context){
//		final String qbUserLogin, final String qbUserPassword, final Context context
//		final BaseActivity baseActivity = new BaseActivity();
//		baseActivity.context = context;
		QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
		
		QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
            	signInQB(context);
            }
         
			@Override
            public void onError(List<String> errors) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	             dialog.setMessage("Chat Session failed: " + errors).create().show();
            }
        });
        
    }
	
	public static void createSession(final QBUser user, final Context context ){
		QBSettings.getInstance().fastConfigInit(Constants.APP_ID, Constants.AUTH_KEY, Constants.AUTH_SECRET);
		
		
			QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
	
				@Override
			    public void onSuccess(QBSession session, Bundle params) {
					
					qbSessionListener.sessionCreated();

				}
			 
			    @Override
			    public void onError(List<String> errors) {
			    	AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		             dialog.setMessage("Chat Session failed: " + errors).create().show();
			    }
			});
		
	}
	
	public void signInToQb(final QBUser loginUser, final BaseActivity baseActivity) {
		mContext=baseActivity;
		
		
		QBUsers.signIn(loginUser, new QBEntityCallbackImpl<QBUser>() {
    	    @Override
    	    public void onSuccess(QBUser user, Bundle args) {
    	    	
    	    	//TODO: Base activity needs to be single-ton
    	    	if (!QBChatService.isInitialized()) {
		              
    	    		QBChatService.init(baseActivity);
		            chatService = QBChatService.getInstance();
    	    	}
		    	qbSignInListener.loggedInQBSuccessfully();
	    		//loginUser.setPassword(baseActivity.getSmToken());
	    		//loginToChat(loginUser);
	
    	    }

			
			@Override
    	    public void onError(List<String> errors) {
				
				signInToQb( loginUser, baseActivity);
				 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	                dialog.setMessage("Signing in chat service Failed: " + errors).create().show();
	                
				
    	    }
    	});
}
		
	private void signInQB(final Context context){
		
		final QBUser qbUser = new QBUser();
		qbUser.setId(Integer.parseInt(MasterUser.getInstance().getUserQbId()));
		qbUser.setLogin(MasterUser.getInstance().getUserQbLogin());
		qbUser.setPassword(MasterUser.getInstance().getUserQbPassword());
		
		QBUsers.signIn(qbUser, new QBEntityCallbackImpl<QBUser>(){
			 @Override
	    	    public void onSuccess(QBUser user, Bundle args) {
				 qbUser.setId(user.getId());
				 loginToChat(qbUser);
			 }
			 
			 @Override
	    	    public void onError(List<String> errors) {
				 AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	                dialog.setMessage("Signing in chat service Failed: " + errors).create().show();
			 }
		});
	}

	private void updateQbUser(QBUser user, BaseActivity baseActivity) {
		user.setFullName(baseActivity.getUserName());
		user.setCustomData(baseActivity.getUserImageUri());
		QBUsers.updateUser(user, new QBEntityCallbackImpl<QBUser>(){
			
			@Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
				
                
            }

            @Override
            public void onError(List<String> strings) {

            }
		});
		
	}
	
	/**Sign up new QB user, in case user has already been created, just log it in.
	 * 
	 * @param qbUserLogin
	 * @param qbUserPassword
	 */
	public void signUpQbUser(String qbUserLogin, final String qbUserPassword,String qbUserName,String qbUserImageUri, final BaseActivity baseActivity) {
		
		final QBUser user = new QBUser(qbUserLogin, qbUserPassword);
							user.setFullName(qbUserName);
							user.setCustomData(qbUserImageUri);
			
	    	QBUsers.signUp(user, new QBEntityCallbackImpl<QBUser>() {
	    	   
				@Override
	    	    public void onSuccess(QBUser user, Bundle args) {
					
					currentUser = new QBUser();
	    	    	currentUser.setLogin(user.getLogin());
	    	    	currentUser.setPassword(qbUserPassword);
	    	    	currentUser.setId(user.getId());
	    	    	
	    	    	
	    	    	baseActivity.setQbUserId(currentUser.getId().toString());
	    	    	baseActivity.setQbUserLogin(currentUser.getLogin());
	    	    	baseActivity.setQbUserPassword(qbUserPassword);

	    	    	currentUser.setId(Integer.parseInt(MasterUser.getInstance().getUserQbId()));
	    	    	currentUser.setLogin(MasterUser.getInstance().getUserQbLogin());
	    	    	currentUser.setPassword(MasterUser.getInstance().getUserQbPassword());
	    	    	
	    	    	ApiRequest apiRequest = new ApiRequest(baseActivity);
	    	    	apiRequest.requestUpdateUserQbId(MasterUser.getInstance(), baseActivity, "responseUpdateQbId");
	    	    	signInToQb(currentUser, baseActivity);
	    	        
	    	    }
	    	 
				@Override
	    	    public void onError(List<String> errors) {
					 AlertDialog.Builder dialog = new AlertDialog.Builder(baseActivity);
		             dialog.setMessage("creating chat user failed: " + errors).create().show();
	    	    }
	    	});
		
	}
	
	@SuppressWarnings("rawtypes")
	public void loginToChat(QBUser loginUser) {
	
		final QBUser tempUser = loginUser;
		if(!QBChatService.isInitialized()){
			QBChatService.init(appContext);
			
		}
		chatService = QBChatService.getInstance();
		
		if(chatService.isLoggedIn()){
			return ;
		}
		
		chatService.login(loginUser, new QBEntityCallbackImpl(){
			 @Override
	            public void onSuccess() {

				 	qbChatLoginListener.loggedInQbChatSuccessfully();

	            }

	            @Override
	            public void onError(List errors) {
	            	for(int i = 0; i< errors.size() ; i++){
	            		if(errors.get(i).equals(LOGGEDIN)){
	            			
	            			qbSignInListener.loggedInQBSuccessfully();
	            		}
	            		else if(errors.get(i).equals("NoResponseException")){
	            			loginToChat(tempUser);
	            		}
	            		else{
	            			 AlertDialog.Builder dialog = new AlertDialog.Builder(appContext);
	    		             dialog.setMessage("Log in chat user failed: " + errors).create().show();
	            		}
	            	}
	            	
	            }
	        });
		
	}
	
	public void responseUpdateQbId(String url,String json, AjaxStatus status ){
		if(json != null){
			
		}
	}
	
	
	public  void signOut(Context context){
		if(chatService!=null){
			
		
			boolean loggedIn = chatService.isLoggedIn();
			
			if(!loggedIn){
				return ;
			}
			
			chatService.stopAutoSendPresence();
			chatService.logout(new QBEntityCallbackImpl() {
				 
			    @Override
			    public void onSuccess() {
			        // success
			    	Log.d("ChatHandler", "LoggedOut from Chat");
			        chatService.destroy();
			    }
			 
			    @Override
			    public void onError(final List list) {
			 
			    }
			});
	}
		
		
	}
	
	public void logInChat(QBUser user , final Context context){

		if (!QBChatService.isInitialized()) {
            
    		QBChatService.init(context);
            chatService = QBChatService.getInstance();
    	}
		
		if(!(chatService.isLoggedIn())){
		
			chatService.login(user, new QBEntityCallbackImpl() {

				 @Override
		            public void onSuccess() {
					 qbSignInListener.loggedInQBSuccessfully();
				 }
				 
				 @Override
		            public void onError(List errors) {
		                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		                dialog.setMessage("chat login errors: " + errors).create().show();
		            }
				
			});
		}
		
		else{
			return ;
		}

		
	}
	
	public void loginChat(final Context context){
		
		if (!QBChatService.isInitialized()) {
            
    		QBChatService.init(context);
            chatService = QBChatService.getInstance();
    	}
		
		if(!(chatService.isLoggedIn())){
		
			QBUser qbUser=new QBUser();
			qbUser.setId(Integer.parseInt(MasterUser.getInstance().getUserQbId()));
			qbUser.setLogin(MasterUser.getInstance().getUserQbLogin());
			qbUser.setPassword(MasterUser.getInstance().getUserQbPassword());
			chatService.login(qbUser, new QBEntityCallbackImpl() {

				 @Override
		            public void onSuccess() {
					 qbChatLoginListener.loggedInQbChatSuccessfully();
					 
				 }
				 
				 @Override
		            public void onError(List errors) {
		                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		                dialog.setMessage("chat login errors: " + errors).create().show();
		            }
				
			});
		}

		
	}

	public void stopChatPresence(Context context){
		if (!QBChatService.isInitialized()) {
            
    		QBChatService.init(context);
            chatService = QBChatService.getInstance();
    	}
		chatService.stopAutoSendPresence();
	}
	
	public void startChatPresence(Context context){
		if (!QBChatService.isInitialized()) {
            
    		QBChatService.init(context);
            chatService = QBChatService.getInstance();
    	}
		try {
			chatService.startAutoSendPresence(Constants.AUTO_PRESENCE_INTERVAL_IN_SECONDS);
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}
	}
	

	public static QBUser getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(QBUser currentUser) {
		ChatHandler.currentUser = currentUser;
	}
	
	 public Integer getOpponentIDForPrivateDialog(QBDialog dialog){
	        Integer opponentID = -1;
	        for(Integer userID : dialog.getOccupants()){
	            if(!Integer.toString(userID).equals(MasterUser.getInstance().getUserQbId())){
	                opponentID = userID;
	                break;
	            }
	        }
	        return opponentID;
	    }
	 
	 public void sendMessage(QBChatMessage message, Activity activity) {
		 try {
			messageHandler.sendMessage(message);
			
		} catch (NotConnectedException e) {
			
			e.printStackTrace();
		} catch (XMPPException e) {
			
			e.printStackTrace();
		}
		 
	 }
	
	 public void addMessageListener(Context context, MessageListener messageListener){

		 messageHandler = new MessageHandler(context);
		
		 messageHandler.addListener(messageListener);
	 }
	 
	 public void addMessageListener(Context context, MessageListener messageListener,int opponentID){

		 messageHandler = new MessageHandler(context,opponentID);
		 messageHandler.addListener(messageListener);
	 }
	
	 public void initializeChat(Context context){
		 if (!QBChatService.isInitialized()) {
             
	    		QBChatService.init(context);
	    		
	            chatService = QBChatService.getInstance();
	            
	    	}
	 
	 }
	 
	 public void getChalDialogsPrivate(Context context){
		 if (!QBChatService.isInitialized()) {
             
    		QBChatService.init(context);
            chatService = QBChatService.getInstance();
	    	
		 }
		 QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
		 requestBuilder.setPagesLimit(100);
		  
		 QBChatService.getChatDialogs(QBDialogType.PRIVATE, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
		     @Override
		     public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args) {
		    	 
		     }
		  
		     @Override
		     public void onError(List<String> errors) {
		  
		     }
		 });
		 
	 }
	 
	 public Map<Integer, QBUser> getDialogsUsers() {
	      
		 return dialogsUsers;
	    }
	 
	 public void setDialogUsers(List<QBUser> usersList){
		 dialogsUsers.clear();
		 
		 for(QBUser user:usersList){
			 dialogsUsers.put(user.getId(), user);
		 }
		 
	 }
	 
	/** This method sets a flag in device preferences which indicates 
	 * whether user session has been made or not <br>
	 * 
	 * true if session has been made, false otherwise
	 * 
	 * @param Boolean flag 
	 */
	 public void setQBSessionFlag(Context context, Boolean flag){
		 SharedPreferences devicePreferences=context.getSharedPreferences("devicePreferences", 0);
		 SharedPreferences.Editor editor=devicePreferences.edit();
		 editor.putBoolean("qbSessionState", flag);
		 editor.commit();
	 }
	 
	 
	 /**
	  * 
	  */
	 public Boolean getQBSessionFlag(Context context){
		SharedPreferences devicePreferences =context.getSharedPreferences("devicePreferences", 0);
		boolean sessionFlag = devicePreferences.getBoolean("qbSessionState", false);

		return sessionFlag;
	 }
	 
	
} 
	



