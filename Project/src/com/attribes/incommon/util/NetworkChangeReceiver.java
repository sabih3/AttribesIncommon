package com.attribes.incommon.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Sabih Ahmed on 28-Jul-15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = checkConnectivity(context);

        if(!isConnected){
            Toast.makeText(context,"Internet connectivity lost, please connect to internet to use the app",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkConnectivity(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        return isConnected;
    }
}
