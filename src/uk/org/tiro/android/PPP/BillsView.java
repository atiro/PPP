package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.app.Activity;

import android.database.Cursor;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;

import android.content.Context;

import android.util.Log;

public class BillsView extends Fragment {

	BillsDBHelper billhelper = null;

	Cursor model = null;
	Context cxt = null;
	String guid = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Integer bill_id = -1;

		super.onActivityCreated(savedInstanceState);

		//Log.v("PPP", "creating BillFragment");

		// date

		cxt = getActivity().getApplicationContext();

		billhelper = new BillsDBHelper(cxt).open();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		EditText title, description, type, stage;

		View v = inflater.inflate(R.layout.bill_fragment, container, false);
		title  = (EditText) v.findViewById(android.R.id.title);
		title.setText(billhelper.getTitle(model));

		description = (EditText) v.findViewById(R.id.description);
		description.setText(billhelper.getDescription(model));

		type = (EditText) v.findViewById(R.id.type);
		type.setText("TODO");

		stage = (EditText) v.findViewById(R.id.stage);
		stage.setText("TODO");

		//Log.v("PPP", "Creating BillFragment view");

		return v;
	}

	// TODO close db when destroyed

	public void setBill(String guid) {
		this.guid = guid;

		//Log.v("PPP", "Setting BillFragment model");

		model = billhelper.getBillByGUID(guid);
	}
}


