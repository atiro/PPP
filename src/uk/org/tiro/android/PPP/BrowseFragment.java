package uk.org.tiro.android.PPP;

import android.os.Bundle;
import android.os.Build;

import android.content.Context;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormatSymbols;
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

	House browse_house = null;
	Chamber browse_chamber = null;
	Integer browse_date = 0;

	Context c = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.browse_fragment, container, false);
		Context c = getActivity().getApplicationContext();

		Log.v("PPP", "Creating BrowseFragment");

		Bundle args = getArguments();

		browse_house = House.values()[args.getInt("house", 0)];
		browse_chamber = Chamber.values()[args.getInt("chamber", 0)];
		browse_date = args.getInt("date", 0);
		
		ArrayAdapter<String> house_adaptor = new ArrayAdapter<String>(c, android.R.layout.simple_gallery_item, houses);
		ArrayAdapter<String> chamber_adaptor = new ArrayAdapter<String>(c, android.R.layout.simple_gallery_item, chambers_commons);

		// TODO - move to correct position ?


		house_gallery = (Gallery)v.findViewById(R.id.house_gallery);
		house_gallery.setAdapter(house_adaptor);
		if(browse_house == House.LORDS) {
			house_gallery.setSelection(1);
		}
		house_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, final int position, long id) {
				browse_house = House.values()[position];

				DebatesFragment debates = (DebatesFragment)getFragmentManager().findFragmentByTag("debates");
				
				debates.updateHouse(browse_house);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		chamber_gallery = (Gallery)v.findViewById(R.id.chamber_gallery);
		chamber_gallery.setAdapter(chamber_adaptor);
		if(browse_chamber != null) {
			chamber_gallery.setSelection(browse_chamber.ordinal());
		}

		chamber_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, final int position, long id) {
				browse_chamber = Chamber.values()[position];
				
				//Log.v("PPP", "Setting chamber to " + chamber.toString());

				DebatesFragment debates = (DebatesFragment)getFragmentManager().findFragmentByTag("debates");
				
				debates.updateChamber(browse_chamber);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// Handle dates to append/prepend to Today/Tom/Yes

		for(int i = -14; i < -1; i++) {
			String month;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i);
			// Taken from http://blog.imaginea.com/calendar-get-display-name-jdk5/
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
			} else {
				DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
				String[] months = dateFormatSymbols.getShortMonths();
				month = months[cal.get(Calendar.MONTH)];
			}
			int day = cal.get(Calendar.DAY_OF_MONTH);
			String date = day + " " + month;
			days.add(date);
		}
		days.add("Yesterday");
		days.add("Today");
		days.add("Tomorrow");
		for(int i = 2; i < 14; i++) {
			String month;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, i);
			// Taken from http://blog.imaginea.com/calendar-get-display-name-jdk5/
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
			} else {
				DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
				String[] months = dateFormatSymbols.getShortMonths();
				month = months[cal.get(Calendar.MONTH)];
			}
			int day = cal.get(Calendar.DAY_OF_MONTH);
			String date = day + " " + month;
			days.add(date);
		}

		ArrayAdapter<String> day_adaptor = new ArrayAdapter<String>(c, android.R.layout.simple_gallery_item, days);

		day_gallery = (Gallery) v.findViewById(R.id.date_gallery);
		day_gallery.setAdapter(day_adaptor);
		day_gallery.setSelection(browse_date + 14);

		day_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, final int position, long id) {
				browse_date = position - 14;
				
				//Log.v("PPP", "Setting day to " + date.toString());
				DebatesFragment debates = (DebatesFragment)getFragmentManager().findFragmentByTag("debates");
				
				debates.updateDate(browse_date);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		return v;
	}

        @Override
        public void onPause() {
                super.onPause();
                Log.v("PPP", "BrowseFragment - onPause");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
                Log.v("PPP", "BrowseFragment - saveInstanceState");
		outState.putInt("house", browse_house.ordinal());
		outState.putInt("chamber", browse_chamber.ordinal());
		outState.putInt("date", browse_date);
	}

	public House getHouse() {
		return browse_house;
	}

	public Chamber getChamber() {
		return browse_chamber;
	}

	public Integer getDate() {
		return browse_date;
	}

}
