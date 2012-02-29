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


public class Acts extends ListActivity {

	ActsDBHelper actshelper = null;
	ActsAdaptor adaptor = null;
	Cursor model = null;

	private EditText filterText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.acts);

		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);

		actshelper = new ActsDBHelper(this).open();
		model = actshelper.getAllActs();
		startManagingCursor(model);

		adaptor = new ActsAdaptor(model);

		setListAdapter(adaptor);
	
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}


		public void onTextChanged(CharSequence s, int start, int before, int count) {

			adaptor.getFilter().filter(s);
			model = actshelper.getAllActsFiltered(s.toString());
			adaptor = new ActsAdaptor(model);
			setListAdapter(adaptor);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		filterText.removeTextChangedListener(filterTextWatcher);
	}
	

    static class ActsHolder {
    	private TextView title = null;
	private TextView summary = null;
	private TextView stage = null;
	private View row = null;

	ActsHolder(View row) {
		this.row = row;

		title = (TextView)row.findViewById(R.id.act_title);
		summary = (TextView)row.findViewById(R.id.act_summary);
//		stage = (TextView)row.findViewById(R.id.act_stage);

	}

	void populateFrom(Cursor c, ActsDBHelper helper) {
		title.setText(helper.getTitle(c));
		summary.setText(helper.getSummary(c));
//		date.setText(helper.getDate(c).toString());
	}

    }

    class ActsAdaptor extends CursorAdapter {

        ActsAdaptor(Cursor c) {
		super(Acts.this, c);
		Log.v("PPP", "Creating acts Adapter");
        }

	@Override
	public void bindView(View row, Context ctct, Cursor c) {
		ActsHolder holder=(ActsHolder)row.getTag();

		holder.populateFrom(c, actshelper);
	}

	@Override
	public View newView(Context ctxt, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = getLayoutInflater();
		View row = inflater.inflate(R.layout.row_act, parent, false);
		ActsHolder holder = new ActsHolder(row);
		row.setTag(holder);
		return(row);
	}
    }
}



