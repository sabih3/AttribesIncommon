package com.attribes.incommon.api.requests;

import java.util.List;

import org.apache.http.NameValuePair;

import com.attribes.incommon.api.GenericRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * User: abmateen
 * Date: 12/6/14
 * Time: 10:31 PM
 */
public class AddInterestRequest extends GenericRequest {

    @Expose
    String userId;
    @Expose
    String interests;

    public AddInterestRequest(String userId, String interests) {

    }

    @Override
    public String getEndPoint() {
        return "interest/add";
    }

    @Override
    public List<NameValuePair> getUrlParamMap() {
        return null;
    }

    @Override
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(this);
        return json;
    }
}
