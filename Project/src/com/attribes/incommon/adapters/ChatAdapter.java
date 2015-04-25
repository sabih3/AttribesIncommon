package com.attribes.incommon.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.attribes.incommon.R;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.chat.model.QBChatHistoryMessage;
import com.quickblox.chat.model.QBMessage;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

public class ChatAdapter extends BaseAdapter {

	private static final String TIME_FORMAT="HH.mm";
    private static final String DATE_FORMAT = "dd MMM yy h.m a";
    private static final String DATE_HIDE = "01 Jan 70";
    private final List<QBMessage> chatMessages;
    private Activity context;
    private int opponentID;
    private static QBUser opponentUser;
    private CircularImageView userImage;
    private String response;
    
    public ChatAdapter(Activity context, List<QBMessage> chatMessages, Integer opponentID) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.opponentID = opponentID;
        getOpponentInfo(opponentID);
        
        
    }

    private void getOpponentInfo(final Integer opponentID) {
    	
    	QBUsers.getUser(opponentID, new QBEntityCallbackImpl<QBUser>(){
			
			@Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
				opponentUser = qbUser;
            }

            @Override
            public void onError(List<String> strings) {

            }
			
		});
		
	}

	@Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public QBMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        QBMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
        	convertView = vi.inflate(R.layout.chat_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        
        		
        boolean isOutgoing = chatMessage.getSenderId() == null || Integer.toString(chatMessage.getSenderId()).equals(MasterUser.getInstance().getUserQbId());
        setAlignment(holder, isOutgoing);
        holder.txtMessage.setText(chatMessage.getBody());
        if (chatMessage.getSenderId() != null) {
        	holder.txtInfo.setTypeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
            holder.txtInfo.setText(getTimeText(chatMessage));
        } else {
            holder.txtInfo.setText(getTimeText(chatMessage));
        }

        return convertView;
    }

    public void add(QBMessage message) {
        chatMessages.add(message);
    }

    public void add(List<QBMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isOutgoing) {
        if (!(isOutgoing)) {
        	 holder.content.setGravity(Gravity.START);
        	 holder.txtMessage.setBackgroundResource(R.drawable.incoming_with_gap_top_grey);
        	 holder.txtMessage.setTextColor(context.getResources().getColor(R.color.color_drawer_title));
             holder.txtMessage.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
        	 holder.txtMessage.setTextSize(14);
             holder.userImage.setVisibility(ImageView.VISIBLE);
             holder.txtInfo.setTextSize(14);
             loadOpponentImage(holder.userImage);
        	 
        	 RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
             		android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        	 
        	
        	params.addRule(RelativeLayout.BELOW,R.id.chatMessage_message);
        	params.leftMargin = 40;
            holder.txtInfo.setLayoutParams(params);
             
 
        } else { 
            holder.content.setGravity(Gravity.END);
        	holder.userImage.setVisibility(ImageView.GONE);
        	holder.txtMessage.setBackgroundResource(R.drawable.out_with_gap_top);
        	holder.txtMessage.setTextColor(context.getResources().getColor(R.color.white));
            holder.txtMessage.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
            holder.txtMessage.setTextSize(14);
            holder.txtInfo.setTextSize(14);
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
             		android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            
            params.addRule(RelativeLayout.BELOW,R.id.chatMessage_message);
            params.leftMargin=40;
            params.addRule(RelativeLayout.ALIGN_RIGHT);
            holder.txtInfo.setLayoutParams(params);
            
        }
    }

    private void loadOpponentImage(final CircularImageView userImage) {
    	context.runOnUiThread(new Runnable() {
            @Override
            public void run() { 
            	if(!(opponentUser == null) && (!opponentUser.getCustomData().isEmpty())){
            		Picasso.with(context).load(opponentUser.getCustomData()).into(userImage);
            	}
        	}

        });
    	
		
	}

	private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.content=(RelativeLayout) v.findViewById(R.id.chatMessage_content);
        holder.messageContent =(RelativeLayout) v.findViewById(R.id.chatMessage_messageContent);
        holder.userImage=(CircularImageView) v.findViewById(R.id.chatMessage_userImage);
        holder.txtMessage=(TextView) v.findViewById(R.id.chatMessage_message);
        holder.txtInfo=(TextView) v.findViewById(R.id.chatMessage_messageInfo);
        return holder;
    }

    private String getTimeText(QBMessage message) {
       /* Date date;
       
        CharSequence relativeDate = null;
        if (message instanceof QBChatHistoryMessage){
            date = new Date(((QBChatHistoryMessage) message).getDateSent() * 1000);
            
            relativeDate = DateUtils.getRelativeDateTimeString(context, date.getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
            
        }else{
        	Date dateNow = new Date();
        	relativeDate = DateUtils.getRelativeDateTimeString(context, dateNow.getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
        }
        return relativeDate;
        */
    	
    	Date date;
        String str = null;
    	 long getRidOfTime = 1000 * 60 * 60 * 24;
    	 if (message instanceof QBChatHistoryMessage){
	         date = new Date(((QBChatHistoryMessage) message).getDateSent()*1000);
	         Date todayDate=new Date();
	         
	         Calendar calendarYesterday=Calendar.getInstance();
	         calendarYesterday.roll(Calendar.DATE, -1);
	          
	         if((date.getTime()/getRidOfTime) == (todayDate.getTime()/getRidOfTime)){
	         	SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
	         	str= "Today "+timeFormat.format(date);
	         	
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
    	 }
    	 
    	 else{
    		 
    		 SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
	   			String currentDateandTime = sdf.format(new Date());
	   			str ="Today "+ currentDateandTime;
    	 }
           
         return str;
    }
    
    private Typeface setCustomFont(String fontName) {
		Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontName);
		
		return custom_font;
	}
    
    private static class ViewHolder {	
    	public RelativeLayout content;
    	public RelativeLayout messageContent;
    	public CircularImageView userImage;
    	public TextView txtMessage;
    	public TextView txtInfo;
    }
    
   
}
