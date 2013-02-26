package uk.org.tiro.android.PPP;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;

import android.widget.CursorAdapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;

public class TriggersListFragment extends SherlockListFragment {

    private static final String[] triggers = {"Debates", "Committees", "Bills", "Acts", "Stat. Inst."};

	TriggersDBHelper triggershelper = null;
	TriggersAdaptor adaptor = null;
	Cursor model = null;
	Context cxt, acxt;
	ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO Retrieve type of trigger from bundle 

		cxt = getActivity().getApplicationContext();
		acxt = getActivity();

		triggershelper = new TriggersDBHelper(cxt).open();

		model = triggershelper.getTriggers();
		// TODO add spinner to chooose trigger type
		//	model = triggershelper.getActTriggers();

		adaptor = new TriggersAdaptor(model);
	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.triggers_fragment, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		lv.setAdapter(adaptor);

		Button addtrigger = (Button)v.findViewById(R.id.add_trigger);
		addtrigger.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
		                Intent i = new Intent(cxt, TriggerNew.class);

		                startActivity(i);
			}
		});

		//Log.v("PPP", "Creating Triggers Fragment view");
		return v;
	}

	public void add_trigger(View v) {
		Intent i = new Intent(cxt, TriggerNew.class);

		startActivity(i);
	}

	public void onListItemClick(ListView parent, View v, int position,
					long id) {
		final String trigger;

		// Retrieve debate guid
		model.moveToPosition(position);

		trigger = triggershelper.getMatch(model);

		new AlertDialog.Builder(acxt)
			.setTitle(trigger)
			.setPositiveButton("Remove", new DialogInterface.
OnClickListener() {
				public void onClick(DialogInterface dlg,
 int sumthing) {
 						triggershelper.remove(model);
						model.requery();
						// TODO memory leak ?
						adaptor = new TriggersAdaptor(model);

						Toast.makeText(acxt, "Removed trigger '" + trigger + "'", Toast.LENGTH_SHORT).show();
					}
				})
			.setNeutralButton("Remove & Clear", new DialogInterface.
OnClickListener() {
				public void onClick(DialogInterface dlg,
 int sumthing) {
 						PoliticsFeedDBHelper feedhelper = new PoliticsFeedDBHelper(cxt).open();
						feedhelper.clearTrigger(triggershelper.getID(model));
						feedhelper.close();
 						triggershelper.remove(model);
						model.requery();
						// TODO memory leak ?
						adaptor = new TriggersAdaptor(model);

						Toast.makeText(acxt, "Removed and cleared trigger '" + trigger + "'", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("Cancel", null)
				.show();

	}


	@Override
	public void onResume() {
		super.onResume();
		model.requery();
		// TODO memory leak ?
		adaptor = new TriggersAdaptor(model);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(model != null) { model.close(); }
		triggershelper.close();
	}
	

    static class TriggersHolder {
    	private TextView match = null;
	private TextView types = null;
	private TextView last = null;
	private View row = null;

	TriggersHolder(View row) {
		this.row = row;
		match = (TextView)row.findViewById(R.id.match);
		types = (TextView)row.findViewById(R.id.types);
		last = (TextView)row.findViewById(R.id.last);
	}

	void populateFrom(Cursor c, TriggersDBHelper helper) {
		match.setText(helper.getMatch(c));
		types.setText(helper.getTypes(c));
		last.setText(helper.getLast(c));
	}
    }

    class TriggersAdaptor extends CursorAdapter {

        TriggersAdaptor(Cursor c) {
		super(cxt, c);
		//Log.v("PPP", "Creating Triggers Adapter");
        }

	@Override
	public void bindView(View row, Context ctct, Cursor c) {
		TriggersHolder holder=(TriggersHolder)row.getTag();
		holder.populateFrom(c, triggershelper);
	}

	@Override
	public View newView(Context ctxt, Cursor c, ViewGroup parent) {
		View row = LayoutInflater.from(getActivity()).inflate(R.layout.row_trigger,parent, false);
		TriggersHolder holder = new TriggersHolder(row);
		row.setTag(holder);
		return(row);
	}
    }
}



