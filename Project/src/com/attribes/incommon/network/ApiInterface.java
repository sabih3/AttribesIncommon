package com.attribes.incommon.network;

import com.attribes.incommon.models.FriendAllResponse;
import com.attribes.incommon.models.InterestsModel;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Sabih Ahmed on 17-Jun-15.
 */
public interface ApiInterface {

    public static String URL_GET_FRIENDS="/friends/all";
    public static String URL_ALL_INTERESTS = "/interests/all";

    @FormUrlEncoded
    @POST(URL_GET_FRIENDS)
    void getFriends(@Field("authorization")String authorization, @Field("sm_token")String sm_token,
                    Callback<FriendAllResponse> callback);


    @FormUrlEncoded
    @POST(URL_ALL_INTERESTS)
    void getAllInterests(@Field("authorization")String authorization, @Field("sm_token")String sm_token,
                         Callback<InterestsModel> callback);


}
