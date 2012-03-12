package uk.org.tiro.android.PPP;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.support.v4.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.content.Context;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import android.util.Log;

public class PPP extends FragmentActivity
{

    private static final String[] alerts = {"Debates", "Committees", "Bills", "Acts", "Stat. Inst."};

    private static final String[] news = {"News Story 1", "News Story 2", "News Story 3", "News Story 4", "News Story 5"};

    private static final String[] legislation= {"Draft Bills", "Current Bills", "Recent Acts", "Draft S.I.", "Stat. Inst."};

    private static final String[] house = {"House of Commons", "House of Lords"};

    private static final String[] newsfeed = {"Alert 1", "Alert 2", "Alert 3", "Older"};

    private BillsDBHelper billshelper;
    private ActsDBHelper actshelper;
    private LordsDBHelper lordshelper;
    private CommonsDBHelper commonshelper;
    private AlertsDBHelper alertshelper;
    private DBAdaptor dbadaptor;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	ListView list_alerts, list_legislation;
	ListView list_house;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	list_alerts = (ListView)findViewById(R.id.list_alerts);

	list_alerts.setAdapter(new AlertsAdaptor() );

	list_alerts.setOnItemClickListener(
		new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
				// Launch Alert List 
				Bundle b = new Bundle();

				if(pos == 0) {
					b.putInt("type", Alerts.DEBATES.ordinal());
				} else if (pos == 1) {
					b.putInt("type", Alerts.COMMITTEES.ordinal());
				} else if (pos == 2) {
					b.putInt("type", Alerts.BILLS.ordinal());
				} else if (pos == 3) {
					b.putInt("type", Alerts.ACTS.ordinal());
				} else {
					b.putInt("type", Alerts.STAT_INST.ordinal());
				}

				Intent i = new Intent(PPP.this, AlertsList.class);
				i.putExtras(b);
					startActivity(i);
			}
		});

	list_legislation = (ListView)findViewById(R.id.list_legislation);

	list_legislation.setAdapter(new LegislationAdaptor() );

	list_legislation.setOnItemClickListener(
		new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
				// Launch Bills for all atm 

				if(pos == 1) {
					Intent i = new Intent(PPP.this, Bills.class);
					startActivity(i);
				} else if (pos == 2) {
					Intent i = new Intent(PPP.this, Acts.class);
					startActivity(i);
				}
			}
		});

	list_house = (ListView)findViewById(R.id.list_house);

	list_house.setAdapter(new ArrayAdapter(this,
				R.layout.row_news,
				R.id.label,
				house));

	list_house.setOnItemClickListener(
		new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
				Bundle b = new Bundle();
				if(pos == 0) {
					b.putInt("house", House.COMMONS.ordinal());
				} else if(pos == 1) {
					b.putInt("house", House.LORDS.ordinal());
				}

				Intent i = new Intent(PPP.this, Debates.class);
				i.putExtras(b);
				startActivity(i);
			}
		});
	

	dbadaptor = new DBAdaptor(this).open();

	WakefulIntentService.scheduleAlarms(new PPPAlarm(),
					this, false);

	// And force it to run now as well


	// WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);

	billshelper = new BillsDBHelper(this).open();

	actshelper = new ActsDBHelper(this).open();

	alertshelper = new AlertsDBHelper(this).open();

	PoliticsFeedFragment feed = (PoliticsFeedFragment)getSupportFragmentManager().findFragmentById(R.id.feed);
    }


    class AlertsAdaptor extends ArrayAdapter<String> {
    	AlertsAdaptor() {
		super(PPP.this, R.layout.row_alert_count, R.id.label, alerts);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);

		TextView count = (TextView)row.findViewById(R.id.count);

		if(position == 0) {
		//	Integer commons_count = commonshelper.getAlertCount();
		//	Integer lords_count = lordshelper.getAlertCount();
		//	Integer total = commons_count + lords_count;
		//	count.setText(total.toString());
			count.setText("0");
		} else if(position == 1) {
			count.setText("0");
		} else if(position == 2) {
			Integer bill_count = alertshelper.getBillAlertsCount();
			count.setText(bill_count.toString());
		} else if(position == 3) {
			Integer acts_count = alertshelper.getActAlertsCount();
			count.setText(acts_count.toString());
		} else {
			count.setText("0");
		}

		return(row);
	}
    }

    class LegislationAdaptor extends ArrayAdapter<String> {
    	LegislationAdaptor() {
		super(PPP.this, R.layout.row_alert_count, R.id.label, legislation);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);

		TextView count = (TextView)row.findViewById(R.id.count);

		if(position == 0) {
			// TODO - no reliable feed yet for draft bills
			count.setText("-");
		} else if(position == 1) {
			Integer bill_count = billshelper.getBillsCount();
			count.setText(bill_count.toString());
		} else if(position == 2) {
			Integer acts_count = actshelper.getAllActsCount();
			count.setText(acts_count.toString());
		} else {
			count.setText("-");
		}

		return(row);
	}
    }

/*
    class NewsAdapter extends ArrayAdapter<String> {
    	NewsAdapter() {
		super(PPP.this, R.layout.row_alert_count, R.id.label, news);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);

		TextView count = (TextView)row.findViewById(R.id.count);

		count.setText("10");

		return(row);
	}
    }

*/


}


