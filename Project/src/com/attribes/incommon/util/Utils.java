package com.attribes.incommon.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utils {
	
	public static int dpToPx(Context ctx, int dp) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public static int pxToDp(Context ctx, int px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return (int) ((px / displayMetrics.density) + 0.5);
    }
    
    /**
     * @param smallString The string to capitalize.
     * @return The provided String with uppercased words.
     */
    public static String getFirstLetterCapital(String smallString) {

    	try{
    		if (smallString.length() > 0)
                return smallString.substring(0, 1).toUpperCase() + smallString.substring(1).toLowerCase();
            else
                return smallString;
    	}catch(NullPointerException npe){
    		npe.printStackTrace();
    		return "";
    	}
    }


}
