package com.attribes.incommon.adapters;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.attribes.incommon.util.Constants;

public class InterestViewAdapter extends BaseAdapter{

	Context context;
	ArrayList<String> userInterests=new ArrayList<String>();
	
	public InterestViewAdapter (Context context, ArrayList<String> userInterestList){
		this.context = context;
		this.userInterests = userInterestList;
	}
	
	@Override
	public int getCount() {
		
		return userInterests.size();
	}

	@Override
	public Object getItem(int position) {
		
		return userInterests.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/"+Constants.FONT_PROXI_LIGHT);	
		TextView textView;
		textView =new TextView(context);
		textView.setMaxLines(1);
		textView.setText(userInterests.get(position));
		textView.setTextSize(15);
		//textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setTypeface(custom_font);  
		Paint p = new Paint();
		p.setTypeface(custom_font); // if custom font use `TypeFace.createFromFile`
		p.setTextSize(15);
		int textWidth = (int) p.measureText(userInterests.get(position)); 
		
    	
		textWidth+=textWidth+100;//10//145
		textView.setLayoutParams(new AbsListView.LayoutParams(textWidth, LayoutParams.WRAP_CONTENT));
		return textView;
	}

}
