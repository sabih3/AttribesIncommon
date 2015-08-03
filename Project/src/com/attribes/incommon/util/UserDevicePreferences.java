package com.attribes.incommon.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.attribes.incommon.SplashScreen;

/**
 * Created by Sabih Ahmed on 17-Jun-15.
 */
public class UserDevicePreferences {


    private static UserDevicePreferences mInstance;
    private static Context mContext;
    private SharedPreferences mPrefs;

    private UserDevicePreferences(){

    }

    public static UserDevicePreferences getInstance(){
        if(mInstance == null){

            mInstance = new UserDevicePreferences();
        }

        return mInstance;
    }

    public void init(Context context){
        this.mContext = context;
        mPrefs = context.getSharedPreferences(SplashScreen.PREFS_NAME,0);
    }

    public String getSmToken(){
        String sMToken= "";
        sMToken = mPrefs.getString(Constants.USER_SM_TOKEN, "");

        return sMToken;
    }


    public String getQbUserId() {

        String qbUserId = mPrefs.getString(
                Constants.QB_USER_ID, "");

        return qbUserId;
    }

    public void setQBSessionFlag(Boolean sessionStatus){
        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putBoolean(Constants.QBSESSION,true);
        editor.commit();
    }

    public boolean getQBSessionFlag(){

        boolean QBSessionFlag = mPrefs.getBoolean(Constants.QBSESSION, false);


        return  QBSessionFlag;

    }

}
