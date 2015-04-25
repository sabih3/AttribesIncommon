package com.attribes.incommon.api.requests;

import java.util.List;

import org.apache.http.NameValuePair;

import com.attribes.incommon.DAO.User;
import com.attribes.incommon.api.GenericRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * User: abmateen
 * Date: 12/6/14
 * Time: 9:52 PM
 */
public class UserCreateRequest extends GenericRequest {

    private User mUser;

    public UserCreateRequest(User user) {
        this.mUser = user;
        setRequestToPost();
    }

    @Override
    public String getEndPoint() {
        return "/users/create";
    }

    @Override
    public List<NameValuePair> getUrlParamMap() {
        return null; // used for get parameters if there are get parameter in the request
    }

    @Override
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(mUser);
        return json;
    }
}
