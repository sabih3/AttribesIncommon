package com.attribes.incommon;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;

import android.widget.Toast;
import com.attribes.incommon.groups.GroupChatScreen;
import com.attribes.incommon.groups.GroupMainScreen;
import com.attribes.incommon.groups.OnDialogTypeReceivedListener;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.attribes.incommon.util.GroupChatList;
import com.attribes.incommon.util.UserDevicePreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import java.util.ArrayList;
import java.util.List;


public class GcmIntentService extends IntentService implements OnDialogTypeReceivedListener {

	 public static final int NOTIFICATION_ID = 1;
     public static final int NOTIFICATION_ID_GROUP_CREATION=2;
	 private NotificationManager mNotificationManager;
	 NotificationCompat.Builder builder;
	 private Push push;
	 private static String starMatchNotificationText ="You have a star match !";
	 private String parsePushData;
	 private String qbPushData;
     private Handler mHandler;
    private QBChatService chatService;


    public GcmIntentService() {
		super("GcmIntentService");
		
	}

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @SuppressWarnings("deprecation")
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras=intent.getExtras();
		
		
		GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        String messageType = googleCloudMessaging.getMessageType(intent);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        if(GoogleCloudMessaging.
                MESSAGE_TYPE_MESSAGE.equals(messageType)){
			
			SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
			int count = registrationPrefs.getInt("notificationCount" , 0);
			
			
				SharedPreferences.Editor editor = registrationPrefs.edit();
				editor.putInt("notificationCount", ++count);
				editor.commit();

            if(extras.get("dialogId")!=null){
                String invitationMessage = extras.getString("message");
                String dialogId = (String) extras.get("dialogId");
                processQBNotificationForGroup(invitationMessage,dialogId);
            }
            else{
                qbPushData = extras.getString("message");
                String dialog_id = (String) extras.get("dialog_id");
                if(qbPushData != null && qbPushData.contains(":")){
                    String[] pushDataArray = qbPushData.split(":");
                    String name = pushDataArray[0];

                    String message = pushDataArray[1];
                    String  opponentQbId = (String) extras.get("user_id");
                    //processQbNotification(opponentQbId, message, name, false);

                    Toast.makeText(GcmIntentService.this,dialog_id,Toast.LENGTH_SHORT).show();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            getDialogType(dialog_id, opponentQbId, message, name);
                        }
                    });




                }
            }


		}
		
		
		if(extras.containsKey("data")){
			
			SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
			int count = registrationPrefs.getInt("notificationCount" , 0);
			
			
				SharedPreferences.Editor editor = registrationPrefs.edit();
				editor.putInt("notificationCount", ++count);
				editor.commit();
				
			parsePushData = extras.get("data").toString();
		
        
			try {
				push = GsonUtility.getObjectFormJsonString(parsePushData, Push.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(push.type.equals("bulk-match")){           							/** In case of Bulk Matches only, here matches means no of people**/
				sendBulkMatchNotification(push);	
			}
			if(push.type.equals("match") && push.matches.equals("5")){  			/**In case of Star match only, here matches mean no of interests matched**/
				Flurry.getInstance().eventStarMatchReceived();
				sendStarMatchNotification(push);
				
			}
			
			if(push.type.equals("match")){                                   		/** In case of ordinary single match, here matches would be no of interests matched**/
				
				Flurry.getInstance().eventMatchReceived();
				sendMatchNotification("You have got a new match with ",push);
				
			}
			
			/** Pushes for Notification(s)**/
			if(push.type.equals("notification")){   
				if(push.notification_type.equals("FRIEND_ADD")){
					sendNotification("You have got a new friend Request",push);
				}
				else if(push.notification_type.equals("FRIEND_ACCEPT")){
					sendNotification("Your friend request has been accepted", push);
				}
				else{
					
					sendNotification("You have got a new Notification", push);
				}
				
			}
		
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

    private void getDialogType(String dialogId, String opponentQbId, String message, String name) {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.in("_id", dialogId);
       // requestBuilder.setPagesLimit(100);



        if(!QBChatService.isInitialized()){

            ChatHandler.getInstance().initializeChat();
        }
       try {

           QBChatService.getInstance().getChatDialogs(null, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {

               @Override
               public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args) {

                   if (dialogs.get(0).getType().equals(QBDialogType.GROUP)) {
                       processQbNotification(opponentQbId, message, name, true, dialogs.get(0));
                   }

                   if (dialogs.get(0).getType().equals(QBDialogType.PRIVATE)) {
                       processQbNotification(opponentQbId, message, name, false, null);
                   }
               }

               @Override
               public void onError(List<String> errors) {

               }
           });

       }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();

       }


//        if(GroupChatList.getInstance().getQBSessionFlag()){
//            if(!QBChatService.isInitialized()){
//
//                ChatHandler.getInstance().initializeChat();
//            }
//
//            QBChatService.getInstance().getChatDialogs(null, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
//
//                @Override
//                public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args) {
//
//                    if (dialogs.get(0).getType().equals(QBDialogType.GROUP)) {
//                        processQbNotification(opponentQbId, message, name, true,dialogs.get(0));
//                    }
//
//                    if (dialogs.get(0).getType().equals(QBDialogType.PRIVATE)) {
//                        processQbNotification(opponentQbId, message, name, false, null);
//                    }
//                }
//
//                @Override
//                public void onError(List<String> errors) {
//
//                }
//            });
//        }
//
//        else{
//
//            ChatHandler.getInstance().QBInit();
//
//            if(!QBChatService.isInitialized()){
//
//                ChatHandler.getInstance().initializeChat();
//            }
//
//            QBChatService.getInstance().getChatDialogs(null, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
//
//                @Override
//                public void onSuccess(ArrayList<QBDialog> dialogs, Bundle args) {
//
//                    if (dialogs.get(0).getType().equals(QBDialogType.GROUP)) {
//                        processQbNotification(opponentQbId, message, name, true, dialogs.get(0));
//                    }
//
//                    if (dialogs.get(0).getType().equals(QBDialogType.PRIVATE)) {
//                        processQbNotification(opponentQbId, message, name, false, null);
//                    }
//                }
//
//                @Override
//                public void onError(List<String> errors) {
//
//                }
//            });
//
//        }



    }

    private void processQBNotificationForGroup(String invitationMessage, String dialogId) {

        Uri defaultNotificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent groupScreen = new Intent(this, GroupMainScreen.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
                groupScreen,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.launcher_incommon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(invitationMessage)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(invitationMessage))
                .setTicker(invitationMessage)
                .setSound(defaultNotificationUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true);


        notificationBuilder.setContentIntent(pendingIntent);

        mNotificationManager.notify(NOTIFICATION_ID_GROUP_CREATION,notificationBuilder.build());
    }


    private void processQbNotification(String pushUserId, String message, String opponentName, boolean isGroup,
                                       QBDialog dialog) {
		Uri defaultNotificationSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = null;
        if(isGroup){
            intent = new Intent(this, GroupChatScreen.class);
            intent.putExtra(Constants.EXTRA_QBDIALOG,dialog);

        }
        else{
            intent = new Intent(this, ChatScreen.class);
            intent.putExtra("opponentQbId", pushUserId);
            intent.putExtra("opponentName", opponentName);
        }

		
		mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent chatScreenIntent = PendingIntent.getActivity(this, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 
		NotificationCompat.Builder mBuilder =
		            new NotificationCompat.Builder(this)
		    .setSmallIcon(R.drawable.launcher_incommon)
		    .setContentTitle(getResources().getString(R.string.app_name))
		    .setContentText(message)
		    .setTicker(message)
		    .setStyle(new NotificationCompat.BigTextStyle()
		    .bigText("You have got a new message"))
		    .setSound(defaultNotificationSound)
            .setPriority(Notification.PRIORITY_LOW)
		    .setAutoCancel(true);
		
		mBuilder.setContentIntent(chatScreenIntent);
		
	    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	private void sendBulkMatchNotification(Push push) {
		Uri defaultNotificationSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		Intent intent = new Intent(this, ActivityScreen.class);
		
		
		mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);

	    NotificationCompat.Builder mBuilder =
	            new NotificationCompat.Builder(this)
	    .setSmallIcon(R.drawable.launcher_incommon)
	    .setContentTitle(getResources().getString(R.string.app_name))
	    .setStyle(new NotificationCompat.BigTextStyle()
	    .bigText("You have got "+push.matches.toString()+ "new matches"))
	    .setSound(defaultNotificationSound)
	    .setAutoCancel(true)
	    .setContentText("You have got "+push.matches.toString()+ "new matches");
	
	    mBuilder.setContentIntent(contentIntent);
	    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
	}

	private void sendStarMatchNotification(Push starMatchPush) {
		Uri defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		
		Intent intent = new Intent(this, StarMatchScreen.class);
		intent.putExtra("user_id", push.getUser_id());
		
		mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);

	    NotificationCompat.Builder mBuilder =
	            new NotificationCompat.Builder(this)
	    .setSmallIcon(R.drawable.launcher_incommon)
	    .setContentTitle(getResources().getString(R.string.app_name))
	    .setStyle(new NotificationCompat.BigTextStyle()
	    .bigText(starMatchNotificationText))
	    .setSound(defaultNotificationSound)
	    .setAutoCancel(true)
	    .setContentText(starMatchNotificationText);
	
	    mBuilder.setContentIntent(contentIntent);
	    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
	}

	private void sendNotification(String string, Push pushObjectNotification) {
		Uri defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Intent intent =new Intent(this, ActivityScreen.class);
		intent.putExtra("notification", true);
		
		mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);

	    NotificationCompat.Builder mBuilder =
	            new NotificationCompat.Builder(this)
	    .setSmallIcon(R.drawable.launcher_incommon)
	    .setContentTitle(getResources().getString(R.string.app_name))
	    .setStyle(new NotificationCompat.BigTextStyle()
	    .bigText(string))
	    .setSound(defaultNotificationSound)
	    .setAutoCancel(true)
	    .setContentText(string);
	
	    mBuilder.setContentIntent(contentIntent);
	    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}

	private void sendMatchNotification(String string,Push pushObject) {
		Uri defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		
		mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent =new Intent(this, MatchProfilePush.class);
			intent.putExtra("user_id", push.getUser_id());
			
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.launcher_incommon)
        .setContentTitle(getResources().getString(R.string.app_name))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(string+pushObject.getMatches()+" interests matched"))
        .setSound(defaultNotificationSound)
        .setAutoCancel(true)
        .setContentText(string);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
	}

    @Override
    public void OnDialogTypeReceived() {

    }

    public class Push {
		String matches;
		String push_hash;
		String user_id;
		String notification_type;
		String type;
		
		public Push (String matches, String push_hash, String user_id){
			this.matches = matches;
			this.push_hash = push_hash;
			this.user_id = user_id;
		}
		public void setMatches(String matches) {
			this.matches = matches;
		}
		public void setPush_hash(String push_hash) {
			this.push_hash = push_hash;
		}
		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}
		public String getMatches() {
			return matches;
		}
		public String getPush_hash() {
			return push_hash;
		}
		public String getUser_id() {
			return user_id;
		}
		
	}
	
	
	
}
