package com.attribes.incommon.groups;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.attribes.incommon.R;
import com.attribes.incommon.util.Constants;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.quickblox.chat.model.QBDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sabih Ahmed on 22-Jun-15.
 */
public class GroupDialogAdapter extends BaseAdapter{
    private static final String DATE_FORMAT = "dd MMM yy";
    private static final String DATE_HIDE = "01 Jan 70";
    private static final String TIME_FORMAT="HH.mm ";

   private Context mContext;
   private ArrayList<QBDialog> dialogsList;

   public GroupDialogAdapter (Context context, ArrayList<QBDialog> dialogsList){
       this.mContext = context;
       this.dialogsList = dialogsList;
   }

    @Override
    public int getCount() {
        return dialogsList.size();
    }

    @Override
    public Object getItem(int position) {
        return dialogsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

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

        viewHolder.userNameView.setText(dialogsList.get(position).getName());
        //viewHolder.lastMessageView.setText(dialogsList.get(position).getLastMessage());
        viewHolder.lastMessageDateView.setText(getTimeText(dialogsList.get(position).getLastMessageDateSent()));

        return convertView;
    }

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

    private ViewHolder createViewHolder(View convertView) {
        ViewHolder viewHolder = new ViewHolder();

        //viewHolder.userImage = (CircularImageView)convertView.findViewById(R.id.list_item_messages_UserImage);
        viewHolder.userNameView = (TextView) convertView.findViewById(R.id.list_item_messages_userName);
        viewHolder.lastMessageView = (TextView) convertView.findViewById(R.id.list_item_message_userMessage);
        viewHolder.lastMessageDateView = (TextView) convertView.findViewById(R.id.list_item_message_messageDate);

        viewHolder.userNameView.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
        viewHolder.lastMessageView.setTypeface(setCustomFont(Constants.FONT_PROXI_LIGHT));
        viewHolder.lastMessageDateView.setTypeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
        return viewHolder;

    }

    private class ViewHolder {
       // CircularImageView userImage;
        TextView userNameView;
        TextView lastMessageView;
        TextView lastMessageDateView;
    }

    private Typeface setCustomFont(String fontName){
        Typeface customFont=Typeface.createFromAsset(mContext.getAssets(), "fonts/"+fontName);

        return customFont;
    }
}
