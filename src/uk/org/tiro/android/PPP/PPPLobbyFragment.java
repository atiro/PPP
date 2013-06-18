package uk.org.tiro.android.PPP;

import android.os.Bundle;
import android.os.Build;

import android.content.Context;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormatSymbols;
import java.util.Locale;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.Gallery;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import android.util.Log;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockFragment;

public class PPPLobbyFragment extends SherlockFragment {

	Context c = null;
	CommonsDBHelper commonshelper = null;
	LordsDBHelper lordshelper = null;
	PoliticsFeedDBHelper feedhelper = null;
	SharedPreferences prefs;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				 Bundle savedInstanceState) {

		TextView tv_debates_commons, tv_debates_lords;
		Integer debates_commons, debates_lords;
		TextView tv_relevant_commons, tv_relevant_lords;
		Integer relevant_commons, relevant_lords;
		TextView tv_relevant_new_commons, tv_relevant_new_lords;
		Integer relevant_new_commons, relevant_new_lords;
		TextView tv_readable_commons, tv_readable_lords;
		Integer readable_commons, readable_lords;
		TextView tv_readable_new_commons, tv_readable_new_lords;
		Integer readable_new_commons, readable_new_lords;
		Integer debates_visible;

		View v = inflater.inflate(R.layout.ppp_lobby_fragment, container, false);
		Context c = getActivity().getApplicationContext();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(c);

		debates_visible = Integer.parseInt(prefs.getString("debates_visible", "2"));
		debates_visible *= 7;
		
		commonshelper = new CommonsDBHelper(c).open();
		debates_commons = commonshelper.countFutureDebates(debates_visible);
		commonshelper.close();

		tv_debates_commons = (TextView) v.findViewById(R.id.upcoming_total_commons_val);
		tv_debates_commons.setText(debates_commons.toString());

		lordshelper = new LordsDBHelper(c).open();
		debates_lords = lordshelper.countFutureDebates(debates_visible);
		lordshelper.close();

		tv_debates_lords = (TextView) v.findViewById(R.id.upcoming_total_lords_val);
		tv_debates_lords.setText(debates_lords.toString());

		feedhelper = new PoliticsFeedDBHelper(c).open();

		relevant_commons = feedhelper.getPoliticsFeedCount(House.COMMONS, debates_visible, false);
		tv_relevant_commons = (TextView) v.findViewById(R.id.relevant_commons_val);
		tv_relevant_commons.setText(relevant_commons.toString());

		relevant_lords = feedhelper.getPoliticsFeedCount(House.LORDS, debates_visible, false);

		tv_relevant_lords = (TextView) v.findViewById(R.id.relevant_lords_val);
		tv_relevant_lords.setText(relevant_lords.toString());

		relevant_new_commons = feedhelper.getPoliticsFeedCount(House.COMMONS, debates_visible, true);
		tv_relevant_new_commons = (TextView) v.findViewById(R.id.relevant_new_commons_val);
		tv_relevant_new_commons.setText(relevant_new_commons.toString());

		relevant_new_lords = feedhelper.getPoliticsFeedCount(House.LORDS, debates_visible, true);

		tv_relevant_new_lords = (TextView) v.findViewById(R.id.relevant_new_lords_val);
		tv_relevant_new_lords.setText(relevant_new_lords.toString());

		readable_lords = feedhelper.getReadableCount(House.LORDS, 16);
		tv_readable_lords = (TextView) v.findViewById(R.id.readable_lords_val);
		tv_readable_lords.setText(readable_lords.toString());

		readable_commons = feedhelper.getReadableCount(House.LORDS, 16);
		tv_readable_commons = (TextView) v.findViewById(R.id.readable_commons_val);
		tv_readable_commons.setText(readable_commons.toString());

		readable_new_lords = feedhelper.getReadableCount(House.LORDS, 1);
		tv_readable_new_lords = (TextView) v.findViewById(R.id.readable_new_lords_val);
		tv_readable_new_lords.setText(readable_new_lords.toString());

		readable_new_commons = feedhelper.getReadableCount(House.LORDS, 1);
		tv_readable_new_commons = (TextView) v.findViewById(R.id.readable_new_commons_val);
		tv_readable_new_commons.setText(readable_new_commons.toString());

		feedhelper.close();



		// TODO - move to correct position ?

		Log.v("PPP", "Creating PPPLobbyFragment");

		return v;
	}

		
}
