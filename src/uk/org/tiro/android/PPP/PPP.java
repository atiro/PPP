package uk.org.tiro.android.PPP;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

public class PPP extends SherlockFragmentActivity implements OnNavigationListener 
{

    private SharedPreferences prefs;
    private boolean first_run;
    private static final int MENU_PLANNER = Menu.FIRST+1;
    private static final int MENU_READER = Menu.FIRST+2;
    private static final int MENU_RULES  = Menu.FIRST+3;
    private static final int MENU_SETTINGS = Menu.FIRST+4;

    private String[] parlNames = new String[] {"Debates", "Bills", "Acts"};
    private String[] appNames = new String[] {"Lobby", "Reader", "Planner", "Rules", "Settings"};
    private List<Fragment> frags_parl = new ArrayList<Fragment>();
    private List<Fragment> frags_app = new ArrayList<Fragment>();
    private int lastPos = -1;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	Bundle extras = getIntent().getExtras();

	setTheme(R.style.Theme_Sherlock);

        super.onCreate(savedInstanceState);

//	this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	setContentView(R.layout.ppp_main);

 	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	getSupportActionBar().setDisplayShowHomeEnabled(true);
	getSupportActionBar().setDisplayShowTitleEnabled(true);
	getSupportActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_USE_LOGO);

	if(savedInstanceState!= null) {
       		Log.v("PPP", "Orientation change - switching to correct tab");
	} else {

	// Setup spinner

	//Spinner ppp_spinner = (Spinner) findViewById(R.id.ppp_spinner);

	ArrayAdapter<CharSequence> spin_adaptor = ArrayAdapter.createFromResource(
		this, R.array.ppp_array, android.R.layout.simple_spinner_item);

	//spin_adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	//ppp_spinner.setAdapter(spin_adaptor);
	getSupportActionBar().setListNavigationCallbacks(spin_adaptor, this);

//        setContentView(R.layout.main);

	// And force it to run now as well

        Log.v("PPP", "Checking first run");
	prefs = PreferenceManager.getDefaultSharedPreferences(this);

	// Ensure default values are set 

	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        first_run = prefs.getBoolean("firstRun", true);

	if(first_run == true) {
        	Log.v("PPP", "First run");

                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("firstRun", false);
                edit.putInt("version", 12); // TODO get from manifest ?
                edit.commit();

		WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);

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
        	Log.v("PPP", "Not first run");
		// Update scheduled daily update (if wi-fi available)
                SharedPreferences.Editor edit = prefs.edit();

                int version = prefs.getInt("version", 12); // TODO get from manifest ?
		if(version < 12) {
		  new AlertDialog.Builder(this)
			.setTitle("Changelog")
			.setMessage("Changes for 0.3-eastleigh-byelection2 :\n    Bugfix Release\n\n    Restored missing settings option\n\n     Removed inactive News & Reports tabs\n\n    Changed naming of alerts (now rules)\n\n    Added titles to lists\n")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg,
int sumthing) {
					}
				})
				.show();
		}


                edit.putInt("version", 12); // TODO get from manifest ?
                edit.commit();

		WakefulIntentService.cancelAlarms(this);

		WakefulIntentService.scheduleAlarms(new PPPAlarm(), this, true);
	}

	}

       	Log.v("PPP", "Created DB");

	int i = 0;
	/*
	for(String tabName: listNames) {
	  FragmentManager fragMgr = getSupportFragmentManager();
	  FragmentTransaction xaction = fragMgr.beginTransaction();
	  Fragment existingFrag= (Fragment)fragMgr.findFragmentByTag(tabName);

	  if(existingFrag != null) {
        	Log.v("PPP", "Should re-attached existing fragment");

		ActionBar bar = getSupportActionBar();
		ActionBar.Tab tab = bar.newTab();
		tab.setText(tabName);
		tab.setTag(tabName);
		tab.setTabListener( new PPPTabListener(this, existingFrag));
		bar.addTab(tab);
		i+=1;
	  	// re-attach existing fragment to tab
		/*
		ActionBar bar = getSupportActionBar();
		ActionBar.Tab tab = bar.getTabAt(0);
		tab.setText(tabName);
		tab.setTabListener( new PPPTabListener(this, frag));
		bar.addTab(tab);
		i+=1;
	*/
	 // } else {

	for(String tabName: appNames) {
	      Fragment frag;
       	      Log.v("PPP", "Creating Tab " + tabName);

	      if(tabName == "Lobby") {
	      	frag = new PPPLobbyFragment();
	      } else if(tabName == "Reader") {
	      	frag = new ReaderFragment();
	      } else if(tabName == "Planner") {
	        frag = new PoliticsFeedFragment();
	      } else if(tabName == "Rules") {
		frag = new TriggersListFragment();
	      // } else if(tabName == "Settings") {
		// frag = new PPPSettingsFrag();
	      } else {
	      	frag = null;
	      }

	      frags_app.add(frag);
	}

	for(String tabName: parlNames) {
	      Fragment frag;
       	      Log.v("PPP", "Creating Tab " + tabName);

	      if(tabName == "Debates") {
	        frag = new Debates();
	      } else if(tabName == "Bills") {
	        frag = new BillsListFragment();
	      } else if(tabName == "Acts") {
	        frag = new ActsListFragment();
	      } else {
	      	frag = null;
	      }
	      frags_parl.add(frag);
	 }


/*
		ActionBar bar = getSupportActionBar();
		ActionBar.Tab tab = bar.newTab();
		tab.setText(tabName);
		tab.setTag(tabName);
		tab.setTabListener( new PPPTabListener(this, frag));
		bar.addTab(tab);
		i+=1;
	  }
	}
*/

//	Intent i = new Intent(this, Debates.class);
	// Only in API 11 and above
//	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//	Bundle b = new Bundle();
//	b.putInt("house", House.COMMONS.ordinal());
//	b.putInt("chamber", Chamber.MAIN.ordinal());
//	i.putExtras(b);
//	startActivity(i);
//	finish();

	if(savedInstanceState!= null) {
       		Log.v("PPP", "Orientation change - really switching to correct tab - " + savedInstanceState.getInt("currentTab"));
		getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("currentTab"));
	} else if(extras != null) {
		if(extras.getInt("notification_start") == 1) {
			viewNewLatestDebates(null);
		} else if(extras.getInt("notification_start") == 2) {
			viewReadableNewDebates(null);
		} else if(extras.getInt("notification_start") == 3) {
			// Not supported yet
		}
	}
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

//		menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, "Refresh").setIcon(R.drawable.ic_menu_refresh);
//		menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings").setIcon(R.drawable.ic_menu_preferences);
//		menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, "About").setIcon(R.drawable.ic_menu_info_details);

//		return(super.onCreateOptionsMenu(menu));

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String)item.getTitle();
		Integer item_id = item.getItemId();

		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction xaction = fragMgr.beginTransaction();

		Fragment frag = (Fragment)fragMgr.findFragmentById(R.id.content);

   		Log.v("PPP", "Handling home & list selection");

		if(frag != null) {
   			Log.v("PPP", "Removing existing fragment");
			xaction.detach(frag);
		}

		String tag = null;

		if(item_id == android.R.id.home) {
			tag = "Lobby";
		} else if(item_id == R.id.menu_reader) {
			tag = "Reader";
		} else if(item_id == R.id.menu_planner) {
			tag = "Planner";
		} else if(item_id == R.id.menu_rules) {
			tag = "Rules";
		}
		Fragment preInitFragment = (Fragment)fragMgr.findFragmentByTag(tag);

		if(preInitFragment != null) {
   			Log.v("PPP", "Adding existing fragment");
			xaction.attach(preInitFragment);
		} else if(item_id == android.R.id.home) {
   			Log.v("PPP", "Adding home fragment");
		 	xaction.add(R.id.content, frags_app.get(0), "Lobby");
		} else if(item_id == R.id.menu_reader) {
		 	xaction.add(R.id.content, frags_app.get(1), "Reader");
		} else if(item_id == R.id.menu_planner) {
		  	xaction.add(R.id.content, frags_app.get(2), "Planner");
		} else if(item_id == R.id.menu_rules) {
		  	xaction.add(R.id.content, frags_app.get(3), "Rules");
		} else if(item_id == R.id.menu_settings) {
			// Need to implement SettingsFragment somehow. Parse 
			// settings file ourself ?
		  	// xaction.add(R.id.content, frags_app.get(4), "Settings");
			Intent i = new Intent(this, PPPSettings.class);
			startActivity(i);
			return(true);

		} else {
       			Log.v("PPP", "Unknown menu item '" + title + "'");
		}
		/*
			case MENU_REFRESH:
				WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);
				return(true);
			case MENU_SETTINGS:
			     Intent i = new Intent(this, PPPSettings.class);
			     startActivity(i);
			     return(true);
			case MENU_ABOUT:
			new AlertDialog.Builder(this)
				.setTitle("About PPP")
			.setMessage("Written by Richard Palmer <richard@tiro.org.uk> using:\n\n   Parliament RSS Feeds\n\n   ActionBarSherlock by Jake Wharton\n\n    CommonsGuy components by Mark Murphy\n\nVersion: 0.3-plebgate-pt1")
				.setNeutralButton("Aye", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg,
int sumthing) {
					}
				})
				.show();
//				return(true);
		}
		*/

		xaction.commit();

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
	public boolean onNavigationItemSelected(int itemPos, long itemId) {
		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction xaction = fragMgr.beginTransaction();

		Fragment frag = (Fragment)fragMgr.findFragmentById(R.id.content);

		if(frag != null) {
			xaction.detach(frag);
		}

		Fragment preInitFragment = (Fragment)fragMgr.findFragmentByTag(parlNames[itemPos]);
       		Log.v("PPP", "Attaching fragment in (first) pos - " + itemPos);

		if(preInitFragment != null) {
   			Log.v("PPP", "Adding existing fragment");
			xaction.attach(preInitFragment);
		} else if(itemPos == 0) {
		  xaction.add(R.id.content, frags_parl.get(0), "Debates");
		} else if(itemPos == 1) {
		  xaction.add(R.id.content, frags_parl.get(1), "Bills");
		} else if(itemPos == 2) {
		  xaction.add(R.id.content, frags_parl.get(2), "Acts");
		}

       		Log.v("PPP", "Attaching fragment in pos - " + itemPos);
		// xaction.attach(pFrag);

		xaction.commit();

		lastPos = itemPos;

		return true;
	}

	public void viewDebates(View v) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment frag = (Fragment)fragmentManager.findFragmentById(R.id.content);
		if(frag != null) {
   			Log.v("PPP", "Removing existing fragment");
			fragmentTransaction.detach(frag);
		}
   		Log.v("PPP", "Viewing scheduled debates");
		Fragment debates = (Fragment)fragmentManager.findFragmentByTag("Debates");
		fragmentTransaction.attach(debates);
//		fragmentTransaction.add(R.id.content, debates, "Debates");
		fragmentTransaction.commit();
	}

	public void viewReadableDebates(View v) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment readable = (Fragment)fragmentManager.findFragmentByTag("Reader");
		Fragment frag = (Fragment)fragmentManager.findFragmentById(R.id.content);
		if(frag != null) {
   			Log.v("PPP", "Removing existing fragment");
			fragmentTransaction.detach(frag);
		}
		if(readable != null) {
			fragmentTransaction.attach(readable);
		} else {
			fragmentTransaction.replace(R.id.content, frags_app.get(1), "Reader");
		}
		fragmentTransaction.commit();
	}

	public void viewReadableNewDebates(View v) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment frag = (Fragment)fragmentManager.findFragmentById(R.id.content);
		if(frag != null) {
   			Log.v("PPP", "Removing existing fragment");
			fragmentTransaction.detach(frag);
		}
		fragmentTransaction.replace(R.id.content, new ReaderNewFragment());
		fragmentTransaction.commit();

	}

	public void viewLatestDebates(View v) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment planner = (Fragment)fragmentManager.findFragmentByTag("Planner");
		Fragment frag = (Fragment)fragmentManager.findFragmentById(R.id.content);
		if(frag != null) {
   			Log.v("PPP", "Removing existing fragment");
			fragmentTransaction.detach(frag);
		}
		if(planner != null) {
			fragmentTransaction.attach(planner);
		} else {
			fragmentTransaction.replace(R.id.content, frags_app.get(2), "Planner");
		}
   		Log.v("PPP", "Viewing new highlighted debates");
		fragmentTransaction.commit();
	}


	public void viewNewLatestDebates(View v) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment frag = (Fragment)fragmentManager.findFragmentById(R.id.content);
		if(frag != null) {
   			Log.v("PPP", "Removing existing fragment");
			fragmentTransaction.detach(frag);
		}
		fragmentTransaction.replace(R.id.content, new PPPLobbyLatest());
		fragmentTransaction.commit();
	}

        @Override
        public void onSaveInstanceState(Bundle outState) {
                super.onSaveInstanceState(outState);
		outState.putInt("currentTab", getSupportActionBar().getSelectedNavigationIndex());
	}

   private class PPPTabListener implements TabListener {
   	private PPP pAct;
	private Fragment pFrag;
	private boolean firstDisplay = true;

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
			firstDisplay = false;
		} else {
		}

	}

	public void onDestroy() {
		//((PPPApp)getApplication()).dbadaptor.close();
       		Log.v("PPP", "PPP - onDestroy()");
		/*
		FragmentManager fragMgr = getSupportFragmentManager();
		FragmentTransaction xaction = fragMgr.beginTransaction();
		Fragment frag = (Fragment)fragMgr.findFragmentById(R.id.content);

		if(frag != null) {
			xaction.detach(frag);
		}
		xaction.commit();
		*/
	}
   	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction transaction) {

	}
  }

}


