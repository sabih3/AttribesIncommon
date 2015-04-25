package com.attribes.incommon;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.ListView;

import com.attribes.incommon.adapters.ProfileAlbumListAdapter;
import com.attribes.incommon.models.Album;

public class ProfileAlbumListScreen extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_profile_album_list);
		setActionBarStyling();
		ArrayList<Album> profileALbums = new ArrayList<Album>();
		
		if(facebookUserRegistsred())
		profileALbums = getFacebookAlbums();
		else
			profileALbums = getGooglePlusAlbums();
		
		ListView listView=(ListView) findViewById(R.id.profileAlbum_list);
		listView.setAdapter(new ProfileAlbumListAdapter(this, profileALbums,facebookUserRegistsred(),getIntent().getBooleanExtra("is_edit_mode",false)));
	}
	
	
	
	

}
