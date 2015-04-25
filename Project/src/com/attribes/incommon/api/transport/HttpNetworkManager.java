package com.attribes.incommon.api.transport;

import com.attribes.incommon.api.GenericRequest;
import com.attribes.incommon.api.RequestRunnable;

/**
 * User: amateen
 * Date: 5/29/13
 * Time: 1:57 PM
 * Http Implementation of Network Manager
 */
public class HttpNetworkManager extends NetworkManager {


    @Override
    public <T extends GenericRequest> void dispatchRequest(T request) {

        final RequestRunnable<GenericRequest> runnable = new RequestRunnable<GenericRequest>(request);
        execute(runnable);
        request.onDispatched();


    }


}
