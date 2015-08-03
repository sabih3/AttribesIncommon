package com.attribes.incommon.chat.core;

import com.quickblox.chat.model.QBChatMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sabih Ahmed on 14-Jul-15.
 */
public class ChatUtils {


    private static final String TIME_FORMAT="HH.mm";
    private static final String DATE_FORMAT = "dd MMM yy h.m a";

    public ChatUtils(){

    }
    public static String getMessageTime(QBChatMessage message) {
        Date date;
        String str = null;
        long getRidOfTime = 1000 * 60 * 60 * 24;
        if (message instanceof QBChatMessage) {

            if (((QBChatMessage) message).getDateSent() == 0) {

                str = getCurrentTime(message);

            } else {
                date = new Date(((QBChatMessage) message).getDateSent() * 1000);
                Date todayDate = new Date();

                Calendar calendarYesterday = Calendar.getInstance();
                calendarYesterday.roll(Calendar.DATE, -1);

                if ((date.getTime() / getRidOfTime) == (todayDate.getTime() / getRidOfTime)) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
                    str = "Today " + timeFormat.format(date);

                } else if (date.getTime() / getRidOfTime == calendarYesterday.getTimeInMillis() / getRidOfTime) {
                    str = "yesterday";
                }

                else{

                 SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                 String currentDateandTime = sdf.format(new Date());
                 str = currentDateandTime;


                 }

            }


        }
        return str;
    }

    private static String getCurrentTime(QBChatMessage message) {
        Date todayDate = new Date();
        String currentTimeString;
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        currentTimeString = "Today "+timeFormat.format(todayDate);


        return currentTimeString;
    }


}