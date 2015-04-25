package com.attribes.incommon.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

//import com.attribes.incommon.MatchProfile;
import com.attribes.incommon.R;
//import com.attribes.incommon.models.SearchFriendsModel;
import com.attribes.incommon.models.FriendAllResponse.Response;
//import com.google.android.gms.common.data.FilteredDataBuffer;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class FriendAllAdapter extends BaseAdapter implements Filterable{

	
	Activity activity;
	public ArrayList<Response> responseArrayList;
	public ArrayList<String> filteredData;
	private ItemFilter mFilter=new ItemFilter();
	
	public FriendAllAdapter(Activity activity,
			ArrayList<Response> response) {
		
		this.activity = activity;
		this.responseArrayList = response;
	}

	@Override
	public int getCount() {
		
		return responseArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return responseArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.friends_search_items, null);
			
			viewHolder = new ViewHolder();
			viewHolder.circularImageView = (CircularImageView) convertView.findViewById(R.id.friend_image);
			viewHolder.friendNameTextView = (TextView) convertView.findViewById(R.id.friend_name);
			viewHolder.isOnlineView = (ImageView) convertView.findViewById(R.id.is_online);
			viewHolder.isOfflineView = (ImageView) convertView.findViewById(R.id.is_offline);
			viewHolder.distance=(TextView) convertView.findViewById(R.id.friend_distance);

			convertView.setTag(viewHolder);
		}
		
		else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		float dist = Float.valueOf(responseArrayList.get(position).distance);
			
		if( dist >= 1){
			String str = String.format("%.2f", dist);
//			Integer dist = Integer.valueOf(getItem(position).distance);
			viewHolder.distance.setText(str+" km away");
		}
		else
			viewHolder.distance.setText("Less than a km away");
		
		if(responseArrayList.get(position).is_login.equals("1")){
			viewHolder.isOnlineView.setVisibility(ImageView.VISIBLE);
			viewHolder.isOfflineView.setVisibility(ImageView.GONE);
			
		}
		
		else{
			viewHolder.isOnlineView.setVisibility(ImageView.GONE);
			viewHolder.isOfflineView.setVisibility(ImageView.VISIBLE);
		}
		
		Picasso.with(activity).load(responseArrayList.get(position).image_uri).into(viewHolder.circularImageView);
		viewHolder.friendNameTextView.setText(responseArrayList.get(position).name+ " , "+ responseArrayList.get(position).age);

		return convertView;
	}
	
	private static class ViewHolder{
		CircularImageView circularImageView;
		TextView friendNameTextView,distance;
		ImageView isOnlineView;
		ImageView isOfflineView;
	}

	@Override
	public Filter getFilter() {
		
		return mFilter;
	}
	
	private class ItemFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence filterText) {
			
			FilterResults results=new FilterResults();
			final ArrayList<Response> list = responseArrayList;
			int count =list.size();
			final ArrayList<Response> nList=new ArrayList<Response>(count);
			
			Response filterableString;
			
			for(int i=0;i<count;i++){
				filterableString=list.get(i);
				
				if(filterableString.name.toString().toLowerCase().
					startsWith(filterText.toString().toLowerCase())){
					nList.add(filterableString);
					
				}
			}
			
			results.values=nList;
			results.count=nList.size();
			
			
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence filterText, FilterResults results) {
			filteredData = new ArrayList<String>();
			
			responseArrayList = (ArrayList<Response>) results.values;
			notifyDataSetChanged();
			
			
		}
		
		
		
	}

}
