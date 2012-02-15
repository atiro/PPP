package uk.org.tiro.android.PPP;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import android.widget.CursorAdapter;

import android.content.Context;
import android.database.Cursor;

import android.util.Log;

public class DebatesFragment extends ListFragment {

        CommonsDBHelper commonshelper = null;
        LordsDBHelper lordshelper = null;
        CommonsDebatesAdaptor commonsadaptor = null;
        LordsDebatesAdaptor lordsadaptor = null;
        Cursor model = null;
        House house;
	Chamber chamber;
	Integer date;

	public void onCreate(Bundle savedInstanceState) {

		Bundle bundle = getArguments();

		house = House.values()[bundle.getInt("house")];
		chamber = Chamber.values()[bundle.getInt("chamber")];
		date = bundle.getInt("date");

		// grab house and chamber and date
		if(house == House.COMMONS) {
			if(date == 0) {
 	                        model = commonshelper.getTodayDebatesChamber(chamber);
        	                startManagingCursor(model);
                	        commonsadaptor = new CommonsDebatesAdaptor(model);
			} else if(date < 0) {
 	                        model = commonshelper.getYesterdaysDebatesChamber(chamber);
        	                startManagingCursor(model);
                	        commonsadaptor = new CommonsDebatesAdaptor(model);
			} else if(date > 0) {
 	                        model = commonshelper.getTomorrowsDebatesChamber(chamber);
        	                startManagingCursor(model);
                	        commonsadaptor = new CommonsDebatesAdaptor(model);
			}
		} else {
			if(date == 0) {
                          model = lordshelper.getTodayDebatesChamber(chamber);
                          startManagingCursor(model);
                          lordsadaptor = new LordsDebatesAdaptor(model);
			} else if(date < 0) {
                          model = lordshelper.getYesterdaysDebatesChamber(chamber);
                          startManagingCursor(model);
                          lordsadaptor = new LordsDebatesAdaptor(model);
			} else if(date > 0) {
                          model = lordshelper.getTomorrowsDebatesChamber(chamber);
                          startManagingCursor(model);
                          lordsadaptor = new LordsDebatesAdaptor(model);
			}
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				 Bundle savedInstanceState) {

		TextView tv_house;
		TextView tv_chamber;
		TextView tv_now, tv_next, tv_prev;

		View debates = inflater.inflate(R.layout.debates, container,
					false);

		tv_house = (TextView)debates.findViewById(R.id.house);
		if(house == House.COMMONS) {
			tv_house.setText("House of Commons");
			setListAdapter(commonsadaptor);
		} else {
			tv_house.setText("House of Lords");
			setListAdapter(lordsadaptor);
		}

		tv_chamber = (TextView)debates.findViewById(R.id.chamber);
		tv_chamber.setText("Main Chamber");

		tv_prev = (TextView)debates.findViewById(R.id.prev);
		tv_prev.setText("Yesterday");
		
		tv_now = (TextView)debates.findViewById(R.id.now);
		tv_now.setText("Today");

		tv_next = (TextView)debates.findViewById(R.id.next);
		tv_next.setText("Tomorrow");

		return debates;
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
		super(DebatesFragment.this, c);
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
		super(DebatesFragment.this, c);
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
