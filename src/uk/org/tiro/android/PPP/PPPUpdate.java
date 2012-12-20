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

	Log.v("PPP", "Updating PPP Feeds");

	dbadaptor = new DBAdaptor(this).open();
	triggershelper = new TriggersDBHelper(this).open();
	feedhelper = new PoliticsFeedDBHelper(this).open();

	feedhelper.markFeedOld();

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
	PendingIntent ppppIntent = PendingIntent.getActivity(this, 0, pppIntent, 0);

	Log.v("PPP", "Alerts - " + alerts );
	if(alerts > 0) {

		NotificationCompat.Builder mBuilder =
 		       new NotificationCompat.Builder(this)
 		       .setSmallIcon(R.drawable.ppp_status_icon)
 		       .setContentTitle("PPP")
		       .setContentIntent(ppppIntent)
 		       .setContentText(alerts + " new scheduled debate matching your interests.");
		       
		       /*

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
		stackBuilder.addParentStack(PPP.class);
		stackBuilder.addNextIntent(pppIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		*/
		NotificationManager mNotificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(pppId, mBuilder.getNotification());
      }

      pppId += 1;
      // Retrieve debates from yesterday that are available to read.
      readable_debates = feedhelper.getReadyCount();

      Log.v("PPP", "Redeable Debates - " + readable_debates );

      if(readable_debates > 0) {
		NotificationCompat.Builder mBuilder =
 		       new NotificationCompat.Builder(this)
 		       .setSmallIcon(R.drawable.ppp_status_icon)
 		       .setContentTitle("PPP")
		       .setContentIntent(ppppIntent)
 		       .setContentText(readable_debates + " debates now available to read.");

		NotificationManager mNotificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(pppId, mBuilder.getNotification());
	}
      	
	// Finally, notify user about any debates today they've marked as
	// being notified about (TODO at moment all today's debates in planner
	// are retrieved)

        pppId += 1;
	notify_debates = feedhelper.getPoliticsTodayCount();

        Log.v("PPP", "Notify Debates - " + notify_debates );

        if(notify_debates > 0) {
		NotificationCompat.Builder mBuilder =
 		       new NotificationCompat.Builder(this)
 		       .setSmallIcon(R.drawable.ppp_status_icon)
 		       .setContentTitle("PPP")
		       .setContentIntent(ppppIntent)
 		       .setContentText(notify_debates + " matching debates taking place today.");

		NotificationManager mNotificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(pppId, mBuilder.getNotification());
	}
      	
	// Finally, notify user about any debates today they've marked as

	feedhelper.close();
	triggershelper.close();
	dbadaptor.close();
		
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

	actshelper.markActsOld();

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

                // quick check if any new bills, if not don't bother scanning
                // for each trigger.

                for(String trigger: triggers) {
                        List<Integer> triggerdebates = commonshelper.getDebatesFiltered(trigger, true, true);
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

	commonshelper.markAllOld();

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
	
	lordshelper.markAllOld();

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

	// quick check if any new bills, if not don't bother scanning
	// for each trigger.

	for(String trigger: triggers) {
		List<Integer> triggerdebates = lordshelper.getDebatesFiltered(trigger, true, true);
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
