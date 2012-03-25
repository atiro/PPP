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
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Context;

import android.content.Intent;

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

	DBAdaptor dbadaptor = null;

	Cursor model = null;
	Context cxt = null;
	Context acxt = null;

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
		acxt = getActivity();

		dbadaptor = new DBAdaptor(cxt).open();

		setList();

	}

	public void updateHouse(House house) {
		this.house = house;
		setList();
	}

	public void updateChamber(Chamber chamber) {
		this.chamber = chamber;
		setList();
	}

	public void updateDate(Integer date) {
		this.date = date;
		setList();
	}

	private void setList() {
		if(house == House.COMMONS) {
			commonshelper = new CommonsDBHelper(cxt).open();
			if(model != null) { model.close(); }
			if(commonsadaptor != null) { commonsadaptor = null; }

			model = commonshelper.getDebatesChamber(chamber, date);
 	            	commonsadaptor = new CommonsDebatesAdaptor(cxt, model);

		} else {
			lordshelper = new LordsDBHelper(cxt).open();
			if(model != null) { model.close(); }
			if(lordsadaptor != null) { lordsadaptor = null; }

			model = lordshelper.getDebatesChamber(chamber, date);
 	                lordsadaptor = new LordsDebatesAdaptor(cxt, model);

		}

		if(house == House.COMMONS) {
	   	    Log.v("PPP", "Setting commons adaptor");
		    setListAdapter(commonsadaptor);
		    commonsadaptor.notifyDataSetChanged();
		    commonshelper.close();
		} else {
	   	    Log.v("PPP", "Setting lords adaptor");
		    setListAdapter(lordsadaptor);
		    lordsadaptor.notifyDataSetChanged();
		    lordshelper.close();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.debates_fragment, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		Log.v("PPP", "Creating DebatesFragment view");


		return v;
	}

	public void onListItemClick(ListView parent, View v, int position,
					long id) {
			String guid;
			String title;
			String subject;

			// Retrieve debate guid
			model.moveToPosition(position);
			if(house == House.COMMONS) {
				guid = commonshelper.getGUID(model);
				title = commonshelper.getTitle(model);
				subject = commonshelper.getSubject(model);
			} else {
				guid = lordshelper.getGUID(model);
				title = lordshelper.getTitle(model);
				subject = lordshelper.getTitle(model);
			}
				
			new AlertDialog.Builder(acxt)
				.setTitle(title)
				.setMessage(subject)
				.setNegativeButton("Share", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthing) {
						Toast.makeText(acxt, "Share Options", Toast.LENGTH_SHORT).show();
					}
				})
				.setNeutralButton("Follow", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthing) {
						Toast.makeText(acxt, "Following", Toast.LENGTH_SHORT).show();
					}
				})
				.setPositiveButton("Remind", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthing) {
						Toast.makeText(acxt, "Reminder", Toast.LENGTH_SHORT).show();
					}
				})
				.show();

/*
			Intent i = new Intent(cxt, DebateView.class);
			Bundle b = new Bundle();
			b.putString("guid", "");
			i.putExtras(b);
			startActivity(i);
*/
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lordshelper.close();
		commonshelper.close();
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


