package com.attribes.incommon.network;

import com.attribes.incommon.util.Constants;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by Sabih Ahmed on 17-Jun-15.
 */
public class RestClient {


    private static ApiInterface mApiInterface;


    private RestClient(){

    }

    static {
        setUpResClient();
    }

    private static void setUpResClient(){

        RequestInterceptor interceptor=new RequestInterceptor() {


            @Override
            public void intercept(RequestFacade request) {

                request.addHeader("Accept","application/json");
            }
        };

        RestAdapter restAdapter =new RestAdapter.Builder()

                    .setEndpoint(Constants.BaseUrl)
                    .setRequestInterceptor(interceptor)
                    .build();


        mApiInterface = restAdapter.create(ApiInterface.class);


    }

    public static ApiInterface getAdapter(){

        return mApiInterface;
    }

}
