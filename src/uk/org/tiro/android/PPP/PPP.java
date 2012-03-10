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

import android.content.Intent;
import android.database.Cursor;
import android.content.Context;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import android.util.Log;

public class PPP extends Activity
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
	ListView list_newsfeed;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	list_alerts = (ListView)findViewById(R.id.list_alerts);

	list_alerts.setAdapter(new AlertsAdaptor() );

	list_alerts.setOnItemClickListener(
		new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
				// Launch Alert List 

				if(pos == 3) {
					Intent i = new Intent(PPP.this, Alerts.class);
					startActivity(i);
				} else if (pos == 4) {
					// TODO add type to bundle
					Intent i = new Intent(PPP.this, Alerts.class);
					startActivity(i);
				}
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
	

	list_newsfeed = (ListView)findViewById(R.id.list_newsfeed);
		
	list_newsfeed.setAdapter(new ArrayAdapter(this,
				R.layout.row_news,
				R.id.label,
				newsfeed));
	
	WakefulIntentService.scheduleAlarms(new AppListener(),
					this, false);

	// And force it to run now as well

	WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);

	dbadaptor = new DBAdaptor(this).open();

	billshelper = new BillsDBHelper(this).open();

	new BillsUpdateTask().execute(); // Retrieve bills
	
	actshelper = new ActsDBHelper(this).open();

	new ActsUpdateTask().execute(); // Retrieve acts

	commonshelper = new CommonsDBHelper(this).open();

	new CommonsUpdateTask().execute();

	lordshelper = new LordsDBHelper(this).open();

	new LordsUpdateTask().execute();

	alertshelper = new AlertsDBHelper(this).open();

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

    class BillsUpdateTask extends AsyncTask<Object, Void, String> {
    	private List<Bill> bills;


	@Override
	protected String doInBackground(Object... args) {

		String rss= "boo";

//		publishProgress();

//		BillsFeedParser parser = new BillsFeedParser("http://services.parliament.uk/bills/AllBills.rss", "");
		BillsFeedParser parser = new BillsFeedParser("http://tiro.org.uk/mobile/AllBills.rss", "");
	
		bills = parser.parse();

		return rss;
	}

	@Override
	protected void onPostExecute(String rss) {

		Cursor c = billshelper.getLatestBill(House.COMMONS);

		if(c.getCount() == 0) {
			// No current entries so easy job - insert all
			Log.v("PPP", "No existing bills in database");

			for(Bill bill : bills) {
				Log.v("PPP", "Inserting bill: " + bill.getTitle() );
				billshelper.insert(bill);
			}

			c.close();
		} else {
			Log.v("PPP", "Bills already in database, not adding");
			// Handle updates
		}

		billshelper.close();
	}
   }

    class ActsUpdateTask extends AsyncTask<Object, Void, String> {
    	private List<Act> acts;

	@Override
	protected String doInBackground(Object... args) {

		String rss= "boo";

//		publishProgress();

//		BillsFeedParser parser = new BillsFeedParser("http://services.parliament.uk/bills/AllBills.rss", "");
		ActsFeedParser parser = new ActsFeedParser("http://tiro.org.uk/mobile/legislation/acts.xml", "");
	
		acts = parser.parse();

		return rss;
	}

	@Override
	protected void onPostExecute(String rss) {

		for(Act act : acts) {
			Log.v("PPP", "Inserting act: " + act.getTitle() );
			actshelper.insert(act);
		}
			/*
		Cursor c = actshelper.getLatestAct();

		if(c.getCount() == 0) {
			// No current entries so easy job - insert all
			Log.v("PPP", "No existing acts in database");

			for(Act act : acts) {
				Log.v("PPP", "Inserting act: " + act.getTitle() );
				actshelper.insert(act);
			}

			c.close();
		} else {
			Log.v("PPP", "Acts already in database, not adding");
			// Handle updates
		}
		*/

		actshelper.close();
	}
   }

    class CommonsUpdateTask extends AsyncTask<Object, Void, String> {
    	private List<CommonsDebate> debates;

	@Override
	protected String doInBackground(Object... args) {

		String rss= "boo";

//		publishProgress();

//		BillsFeedParser parser = new BillsFeedParser("http://services.parliament.uk/bills/AllBills.rss", "");
		CommonsFeedParser parser = new CommonsFeedParser("http://tiro.org.uk/mobile/commons_main_chamber.rss", "");
		// Get select committees
		// Get westminster hall
	
		debates = parser.parse();

		return rss;
	}

	@Override
	protected void onPostExecute(String rss) {

		for(CommonsDebate debate : debates) {
			Log.v("PPP", "Inserting commons debate: " + debate.getTitle() );
			commonshelper.insert(debate);
		}
			/*
		Cursor c = actshelper.getLatestAct();

		if(c.getCount() == 0) {
			// No current entries so easy job - insert all
			Log.v("PPP", "No existing acts in database");

			for(Act act : acts) {
				Log.v("PPP", "Inserting act: " + act.getTitle() );
				actshelper.insert(act);
			}

			c.close();
		} else {
			Log.v("PPP", "Acts already in database, not adding");
			// Handle updates
		}
		*/

		commonshelper.close();
	}
   }

    class LordsUpdateTask extends AsyncTask<Object, Void, String> {
    	private List<LordsDebate> debates;

	@Override
	protected String doInBackground(Object... args) {

		String rss= "boo";

//		publishProgress();

//		BillsFeedParser parser = new BillsFeedParser("http://services.parliament.uk/bills/AllBills.rss", "");
		LordsFeedParser parser = new LordsFeedParser("http://tiro.org.uk/mobile/lords_main_chamber.rss", "");
	
		debates = parser.parse();

		return rss;
	}

	@Override
	protected void onPostExecute(String rss) {

		for(LordsDebate debate : debates) {
			Log.v("PPP", "Inserting lords debate: " + debate.getTitle() );
			lordshelper.insert(debate);
		}
			/*
		Cursor c = actshelper.getLatestAct();

		if(c.getCount() == 0) {
			// No current entries so easy job - insert all
			Log.v("PPP", "No existing acts in database");

			for(Act act : acts) {
				Log.v("PPP", "Inserting act: " + act.getTitle() );
				actshelper.insert(act);
			}

			c.close();
		} else {
			Log.v("PPP", "Acts already in database, not adding");
			// Handle updates
		}
		*/

		lordshelper.close();
	}
   }


}


