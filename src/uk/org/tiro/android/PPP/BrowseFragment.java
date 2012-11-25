package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.content.Context;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.Gallery;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockFragment;



public class BrowseFragment extends SherlockFragment {
	String [] houses = new String[] { "House of Commons", "House of Lords" };
	String [] chambers_lords = new String [] { "Main Chamber", "Select Committees", "Grand Committee"};
	String [] chambers_commons = new String [] { "Main Chamber", "Select Committees", "Westminister Hall", "Grand Committee", "General Committee"};
	ArrayList<String> days = new ArrayList<String>();

	Gallery house_gallery = null;
	Gallery chamber_gallery = null;
	Gallery day_gallery = null;

	Context c = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.browse_fragment, container, false);
		Context c = getActivity().getApplicationContext();

		ArrayAdapter<String> house_adaptor = new ArrayAdapter<String>(c, android.R.layout.simple_gallery_item, houses);
		ArrayAdapter<String> chamber_adaptor = new ArrayAdapter<String>(c, android.R.layout.simple_gallery_item, chambers_commons);

		// TODO - move to correct position ?

		house_gallery = (Gallery)v.findViewById(R.id.house_gallery);
		house_gallery.setAdapter(house_adaptor);
		house_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, final int position, long id) {
				House house = House.COMMONS;
			
				house = house.values()[position];

				DebatesFragment debates = (DebatesFragment)getFragmentManager().findFragmentByTag("debates");
				
				debates.updateHouse(house);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		chamber_gallery = (Gallery)v.findViewById(R.id.chamber_gallery);
		chamber_gallery.setAdapter(chamber_adaptor);
		chamber_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, final int position, long id) {
				Chamber chamber = Chamber.MAIN;

				chamber = chamber.values()[position];
				
				//Log.v("PPP", "Setting chamber to " + chamber.toString());

				DebatesFragment debates = (DebatesFragment)getFragmentManager().findFragmentByTag("debates");
				
				debates.updateChamber(chamber);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// Handle dates to append/prepend to Today/Tom/Yes

		for(int i = -14; i < -1; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i);
			String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			String date = day + " " + month;
			days.add(date);
		}
		days.add("Yesterday");
		days.add("Today");
		days.add("Tomorrow");
		for(int i = 2; i < 14; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i);
			String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			String date = day + " " + month;
			days.add(date);
		}

		ArrayAdapter<String> day_adaptor = new ArrayAdapter<String>(c, android.R.layout.simple_gallery_item, days);

		day_gallery = (Gallery) v.findViewById(R.id.date_gallery);
		day_gallery.setAdapter(day_adaptor);
		day_gallery.setSelection(14);

		day_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, final int position, long id) {
				Integer date = position - 14;
				
				//Log.v("PPP", "Setting day to " + date.toString());
				DebatesFragment debates = (DebatesFragment)getFragmentManager().findFragmentByTag("debates");
				
				debates.updateDate(date);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		return v;
	}

}
