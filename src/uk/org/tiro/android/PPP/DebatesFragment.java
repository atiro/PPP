package uk.org.tiro.android.PPP;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.CursorAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import android.content.Context;
import android.database.Cursor;

import android.util.Log;

public class DebatesFragment extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

        CommonsDebatesAdaptor commonsadaptor = null;
        LordsDebatesAdaptor lordsadaptor = null;
	ListView lv = null;
        Cursor model = null;
        House house;
	Chamber chamber;
	Integer date;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();

		house = House.values()[bundle.getInt("house")];
		chamber = Chamber.values()[bundle.getInt("chamber")];
		date = bundle.getInt("date");

		getSupportLoaderManager().initLoader(0, null, this);

		// grab house and chamber and date
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				 Bundle savedInstanceState) {

		TextView tv_house;
		TextView tv_chamber;
		TextView tv_now, tv_next, tv_prev;

		View debates = inflater.inflate(R.layout.debates, container,
					false);

		lv = (ListView)findViewById(R.id.list);

		tv_house = (TextView)debates.findViewById(R.id.house);
		if(house == House.COMMONS) {
			tv_house.setText("House of Commons");
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


	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader;

		return(new DebatesCursorLoader(this, house, chamber, date));

	}

	public void onLoadFinished(Loader<Cursor>loader, Cursor cursor) {

	     if(house == COMMONS) {
 	            commonsadaptor = new CommonsDebatesAdaptor(cursor);
		    lv.setListAdapter(commonsadaptor);
	     } else {
 	            lordsadaptor = new LordsDebatesAdaptor(cursor);
		    lv.setListAdapter(lordsadaptor);
	     }

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
