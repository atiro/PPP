package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.app.Activity;

import android.database.Cursor;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.Gallery;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;

import android.graphics.Color;

import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;

public class ReaderFragment extends SherlockListFragment {

	ListView lv = null;

	PoliticsFeedDBHelper feedhelper = null;
	PoliticsFeedAdaptor feedadaptor = null;

	Cursor model = null;
	Context cxt = null;
	Context acxt = null;

	private static final int MENU_REFRESH = Menu.FIRST+1;
	private static final int MENU_DEBATES = Menu.FIRST+2;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//Log.v("PPP", "creating ReaderFragment");

		// date



	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		Log.v("PPP", "Creating ReaderFragment view");

		View v = inflater.inflate(R.layout.reader_fragment, container, false);
		//lv = (ListView) v.findViewById(android.R.id.list);

		cxt = getActivity().getApplicationContext();

		acxt = getActivity();

	//	feedhelper = new PoliticsFeedDBHelper(cxt).open();

			// TODO this will just show debates with Hansard to 
			//	read, scrapbook should cover more than this.
			//	Also, how much to show ? (could scroll down
			//	for past. But annoying if looking for old
			//	debate)
			//	Also, where do acts/bills fit in. Not in stream
			//	anymore as non-chronological, but need some
			//	way to fit them in.

	//	model = feedhelper.getReadable();

	//	feedadaptor = new PoliticsFeedAdaptor(cxt, model);

	//	setListAdapter(feedadaptor);
		// lv = (ListView) v.findViewById(android.R.id.list);
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v("PPP", "ReaderFragment - onDestroy");

		if(model != null) {
			model.close();
			model = null;
		}

		if(feedhelper != null) {
			feedhelper.close();
			feedhelper = null;
		}
	}


	@Override
	public void onPause() {
		super.onPause();

		Log.v("PPP", "ReaderFragment - onPause");

		if(model != null) {
			model.close();
			model = null;
		}

		if(feedhelper != null) {
			feedhelper.close();
			feedhelper = null;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v("PPP", "ReaderFragment - onResume");
		//WakefulIntentService.sendWakefulWork(cxt, PPPRefresh.class);
		if(feedhelper == null) {
			feedhelper = new PoliticsFeedDBHelper(cxt).open();
		}
		if(model == null) {
			model = feedhelper.getReadable(14);
		}

		// Mark all read=2 as read=1 (i.e. no longer new but not read)
		feedhelper.markFeedUnread();

		// TODO Memory leak from old adaptor ?
		feedadaptor = new PoliticsFeedAdaptor(cxt, model);

		setListAdapter(feedadaptor);
		//lv.invalidateViews();
	}

	public void refresh() {
		model.requery();
		// TODO Memory leak from old adaptor ?
		feedadaptor = new PoliticsFeedAdaptor(cxt, model);
		setListAdapter(feedadaptor);
	}

	public void onListItemClick(ListView parent, View v, int position,
					long id) {
		String guid;
		final String title;
		final String subject;
		final String url;
		final House house;
		final Date date;

		// TODO - Retrieve from Hansard/TWFY and display

		// Retrieve debate guid
		model.moveToPosition(position);

		title =  feedhelper.getHouse(model);
		subject = feedhelper.getMessage(model);
		house = feedhelper.getHouseRaw(model);
		url = feedhelper.getURL(model);
		date = feedhelper.getDateLong(model);


		  //if(chamber == Chamber.MAIN) {
		    new AlertDialog.Builder(acxt)
			.setTitle(title)
			.setMessage(subject)

                        .setNegativeButton("Cancel", null)
			.setNeutralButton("Read", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dlg, int sumthing) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.UK);
				String date_ymd = sdf.format(date);
				String debate_url;
						
				Intent i = new Intent(Intent.ACTION_VIEW);
				if(house == House.COMMONS) {
					debate_url = "http://www.publications.parliament.uk/pa/cm201213/cmhansrd/cm" + date_ymd + "/debindx/" + 
date_ymd + "-x.htm";
				} else {
					debate_url = "http://www.publications.parliament.uk/pa/ld201213/ldhansrd/index/" + date_ymd + ".html";
				}
				i.setData(Uri.parse(debate_url));
				Log.v("PPP", "Launching browser with URL:" + url);
				startActivity(i);
			}}).show();
		//}

	}


    static class PoliticsFeedHolder {
	private TextView type = null;
	private TextView trigger = null;
	private TextView msg = null;
	private TextView separator = null;
	private View row = null;
	private int latest = 0;

	PoliticsFeedHolder(View row) {
		this.row = row;

		msg = (TextView)row.findViewById(R.id.msg);
		type = (TextView)row.findViewById(R.id.type);
		trigger = (TextView)row.findViewById(R.id.trigger);
		separator = (TextView)row.findViewById(R.id.separator);
	}

	void populateFrom(Cursor c, PoliticsFeedDBHelper helper) {
		latest = helper.getNew(c);

		trigger.setText(helper.getMatch(c));

		if(latest > 0) {
			row.setBackgroundColor(Color.parseColor("#3F3F3F"));
		} else {
			row.setBackgroundColor(Color.parseColor("#000000"));
		}

		msg.setText(helper.getMessage(c));
		type.setText(helper.getTriggerType(c) + " " + helper.getHouse(c));
		if(helper.getHouseRaw(c) == House.COMMONS) {
			type.setTextColor(Color.parseColor("#62B367"));
		} else {
			type.setTextColor(Color.parseColor("#DA5254"));
		}
	}

    }

    class PoliticsFeedAdaptor extends CursorAdapter {

	private static final int STATE_UNKNOWN = 0;
	private static final int STATE_SECTIONED_CELL = 1;
	private static final int STATE_NORMAL_CELL = 2;

	private int[] mCellStates;
	private Integer[] mCellDates;
	private Integer mDate;

        PoliticsFeedAdaptor(Context context, Cursor c) {
		super(context, c);
		//Log.v("PPP", "Creating PoliticsFeed Adapter");
		mCellStates = c == null ? null : new int[c.getCount()];
		mCellDates = c == null ? null : new Integer[c.getCount()];

        }

	@Override
	public void bindView(View row, Context context, Cursor c) {
		PoliticsFeedHolder holder=(PoliticsFeedHolder)row.getTag();

		boolean needSep = false;

		int position = c.getPosition();
		//Log.v("PPP", "Binding view with position " + position);

		switch(mCellStates[position]) {
		
		  case STATE_SECTIONED_CELL:
		  	needSep = true;
			break;
	
		  case STATE_NORMAL_CELL:
		  	needSep = false;
			break;

		  case STATE_UNKNOWN:
		  default:

		  	if(position == 0) {
				needSep = true;
				mDate = feedhelper.getDate(c);
				//Log.v("PPP", "First seperator with date " + mDate.toString() );
			} else {
				
				if(mDate != feedhelper.getDate(c)) {
					//Log.v("PPP", "New seperator as old date" + mDate.toString() );
					needSep = true;
					mDate = feedhelper.getDate(c);
					//Log.v("PPP", "does not match new date" + mDate.toString() );
				}
			}

			mCellStates[position] = needSep ? STATE_SECTIONED_CELL : STATE_NORMAL_CELL;
			mCellDates[position] = mDate;
		  }

		if(needSep) {
			//Log.v("PPP", "Adding seperator with date " + mCellDates[position].toString() );
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis((long)mCellDates[position] * 1000);
			SimpleDateFormat df = new SimpleDateFormat();
			df.applyPattern("dd MMM yyyy");

			holder.separator.setText(df.format(cal.getTime()));
			holder.separator.setVisibility(View.VISIBLE);

		} else {
			holder.separator.setVisibility(View.GONE);
		}

		holder.populateFrom(c, feedhelper);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View row = LayoutInflater.from(getActivity()).inflate(R.layout.row_item, parent, false);
		PoliticsFeedHolder holder = new PoliticsFeedHolder(row);
		row.setTag(holder);
		return(row);
	}
    }

}


