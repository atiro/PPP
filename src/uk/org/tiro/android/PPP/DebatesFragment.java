package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.app.Activity;

import android.database.Cursor;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.support.v4.app.ListFragment;
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

public class DebatesFragment extends ListFragment {

	private boolean detailsInline = false;

	House house;
	Chamber chamber;
	Integer date;

	ListView lv = null;

	CommonsDBHelper commonshelper = null;
	LordsDBHelper lordshelper = null;
	LordsDebatesAdaptor lordsadaptor = null;
	CommonsDebatesAdaptor commonsadaptor = null;

	Cursor model = null;
	Context cxt = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle args = this.getArguments();

		//house = House.values()[args.getInt("house")];
		//chamber = Chamber.values()[args.getInt("chamber")];
		house = House.COMMONS;
		chamber = Chamber.MAIN;
		date = 0;

		Log.v("PPP", "creating DebatesFragment");

		// date

		cxt = getActivity().getApplicationContext();

		if(house == House.COMMONS) {
			commonshelper = new CommonsDBHelper(cxt).open();

			if(date == 0) {
			  model = commonshelper.getTodayDebatesChamber(chamber);
 	            	  commonsadaptor = new CommonsDebatesAdaptor(cxt, model);
			} else if(date < 0) {
			  model = commonshelper.getYesterdaysDebatesChamber(chamber);
 	            	  commonsadaptor = new CommonsDebatesAdaptor(cxt, model);
			} else if(date > 0) {
			  model = commonshelper.getTomorrowsDebatesChamber(chamber);
 	            	  commonsadaptor = new CommonsDebatesAdaptor(cxt, model);
			}
		} else {
			lordshelper = new LordsDBHelper(cxt).open();

			if(date == 0) {
			  model = lordshelper.getTodayDebatesChamber(chamber);
 	                  lordsadaptor = new LordsDebatesAdaptor(cxt, model);
			} else if(date < 0) {
			  model = lordshelper.getYesterdaysDebatesChamber(chamber);
 	                  lordsadaptor = new LordsDebatesAdaptor(cxt, model);
			} else if(date > 0) {
			  model = lordshelper.getTomorrowsDebatesChamber(chamber);
 	                  lordsadaptor = new LordsDebatesAdaptor(cxt, model);
			}
		}


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.debates_fragment, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		if(house == House.COMMONS) {
		    lv.setAdapter(commonsadaptor);
		} else {
		    lv.setAdapter(lordsadaptor);
		}

		return v;
	}


    static class CommonsDebatesHolder {
	private TextView time = null;
    	private TextView committee = null;
    	private TextView subject = null;
	private View row = null;

	CommonsDebatesHolder(View row) {
		this.row = row;

		time = (TextView)row.findViewById(R.id.time);
		committee = (TextView)row.findViewById(R.id.committee);
		subject = (TextView)row.findViewById(R.id.subject);
	}

	void populateFrom(Cursor c, CommonsDBHelper helper) {
		time.setText(helper.getTime(c));
		committee.setText(helper.getCommittee(c));
		subject.setText(helper.getSubject(c));
	}

    }
    static class LordsDebatesHolder {
	private TextView time = null;
    	private TextView committee = null;
    	private TextView subject = null;
	private View row = null;

	LordsDebatesHolder(View row) {
		this.row = row;

		time = (TextView)row.findViewById(R.id.time);
		committee = (TextView)row.findViewById(R.id.committee);
		subject = (TextView)row.findViewById(R.id.subject);
	}

	void populateFrom(Cursor c, LordsDBHelper helper) {
		time.setText(helper.getTime(c));
		committee.setText(helper.getCommittee(c));
		subject.setText(helper.getSubject(c));
	}

    }

    class CommonsDebatesAdaptor extends CursorAdapter {

        CommonsDebatesAdaptor(Context context, Cursor c) {
		super(context, c);
		Log.v("PPP", "Creating Commons Debates Adapter");
        }

	@Override
	public void bindView(View row, Context context, Cursor c) {
		CommonsDebatesHolder holder=(CommonsDebatesHolder)row.getTag();

		holder.populateFrom(c, commonshelper);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View row = LayoutInflater.from(getActivity()).inflate(R.layout.row_debate, parent, false);
		CommonsDebatesHolder holder = new CommonsDebatesHolder(row);
		row.setTag(holder);
		return(row);
	}
    }

    class LordsDebatesAdaptor extends CursorAdapter {

        LordsDebatesAdaptor(Context context, Cursor c) {
		super(context, c);
		Log.v("PPP", "Creating Lords Debates Adapter");
        }

	@Override
	public void bindView(View row, Context context, Cursor c) {
		LordsDebatesHolder holder=(LordsDebatesHolder)row.getTag();

		holder.populateFrom(c, lordshelper);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View row = LayoutInflater.from(getActivity()).inflate(R.layout.row_debate, parent, false);
		LordsDebatesHolder holder = new LordsDebatesHolder(row);
		row.setTag(holder);
		return(row);
	}
    }

}


