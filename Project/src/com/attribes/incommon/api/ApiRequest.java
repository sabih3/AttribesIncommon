package com.attribes.incommon.api;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.BaseActivity;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;

public class ApiRequest {

	public Context context;
	private AQuery aq;
	public ApiRequest(Context context){
		this.context = context;
		aq = new AQuery(context);
	}

	public void post(String endpoint, Map<String,String> params, final Callback callback){
		String url = Constants.BaseUrl + endpoint;

		params.put("Authorization",Constants.AUTHORIZATION);
		JSONObject json = new JSONObject(params);

	}

	/**Send user create request to server
	 * 
	 * @param socialMedia
	 * @param token
	 * @param lat
	 * @param lon
	 * @param deviceId
	 * @param description
	 * @param ImageUri
	 * @param name
	 * @param age
	 * @param selectedInterest List
	 */
	public void userCreate(String socialMedia,String token,
			String lat,String lon,String deviceId,
			String description ,String ImageUri,
			String name,
			String age,
			ArrayList<String> selectedInterests,
			String installation_id,
			String qb_id,String gender
			, Object obj, String functionName){

		final BaseActivity baseActivity=new BaseActivity(); 
		baseActivity.context = context;		
		String interest="";
		for(int i=0;i<selectedInterests.size();i++){
			interest+=selectedInterests.get(i);
			if (i != (selectedInterests.size() - 1)) {
				interest+=",";
			}

		}
		String url = Constants.BaseUrl +"/users/create";
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(obj, functionName);

		
		cb.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		cb.param("sm_type", socialMedia);
		cb.param("sm_token", token);
		cb.param("latitude", lat);
		cb.param("longitude", lon);
		cb.param("device_id", deviceId);
		cb.param("description",description);
		cb.param("image_uri", ImageUri);
		cb.param("name",name);
		cb.param("age",age);
		cb.param("interests", interest);
		cb.param("installation_id",installation_id);
		cb.param("qb_id",qb_id);
		cb.param("gender", gender);
		
		aq.ajax(cb);

	}

	public void reportResult(String url, String json, AjaxStatus status){
		if(json != null){
			
		}
	}
	public void userDetail(String userId,String sm_token){

		final String URL=Constants.BaseUrl+ "/users/detail";

		HashMap<String, String> params=new HashMap<String, String>();
		params.put("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		params.put("user_id", userId);
		params.put("sm_token",sm_token);


		JSONObject userDetailParamJson=new JSONObject(params);

		/*JsonObjectRequest userDetailRequest = new JsonObjectRequest(Method.POST, URL,
				userDetailParamJson, 
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						try {
							JSONObject jsonObject = response.getJSONObject("response");
							User userDetail = new User();

							userDetail.setUserDetail(jsonObject.getString("description"), jsonObject.getString("latitude"), 
													 jsonObject.getString("longitude"), jsonObject.getString("image_uri"));
							Log.d("INCOMMON", response.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, 
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {


					}

				});

		AppController.getInstance().addToRequestQueue(userDetailRequest);	*/

	}


	private static int getUserIdFromResponse(JSONObject response) {

		int userId = 0;

		try {

			userId = response.getInt("response");

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return userId;
	}

	public void getUserDetailApi(String userId, Object obj, String functionName, String smToken){
		String url = Constants.BaseUrl + Constants.UserDetail;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(obj, functionName);

		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", smToken);
		cb.param("user_id", userId);
		aq.ajax(cb);
	}
	public void getMyDetailApi(String userId, Object obj, String functionName, String smToken){
		String url = Constants.BaseUrl + Constants.USER_GET;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(obj, functionName);

		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", smToken);
		cb.param("user_id", MasterUser.getInstance().getUserId());
		
		aq.ajax(cb);
	}
	
	public void setUserStatus(int userStatus, Object object, String callBackFuntionName){
		String url = Constants.BaseUrl + Constants.USER_STATUS;
		AjaxCallback<String> callBack = new AjaxCallback<String>();
		callBack.url(url).type(String.class).weakHandler(object, callBackFuntionName);
		
		callBack.param(Constants.PARAM_AUTHORIZATION, Constants.AUTHORIZATION);
		callBack.param(Constants.PARAM_SM_TOKEN , MasterUser.getInstance().getUserSmToken());
		callBack.param(Constants.PARAM_USER_STATUS, Integer.toString(userStatus));

		aq.ajax(callBack);
	}

	public void requestUpdatePreferences(MasterUser masterUser, Context context, String responseFuncName) {
		
		String url=Constants.BaseUrl+Constants.USER_UPDATE;
		AjaxCallback<String> callBack=new AjaxCallback<String>();
		callBack.url(url).type(String.class).weakHandler(context, responseFuncName);
	
		String prefsAge = Integer.toString((masterUser.getPreference_minAge()))+"-"+
				Integer.toString(masterUser.getPreference_maxAge());
		
		int prefsDistance = masterUser.getPreference_distance();
		
		int prefsMatches = masterUser.isPreference_newMatches() ? 1: 0 ;
		int prefsAlerts =  masterUser.isPreference_alerts() ? 1 : 0 ;
		int prefsMessages = masterUser.isPreference_messages() ? 1 :0 ;

		
		String gender = null;
		
		if(MasterUser.getInstance().isPreferences_showWomen()){
			gender = "women";
		}
		
		if(MasterUser.getInstance().isPreferences_showMen()){
			gender = "men";
		}
		
		if(MasterUser.getInstance().isPreferences_showBoth()){
			gender = "both";
		}
		
		callBack.param(Constants.PARAM_AUTHORIZATION, Constants.AUTHORIZATION);
		callBack.param(Constants.PARAM_SM_TOKEN, masterUser.getUserSmToken());
		callBack.param(Constants.PARAM_USER_ID, masterUser.getUserId());
		callBack.param(Constants.PARAM_PREFS_AGE, prefsAge);
		callBack.param(Constants.PARAM_PREFS_DISTANCE, prefsDistance);
		callBack.param(Constants.PARAM_PREFS_GENDER, gender);
		callBack.param(Constants.PARAM_PREFS_MATCHES, prefsMatches);
		callBack.param(Constants.PARAM_PREFS_ALERTS,prefsAlerts);
		callBack.param(Constants.PARAM_PREFS_MESSAGES, prefsMessages);
		
		aq.ajax(callBack);
		
		
	}
	
	public void requestUpdateUser(MasterUser masterUser, Context context , String responseFunctionName){

		String url = Constants.BaseUrl + Constants.USER_UPDATE;
		AjaxCallback<String> callBack = new AjaxCallback<String>();
		callBack.url(url).type(String.class).weakHandler(context, responseFunctionName);
		
		callBack.param(Constants.PARAM_AUTHORIZATION, Constants.AUTHORIZATION);
		callBack.param(Constants.PARAM_SM_TOKEN, masterUser.getUserSmToken());
		callBack.param(Constants.PARAM_USER_ID,masterUser.getUserId());
//		callBack.param("qb_id", masterUser.getUserQbId());
//		callBack.param("installation_id",masterUser.getUserParseObjectId());
		//callBack.param(Constants.PARAM_GENDER,getUserGender);
		ArrayList<String> selectedInterests = masterUser.getUserInterests();
		
		String interest="";
		for(int i=0;i<selectedInterests.size();i++){
			interest+=selectedInterests.get(i);
			if (i != (selectedInterests.size() - 1)) {
				interest+=",";
			}

		}

		callBack.param("interests", interest);

		callBack.param(Constants.PARAM_DESCRIPTION, masterUser.getUserDescription());
		callBack.param(Constants.PARAM_IMAGE_URI, masterUser.getUserImageUri());
		
		
		aq.ajax(callBack);
	}
	
	public void requestUpdateUserQbId(MasterUser masterUser, Context context , String responseFunctionName){

		String url = Constants.BaseUrl + Constants.USER_UPDATE;
		AjaxCallback<String> callBack = new AjaxCallback<String>();
		callBack.url(url).type(String.class).weakHandler(context, responseFunctionName);
		
		callBack.param(Constants.PARAM_AUTHORIZATION, Constants.AUTHORIZATION);
		callBack.param(Constants.PARAM_SM_TOKEN, masterUser.getUserSmToken());
		callBack.param(Constants.PARAM_USER_ID,masterUser.getUserId());
		callBack.param("qb_id", masterUser.getUserQbId());
		
		
		
		aq.ajax(callBack);
	}
	
	public void requestUpdateUserParseInstallationId(MasterUser masterUser, Context context , String responseFunctionName){
		

		String url = Constants.BaseUrl + Constants.USER_UPDATE;
		AjaxCallback<String> callBack = new AjaxCallback<String>();
		callBack.url(url).type(String.class).weakHandler(context, responseFunctionName);
		
		callBack.param(Constants.PARAM_AUTHORIZATION, Constants.AUTHORIZATION);
		callBack.param(Constants.PARAM_SM_TOKEN, masterUser.getUserSmToken());
		callBack.param(Constants.PARAM_USER_ID,masterUser.getUserId());
		callBack.param("installation_id",masterUser.getUserParseObjectId());

		aq.ajax(callBack);
	}



}

