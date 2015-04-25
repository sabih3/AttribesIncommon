package com.attribes.incommon.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.attribes.incommon.taketour.TakeATourFragmentFour;
import com.attribes.incommon.taketour.TakeATourFragmentOne;
import com.attribes.incommon.taketour.TakeATourFragmentThree;
import com.attribes.incommon.taketour.TakeATourFragmentTwo;

public class ViewPagerAdapter extends FragmentStatePagerAdapter{

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment=new Fragment();
		
		switch (arg0) {
		case 0:
			fragment = TakeATourFragmentOne.newInstance();
			break;
		case 1:
			fragment = TakeATourFragmentTwo.newInstance();
			break;

		case 2:
			fragment = TakeATourFragmentThree.newInstance();
			break;
			
		case 3:
			fragment = TakeATourFragmentFour.newInstance();
			break;
		
		}
		return fragment;
	}

	@Override
	public int getCount() {
		
		return 4;
	}

}
