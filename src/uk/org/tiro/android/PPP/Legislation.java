package uk.org.tiro.android.PPP;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;

import android.content.Intent;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.View;
import android.widget.TextView;

public class Legislation extends FragmentActivity {

	private static final int NUMBER_OF_PAGES = 4;

	private LegislationPagerAdaptor mLegislationPagerAdaptor;
	private ViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.legislation);

		mViewPager = (ViewPager) findViewById(R.id.page_viewer);
		mLegislationPagerAdaptor = new LegislationPagerAdaptor(getSupportFragmentManager());
		mViewPager.setAdapter(mLegislationPagerAdaptor);
		mViewPager.setCurrentItem(1);

	}

	// Handle buttons for all out pages here

	public void add_trigger(View v) {
		Intent i = new Intent(Legislation.this, TriggerNew.class);

		startActivity(i);
	}

	private static class LegislationPagerAdaptor extends FragmentPagerAdapter {
			
		public LegislationPagerAdaptor(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			if(index == 0) {
				return new TriggersListFragment();
			} else if(index == 1) {
				return new PoliticsFeedFragment();
			} else if(index == 2) {
				return new BillsListFragment();
			} else {
				return new ActsListFragment();
			}
			// TODO stat inst when available
			// TODO reports (feed per ministry?!)

		}

		@Override
		public int getCount() {
			return NUMBER_OF_PAGES;
		}
	}

}
			
