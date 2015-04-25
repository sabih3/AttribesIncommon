package com.attribes.incommon;

//import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
//import android.util.Log;
import android.view.Menu;
//import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
//import android.widget.SearchView;
//import android.widget.TextView;



import com.attribes.incommon.adapters.ActivityTabAdapter;
import com.attribes.incommon.util.Constants;
//import com.viewpagerindicator.TabPageIndicator;

public class ActivityScreen extends SearchFriends{

	//was extended from SearchFriends
	private static ViewPager pager;
	//private FragmentPagerAdapter activityTabAdapter;
	//private android.app.ActionBar actionBar;
	
	//private static final String[] CONTENT = new String[] { "Matches", "Messages", "Notifications"};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_screen);
		
		RelativeLayout searchFriendsLayout=(RelativeLayout) findViewById(R.id.searchFriends_relativeLayout);
		searchFriendsLayout.setVisibility(RelativeLayout.GONE);
		 
		ViewGroup content = (ViewGroup) findViewById(R.id.frame_container);
		getLayoutInflater().inflate(R.layout.activity_screen, content, true);
	        
        //activityTabAdapter = new ActivityTabAdapter(getSupportFragmentManager());
        
        pager = (ViewPager)findViewById(R.id.activityScreen_pager);
        pager.setAdapter(new ActivityTabAdapter(getSupportFragmentManager()));
       

        
           TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator_tab);
           indicator.setTypeFace(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/"+Constants.FONT_PROXI_REGULAR));
           indicator.setViewPager(pager, 0);
          

//        PagerTabStrip titleStrip = (PagerTabStrip)findViewById(R.id.pager_title_strip);
//        titleStrip.setTextSpacing(10);
       
        
        if(getIntent().getBooleanExtra("notification", false)){
        	
        	pager.setCurrentItem(2,true);
        }
        
        else{
            
        	pager.setCurrentItem(0, true);   

        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  
	   getMenuInflater().inflate(R.menu.no_search_bar, menu);

	//			searchView = (SearchView) menu.findItem(R.id.menu_search)
	//					.getActionView();
	//
	//			searchView.setVisibility(View.GONE);
	
	
		
			return true;
		
	} 
   
//	@Override
//	protected void onStop() {
////		 super.onStop();
////		ChatHandler.getInstance().signOut(this);
//	}
//	
//	@Override
//	protected void onResume(){
//		super.onResume();
//	}
	
}
