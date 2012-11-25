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

	House house;
	Chamber chamber;
	Integer date;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Bundle args;

		super.onCreate(savedInstanceState);

//		house = House.values()[getIntent().getExtras().getInt("house")];
//		chamber = Chamber.values()[getIntent().getExtras().getInt("chamber")];
		house = House.COMMONS;
		chamber = Chamber.MAIN;
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

	View v = inflater.inflate(R.layout.debates, container, false);

	FragmentManager fm = getActivity().getSupportFragmentManager();
	FragmentTransaction ft = fm.beginTransaction();

	BrowseFragment browseFrag = new BrowseFragment();
	ft.add(R.id.main_frag_container, browseFrag, "browse");
	DebatesFragment debatesFrag = new DebatesFragment();
	ft.add(R.id.main_frag_container, debatesFrag, "debates");

	ft.commit();

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
	public void onDestroy() {
		super.onDestroy();
	}

}
