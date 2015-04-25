package com.attribes.incommon.api.transport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.attribes.incommon.api.GenericRequest;
import com.attribes.incommon.api.RequestRunnable;

/**
 *
 * User: amateen
 * Date: 5/29/13
 * Time: 1:51 PM
 * Generic NetworkManager for making Network Requests
 */
public abstract class NetworkManager {

    private static final String TAG = "NetworkManager";
    private ExecutorService service = Executors.newFixedThreadPool(10);

    public abstract<T extends GenericRequest> void dispatchRequest(T request);

    public void execute(RequestRunnable<?> runnable){
        Log.d(TAG,"execute()");
        service.execute(runnable);
    }



}
