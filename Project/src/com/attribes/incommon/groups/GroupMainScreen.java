package com.attribes.incommon.groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.androidquery.AQuery;
import com.attribes.incommon.BaseActivity;
import com.attribes.incommon.R;
import com.attribes.incommon.util.Constants;

public class GroupMainScreen extends BaseActivity{
	
	private AQuery mAquery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_main);
		//ViewGroup content=(ViewGroup)findViewById(R.id.frame_container);
		//getLayoutInflater().inflate(R.layout.activity_group_main,content,true);
		initContents();
	}

	private void initContents() {
		mAquery=new AQuery(this);
		mAquery.id(R.id.groupMainScreen_greetingText).
		typeface(setCustomFont(Constants.FONT_PROXI_REGULAR));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.group_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		int id=item.getItemId();
		
		if(id == R.id.action_createGroup){
			Intent intent=new Intent(this,GroupCreateScreen.class);
			startActivity(intent);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	} 
	
	

}
