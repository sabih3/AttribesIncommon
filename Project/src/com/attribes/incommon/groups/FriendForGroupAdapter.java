package com.attribes.incommon.groups;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.attribes.incommon.R;
import com.attribes.incommon.models.FriendAllResponse.Response;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class FriendForGroupAdapter extends BaseAdapter{

	private Activity mActivity;
	private ArrayList<Response> friendList;
	
	public FriendForGroupAdapter(Activity activity, ArrayList<Response> friendList) {
		this.mActivity = activity;
		this.friendList = friendList;
	}
	@Override
	public int getCount() {
		
		return friendList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return friendList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null ){
			LayoutInflater inflater=(LayoutInflater) mActivity.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.group_friend_row_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.imageView = (CircularImageView) convertView.findViewById(R.id.group_friend_image);
			viewHolder.textView=(CheckedTextView) convertView.findViewById(R.id.group_friend_name);
		
			convertView.setTag(viewHolder);
		}
		
		else{
			
			viewHolder = (ViewHolder) convertView.getTag(); 
		}
		
		Picasso.with(mActivity).load(friendList.get(position).image_uri).into(viewHolder.imageView);
		viewHolder.textView.setText(friendList.get(position).name);
		
		return convertView;
	}
	
	private static class ViewHolder{
		CircularImageView imageView;
		CheckedTextView textView;
		
	}

}
