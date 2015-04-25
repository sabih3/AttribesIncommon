package com.attribes.incommon;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService{

	 public static final int NOTIFICATION_ID = 1;
	 private NotificationManager mNotificationManager;
	 NotificationCompat.Builder builder;
	 private Push push;
	 private static String starMatchNotificationText ="You have a star match !";
	 private String parsePushData;
	 private String qbPushData;
	
	 public GcmIntentService() {
		super("GcmIntentService");
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras=intent.getExtras();
		
		
		GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        String messageType = googleCloudMessaging.getMessageType(intent);

		if(GoogleCloudMessaging.
                MESSAGE_TYPE_MESSAGE.equals(messageType)){
			
			SharedPreferences registrationPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
			int count = registrationPrefs.getInt("notificationCount" , 0);
			
			
				SharedPreferences.Editor editor = registrationPrefs.edit();
				editor.putInt("notificationCount", ++count);
				editor.commit();
				
			qbPushData = extras.getString("message");
			
			if(qbPushData !=null && qbPushData.contains(":")){
				String[] pushDataArray = qbPushData.split(":");
				String name = pushDataArray[0];
				
				String message = pushDataArray[1];
				String  opponentQbId = (String) extras.get("user_id");
				processQbNotification(opponentQbId,message,name);	
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

	
	private void processQbNotification(String pushUserId, String message, String opponentName) {
		Uri defaultNotificationSound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		Intent intent = new Intent(this, ChatScreen.class);
		intent.putExtra("opponentQbId", pushUserId);
		intent.putExtra("opponentName", opponentName);
		
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
