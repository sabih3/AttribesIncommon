package com.attribes.incommon.api;

import com.attribes.incommon.api.requests.AddInterestRequest;
import com.attribes.incommon.api.transport.HttpNetworkManager;

/**
 * User: abmateen
 * Date: 12/7/14
 * Time: 2:03 PM
 */
public class InterestBAL extends BaseBAL {

    public void addInterest(String interenstCsv, String userId, ResponseCallback callback){
        AddInterestRequest request = new AddInterestRequest(userId, interenstCsv);
        request.setResponseCallback(callback);

        HttpNetworkManager networkManager = new HttpNetworkManager();
        networkManager.dispatchRequest(request);
    }


}
