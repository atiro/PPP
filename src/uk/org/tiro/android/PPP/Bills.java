package uk.org.tiro.android.PPP;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

import android.widget.CursorAdapter;

import android.content.Context;
import android.database.Cursor;

import android.util.Log;


public class Bills extends ListActivity {

	BillsDBHelper billshelper = null;
	BillsAdaptor adaptor = null;
	Cursor model = null;

	private EditText filterText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bills);

		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);

		billshelper = new BillsDBHelper(this).open();
		model = billshelper.getAllBills(House.COMMONS);
		startManagingCursor(model);

		adaptor = new BillsAdaptor(model);

		setListAdapter(adaptor);
	
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		super(Bills.this, c);
		Log.v("PPP", "Creating Bills Adapter");
        }

	@Override
	public void bindView(View row, Context ctct, Cursor c) {
		BillsHolder holder=(BillsHolder)row.getTag();

		holder.populateFrom(c, billshelper);
	}

	@Override
	public View newView(Context ctxt, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = getLayoutInflater();
		View row = inflater.inflate(R.layout.row_bill, parent, false);
		BillsHolder holder = new BillsHolder(row);
		row.setTag(holder);
		return(row);
	}
    }
}



