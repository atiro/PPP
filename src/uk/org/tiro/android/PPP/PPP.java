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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

//        setContentView(R.layout.main);

//	WakefulIntentService.scheduleAlarms(new PPPAlarm(),
//					this, false);

	// And force it to run now as well

	WakefulIntentService.sendWakefulWork(this, PPPUpdate.class);

	//PoliticsFeedFragment feed = (PoliticsFeedFragment)getSupportFragmentManager().findFragmentById(R.id.feed);

	// TODO start politicsfeed or calendar basedon settings

	dbadaptor = new DBAdaptor(this).open();
	dbadaptor.close();

	Intent i = new Intent(this, Debates.class);
	Bundle b = new Bundle();
	b.putInt("house", House.COMMONS.ordinal());
	b.putInt("chamber", Chamber.MAIN.ordinal());
	i.putExtras(b);
	startActivity(i);
    }



/*
    class NewsAdapter extends ArrayAdapter<String> {
    	NewsAdapter() {
		super(PPP.this, R.layout.row_alert_count, R.id.label, news);
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


