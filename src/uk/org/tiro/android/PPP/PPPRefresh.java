package uk.org.tiro.android.PPP;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.List;
import java.util.ArrayList;
import android.util.Log;


public class PPPRefresh extends WakefulIntentService {
	private DBAdaptor dbadaptor;
	private TriggersDBHelper triggershelper;
	private PoliticsFeedDBHelper feedhelper;

	public PPPRefresh() {
		super("PPPRefresh");
	}

	@Override
	protected void doWakefulWork(Intent intent) {

		// Download
		// Parse
		// Stick in database
		// scan for matching text
		// add to feed matches
		// notify matches marked as important

	dbadaptor = new DBAdaptor(this).open();
	triggershelper = new TriggersDBHelper(this).open();
	feedhelper = new PoliticsFeedDBHelper(this).open();


	// Do first as first thing on display

	commonsupdate();
	lordsupdate();

	com_selectupdate();
	lords_selectupdate();

	com_westupdate();
	com_genupdate();

	lords_grandupdate();

	billsupdate(); // Retrieve bills
	actsupdate();
	// siupdate(); // Stat. Instruments not my flatmate


	/* TODO - using theyworkforyou API

	hansardsupdate(); 

	*/

	feedhelper.close();
	triggershelper.close();
	dbadaptor.close();
	}

	protected void billsupdate() {
    		List<Bill> bills;
		List<String> triggers = new ArrayList<String>();
		BillsDBHelper billshelper = new BillsDBHelper(this).open();

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
	
	  actshelper.markActsOld();

	  actshelper.close();
     }

     protected void commonsupdate() {
    	List<CommonsDebate> debates;
	CommonsDBHelper commonshelper = new CommonsDBHelper(this).open();

	commonshelper.close();

	}

     protected void com_selectupdate() {
    	List<CommonsDebate> debates;
	CommonsDBHelper commonshelper = new CommonsDBHelper(this).open();

	// Get select committees
	// Get westminster hall
	
	commonshelper.close();
    }

    protected void lordsupdate() {
    	List<LordsDebate> debates;
	LordsDBHelper lordshelper = new LordsDBHelper(this).open();
	
		// TODO sleect & grant committees

	lordshelper.close();
   }

    protected void lords_selectupdate() {
    	List<LordsDebate> debates;
	LordsDBHelper lordshelper = new LordsDBHelper(this).open();

	lordshelper.close();
   }

     protected void com_westupdate() {
    	List<CommonsDebate> debates;
	CommonsDBHelper commonshelper = new CommonsDBHelper(this).open();

	commonshelper.close();
	}

     protected void com_genupdate() {
    	List<CommonsDebate> debates;
	CommonsDBHelper commonshelper = new CommonsDBHelper(this).open();

	commonshelper.close();
	}

    protected void lords_grandupdate() {
    	List<LordsDebate> debates;
	LordsDBHelper lordshelper = new LordsDBHelper(this).open();

	lordshelper.close();
   }
}
