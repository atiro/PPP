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
import android.widget.AdapterView.OnItemClickListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.view.Window;



import android.support.v4.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.content.Context;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import android.util.Log;

public class PPP extends FragmentActivity
{

    private DBAdaptor dbadaptor;
    private SharedPreferences prefs;
    private boolean first_run;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

	this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//        setContentView(R.layout.main);

//	WakefulIntentService.scheduleAlarms(new PPPAlarm(),
//					this, false);

	// And force it to run now as well

        Log.v("PPP", "Checking first run");
	prefs = PreferenceManager.getDefaultSharedPreferences(this);

        first_run = prefs.getBoolean("firstRun", true);

	if(first_run == true) {
		WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);

                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("firstRun", false);
                edit.commit();
	}

	//PoliticsFeedFragment feed = (PoliticsFeedFragment)getSupportFragmentManager().findFragmentById(R.id.feed);

	// TODO start politicsfeed or calendar basedon settings

	dbadaptor = new DBAdaptor(this).open();
	dbadaptor.close();

	Intent i = new Intent(this, Debates.class);
	// Only in API 11 and above
//	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	Bundle b = new Bundle();
	b.putInt("house", House.COMMONS.ordinal());
	b.putInt("chamber", Chamber.MAIN.ordinal());
	i.putExtras(b);
	startActivity(i);
	finish();
    }



/*
    class NewsAdapter extends ArrayAdapter<String> {
    	NewsAdapter() {
		super(PPP.this, R.layout.row_trigger_count, R.id.label, news);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);

		TextView count = (TextView)row.findViewById(R.id.count);

		count.setText("10");

		return(row);
	}
    }

*/


}


