package com.attribes.incommon;

import java.util.ArrayList;
import java.util.List;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.adapters.ChatAdapter;
import com.attribes.incommon.chat.core.MessageHandler.MessageListener;
import com.attribes.incommon.models.FriendsDetailModel;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.google.gson.Gson;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.model.QBChatHistoryMessage;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBMessage;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;

public class ChatScreen extends ActionBarActivity implements MessageListener,
QBChatLoginListener{
	 private EditText messageEditText;
     private ListView messagesContainer;
     private ImageView sendButton;
     private ProgressBar progressBar;
     private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
     private static final String TAG = ChatScreen.class.getSimpleName();
     private QBDialog mDialog ;
     private ChatAdapter adapter;
     private ArrayList<Integer> opponentId;
     private Integer opponentID;
     private String opponentName;
     private AQuery mAquery;
     private String opponentQbUserId;
     private FriendsDetailModel profile;
     private TextView actionBarStatus;
     private String opponentUserId;
     private Gson gson;
     private ViewGroup actionBarLayout;
     private ActionBar actionBar;
     private TextView actionBarTitle;
     private Bundle extras;
     private QBDialog dialog;
    
     public ChatScreen() {
    	
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		mAquery = new AQuery(this);
		userStatus(1);
		
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		Intent intent = getIntent();
		extras = intent.getExtras();
		
		opponentQbUserId = extras.getString("opponentQbId");
		opponentUserId = extras.getString("opponentUserId");
		
		styleActionBar();
		if(extras.containsKey("opponentName") && !(extras.getString("opponentName").isEmpty())){
			opponentName = getIntent().getStringExtra("opponentName");
			setActionBarTitle(opponentName);
			
			actionBarTitle.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent profileScreenIntent=new Intent(ChatScreen.this, MatchProfile.class);
					profileScreenIntent.putExtra("user_id", opponentUserId);
					profileScreenIntent.putExtra("title", "Search Profile");
					profileScreenIntent.putExtra("opponentQbUserId", opponentQbUserId);
					startActivity(profileScreenIntent);
				}
			});
		}
		else{
			getUserDetail(opponentQbUserId);
		}
		if(!getIntent().getStringExtra("opponentQbId").isEmpty()){
			opponentID = Integer.parseInt(getIntent().getStringExtra("opponentQbId"));	
		}
		
		ChatHandler.getInstance().addQbChatLoginListener(this);
		
		loginQBChat();
		
				
		
		show(progressBar);
		
	}

	
	private void loginQBChat() {
		
		if(!(ChatHandler.getInstance().getQBSessionFlag(this))){
			ChatHandler.getInstance().createSession(this);
		}
		
		else{
			
			ChatHandler.getInstance().loginChat(this);	
		}

	}

	private void setActionBarTitle(String opponentName) {
		actionBarTitle.setText(opponentName);
		
	}

	private void styleActionBar() {
		actionBarLayout=(ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_chat, null);	

		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_solid_background));
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);
		
		actionBarTitle = (TextView) findViewById(R.id.actionBarChat_title);
		actionBarTitle.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		actionBarTitle.setTextSize(20);
		actionBarStatus = (TextView) findViewById(R.id.actionBarChat_status);
		actionBarStatus.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		
	}

	private void initViews(QBDialog dialog, int receipientId) {
			messagesContainer = (ListView) findViewById(R.id.messagesContainer);
	        messageEditText = (EditText) findViewById(R.id.messageEdit);
	        sendButton = (ImageView) findViewById(R.id.chatSendButton);
	        progressBar = (ProgressBar) findViewById(R.id.progressBar);
	        
	        loadChatHistory(dialog,receipientId);
	        
	        sendButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String messageText = messageEditText.getText().toString();
	                if (TextUtils.isEmpty(messageText)) {
	                    return;
	                }

	                // Send chat message
	                //
	                QBChatMessage chatMessage = new QBChatMessage();
	                chatMessage.setBody(messageText);
	                chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
	                
	                ChatHandler.getInstance().sendMessage(chatMessage, ChatScreen.this);
	                messageEditText.setText("");
	                messagesContainer.setAdapter(adapter);
	                showMessage(chatMessage);
	                
	                Flurry.getInstance().eventMessageSend();
					
				}
				
			});
	        
		
	}

	
	private void loadChatHistory(QBDialog dialog, final int receipientId) {
		
		QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
	        customObjectRequestBuilder.setPagesLimit(500);

	        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatHistoryMessage>>() {
	            

				@Override
	            public void onSuccess(ArrayList<QBChatHistoryMessage> messages, Bundle args) {
	                adapter = new ChatAdapter(ChatScreen.this, new ArrayList<QBMessage>(), receipientId);
	                messagesContainer.setAdapter(adapter);

	                for(QBMessage msg : messages) {
	                    showMessage(msg);
	                }
	                
	                hide(progressBar);
	               
	            }

	            @Override
	            public void onError(List<String> errors) {
	                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatScreen.this);
	                dialog.setMessage("load chat history errors: " + errors).create().show();
	                hide(progressBar);
	            }
	        });
		
	}

	public void showMessage(final QBMessage message) {
		
		//adapter.add(message);

	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	if(adapter !=null ){
		            	adapter.add(message);
		                adapter.notifyDataSetChanged();
		                scrollDown();
	            	}
	            }
	
	        });
	        
		
		
	}

	private void scrollDown() {
		 messagesContainer.setSelection(messagesContainer.getCount() - 1);
	}
	
	public Typeface setCustomFont(String fontName) {
		Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/"+fontName);
		
		return custom_font;
	}

	public void showIsTypingView() {
		TextView isTypingView = (TextView) findViewById(R.id.isTypingView);
		isTypingView.setVisibility(View.VISIBLE);
		
	}
	
	public void hideisTypingView() {
		TextView isTypingView = (TextView) findViewById(R.id.isTypingView);
		isTypingView.setVisibility(View.GONE);
		
	}
	
	@Override
	public void chatError(QBPrivateChat sender, QBChatException exception,
			QBChatMessage message) {
		
		
	}
	@Override
	public void chatDidReceiveMessage(QBPrivateChat sender,
			QBChatMessage message) {
		showMessage(message);
		
	}
	@Override
	public void chatDidDeliverMessage(QBPrivateChat sender, String messageID) {
		
		
	}
	@Override
	public void chatDidReadMessage(QBPrivateChat sender, String messageID) {
		
		
	}
	
	private void show(View view){
		view.setVisibility(View.VISIBLE);	
	}
	
	private void hide(View view){
		view.setVisibility(View.GONE);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		userStatus(1);
		loginQBChat();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		userStatus(1);
		//ChatHandler.getInstance().loginChat(ChatScreen.this);
		
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		userStatus(1);
		
		
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		userStatus(0);
		setStatusInBackground(0);
		ChatHandler.getInstance().signOut(ChatScreen.this);
		ChatHandler.getInstance().setQBSessionFlag(this, false);
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		//userStatus(0);
		//ChatHandler.getInstance().signOut(ChatScreen.this);
		
	}
	@Override
	protected void onStop() {
		super.onStop();
		userStatus(0);
		
		ChatHandler.getInstance().signOut(ChatScreen.this);
		ChatHandler.getInstance().setQBSessionFlag(this, false);
		
	}
	
	public void userStatus(int status){
		
		String url = Constants.BaseUrl + Constants.CHAT_STATUS;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(ChatScreen.this, "searchResult");
		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", MasterUser.getInstance().getUserSmToken());
		cb.param("user_id", MasterUser.getInstance().getUserId());
		cb.param("status", status);
		mAquery.ajax(cb);
		
	}
	
	public void getUserDetail(String qbId){
		String url = Constants.BaseUrl + Constants.UserDetail;
		
		AjaxCallback<String> callBack = new AjaxCallback<String>();        
		callBack.url(url).type(String.class).weakHandler(this, "userStatus");
		
		callBack.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		callBack.param("sm_token", MasterUser.getInstance().getUserSmToken());
		callBack.param("qb_id", qbId);
		
		mAquery.ajax(callBack);
	}
	
	public void userStatus(String Url, String json, AjaxStatus status){
		String name;
		if(json!=null){
			gson = new Gson();
			
			profile = gson.fromJson(json.toString(), FriendsDetailModel.class);
			name = profile.response.name;
			
			if((!(extras.containsKey("opponentName")) || (extras.getString("opponentName").isEmpty()))){
				setActionBarTitle(name);
			}
			
			if(profile.response.chat_login.equals("1")){
				actionBarStatus.setText("Online now");
				actionBarStatus.setTypeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
				actionBarStatus.setTextColor(getResources().getColor(R.color.chat_message_time));
			}
			
			else{
				actionBarStatus.setText("Offline now");
				actionBarStatus.setTypeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
				actionBarStatus.setTextColor(getResources().getColor(R.color.chat_message_time));
			}
		}

	}

	// QBChatLoginListener
	@Override
	public void loggedInQbChatSuccessfully() {
		opponentId = new ArrayList<Integer>();
		opponentId.add(opponentID);
		dialog = new QBDialog();
		dialog.setOccupantsIds(opponentId);
		dialog.setType(QBDialogType.PRIVATE);
		
		
		createDialog(dialog);
		ChatHandler.getInstance().addMessageListener(ChatScreen.this,this,opponentID);
		ChatHandler.getInstance().setQBSessionFlag(this, true);
	
	}
	
	private void createDialog(final QBDialog dialog){
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				
				
				QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
				
					
				groupChatManager.createDialog(dialog ,new QBEntityCallbackImpl<QBDialog>(){
					
					@Override
		            public void onSuccess(QBDialog dialog, Bundle args) {
						mDialog = dialog;
						int receipientId = ChatHandler.getInstance().getOpponentIDForPrivateDialog(mDialog);
						adapter = new ChatAdapter(ChatScreen.this, new ArrayList<QBMessage>(),receipientId);
						
						initViews(mDialog,receipientId);
						getUserDetail(opponentQbUserId);
						
		            }

		            @Override
		            public void onError(List<String> errors) {
		                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatScreen.this);
		                dialog.setMessage("dialog creation errors: " + errors).create().show();
		            }
				});
				
			}
	});
		
		
		
}
	
	private void setStatusInBackground(final int status){
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				userStatus(status);
				return null;
			}
		};
	}
	
}

