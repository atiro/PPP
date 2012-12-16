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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import android.view.Window;
import android.text.format.Time;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.content.Context;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PPP extends SherlockFragmentActivity 
{

    private DBAdaptor dbadaptor;
    private SharedPreferences prefs;
    private boolean first_run;
    private static final int MENU_REFRESH = Menu.FIRST+1;
    private static final int MENU_ABOUT = Menu.FIRST+2;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

	setTheme(R.style.Theme_Sherlock);

        super.onCreate(savedInstanceState);

//	this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	setContentView(R.layout.ppp_main);

 	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	getSupportActionBar().setDisplayShowHomeEnabled(false);
	getSupportActionBar().setDisplayShowTitleEnabled(false);
	getSupportActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_USE_LOGO);

//        setContentView(R.layout.main);

	// And force it to run now as well

        Log.v("PPP", "Checking first run");
	prefs = PreferenceManager.getDefaultSharedPreferences(this);

        first_run = prefs.getBoolean("firstRun", true);

	if(first_run == true) {
        	Log.v("PPP", "First run");

		WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("firstRun", false);
		Time now = new Time();
		now.setToNow();
                edit.putLong("lastRun", now.toMillis(true));
                edit.putInt("version", 4); // TODO get from manifest ?
                edit.commit();

		// Now schedule weekly update (if wi-fi available)
		WakefulIntentService.scheduleAlarms(new PPPAlarm(), this, false);

		new AlertDialog.Builder(this)
			.setTitle("Welcome")
			.setMessage("This app is intented to allow you to follow Debates, Bills, and Acts of Parliament that match your interests. \n\nAs this is the first time you have run the app, it will need a few moments to initialise and download the various data feeds provided by Parliament.\n\n")
				.setNeutralButton("Aye", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg,
int sumthing) {
					}
				})
				.show();

	} else { // TODO get current version and display update message

		// Update scheduled daily update (if wi-fi available)
		WakefulIntentService.cancelAlarms(this);
		WakefulIntentService.scheduleAlarms(new PPPAlarm(), this, false);
                SharedPreferences.Editor edit = prefs.edit();
		Time now = new Time();
		now.setToNow();
                edit.putLong("lastRun", now.toMillis(true));
                edit.putInt("version", 4); // TODO get from manifest ?
                edit.commit();
	}

	//PoliticsFeedFragment feed = (PoliticsFeedFragment)getSupportFragmentManager().findFragmentById(R.id.feed);

	// TODO start politicsfeed or calendar basedon settings

	dbadaptor = new DBAdaptor(this).open();
	dbadaptor.close();
       	Log.v("PPP", "Created DB");

//	ActionBar bar = getActionBar();


	String tabs[] = new String[] { "Reader", "Planner", "Debates", "Bills", "Acts", "Alerts" };
				// Toadd - News, Reports, 

	//String tabs[] = new String[] { "Debates" };
	Fragment frags[] = new Fragment[] { 
					    new ReaderFragment(),
					    new PoliticsFeedFragment(),
					    new Debates(),
					    new BillsListFragment(),
					    new ActsListFragment(),
					    new TriggersListFragment() };

	int i = 0;
	for(String tabname: tabs) {
       		Log.v("PPP", "Creating Tab" + tabname);
		ActionBar bar = getSupportActionBar();
		ActionBar.Tab tab = bar.newTab();
		tab.setText(tabname);
		tab.setTabListener( new PPPTabListener(this, frags[i]));
		bar.addTab(tab);
		i+=1;
	}


//	Intent i = new Intent(this, Debates.class);
	// Only in API 11 and above
//	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//	Bundle b = new Bundle();
//	b.putInt("house", House.COMMONS.ordinal());
//	b.putInt("chamber", Chamber.MAIN.ordinal());
//	i.putExtras(b);
//	startActivity(i);
//	finish();
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, "Refresh").setIcon(
R.drawable.ic_menu_refresh);
		menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, "About"
).setIcon(R.drawable.ic_menu_info_details);

		return(super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case MENU_REFRESH:
				WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);
				return(true);
			case MENU_ABOUT:
			new AlertDialog.Builder(this)
				.setTitle("About PPP")
			.setMessage("Written by Richard Palmer <richard@tiro.org.uk> using:\n\n   Parliament RSS Feeds\n\n   ActionBarSherlock by Jake Wharton\n\n    CommonsGuy components by Mark Murphy\n\nVersion: 0.2-autumnstatement")
				.setNeutralButton("Aye", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg,
int sumthing) {
					}
				})
				.show();
//				return(true);
		}

		return(super.onOptionsItemSelected(item));
	}


/*
    class NewsAdapter extends ArrayAdapter<String> {
    	NewsAdapter() {
		super(PPP.this, R.layout.row_trigger_count, R.id.label, news);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);

		TextView count = (TextView)row.findViewById(R.id.count);

		count.setText("10");

		return(row);
	}
    }

*/

        @Override
        public void onSaveInstanceState(Bundle outState) {
                super.onSaveInstanceState(outState);
	}

   private class PPPTabListener implements TabListener {
   	private PPP pAct;
	private Fragment pFrag;
	private boolean firstDisplay = true;
	private String[] tabNames = new String[] {"Reader", "Planner", "Debates", "Bills", "Acts", "Alerts"};

	public PPPTabListener(PPP act, Fragment frag) {
		pAct = act;
		pFrag = frag;
	}

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction transaction) {
   	}

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction xaction = fragMgr.beginTransaction();

		if(firstDisplay == true) {
			int pos = tab.getPosition();
			Fragment preInitFragment = (Fragment)fragMgr.findFragmentByTag(tabNames[pos]);
			if(preInitFragment != null) {
				xaction.attach(preInitFragment);
			} else if(pos == 0) {
			  xaction.add(R.id.content, pFrag, "Reader");
			} else if(pos == 1) {
			  xaction.add(R.id.content, pFrag, "Planner");
			} else if(pos == 2) {
			  xaction.add(R.id.content, pFrag, "Debates");
			} else if(pos == 3) {
			  xaction.add(R.id.content, pFrag, "Bills");
			} else if(pos == 4) {
			  xaction.add(R.id.content, pFrag, "Acts");
			} else if(pos == 5) {
			  xaction.add(R.id.content, pFrag, "Alerts");
			}
			firstDisplay = false;
		} else {
			xaction.attach(pFrag);
		}

		xaction.commit();
	}

   	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction xaction = fragMgr.beginTransaction();

		Fragment frag = (Fragment)fragMgr.findFragmentById(R.id.content);

		if(frag != null) {
			xaction.detach(frag);
		}
		xaction.commit();

	}
  }

}


