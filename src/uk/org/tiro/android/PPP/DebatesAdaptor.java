package uk.org.tiro.android.PPP;


import java.util.List;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DebatesAdaptor extends FragmentPagerAdapter {
	House house;
	Chamber chamber;
	// Date

	public DebatesAdaptor(FragmentManager fm, House house, Chamber chamber) {
		super(fm);
		this.house = house;
		this.chamber = chamber;
		// TODO date
	}

	@Override
    	public int getCount() {
		return 3;
	}

	@Override
	public Fragment getItem(int position) {
		
		DebatesFragment debate;
		Bundle bundle;

		bundle = new Bundle();

		bundle.putInt("house", house.ordinal());
		bundle.putInt("chamber", chamber.ordinal());

		switch(position) {
		case 0:
			bundle.putInt("date", -1);
			debate = new DebatesFragment();
			debate.setArguments(bundle);
		case 1:
			bundle.putInt("date", 0);
			debate = new DebatesFragment();
			debate.setArguments(bundle);
			break;
		case 2: 
			bundle.putInt("date", 1);
			debate = new DebatesFragment();
			debate.setArguments(bundle);
			break;
		default:
			bundle.putInt("date", 0);
			debate = new DebatesFragment();
			debate.setArguments(bundle);
			
		}

		return debate;
	}
}



