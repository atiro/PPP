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

import android.widget.Gallery;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;

import android.content.Context;



import android.util.Log;

public class Debates extends FragmentActivity {

	private boolean detailsInline = false;

	House house;
	Chamber chamber;
	Integer date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debates);

		house = House.values()[getIntent().getExtras().getInt("house")];
		chamber = Chamber.values()[getIntent().getExtras().getInt("chamber")];
		date = 0;

		// date

		BrowseFragment browse = (BrowseFragment)getSupportFragmentManager().findFragmentById(R.id.browse);

		DebatesFragment debates = (DebatesFragment)getSupportFragmentManager().findFragmentById(R.id.debates);

		getSupportLoaderManager().initLoader(0, null, this);

	}

}
