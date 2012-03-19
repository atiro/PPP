package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.app.Activity;

import android.database.Cursor;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;

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



import android.util.Log;

public class Debates extends FragmentActivity {

	private static final int MENU_LEGISLATION = Menu.FIRST+1;

	private boolean detailsInline = false;

	House house;
	Chamber chamber;
	Integer date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle args;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.debates);

		house = House.values()[getIntent().getExtras().getInt("house")];
		chamber = Chamber.values()[getIntent().getExtras().getInt("chamber")];
		date = 0;

		// date

		BrowseFragment browse = (BrowseFragment)getSupportFragmentManager().findFragmentById(R.id.browse);

		args = new Bundle();
		args.putInt("house", house.ordinal());
		args.putInt("chamber", chamber.ordinal());
		args.putInt("date", 0);

//		browse.setArguments(args);

		DebatesFragment debates = (DebatesFragment)getSupportFragmentManager().findFragmentById(R.id.debates);
//		debates.setArguments(args);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_LEGISLATION, Menu.NONE, "Politics Feed").setIcon(R.drawable.ic_menu_info_details);

		return(super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(Debates.this, Legislation.class);
		startActivity(i);
		return(true);
	}

}
