package com.attribes.incommon.groups;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.attribes.incommon.R;
import com.attribes.incommon.models.MasterUser;
import com.attribes.incommon.util.Constants;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.chat.model.QBChatHistoryMessage;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBMessage;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sabih Ahmed on 19-Jun-15.
 */
public class GroupChatAdapter extends BaseAdapter{

    private static final String TIME_FORMAT="HH.mm";
    private static final String DATE_FORMAT = "dd MMM yy h.m a";
    private static final String DATE_HIDE = "01 Jan 70";
    private QBMessage chatMessage;

    private Context mContext;
    private ArrayList<QBMessage> messageList;

    public GroupChatAdapter(Context context, ArrayList<QBMessage> qbMessagesList) {
        this.mContext = context;
        this.messageList = qbMessagesList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public QBMessage getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.group_chat_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean isOutgoing = chatMessage.getSenderId() == null ||
                Integer.toString(chatMessage.getSenderId()).equals(MasterUser.getInstance().getUserQbId());

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

    private String getTimeText(QBMessage message) {

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

    private void setAlignment(ViewHolder holder, boolean isOutgoing) {
        if (!(isOutgoing)) {
            holder.content.setGravity(Gravity.START);
            holder.txtMessage.setBackgroundResource(R.drawable.incoming_with_gap_top_grey);
            holder.txtMessage.setTextColor(mContext.getResources().getColor(R.color.color_drawer_title));
            holder.txtMessage.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
            holder.txtMessage.setTextSize(14);
            holder.userImage.setVisibility(ImageView.VISIBLE);
            holder.txtInfo.setTextSize(14);
            holder.senderName.setVisibility(View.VISIBLE);
            holder.senderName.setText(chatMessage.getProperty("name"));
            loadOpponentImage(holder.userImage);

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);

            params.leftMargin = 80;
            holder.txtInfo.setLayoutParams(params);



        } else {
            holder.content.setGravity(Gravity.END);
            holder.userImage.setVisibility(ImageView.GONE);
            holder.txtMessage.setBackgroundResource(R.drawable.out_with_gap_top);
            holder.txtMessage.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.txtMessage.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
            holder.txtMessage.setTextSize(14);
            holder.txtInfo.setTextSize(14);
            holder.senderName.setVisibility(View.GONE);

//            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
//
//
//            params.leftMargin=40;
//
//            holder.txtInfo.setLayoutParams(params);

        }
    }

    private void loadOpponentImage(CircularImageView userImage) {
        String decodedImageUrl = URLDecoder.decode(chatMessage.getProperty("image_url"));
        Picasso.with(mContext).load(decodedImageUrl).into(userImage);
    }

    private Typeface setCustomFont(String fontName) {
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/"+fontName);

        return custom_font;
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
//        holder.content = (RelativeLayout) v.findViewById(R.id.groupChatMessage_content);
//        holder.messageContent =(RelativeLayout) v.findViewById(R.id.groupChatMessage_messageContent);
//        holder.userImage =(CircularImageView) v.findViewById(R.id.groupChatMessage_userImage);
//        holder.txtMessage =(TextView) v.findViewById(R.id.groupChatMessage_message);
//        holder.txtInfo =(TextView) v.findViewById(R.id.groupChatMessage_messageInfo);
//        holder.senderName=(TextView)v.findViewById(R.id.groupChatMessage_senderName);

        holder.content = (LinearLayout) v.findViewById(R.id.groupChatMessage_content);
        holder.messageContent =(LinearLayout) v.findViewById(R.id.groupChatMessage_messageContent);
        holder.userImage =(CircularImageView) v.findViewById(R.id.groupChatMessage_userImage);
        holder.txtMessage =(TextView) v.findViewById(R.id.groupChatMessage_message);
        holder.txtInfo =(TextView) v.findViewById(R.id.groupChatMessage_messageInfo);
        holder.senderName=(TextView)v.findViewById(R.id.groupChatMessage_senderName);
        return holder;

    }

    public void add(QBMessage chatMessage) {

        messageList.add(chatMessage);

    }


    private static class ViewHolder {
//        public RelativeLayout content;
//        public RelativeLayout messageContent;
//        public CircularImageView userImage;
//        public TextView txtMessage;
//        public TextView txtInfo;
//        public TextView senderName;

        public LinearLayout content;
        public LinearLayout messageContent;
        public CircularImageView userImage;
        public TextView txtMessage;
        public TextView txtInfo;
        public TextView senderName;

    }
}
