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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockFragment;


public class DebateFragment extends SherlockFragment {

	CommonsDBHelper commonshelper = null;
	LordsDBHelper lordshelper = null;

	Cursor model = null;
	Context cxt = null;
	String guid = null;
	House house = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Integer debate_id = -1;

		super.onCreate(savedInstanceState);

		Log.v("PPP", "Creating DebateFragment");

		// date

		cxt = getActivity().getApplicationContext();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		TextView title, house, chamber, date;

		View v = inflater.inflate(R.layout.debate_fragment, container, false);
		title  = (TextView) v.findViewById(R.id.title);
		house = (TextView) v.findViewById(R.id.house);
		chamber = (TextView) v.findViewById(R.id.chamber);
		date = (TextView) v.findViewById(R.id.date);

		if(this.house == null) {
			title.setText("TODO");
		} else if(this.house == House.COMMONS) {
			title.setText(commonshelper.getTitle(model));
		} else {
			title.setText(lordshelper.getTitle(model));
		}

		house.setText("TODO");
		chamber.setText("TODO");
		date.setText("TODO");

		//Log.v("PPP", "Creating DebateFragment view");

		return v;
	}

	// TODO close db when destroyed

	public void setDebate(Integer house, String guid) {
		this.guid = guid;
		this.house = House.values()[house];

		if(this.house == House.COMMONS) {
			commonshelper = new CommonsDBHelper(cxt).open();
			model = commonshelper.getDebateByGUID(guid);
		} else {
			lordshelper = new LordsDBHelper(cxt).open();
			model = lordshelper.getDebateByGUID(guid);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(model != null) {
			model.close();
		}

		if(commonshelper != null) {
			commonshelper.close();
		}
		if(lordshelper != null) {
			lordshelper.close();
		}
	}

}


