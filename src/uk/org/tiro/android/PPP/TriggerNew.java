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
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.AdapterView.OnItemClickListener;

import android.content.Intent;
import android.database.Cursor;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import android.util.Log;

public class TriggerNew extends Activity {
	TriggersDBHelper triggershelper;
        private PoliticsFeedDBHelper feedhelper;
	private BillsDBHelper billshelper;
	private ActsDBHelper actshelper;
	private CommonsDBHelper commonshelper;
	private LordsDBHelper lordshelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.trigger_new);

		triggershelper = new TriggersDBHelper(this).open();
	}


	public void okButton(View v) {
		List<Integer> bills;
		List<Integer> acts;
		List<Integer> lords;
		List<Integer> commons;
		CheckBox chk_acts, chk_bills, chk_com;
		CheckBox chk_lords, chk_si, chk_dsi;
		CheckBox chk_notify, chk_case, chk_name;
		Long trigger_id = -1L;
		boolean ignore_case = true;
		boolean ignore_name = true;

		EditText match_field = (EditText) findViewById(R.id.new_trigger);
		String match = match_field.getText().toString();

		chk_bills = (CheckBox) findViewById(R.id.match_bills);
		chk_acts = (CheckBox) findViewById(R.id.match_acts);
		chk_com = (CheckBox) findViewById(R.id.match_commons);
		chk_lords = (CheckBox) findViewById(R.id.match_lords);

		// Add trigger to database
		chk_notify = (CheckBox) findViewById(R.id.notify);
		chk_case = (CheckBox) findViewById(R.id.ignorecase);
		chk_name = (CheckBox) findViewById(R.id.ignorename);

		trigger_id = triggershelper.insert(match, chk_acts.isChecked(), chk_bills.isChecked(), chk_com.isChecked(), chk_lords.isChecked(), chk_notify.isChecked(), chk_case.isChecked(), chk_name.isChecked() );

		//Log.v("PPP", "Added trigger with id: " + trigger_id);
		// Run fresh scan of database
		// Check which ticked and run scan

		feedhelper = new PoliticsFeedDBHelper(this).open();

		if(chk_case.isChecked() == false) {
		   ignore_case = false;
		}
		
		if(chk_name.isChecked() == false) {
		   ignore_name = false;
		}
		
		if(chk_bills.isChecked()) {

		  billshelper = new BillsDBHelper(this).open();

		  bills = billshelper.getBillsFiltered(match, ignore_case);

		  for(Integer bill: bills) {
			//Log.v("PPP", "Found matching bill for trigger: " + match);
			feedhelper.insert_bill(match, trigger_id, bill, true);
			triggershelper.updateLast(trigger_id);
		  }

		  billshelper.close();

		}


		if(chk_acts.isChecked()) {

		  actshelper = new ActsDBHelper(this).open();

		  acts = actshelper.getActsFiltered(match, ignore_case);

		  for(Integer act: acts) {
			//Log.v("PPP", "Found matching act for trigger: " + match);
			feedhelper.insert_act(match, trigger_id, act, true);
			triggershelper.updateLast(trigger_id);
		  }

		  actshelper.close();

		}


		if(chk_com.isChecked()) {
	  	  commonshelper = new CommonsDBHelper(this).open();

		  commons = commonshelper.getDebatesFiltered(match, ignore_case, ignore_name);

		  for(Integer common: commons) {
			//Log.v("PPP", "Found matching debate for trigger: " + match);
			feedhelper.insert_commons_debate(match, trigger_id, common, true);
			triggershelper.updateLast(trigger_id);
		  }

		  commonshelper.close();

		}

		
		if(chk_lords.isChecked()) {
		  lordshelper = new LordsDBHelper(this).open();

		  lords = lordshelper.getDebatesFiltered(match, ignore_case, ignore_name);

		  for(Integer lord: lords) {
			//Log.v("PPP", "Found matching debate for trigger: " + match);
			feedhelper.insert_lords_debate(match, trigger_id, lord, true);
			triggershelper.updateLast(trigger_id);
		  }

		  lordshelper.close();

		}

		feedhelper.close();
		triggershelper.close();

		setResult(RESULT_OK);
		finish();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(triggershelper != null) {
			triggershelper.close();
		}
	}

}


