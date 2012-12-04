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

import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Context;
import android.database.Cursor;
import android.content.Intent;
import android.net.Uri;

import com.actionbarsherlock.app.SherlockListFragment;

import android.util.Log;


public class ActsListFragment extends SherlockListFragment {

	ActsDBHelper actshelper = null;
	ActsAdaptor adaptor = null;
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

		actshelper = new ActsDBHelper(cxt).open();
		model = actshelper.getAllActs();

		adaptor = new ActsAdaptor(model);

	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.acts_fragment, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		lv.setAdapter(adaptor);
		//Log.v("PPP", "Creating Acts view");

		filterText = (EditText) v.findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);

		return v;

	}


	@Override
	public void onListItemClick(ListView parent, View v, int position,
					long id) {
		String guid;
		final String title;
		final String summary;
		final String url;

		// Retrieve debate guid
		model.moveToPosition(position);

		title =  actshelper.getTitle(model);
		summary = actshelper.getSummary(model);
		url = actshelper.getURL(model);

		//Log.v("PPP", "Act URL = " + url);

		new AlertDialog.Builder(acxt)
			.setTitle(title)
			.setMessage(summary)
			.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dlg, int sumthing) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, summary);
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
;
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, "Share with..."));

			      }
                                })
				.setNeutralButton("View", new DialogInterface. OnClickListener() {
					public void onClick(DialogInterface dlg,int sumthing) {

                                         Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                         startActivity(sendIntent);
					}
				})
				.setNegativeButton("Cancel", null)
				.show();

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
	public void onDestroy() {
		super.onDestroy();
		if(actshelper != null) {
			actshelper.close();
		}
		if(filterText != null) {
			filterText.removeTextChangedListener(filterTextWatcher);
		}
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
		super(cxt, c);
		//Log.v("PPP", "Creating acts Adapter");
        }

	@Override
	public void bindView(View row, Context ctct, Cursor c) {
		ActsHolder holder=(ActsHolder)row.getTag();

		holder.populateFrom(c, actshelper);
	}

	@Override
	public View newView(Context ctxt, Cursor c, ViewGroup parent) {
		View row = LayoutInflater.from(getActivity()).inflate(R.layout.row_act, parent, false);
		ActsHolder holder = new ActsHolder(row);
		row.setTag(holder);
		return(row);
	}
    }
}



