package com.attribes.incommon.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.attribes.incommon.R;
import com.attribes.incommon.models.MatchAllResponse.Response;
import com.attribes.incommon.util.Constants;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class MatchAllAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private Context context;
	private ArrayList<Response> matchesAllResponseList ;
	
	private static final String TIME_FORMAT="HH.mm";
	private static final String FORMAT_DATE_SERVER = "yy-MM-dd k:m:s";
	private static final String FORMAT_DATE_APP="dd MMM yy";
	private static final String DATE_HIDE = "01 Jan 70";
	private AQuery mAquery;
	
	public MatchAllAdapter(Context context,Fragment matchFragment, ArrayList<Response> matchesAllResponseList) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		this.matchesAllResponseList = matchesAllResponseList;
		
	}
	
	@Override
	public int getCount() {
		
		return matchesAllResponseList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return matchesAllResponseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			mAquery = new AQuery(context);
			convertView = inflater.inflate(R.layout.list_item_match, null);
			
			viewHolder.userImage = (CircularImageView) convertView.findViewById(R.id.matchListItem_friendImage);
			viewHolder.userNameText = (TextView) convertView.findViewById(R.id.matchListItem_friendName);
			viewHolder.userStatusOnline = (ImageView) convertView.findViewById(R.id.matchListItem_isOnline);
			viewHolder.userStatusOffline = (ImageView) convertView.findViewById(R.id.matchListItem_isOffline);
			viewHolder.distance = (TextView) convertView.findViewById(R.id.matchListItem_distance);
			viewHolder.userMatchTime = (TextView) convertView.findViewById(R.id.list_item_match_time);
			
			viewHolder.userNameText.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
			viewHolder.distance.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
			viewHolder.userMatchTime.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
			
			
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		float dist = Float.valueOf(matchesAllResponseList.get(position).distance);
		if( dist >= 1){
			String str = String.format("%.2f", dist);
			viewHolder.distance.setText(str+" km away");
		}
		else
			viewHolder.distance.setText("Less than a km away");
		
		viewHolder.userNameText.setText(matchesAllResponseList.get(position).name+", "+matchesAllResponseList.get(position).age);
		if(!(matchesAllResponseList.get(position).image_uri.isEmpty())){
			
			Picasso.with(context).load(matchesAllResponseList.get(position).image_uri).into(viewHolder.userImage);
			
		}
		
		if(matchesAllResponseList.get(position).is_login.equals("0")){
			viewHolder.userStatusOnline.setVisibility(ImageView.GONE);
			viewHolder.userStatusOffline.setVisibility(ImageView.VISIBLE);
		}
		
		if(matchesAllResponseList.get(position).is_login.equals("1")){
			viewHolder.userStatusOnline.setVisibility(ImageView.VISIBLE);
			viewHolder.userStatusOffline.setVisibility(ImageView.GONE);
		}
		
		
		 String time_logged = matchesAllResponseList.get(position).date_added;
		 viewHolder.userMatchTime.setText(getTimeText(time_logged));
		
		return convertView;
	}

	@SuppressLint("SimpleDateFormat")
	private String getTimeText(String time_logged) {
	
		 Date date = null ;
	        String str = null;
	        
	        long getRidOfTime = 1000 * 60 * 60 * 24;
	 
	        SimpleDateFormat formatServerDate  = new SimpleDateFormat(FORMAT_DATE_SERVER);
	     
	        try {
	        	date = new Date();
			    date = (Date) formatServerDate.parse(time_logged);

			    } 
	        
	        catch (ParseException e) {
				
				e.printStackTrace();
			}
	       
	        date = new Date(date.getTime());
	        Date todayDate=new Date();
	        
	        Calendar calendarYesterday=Calendar.getInstance();
	        calendarYesterday.roll(Calendar.DATE, -1);
	         
	        if((date.getTime()/getRidOfTime) == (todayDate.getTime()/getRidOfTime)){
	        	SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
	        	str=timeFormat.format(date);
	        	
	        }
	        else if(date.getTime() / getRidOfTime == calendarYesterday.getTimeInMillis()/getRidOfTime){
	        	  str="yesterday";
	         }
	          
	        else{
	    	  str = DateFormat.format(FORMAT_DATE_APP, date).toString();
	          if(str.equals(DATE_HIDE)){
	          	
	          	SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_SERVER);
	  			String currentDateandTime = sdf.format(new Date());
	  			str = currentDateandTime;
	          }
	          
	          }

	        return str;
	}

	private Typeface setCustomFont(String fontName){
		Typeface customFont=Typeface.createFromAsset(context.getAssets(), "fonts/"+fontName);
	
	return customFont;
	}
	
	static class ViewHolder{
		CircularImageView userImage;
		TextView userNameText,distance;
		ImageView userStatusOnline;
		ImageView userStatusOffline;
		TextView userMatchTime;
		
	}
	
}
