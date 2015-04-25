package com.attribes.incommon.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.BaseActivity;
import com.attribes.incommon.MatchProfile;
import com.attribes.incommon.NotificationFragment;
import com.attribes.incommon.R;
import com.attribes.incommon.models.NotificationResponse.Response;
import com.attribes.incommon.util.Constants;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class FriendRequestAdapter extends BaseAdapter{
	
	static class ViewHolder{
		CircularImageView circularImageView;
		TextView nameAndAgeText;
		TextView notificationStatusText;
		ImageView acceptImage;
		ImageView rejectImage;
		TextView  notificationCount;
		TextView time;
	}
	
	private Context context;
	AQuery aquery;
	LayoutInflater inflater ;
	private ArrayList<Response> notificationResponse = new ArrayList<Response>();
	private static final String TIME_FORMAT="HH.mm";
	private static final String FORMAT_DATE_SERVER = "yy-MM-dd k:m:s";
	private static final String FORMAT_DATE_APP="dd MMM yy";
	private static final String DATE_HIDE = "01 Jan 70";
	
	
	public FriendRequestAdapter(Context context, NotificationFragment notificationFragment, ArrayList<Response> notificationResponse){
		this.context = context;
		
		this.setListener(notificationFragment);
		this.notificationResponse = notificationResponse;
		inflater= (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		
		return notificationResponse.size();
	}

	@Override
	public Object getItem(int position) {
		
		return notificationResponse.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		
		if(convertView == null){
			viewHolder = new ViewHolder();
			aquery = new AQuery(context);
			convertView = inflater.inflate(R.layout.friend_request_list_item, null);

			viewHolder.nameAndAgeText = (TextView) convertView.findViewById(R.id.friendRequest_name);
			viewHolder.circularImageView = (CircularImageView) convertView.findViewById(R.id.friendRequest_image);
			
			viewHolder.acceptImage = (ImageView) convertView.findViewById(R.id.friendRequest_accept);
			viewHolder.rejectImage = (ImageView) convertView.findViewById(R.id.friendRequest_reject);
			viewHolder.notificationStatusText = (TextView) convertView.findViewById(R.id.friendRequest_notificationType);
			viewHolder.time = (TextView)convertView.findViewById(R.id.friendRequest_time);
			
			viewHolder.nameAndAgeText.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
			viewHolder.notificationStatusText.setTypeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
			viewHolder.time.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
			
			convertView.setTag(viewHolder);
		}
		
		else{
			
			viewHolder = (ViewHolder) convertView.getTag();
			
		}
		
		final View tempView = convertView;
		
	    switch (notificationResponse.get(position).notification_type) {
		
		case "FRIEND_ADD":
			
			if(notificationResponse.get(position).action == null && notificationResponse.get(position).is_read.equals("0")){
				
				viewHolder.nameAndAgeText.setTextColor(context.getResources().getColor(R.color.orange));
				viewHolder.nameAndAgeText.setText(notificationResponse.get(position).source_user.name+", "+
						 notificationResponse.get(position).source_user.age);
				Picasso.with(context).load(notificationResponse.get(position).source_user.image_uri).into(viewHolder.circularImageView);
				viewHolder.notificationStatusText.setText("Friend request");
				
				
				viewHolder.acceptImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						viewHolder.acceptImage.setVisibility(ImageView.GONE);
						viewHolder.rejectImage.setVisibility(ImageView.GONE);
						viewHolder.notificationStatusText.setText("You and "+notificationResponse.get(position).source_user.name+" are now friends");
						RequestFriendAccept(notificationResponse.get(position).source_user.id);
						tempView.invalidate();
					}

				});
				
				viewHolder.rejectImage.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						
						viewHolder.acceptImage.setVisibility(ImageView.GONE);
						viewHolder.rejectImage.setVisibility(ImageView.GONE);
						RequestFriendReject(notificationResponse.get(position).source_user.id);
						viewHolder.notificationStatusText.setText("Friend request rejected");
						tempView.invalidate();
					}

					
				});
				
			}
			
			if((notificationResponse.get(position).action == null && notificationResponse.get(position).is_read.equals("1"))){
				viewHolder.nameAndAgeText.setTextColor(context.getResources().getColor(R.color.black_font));
				viewHolder.nameAndAgeText.setText(notificationResponse.get(position).source_user.name+", "+
						 notificationResponse.get(position).source_user.age);
				Picasso.with(context).load(notificationResponse.get(position).source_user.image_uri).into(viewHolder.circularImageView);
				viewHolder.notificationStatusText.setText("Friend request");
				
				
				viewHolder.acceptImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						viewHolder.acceptImage.setVisibility(ImageView.GONE);
						viewHolder.rejectImage.setVisibility(ImageView.GONE);
						viewHolder.notificationStatusText.setText("You and "+notificationResponse.get(position).source_user.name+" are now friends");
						RequestFriendAccept(notificationResponse.get(position).source_user.id);
						tempView.invalidate();
					}

				});
				
				viewHolder.rejectImage.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						
						viewHolder.acceptImage.setVisibility(ImageView.GONE);
						viewHolder.rejectImage.setVisibility(ImageView.GONE);
						RequestFriendReject(notificationResponse.get(position).source_user.id);
						viewHolder.notificationStatusText.setText("Friend request rejected");
						tempView.invalidate();
					}

					
				});
			}
			
			if(((notificationResponse.get(position).action != null) && (notificationResponse.get(position).action.equals("1"))) 
					&& notificationResponse.get(position).is_read.equals("1")){
				Picasso.with(context).load(notificationResponse.get(position).source_user.image_uri).into(viewHolder.circularImageView);
				viewHolder.nameAndAgeText.setTextColor(context.getResources().getColor(R.color.black_font));
				viewHolder.acceptImage.setVisibility(ImageView.GONE);
				viewHolder.rejectImage.setVisibility(ImageView.GONE);
				viewHolder.nameAndAgeText.setText(notificationResponse.get(position).source_user.name+", "+
						notificationResponse.get(position).source_user.age);
				viewHolder.notificationStatusText.setText("You and "+notificationResponse.get(position).source_user.name+" are now friends");
				
			}
			
			if(((notificationResponse.get(position).action != null) && (notificationResponse.get(position).action.equals("0")))
					&& notificationResponse.get(position).is_read.equals("1")){
				Picasso.with(context).load(notificationResponse.get(position).source_user.image_uri).into(viewHolder.circularImageView);
				viewHolder.nameAndAgeText.setTextColor(context.getResources().getColor(R.color.black_font));
				viewHolder.acceptImage.setVisibility(ImageView.GONE);
				viewHolder.rejectImage.setVisibility(ImageView.GONE);
				viewHolder.nameAndAgeText.setText(notificationResponse.get(position).source_user.name);
				viewHolder.notificationStatusText.setText("Friend request rejected");
			}
			
			viewHolder.nameAndAgeText.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showOpponentProfileScreen(notificationResponse.get(position).source_user.id);				
				}

				
			});
			viewHolder.circularImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					showOpponentProfileScreen(notificationResponse.get(position).source_user.id);
				}
			});
			
			
			break;

		case "FRIEND_ACCEPT":
			
			if(viewHolder.acceptImage.getVisibility() == ImageView.VISIBLE && 
				viewHolder.rejectImage.getVisibility()==ImageView.VISIBLE){
				viewHolder.acceptImage.setVisibility(ImageView.GONE);
				viewHolder.rejectImage.setVisibility(ImageView.GONE);
			}
			
			if(notificationResponse.get(position).is_read.equals("0")){
				viewHolder.nameAndAgeText.setTextColor(context.getResources().getColor(R.color.orange));
			}
			else{
				viewHolder.nameAndAgeText.setTextColor(context.getResources().getColor(R.color.black_font));
			}
			viewHolder.nameAndAgeText.setText(notificationResponse.get(position).source_user.name+", "+
					notificationResponse.get(position).source_user.age);
			viewHolder.notificationStatusText.setText(notificationResponse.get(position).source_user.name +" has accepted your friend request ");
			Picasso.with(context).load(notificationResponse.get(position).source_user.image_uri).into(viewHolder.circularImageView);
			
			viewHolder.nameAndAgeText.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showOpponentProfileScreen(notificationResponse.get(position).source_user.id);				
				}

				
			});
			viewHolder.circularImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					showOpponentProfileScreen(notificationResponse.get(position).source_user.id);
				}
			});
			
			
			break;
		
		case "USER_LIKE":
			if(viewHolder.acceptImage.getVisibility() == ImageView.VISIBLE && 
			viewHolder.rejectImage.getVisibility()==ImageView.VISIBLE){
			viewHolder.acceptImage.setVisibility(ImageView.GONE);
			viewHolder.rejectImage.setVisibility(ImageView.GONE);
		}
		
			if(notificationResponse.get(position).is_read.equals("0")){
				viewHolder.nameAndAgeText.setTextColor(context.getResources().getColor(R.color.orange));
			}
			else{
				viewHolder.nameAndAgeText.setTextColor(context.getResources().getColor(R.color.black_font));
			}
		viewHolder.nameAndAgeText.setText(notificationResponse.get(position).source_user.name+", "+
				notificationResponse.get(position).source_user.age);
		viewHolder.notificationStatusText.setText(notificationResponse.get(position).source_user.name +" has liked you");
		Picasso.with(context).load(notificationResponse.get(position).source_user.image_uri).into(viewHolder.circularImageView);
		
		viewHolder.nameAndAgeText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showOpponentProfileScreen(notificationResponse.get(position).source_user.id);				
			}

			
		});
		viewHolder.circularImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				showOpponentProfileScreen(notificationResponse.get(position).source_user.id);
			}
		});
		
		viewHolder.notificationStatusText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showOpponentProfileScreen(notificationResponse.get(position).source_user.id);
			}
		});
	    	break;
		}
	    
	    String time_logged= notificationResponse.get(position).time_logged;
	    viewHolder.time.setText(getTimeText(time_logged));
	    
	
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
        Date todayDate = new Date();
        
        Calendar calendarYesterday = Calendar.getInstance();
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
	
	
	private void showOpponentProfileScreen(String opponentId) {
		Intent profileScreenIntent = new Intent(context, MatchProfile.class);
		profileScreenIntent.putExtra("user_id", opponentId);
		profileScreenIntent.putExtra("title", "Search Profile");
		context.startActivity(profileScreenIntent);
		
	}
	
	public void RequestFriendAccept(String friendRequestUserId) {
		BaseActivity baseActivity=new BaseActivity();
		baseActivity.context = context;
		AQuery aq = new AQuery(context);
		String url = Constants.BaseUrl + Constants.friendAccept;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(context, "RequestFriendAcceptResponse");
		
		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", baseActivity.getSmToken());
		cb.param("user_id", friendRequestUserId);
		cb.param("action", "1");
		aq.ajax(cb);
		
	}
	
	public void RequestFriendAcceptResponse(String url, String json, AjaxStatus status){
		if(!(json.isEmpty())){
			//notifyDataSetChanged();
		}
		
	}
	
	public void RequestFriendReject(String id) {
		BaseActivity baseActivity=new BaseActivity();
		baseActivity.context = context;
		AQuery aq = new AQuery(context);
		String url = Constants.BaseUrl + Constants.FRIEND_REJECT;
		AjaxCallback<String> cb = new AjaxCallback<String>();        
		cb.url(url).type(String.class).weakHandler(context, "RequestFriendRejectResponse");
		
		cb.param("authorization", Constants.AUTHORIZATION);
		cb.param("sm_token", baseActivity.getSmToken());
		cb.param("user_id", id);
		aq.ajax(cb);
	}
	
	public void RequestFriendRejectResponse(String url, String json, AjaxStatus status){
		if(!(json.isEmpty())){
			//notifyDataSetChanged();
		}
	}
	
	private Typeface setCustomFont(String fontName) {
		Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontName);
		return custom_font;
	}
	
	CustomInterface listener;
	
	public void setListener(CustomInterface listener){
		this.listener = listener;
	}
	
	public interface CustomInterface{
		public void callBack(View view,int position);
	}
	
	
}


