package uk.org.tiro.android.PPP;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

import android.widget.CursorAdapter;

import android.content.Context;
import android.database.Cursor;

import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;

public class BillsListFragment extends SherlockListFragment {

	BillsDBHelper billshelper = null;
	BillsAdaptor adaptor = null;
	Cursor model = null;
	Context cxt;
	Context acxt;
	ListView lv;

	private EditText filterText = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cxt = getActivity().getApplicationContext();
		acxt = getActivity();

		billshelper = new BillsDBHelper(cxt).open();
		model = billshelper.getAllBills(House.COMMONS);

		adaptor = new BillsAdaptor(model);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

//		setTheme(R.style.Theme_Sherlock);
		View v = inflater.inflate(R.layout.bills_fragment, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		lv.setAdapter(adaptor);
		Log.v("PPP", "Creating Bills view");

		filterText = (EditText) v.findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);

		return v;

	}

	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}


		public void onTextChanged(CharSequence s, int start, int before, int count) {

			adaptor.getFilter().filter(s);
			model = billshelper.getAllBillsFiltered(s.toString());
			adaptor = new BillsAdaptor(model);
			setListAdapter(adaptor);
		}
	};


	public void onListItemClick(ListView parent, View v, int position,
					long id) {
		String msg = "If bill identifiers were used in related debates in Parliament, I could link bills with debates and you could choose to follow them here.\n\nBut at the moment they aren't; so I can't; so you can't.\n\nSorry.";

		new AlertDialog.Builder(acxt)
			.setMessage(msg)
			.setPositiveButton("Cancel", null)
			.show();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		billshelper.close();
		filterText.removeTextChangedListener(filterTextWatcher);
	}
	

    static class BillsHolder {
    	private TextView title = null;
	private TextView description = null;
	private TextView stage = null;
	private View row = null;

	BillsHolder(View row) {
		this.row = row;

		title = (TextView)row.findViewById(R.id.bill_title);
		description = (TextView)row.findViewById(R.id.bill_description);
		stage = (TextView)row.findViewById(R.id.bill_stage);

	}

	void populateFrom(Cursor c, BillsDBHelper helper) {
		title.setText(helper.getTitle(c));
		description.setText(helper.getDescription(c));
		stage.setText(helper.getStage(c).toString());
	}

    }

    class BillsAdaptor extends CursorAdapter {

        BillsAdaptor(Cursor c) {
		super(cxt, c);
		//Log.v("PPP", "Creating bills Adapter");
        }

	@Override
	public void bindView(View row, Context ctct, Cursor c) {
		BillsHolder holder=(BillsHolder)row.getTag();

		holder.populateFrom(c, billshelper);
	}

	@Override
	public View newView(Context ctxt, Cursor c, ViewGroup parent) {
		View row = LayoutInflater.from(getActivity()).inflate(R.layout.row_bill, parent, false);
		BillsHolder holder = new BillsHolder(row);
		row.setTag(holder);
		return(row);
	}
    }
}



