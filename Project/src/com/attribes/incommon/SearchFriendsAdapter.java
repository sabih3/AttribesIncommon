package com.attribes.incommon;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
//import com.attribes.incommon.adapters.MessageAdapter.ViewHolder;
//import com.attribes.incommon.DAO.User;
import com.attribes.incommon.models.SearchFriendsModel;
//import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
//import com.attribes.incommon.models.SearchFriendsModel.Response;

public class SearchFriendsAdapter extends BaseAdapter{

	Context ctx;
	AQuery aqAdapter;
	ArrayList<SearchFriendsModel.User> array;
	
	public SearchFriendsAdapter(Context ctx, ArrayList<SearchFriendsModel.User> array) {
		this.ctx = ctx;
		this.array = array;
	}

	@Override
	public int getCount() {
	
		
		return array.size();
		
	}

	@Override
	public com.attribes.incommon.models.SearchFriendsModel.User getItem(int position) {
		//TODO return real object (from SearchFriendsModel)
		return array.get(position);
		//return SearchFriendsModel.getInstance().response.users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("DefaultLocale") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		ViewHolder viewHolder;
		if(view == null){
			view = LayoutInflater.from(ctx).inflate(R.layout.friends_search_items, parent, false);
			viewHolder = createViewHolder(view);
			view.setTag(viewHolder);
			aqAdapter = new AQuery(view);
		}else{
			viewHolder = (ViewHolder) view.getTag();
		}
		String image = getItem(position).image_uri;
		if(image.length()>2){
			Picasso.with(ctx).load(getItem(position).image_uri).placeholder(R.drawable.placeholder).into(viewHolder.user_Image);
		}else{
			viewHolder.user_Image.setImageResource(R.drawable.placeholder);
		}
		
			//aqAdapter.id(R.id.friend_image).image(getItem(position).image_uri);
//		else
//			aqAdapter.id(R.id.friend_image).image(R.drawable.placeholder);
		viewHolder.name.setText(getItem(position).name + " , " + getItem(position).age);
		//aqAdapter.id(R.id.friend_name).text(getItem(position).name);
		float dist = Float.valueOf(getItem(position).distance);
		if( dist >= 1){
			String str = String.format("%.2f", dist);
			viewHolder.distance.setText(str+" km away");
//			Integer dist = Integer.valueOf(getItem(position).distance);
			//aqAdapter.id(R.id.friend_distance).text(str+" km away");
		}
		else
			viewHolder.distance.setText("Less than a km away");
			//aqAdapter.id(R.id.friend_distance).text("Less than a km away");
		
		if(getItem(position).is_login.equals("1")){
			viewHolder.onilne_Image.setVisibility(ImageView.VISIBLE);
			viewHolder.offlineImage.setVisibility(ImageView.GONE);
		}
		else{
			viewHolder.onilne_Image.setVisibility(ImageView.GONE);
			viewHolder.offlineImage.setVisibility(ImageView.VISIBLE);
		}
		return view;
	}

	private static class ViewHolder{
		private TextView name,distance;
		private ImageView user_Image, onilne_Image, offlineImage;
		
	}
	private ViewHolder createViewHolder(View convertView) {
		
		ViewHolder viewHolder = new ViewHolder();
		
		viewHolder.user_Image = (ImageView)convertView.findViewById(R.id.friend_image);
		viewHolder.name = (TextView) convertView.findViewById(R.id.friend_name);
		viewHolder.distance = (TextView) convertView.findViewById(R.id.friend_distance);
		viewHolder.onilne_Image = (ImageView) convertView.findViewById(R.id.is_online);
		viewHolder.offlineImage = (ImageView) convertView.findViewById(R.id.is_offline);
		return viewHolder;
		
	}
}
