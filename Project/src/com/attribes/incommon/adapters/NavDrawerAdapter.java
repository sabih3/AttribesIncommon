package com.attribes.incommon.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.attribes.incommon.CompleteProfileScreen;
import com.attribes.incommon.R;
import com.attribes.incommon.models.NavDrawerItem;
import com.attribes.incommon.util.Constants;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class NavDrawerAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<NavDrawerItem> drawerItemList=new ArrayList<NavDrawerItem>();
	private String userFullName;
	private String userImageUri;
	
	public NavDrawerAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItemList, String userFullName,
			String userImageUri){
		this.context = context;
		this.drawerItemList = navDrawerItemList;
		this.userFullName = userFullName;
		this.userImageUri = userImageUri;
	}
	@Override
	public int getCount() {
		
		return drawerItemList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return drawerItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if(convertView == null){
			LayoutInflater mInflater = (LayoutInflater)
	                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        convertView = mInflater.inflate(R.layout.drawer_list_item, null);
	        
			viewHolder = new ViewHolder();
			viewHolder.relative = (RelativeLayout)convertView.findViewById(R.id.drawerItemParentView);
			viewHolder.userImage = (CircularImageView) convertView.findViewById(R.id.drawerItem_image);
			viewHolder.nextArrow = (ImageView) convertView.findViewById(R.id.drawerItem_arrow);
			viewHolder.counter = (TextView) convertView.findViewById(R.id.drawerItem_counter);
			viewHolder.drawerTitle = (TextView) convertView.findViewById(R.id.title);
			viewHolder.drawerTitle.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
			
			convertView.setTag(viewHolder);
		}
		
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
    
		viewHolder.userImage.setVisibility(View.GONE);
		viewHolder.nextArrow.setVisibility(View.GONE);
		viewHolder.counter.setVisibility(View.GONE);
		viewHolder.drawerTitle.setTextColor(context.getResources().getColor(R.color.color_drawer_title));
		viewHolder.drawerTitle.setText(drawerItemList.get(position).getTitle());
		convertView.setBackgroundColor(context.getResources().getColor(R.color.color_drawer_rows));
         
        switch (position) {
		
        case 0:
        	viewHolder.userImage.setVisibility(View.VISIBLE);
        	if(!userImageUri.isEmpty()){
        		Picasso.with(context).load(userImageUri).into(viewHolder.userImage);	
        	}
        	
        	viewHolder.nextArrow.setVisibility(View.VISIBLE);
        	viewHolder.drawerTitle.setText(userFullName);
        	viewHolder.drawerTitle.setTextColor(context.getResources().getColor(R.color.list_background));
        	viewHolder.counter.setVisibility(View.GONE);
        	
        	convertView.setBackgroundResource(R.drawable.drawer_list_normal);
        	
        	viewHolder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					
					Intent intent=new Intent(context, CompleteProfileScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					
				}
			});
        	
			break;

        case 1:
        	viewHolder.userImage.setVisibility(View.GONE);
        	viewHolder.nextArrow.setVisibility(View.GONE);
        	viewHolder.counter.setVisibility(View.GONE);
        	viewHolder.drawerTitle.setTextColor(context.getResources().getColor(R.color.color_drawer_title));
        	break;
		case 2:
        	viewHolder.counter.setVisibility(View.GONE);
        	SharedPreferences registrationPrefs = context.getSharedPreferences(Constants.PREFS_NAME, 0);
    		int count = registrationPrefs.getInt("notificationCount" , 0);
    		if(count != 0){
    			viewHolder.counter.setVisibility(View.VISIBLE);
    			viewHolder.counter.setText(""+count);
    		}
        	
        	break;
        	
		}
        

        return convertView;
		
	}
	
	public Typeface setCustomFont(String fontName) {
		Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontName);
		
		return custom_font;
	}
	
	private static class ViewHolder{
		CircularImageView userImage;
		ImageView nextArrow;
		TextView drawerTitle;
		TextView counter;
		RelativeLayout relative;
	}

}
