package com.attribes.incommon.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.attribes.incommon.MatchFragment;
import com.attribes.incommon.MessageFragment;
import com.attribes.incommon.NotificationFragment;

public class ActivityTabAdapter extends FragmentStatePagerAdapter{
	private static final String[] CONTENT = new String[] { "Matches", "Messages", "Notifications"};
	public ActivityTabAdapter(FragmentManager fm) {
		super(fm);
		
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = new Fragment();
		
		switch(position){
		
		case 0:
			return new MatchFragment();
			//fragment = MatchFragment.newInstance();
			
		
		case 1:
			return new MessageFragment();
			//fragment = MessageFragment.newInstance();
			//break;
			
		
		case 2:
			return new NotificationFragment();
//			fragment = NotificationFragment.newInstance();
//			
//			break;
		
	case 3:
	    	//return new NotificationFragment();
		
		}
		return null;
	}

	@Override
	public int getCount() {
		
		return CONTENT.length;
	}
	
	@Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length];//.toUpperCase();
    }

}
