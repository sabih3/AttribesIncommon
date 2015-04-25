package com.attribes.incommon.adapters;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.attribes.incommon.AlbumPhotosGridScreen;
import com.attribes.incommon.R;
import com.attribes.incommon.models.Album;

public class ProfileAlbumListAdapter extends BaseAdapter{

	ArrayList<Album>profileALbumList = new ArrayList<Album>();
	Context context;
	boolean isFaceBookUserRegistered,isEditing;
	public ProfileAlbumListAdapter(Context context, ArrayList<Album> profileAlbumList,boolean isFaceBookUserRegistered,boolean isEditing) {
		this.profileALbumList = profileAlbumList;
		this.context = context;
		this.isEditing = isEditing;
		this.isFaceBookUserRegistered = isFaceBookUserRegistered;
	}
	
	@Override
	public int getCount() {
		
		return profileALbumList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return profileALbumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		 view =  inflater.inflate(R.layout.profile_album_item, null);
		 
		 TextView albumTitle = (TextView) view.findViewById(R.id.profileAlbum_albumTitle);
		 TextView albumCount = (TextView) view.findViewById(R.id.profileAlbum_albumCount);
		 
		 albumCount.setText(profileALbumList.get(position).getCount());
		 albumTitle.setText(profileALbumList.get(position).getName());
		 
		 view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, AlbumPhotosGridScreen.class);
				intent.putExtra("albumId", profileALbumList.get(position).getId());
				if(isEditing){
					intent.putExtra("is_edit_mode", true);
				}
				context.startActivity(intent);
				
				
				
			}
		});
		 
		return view;
	}

}
