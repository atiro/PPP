package uk.org.tiro.android.PPP;

import android.content.Intent;
import android.app.PendingIntent;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.text.format.Time;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.List;
import java.util.ArrayList;
import android.util.Log;


public class PPPUpdate extends WakefulIntentService {
	private DBAdaptor dbadaptor;
	private TriggersDBHelper triggershelper;
	private PoliticsFeedDBHelper feedhelper;
	private int pppId = 1;

	private int alerts = 0;
	private int readable_debates = 0;
	private int notify_debates = 0;

	private Integer debates_visible;
	private Integer old_debates_visible;

	private boolean notify_today = false;
	private boolean notify_readable = false;
	private boolean notify_new = false;

	public PPPUpdate() {
		super("PPPUpdate");
	}

	@Override
	protected void doWakefulWork(Intent intent) {

		// Download
		// Parse
		// Stick in database
		// scan for matching text
		// add to feed matches
		// notify matches marked as important
	// Hack - for some reason we are being called each time app is started
	// (presumably for code setting alarm). Try to ensure we only run once
	// a day. Will stop forced updates working for the moment

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

	long lastrun_time = prefs.getLong("lastRun", 0);

        Time now = new Time();
	now.setToNow();
	long cur_time = now.toMillis(true);
	long time_diff = cur_time - lastrun_time;

	/* 82800000 us 23 hours in milliseconds */
	if(lastrun_time > 0 && (time_diff < 82800000)) {
	   /* Must have run in last 23 hours. Ignore call */
	   Log.v("PPP", "Ignoring PPPUpdate call, last run within 23 hours");
	   return;
	}

	Log.v("PPP", "Updating PPP Feeds (last ran: " + time_diff + " )");

	debates_visible = Integer.parseInt(prefs.getString("debates_visible", "2"));
	old_debates_visible = Integer.parseInt(prefs.getString("old_debates_visible", "2"));

	debates_visible *= 7;	// Turn weeks into number of days
	old_debates_visible *= 7;	// Turn weeks into number of days

	dbadaptor = new DBAdaptor(this).open();
	triggershelper = new TriggersDBHelper(this).open();
	feedhelper = new PoliticsFeedDBHelper(this).open();

	feedhelper.markFeedStale();

	// Do first as first thing on display

	// If no network connection can't report anything as not sure if running
	// from app or scheduled run. Need to have a "Last Updated" notice
	// somewhere

	// NB These two mark all existing debates in their House as old, needed
	// for notifications to work.

	com_selectupdate();
	lords_selectupdate();

	com_westupdate();
	com_genupdate();

	lords_grandupdate();

	// Do next two last so we catch all commons/lords debates with
	// triggers, regardless of which chamber it was in.

	commonsupdate();
	lordsupdate();

	billsupdate(); // Retrieve bills
	actsupdate();
	// siupdate(); // Stat. Instruments not flatmate


	/* TODO - using theyworkforyou API

	hansardsupdate(); 

	*/

	Intent pppIntent = new Intent(this, PPP.class);
	pppIntent.putExtra("notification_start", 1);

	PendingIntent ppppIntent = PendingIntent.getActivity(this, 0, pppIntent, 0);

	NotificationManager mNotificationManager =
		(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	mNotificationManager.cancelAll(); // Remove any hanging around from before
	Log.v("PPP", "Alerts - " + alerts );

	notify_today = prefs.getBoolean("pref_key_note_newdebates", false);

	if((notify_today == true) && (alerts > 0)) {

		NotificationCompat.Builder mBuilder =
 		       new NotificationCompat.Builder(this)
 		       .setSmallIcon(R.drawable.ppp_status_icon)
 		       .setContentTitle("PPP")
		       .setContentIntent(ppppIntent)
		       .setAutoCancel(true)
 		       .setContentText(alerts + " new scheduled debate matching your interests.");
		       
		       /*

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
		stackBuilder.addParentStack(PPP.class);
		stackBuilder.addNextIntent(pppIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		*/
		mNotificationManager.notify(pppId, mBuilder.getNotification());
      }

      pppId += 1;
      // Retrieve debates from yesterday that are available to read.

      pppIntent.putExtra("notification_start", 2);

      readable_debates = feedhelper.getReadableCount(House.BOTH, 2);

      Log.v("PPP", "Redeable Debates - " + readable_debates );

      notify_readable = prefs.getBoolean("pref_key_note_readable", false);

      if((notify_readable == true) && (readable_debates > 0)) {
		NotificationCompat.Builder mBuilder =
 		       new NotificationCompat.Builder(this)
 		       .setSmallIcon(R.drawable.ppp_status_icon)
 		       .setContentTitle("PPP")
		       .setContentIntent(ppppIntent)
		       .setAutoCancel(true)
 		       .setContentText(readable_debates + " debates now available to read.");

		mNotificationManager.notify(pppId, mBuilder.getNotification());
	}
      	
	// Finally, notify user about any debates today they've marked as
	// being notified about (TODO at moment all today's debates in planner
	// are retrieved)

        pppIntent.putExtra("notification_start", 3);
        pppId += 1;
	notify_debates = feedhelper.getPoliticsTodayCount();

        Log.v("PPP", "Notify Debates - " + notify_debates );

        notify_today = prefs.getBoolean("pref_key_note_today", false);

        if((notify_today == true) && (notify_debates > 0)) {
		NotificationCompat.Builder mBuilder =
 		       new NotificationCompat.Builder(this)
 		       .setSmallIcon(R.drawable.ppp_status_icon)
 		       .setContentTitle("PPP")
		       .setContentIntent(ppppIntent)
		       .setAutoCancel(true)
 		       .setContentText(notify_debates + " matching debates taking place today.");

		mNotificationManager.notify(pppId, mBuilder.getNotification());
	}
      	
	// Finally, notify user about any debates today they've marked as

	feedhelper.close();
	triggershelper.close();
	dbadaptor.close();
		
	// Update time

	SharedPreferences.Editor edit = prefs.edit();
	now = new Time();
	now.setToNow();
	edit.putLong("lastRun", now.toMillis(true));

	// If old_debates_visible different to debates_visible, update it now
	// (delayed to avoid losing debates if changed.

	if(old_debates_visible != debates_visible) {
		debates_visible /= 7; // Turn days to weeks
		edit.putString("old_debates_visible", debates_visible.toString());
	}

	edit.commit();

	Log.v("PPP", "Finished updating PPP Feeds");
	}

	protected void billsupdate() {
    		List<Bill> bills;
		List<String> triggers = new ArrayList<String>();
		BillsDBHelper billshelper = new BillsDBHelper(this).open();
		BillsFeedParser parser = new BillsFeedParser("http://services.parliament.uk/bills/AllBills.rss", "");
//		BillsFeedParser parser = new BillsFeedParser("http://tiro.org.uk/mobile/AllBills.rss", "");

		if(parser == null) {
			return;
		}

		bills = parser.parse();

		for(Bill bill : bills) {
			//Log.v("PPP", "Will try to insert bill: " + bill.getTitle() );
			billshelper.insert(bill);
				
		}


		triggers = triggershelper.bill_triggers();

		// quick check if any new bills, if not don't bother scanning
		// for each trigger.

		for(String trigger: triggers) {
 	                List<Integer> triggerbills = billshelper.getBillsFiltered(trigger, true);
        	        for(Integer bill: triggerbills) {
				// TODO put proper trigger_id in
                	        feedhelper.insert_bill(trigger, 0, bill, false);
                	}
		}
	
		// Avoid same bills being triggered next time round (unless 
		// something changed )

		billshelper.markBillsOld();

		billshelper.close();
	}

        protected void actsupdate() {
    	  List<Act> acts;
	  ActsDBHelper actshelper = new ActsDBHelper(this).open();
	 // ActsFeedParser parser = new ActsFeedParser("http://tiro.org.uk/mobile/legislation/acts.xml", "");
	  ActsFeedParser parser = new ActsFeedParser("http://www.legislation.gov.uk/new/ukpga/data.feed", "");
	
	  if(parser == null) {
		return;
	  }

	  acts = parser.parse();

	  for(Act act : acts) {
		//Log.v("PPP", "Inserting act: " + act.getTitle() );
		actshelper.insert(act);
	  }

	actshelper.markActsOld(); // No date for acts, can't pass period of days

	actshelper.close();
     }

     protected void commonsupdate() {
    	List<CommonsDebate> debates;
	List<String> triggers = new ArrayList<String>();
	CommonsDBHelper commonshelper = new CommonsDBHelper(this).open();
	CommonsFeedParser parser = new CommonsFeedParser("http://services.parliament.uk/calendar/commons_main_chamber.rss", "");

	  	if(parser == null) {
			return;
	  	}	

		// Get select committees
		// Get westminster hall
	
		debates = parser.parse();


		for(CommonsDebate debate : debates) {
			//Log.v("PPP", "Inserting commons debate: " + debate.getTitle() );
			commonshelper.insert(debate);
		}

                triggers = triggershelper.com_debates_triggers();

		// Check for any new debates matching triggers, if so add to feed
                for(String trigger: triggers) {
                        List<Integer> triggerdebates = commonshelper.getDebatesFiltered(trigger, true, true, debates_visible);
                        for(Integer debate: triggerdebates) {
                                // TODO put proper trigger_id in
                                feedhelper.insert_commons_debate(trigger, 0, debate, false);
				alerts += 1;
                        }
                }

		commonshelper.close();
	}

     protected void com_selectupdate() {
    	List<CommonsDebate> debates;
	CommonsDBHelper commonshelper = new CommonsDBHelper(this).open();
	// CommonsFeedParser parser = new CommonsFeedParser("http://tiro.org.uk/mobile/commons_select_committee.rss", "");
	CommonsFeedParser parser = new CommonsFeedParser("http://services.parliament.uk/calendar/commons_select_committee.rss", "");

	// This covers all debates (main, select, etc) in the commons
	commonshelper.markAllOld(old_debates_visible);

	if(parser == null) {
		return;
	}
	// Get select committees
	// Get westminster hall
	
	debates = parser.parse();

	for(CommonsDebate debate : debates) {
		//Log.v("PPP", "Inserting commons debate: " + debate.getTitle() );
		commonshelper.insert(debate);
	}


	commonshelper.close();
    }

    protected void lordsupdate() {
    	List<LordsDebate> debates;
	List<String> triggers = new ArrayList<String>();
	LordsDBHelper lordshelper = new LordsDBHelper(this).open();
	// LordsFeedParser parser = new LordsFeedParser("http://tiro.org.uk/mobile/lords_main_chamber.rss", "");
	LordsFeedParser parser = new LordsFeedParser("http://services.parliament.uk/calendar/lords_main_chamber.rss", "");
	

	if(parser == null) {
		return;
	}
		// TODO sleect & grant committees

	debates = parser.parse();

	for(LordsDebate debate : debates) {
		//Log.v("PPP", "Inserting lords debate: " + debate.getTitle() );
		lordshelper.insert(debate);
	}

	triggers = triggershelper.lords_debates_triggers();

	// Check for any new debates matching triggers, if so add to feed

	for(String trigger: triggers) {
		List<Integer> triggerdebates = lordshelper.getDebatesFiltered(trigger, true, true, debates_visible);
		for(Integer debate: triggerdebates) {
			// TODO put proper trigger_id in
			feedhelper.insert_lords_debate(trigger, 0, debate, false);
			alerts += 1;
		}
	}

	lordshelper.close();
   }

    protected void lords_selectupdate() {
    	List<LordsDebate> debates;
	LordsDBHelper lordshelper = new LordsDBHelper(this).open();
	// LordsFeedParser parser = new LordsFeedParser("http://tiro.org.uk/mobile/lords_select_committee.rss", "");
	LordsFeedParser parser = new LordsFeedParser("http://services.parliament.uk/calendar/lords_select_committee.rss", "");
	
	// This covers all debates (main, select, etc) in the lords
	lordshelper.markAllOld(old_debates_visible);

	if(parser == null) {
		return;
	}
		// TODO sleect & grant committees

	debates = parser.parse();

	for(LordsDebate debate : debates) {
		//Log.v("PPP", "Inserting lords committee: " + debate.getTitle() );
		lordshelper.insert(debate);
	}

	lordshelper.close();
   }

     protected void com_westupdate() {
    	List<CommonsDebate> debates;
	CommonsDBHelper commonshelper = new CommonsDBHelper(this).open();
	// CommonsFeedParser parser = new CommonsFeedParser("http://tiro.org.uk/mobile/commons_westminster_hall.rss", "");
	CommonsFeedParser parser = new CommonsFeedParser("http://services.parliament.uk/calendar/commons_westminster_hall.rss", "");

	if(parser == null) {
		return;
	}
		// Get select committees
		// Get westminster hall
	
		debates = parser.parse();

		for(CommonsDebate debate : debates) {
			//Log.v("PPP", "Inserting westminster hall debate: " + debate.getTitle() );
			commonshelper.insert(debate);
		}

		commonshelper.close();
	}

     protected void com_genupdate() {
    	List<CommonsDebate> debates;
	CommonsDBHelper commonshelper = new CommonsDBHelper(this).open();
	// CommonsFeedParser parser = new CommonsFeedParser("http://tiro.org.uk/mobile/commons_general_committee.rss", "");
	CommonsFeedParser parser = new CommonsFeedParser("http://services.parliament.uk/calendar/commons_general_committee.rss", "");

	if(parser == null) {
		return;
	}
		// Get select committees
		// Get westminster hall
	
		debates = parser.parse();

		for(CommonsDebate debate : debates) {
			//Log.v("PPP", "Inserting commons gen committee: " + debate.getTitle() );
			commonshelper.insert(debate);
		}

		commonshelper.close();
	}

    protected void lords_grandupdate() {
    	List<LordsDebate> debates;
	LordsDBHelper lordshelper = new LordsDBHelper(this).open();
	// LordsFeedParser parser = new LordsFeedParser("http://tiro.org.uk/mobile/lords_grand_committee.rss", "");
	LordsFeedParser parser = new LordsFeedParser("http://services.parliament.uk/calendar/lords_grand_committee.rss", "");
	
	if(parser == null) {
		return;
	}
		// TODO sleect & grant committees

	debates = parser.parse();

	for(LordsDebate debate : debates) {
		//Log.v("PPP", "Inserting grand committee: " + debate.getTitle() );
		lordshelper.insert(debate);
	}

	lordshelper.close();
   }
}
