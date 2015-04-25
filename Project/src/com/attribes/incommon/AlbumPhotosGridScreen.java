package com.attribes.incommon;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.adapters.AlbumPhotoGridAdapter;
//         import com.attribes.incommon.models.Album;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
import com.parse.ParseFacebookUtils;

public class AlbumPhotosGridScreen extends BaseActivity{

	Context context;
	boolean isEditing;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_photos_grid);
		
		setActionBarStyling();
		String albumId = getIntent().getStringExtra("albumId");
		isEditing = getIntent().getBooleanExtra("is_edit_mode",false);
		if(facebookUserRegistsred()){
		
		retrieveProfileImages(albumId);
		}
		else{
			getPhotos(albumId);
		}
		context = this;
	}
	
	private void retrieveProfileImages(String albumId) {
		final ArrayList<ImageVO> albumImagesArray=new ArrayList<ImageVO>();		
		//final int width = 1080;

		@SuppressWarnings("unused")
		RequestAsyncTask photoRequest=Request.newGraphPathRequest(ParseFacebookUtils.getSession(), albumId+"/photos", new Request.Callback() {
			
			@Override
			public void onCompleted(Response response) {
				if(response.getGraphObject()!=null){
					JSONObject object= response.getGraphObject().getInnerJSONObject();
					JSONArray imagesArray;
					try {
						JSONArray dataArray = object.getJSONArray("data");
						
						for(int i = 0; i < dataArray.length(); i++){
							imagesArray = dataArray.getJSONObject(i).getJSONArray("images");
							
							for(int j = 0; j <1  ; j++){
								ImageVO img = GsonUtility.getObjectFormJsonString(imagesArray.get(0).toString(), ImageVO.class);								
	
								albumImagesArray.add(new ImageVO(img.getHeight(),img.getWidth(),img.getSource()));
								
							}
	
						}
						
						
					} catch (JSONException e) {
	
						e.printStackTrace();
					}
					catch (Exception ex){
						ex.printStackTrace();
					}
					
					setGridView(albumImagesArray);
//					setFacebookProfileImagesArray(profileImagesArray);
//					
//					setUserImageUri(profileImagesArray.get(0).getSource());
//					setFacebookProfileImage();
//					getFacebookAlbums();
				}
			}

			
			
			}).executeAsync();
		
	}
	private void setGridView(ArrayList<ImageVO> albumImagesArray) {
		GridView gridView=(GridView) findViewById(R.id.albumPhotosGrid_gridView);
		gridView.setAdapter(new AlbumPhotoGridAdapter(context,albumImagesArray,facebookUserRegistsred(),isEditing));
		Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center);
        gridView.setAnimation(anim);
        anim.start();
	}
	
public void getPhotos(String albumID){
		
	Log.i("album url", albumID);
	AQuery mAquery = new AQuery(this);
	//AjaxCallback<String> callBack = new AjaxCallback<String>();        
	//callBack.url(url).type(String.class).weakHandler(MainActivity.this, "retrieveGoogleAlbums").method(AQuery.METHOD_GET);
	
	mAquery.ajax(albumID,null , String.class, new AjaxCallback<String>() {

        @Override
        public void callback(String url, String json, AjaxStatus status) {
            if(status.getCode() == 200) {

            	retrieveGoogleAlbums(url,  json,  status);
            	
            } else {

            	Log.i("failed","albums");
            }

        }
    }.method(AQuery.METHOD_GET));
	
	
	
	}

public void retrieveGoogleAlbums(String Url, String json, AjaxStatus status){
	
	ArrayList<String> images = new ArrayList<String>();
	try{
	JSONObject resp = new JSONObject(json);
	JSONObject feed = resp.getJSONObject("feed");
	JSONArray entry = feed.getJSONArray("entry");
	
	
	//Log.i("mediaContent", mediaContent.toString());
	
	for(int i = 0 ; i< entry.length() ; i++){
		 JSONObject obj  =  entry.getJSONObject(i);
		JSONObject mediaGroup = obj.getJSONObject("media$group");
		JSONArray mediaContent =mediaGroup.getJSONArray("media$content");
		JSONObject imageObj = mediaContent.getJSONObject(0);
		
		String str =  imageObj.getString("url");
		images.add(str);
	}

	}
	catch(Exception e){
		
	}
	
	
	setGridViewForGoogle(images);
}
private void setGridViewForGoogle(ArrayList<String> albumImagesArray) {
	GridView gridView=(GridView) findViewById(R.id.albumPhotosGrid_gridView);
	gridView.setAdapter(new AlbumPhotoGridAdapter(context,albumImagesArray,isEditing));
	Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fly_in_from_center);
    gridView.setAnimation(anim);
    anim.start();
}

}
