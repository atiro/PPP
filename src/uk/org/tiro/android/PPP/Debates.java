package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.util.Log;


public class Debates extends FragmentActivity {

	House house;
	Chamber chamber;

	DebatesAdaptor dAdaptor;
	ViewPager dPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debates_pager);

		house = House.values()[getIntent().getExtras().getInt("house")];
		chamber = Chamber.values()[getIntent().getExtras().getInt("chamber")];
		// date

		dAdaptor = new DebatesAdaptor(getSupportFragmentManager(), house, chamber);

		dPager = (ViewPager)findViewById(R.id.viewpager);
		dPager.setAdapter(dAdaptor);
	}

}


