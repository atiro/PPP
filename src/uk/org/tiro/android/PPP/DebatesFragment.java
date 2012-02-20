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

public class DebatesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private boolean detailsInline = false;

	House house;
	Chamber chamber;
	Integer date;

	ListView lv = null;

	LordsDebatesAdaptor lordsadaptor = null;
	CommonsDebatesAdaptor commonsadaptor = null;

	@Override
	protected void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


		house = House.values()[getIntent().getExtras().getInt("house")];
		chamber = Chamber.values()[getIntent().getExtras().getInt("chamber")];
		date = 0;

		// date


		getSupportLoaderManager().initLoader(0, null, this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.debates_fragment, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);

		return v;
	}


	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return(new DebateCursorLoader(this, house, chamber, date));
	}

	public void onLoadFinished(Loader<Cursor>loader, Cursor cursor) {

	     if(house == House.COMMONS) {
 	            commonsadaptor = new CommonsDebatesAdaptor(cursor);
		    lv.setAdapter(commonsadaptor);
	     } else {
 	            lordsadaptor = new LordsDebatesAdaptor(cursor);
		    lv.setAdapter(lordsadaptor);
	     }

	}

	public void onLoaderReset(Loader<Cursor> loader) {
		lv.setAdapter(null);
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

        CommonsDebatesAdaptor(Cursor c) {
		super(Debates.this, c);
		Log.v("PPP", "Creating Commons Debates Adapter");
        }

	@Override
	public void bindView(View row, Context ctct, Cursor c) {
		CommonsDebatesHolder holder=(CommonsDebatesHolder)row.getTag();

		holder.populateFrom(c, commonshelper);
	}

	@Override
	public View newView(Context ctxt, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = getLayoutInflater();
		View row = inflater.inflate(R.layout.row_debate, parent, false);
		CommonsDebatesHolder holder = new CommonsDebatesHolder(row);
		row.setTag(holder);
		return(row);
	}
    }

    class LordsDebatesAdaptor extends CursorAdapter {

        LordsDebatesAdaptor(Cursor c) {
		super(Debates.this, c);
		Log.v("PPP", "Creating Lords Debates Adapter");
        }

	@Override
	public void bindView(View row, Context ctct, Cursor c) {
		LordsDebatesHolder holder=(LordsDebatesHolder)row.getTag();

		holder.populateFrom(c, lordshelper);
	}

	@Override
	public View newView(Context ctxt, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = getLayoutInflater();
		View row = inflater.inflate(R.layout.row_debate, parent, false);
		LordsDebatesHolder holder = new LordsDebatesHolder(row);
		row.setTag(holder);
		return(row);
	}
    }

}


