package com.attribes.incommon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundService extends Service implements AccelerometerListener{

	

	@Override
	public void onCreate() {
		super.onCreate();
		if (AccelerometerManager.isSupported(this)) {
            
            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		
		
	}

	@Override
	public void onShake(float force) {
		Intent intent = new Intent(getApplicationContext(), SearchFriends.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
