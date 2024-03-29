package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.app.Activity;
import android.view.Window;

import android.database.Cursor;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;

import android.widget.Gallery;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;

import android.content.Context;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockFragment;

import android.util.Log;

public class Debates extends SherlockFragment {

	private static final int MENU_REFRESH = Menu.FIRST+1;
	private static final int MENU_LEGISLATION = Menu.FIRST+2;

	private boolean detailsInline = false;
	private DebatesFragment debatesFrag = null;
	private BrowseFragment browseFrag = null;

	House house;
	Chamber chamber;
	Integer date;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			house = House.values()[savedInstanceState.getInt("house")];
			chamber = Chamber.values()[savedInstanceState.getInt("chamber")];
		} else {
			house = House.COMMONS;
			chamber = Chamber.MAIN;
		}
		date = 0;

		// date

//		BrowseFragment browse = (BrowseFragment)getSupportFragmentManager().findFragmentById(R.id.browse);

//		args = new Bundle();
//		args.putInt("house", house.ordinal());
//		args.putInt("chamber", chamber.ordinal());
//		args.putInt("date", 0);

//		browse.setArguments(args);

//		DebatesFragment debates = (DebatesFragment)getSupportFragmentManager().findFragmentById(R.id.debates);
//		debates.setArguments(args);


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Bundle args;

	View v = inflater.inflate(R.layout.debates, container, false);

	FragmentManager fm = getActivity().getSupportFragmentManager();
	FragmentTransaction ft = fm.beginTransaction();

	Log.v("PPP", "Debates onCreateView");

	if(savedInstanceState != null) {
		Log.v("PPP", "Debates onCreateView - Retrieving state");
		house = House.values()[savedInstanceState.getInt("house")];
		chamber = Chamber.values()[savedInstanceState.getInt("chamber")];
		date = savedInstanceState.getInt("date");
	} else {

		Log.v("PPP", "Debates onCreateView Day = " + date);
		Log.v("PPP", "Debates onCreateView house = " + house);
		Log.v("PPP", "Debates onCreateView chamber = " + chamber);

		browseFrag = new BrowseFragment();
		args = new Bundle();
		args.putInt("house", house.ordinal());
		args.putInt("chamber", chamber.ordinal());
		args.putInt("date", date);

		browseFrag.setArguments(args);
//	browseFrag.setRetainInstance(true);

		ft.add(R.id.main_frag_container, browseFrag, "browse");
		debatesFrag = new DebatesFragment();
		ft.add(R.id.main_frag_container, debatesFrag, "debates");
//	debatesFrag.setRetainInstance(true);


		ft.commit();

		if((house.ordinal() != 0) && (chamber.ordinal() != 0) && date != 0) {
			debatesFrag.updateAll(house, chamber, date);
		}
	}

	return v;

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, "Refresh").setIcon(R.drawable.ic_menu_refresh);
//		menu.add(Menu.NONE, MENU_LEGISLATION, Menu.NONE, "Politics Feed").setIcon(R.drawable.ic_menu_info_details);
//
//		return(super.onCreateOptionsMenu(menu));
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch(item.getItemId()) {
//			case MENU_REFRESH:
//				WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);
//				return(true);
//			case MENU_LEGISLATION:
//				Intent i = new Intent(Debates.this, Legislation.class);
//				startActivity(i);
////				return(true);
//		}

//		return(super.onOptionsItemSelected(item));
//	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("house", house.ordinal());
		outState.putInt("chamber", chamber.ordinal());
		outState.putInt("date", date);
	}

/*
	@Override
	public void onRestoreInstanceState(Bundle outState) {
		super.onRestoreInstanceState(outState);
		outState.putInt("house", house.ordinal());
		outState.putInt("chamber", chamber.ordinal());
		outState.putInt("date", date);
		if(savedInstanceState != null) {
		Log.v("PPP", "Debates onCreateView - Retrieving state");
		house = House.values()[savedInstanceState.getInt("house")];
		chamber = Chamber.values()[savedInstanceState.getInt("chamber")];
		date = savedInstanceState.getInt("date");
	} 

	}
	*/
		
	@Override
	public void onPause() {
		super.onPause();
		Log.v("PPP", "Debates onPause()");

		if(browseFrag != null) {
			house = browseFrag.getHouse();
			chamber = browseFrag.getChamber();
			date = browseFrag.getDate();

			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			if(debatesFrag != null) {
				ft.remove(debatesFrag);
				debatesFrag = null;
			}
			if(browseFrag != null) {
				ft.remove(browseFrag);
				browseFrag = null;
			}
			ft.commit();
		}
	}

	@Override
	public void onResume() {
		Log.v("PPP", "Debates onResume()");
		super.onResume();
	}

	@Override
	public void onDestroy() {
		Log.v("PPP", "Debates onDestroy()");
		super.onDestroy();
	}

}
