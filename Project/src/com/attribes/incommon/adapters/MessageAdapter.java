package com.attribes.incommon.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.attribes.incommon.ChatHandler;
import com.attribes.incommon.ChatScreen;
import com.attribes.incommon.R;
import com.attribes.incommon.groups.GroupChatScreen;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.attribes.incommon.util.Flurry;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.squareup.picasso.Picasso;

public class MessageAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<QBDialog> mDialogs;
	private static final String DATE_FORMAT = "dd MMM yy";
	private static final String DATE_HIDE = "01 Jan 70";
	private static final String TIME_FORMAT="HH.mm ";
	private String opponentUserName;
	private AQuery mAquery ;
	public MessageAdapter(FragmentActivity activity, ArrayList<QBDialog> dialogs) {
		this.mContext = activity;
		this.mDialogs = dialogs;
		mAquery = new AQuery(mContext);
	}

	@Override
	public int getCount() {
		
		return mDialogs.size();
	}

	@Override
	public Object getItem(int position) {
		
		return mDialogs.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@SuppressLint("SimpleDateFormat") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(convertView == null){
			
			convertView = layout.inflate(R.layout.list_item_messages, null);
			viewHolder = createViewHolder(convertView);
			convertView.setTag(viewHolder);
		}
		
		else{
			viewHolder = (ViewHolder) convertView.getTag();
			
		}
		
			
		final Integer opponentID = ChatHandler.getInstance().getOpponentIDForPrivateDialog(mDialogs.get(position));
		final com.quickblox.users.model.QBUser user=ChatHandler.getInstance().getDialogsUsers().get(opponentID);
		
		getUserDetail(opponentID);
		
		
		viewHolder.lastMessageView.setText(mDialogs.get(position).getLastMessage());
		viewHolder.lastMessageDateView.setText(getTimeText(mDialogs.get(position).getLastMessageDateSent()));
		
		if((user != null) && (mDialogs.get(position).getType().equals(QBDialogType.PRIVATE))){
			opponentUserName = user.getFullName() == null ? user.getLogin(): user.getFullName();
			viewHolder.userNameView.setText(opponentUserName);		
			if(mDialogs.get(position).getUnreadMessageCount() != 0){
				viewHolder.userNameView.setTextColor(mContext.getResources().getColor(R.color.orange));
			}
			
			else{
				viewHolder.userNameView.setTextColor(mContext.getResources().getColor(R.color.black_font));
			}
			if(!(user.getCustomData() == null) && mDialogs.get(position).getType()==QBDialogType.PRIVATE){
				Picasso.with(mContext).load(user.getCustomData()).
                        placeholder(R.drawable.human_place_holder).into(viewHolder.userImage);
				
			}
		}
        if(mDialogs.get(position).getType()== QBDialogType.GROUP){
            Picasso.with(mContext).load(R.drawable.groups_placeholder).into(viewHolder.userImage);

            if(mDialogs.get(position).getUnreadMessageCount() != 0){
                viewHolder.userNameView.setTextColor(mContext.getResources().getColor(R.color.orange));
            }

            else{
                viewHolder.userNameView.setTextColor(mContext.getResources().getColor(R.color.black_font));
            }

            viewHolder.userNameView.setText(mDialogs.get(position).getName());
        }
		
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if((mDialogs.get(position).getType()==QBDialogType.PRIVATE) && (opponentID!=null && user!=null)){
					showChatScreen(opponentID,user.getFullName() == null ? user.getLogin(): user.getFullName(),
							ChatHandler.getInstance().getOpponentIDForPrivateDialog(mDialogs.get(position)));	
				}

                if(mDialogs.get(position).getType()== QBDialogType.GROUP){
                    showGroupChatScreen(mDialogs.get(position));
                }
				
				Flurry.getInstance().eventMessageRead();
			}

			
		});
		return convertView;
	}

    private void showGroupChatScreen(QBDialog qbDialog) {

        Intent intent = new Intent(mContext,GroupChatScreen.class);

        intent.putExtra(Constants.EXTRA_QBDIALOG, qbDialog);
        mContext.startActivity(intent);
        //overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_in);
    }

	private ViewHolder createViewHolder(View convertView) {
		ViewHolder viewHolder = new ViewHolder();
		
		viewHolder.userImage = (CircularImageView)convertView.findViewById(R.id.list_item_messages_UserImage);
		viewHolder.userNameView = (TextView) convertView.findViewById(R.id.list_item_messages_userName);
		viewHolder.lastMessageView = (TextView) convertView.findViewById(R.id.list_item_message_userMessage);
		viewHolder.lastMessageDateView = (TextView) convertView.findViewById(R.id.list_item_message_messageDate);
		
		viewHolder.userNameView.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		viewHolder.lastMessageView.setTypeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
		viewHolder.lastMessageDateView.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
		return viewHolder;
		
	}

	@SuppressLint("SimpleDateFormat")
	private String getTimeText(long lastMessageDate) {
		Date date;
        String str = null;
        
        long getRidOfTime = 1000 * 60 * 60 * 24;
        
        date = new Date(lastMessageDate*1000);
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
    	  str = DateFormat.format(DATE_FORMAT, date).toString();
          if(str.equals(DATE_HIDE)){
          	
          	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
  			String currentDateandTime = sdf.format(new Date());
  			str = currentDateandTime;
          }
          
          }
          
        return str;
    }
	
	private void showChatScreen(Integer opponentID,String opponentUserName, Integer integer) {
		Intent intent = new Intent(mContext, ChatScreen.class);
		intent.putExtra("opponentName", opponentUserName);
//		intent.putExtra("opponentUserId",Integer.toString(opponentID));
		intent.putExtra("opponentQbId", Integer.toString(opponentID));
		
		mContext.startActivity(intent);
		
	}
	private static class ViewHolder{
		CircularImageView userImage;
		TextView userNameView;
		TextView lastMessageView;
		TextView lastMessageDateView;
		
	}
	
	private void getUserDetail(Integer qbId){
		String url = Constants.BaseUrl + Constants.USER_GET;
		
		AjaxCallback<String> callBack = new AjaxCallback<String>();        
		callBack.url(url).type(String.class).weakHandler(mContext, "ResultUserDetail");
		
		callBack.param("authorization", "27AbcxXePB5fJPNUoluyx9byF61u");
		callBack.param("sm_token", MasterUser.getInstance().getUserSmToken());
		callBack.param("qb_id", qbId.toString());
		
		mAquery.ajax(callBack);
	}
	
	public void ResultUserDetail(String Url, String json, AjaxStatus status){
		if(json!=null){		
		}

	}
	
	private Typeface setCustomFont(String fontName){
		Typeface customFont=Typeface.createFromAsset(mContext.getAssets(), "fonts/"+fontName);
	
	return customFont;
	}
}
