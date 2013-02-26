package uk.org.tiro.android.PPP;

import android.os.Bundle;

import android.app.Activity;

import android.database.Cursor;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager;

import android.net.Uri;

import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.Gallery;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Context;

import android.content.Intent;

import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DebatesFragment extends SherlockListFragment {

	private boolean detailsInline = false;

	House house;
	Chamber chamber;
	Integer date;

	ListView lv = null;

	CommonsDBHelper commonshelper = null;
	LordsDBHelper lordshelper = null;

	LordsDebatesAdaptor lordsadaptor = null;
	CommonsDebatesAdaptor commonsadaptor = null;

	DBAdaptor dbadaptor = null;

	Cursor model = null;
	Context cxt = null;
	Context acxt = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
//		setTheme(R.style.Theme_Sherlock);
		super.onActivityCreated(savedInstanceState);

	//	Bundle args = this.getArguments();

	//	house = House.values()[args.getInt("house")];
	//	chamber = Chamber.values()[args.getInt("chamber")];
		house = House.COMMONS;
		chamber = Chamber.MAIN;
		date = 0;

		Log.v("PPP", "creating DebatesFragment");

		// date

		cxt = getActivity().getApplicationContext();
		acxt = getActivity();

//		dbadaptor = new DBAdaptor(cxt).open();

//		setList();

	}

	public void updateAll(House house, Chamber chamber, Integer date) {
		this.house = house;
		this.chamber = chamber;
		this.date = date;
	}

	public void updateHouse(House house) {
		this.house = house;
		setList();
	}

	public void updateChamber(Chamber chamber) {
		this.chamber = chamber;
		setList();
	}

	public void updateDate(Integer date) {
		this.date = date;
		setList();
	}

	private void setList() {
		if(house == House.COMMONS) {
			if(model != null) { model.close(); }
			if(commonsadaptor != null) { commonsadaptor = null; }
			if(commonshelper == null) {
				commonshelper = new CommonsDBHelper(cxt).open();
			}

			model = commonshelper.getDebatesChamber(chamber, date);
 	            	commonsadaptor = new CommonsDebatesAdaptor(cxt, model);

		} else {
			if(model != null) { model.close(); }
			if(lordsadaptor != null) { lordsadaptor = null; }
			if(lordshelper == null) {
				lordshelper = new LordsDBHelper(cxt).open();
			}

			model = lordshelper.getDebatesChamber(chamber, date);
 	                lordsadaptor = new LordsDebatesAdaptor(cxt, model);

		}

		if(house == House.COMMONS) {
	   	    //Log.v("PPP", "Setting commons adaptor");
		    setListAdapter(commonsadaptor);
		    commonsadaptor.notifyDataSetChanged();
		} else {
	   	    //Log.v("PPP", "Setting lords adaptor");
		    setListAdapter(lordsadaptor);
		    lordsadaptor.notifyDataSetChanged();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.debates_fragment, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		//Log.v("PPP", "Creating DebatesFragment view");

	//	setList();

		return v;
	}

	public void onListItemClick(ListView parent, View v, int position,
					long id) {
		        final PoliticsFeedDBHelper feedhelper;
			final int debate;
			final String guid;
			final String title;
			final String subject;
			final String content;
			final String url;
			final Date date;
			Date today = new Date();

			today.setDate(today.getDate() - 1);
			today.setMinutes(23);
			today.setSeconds(59);

			// Retrieve debate guid
			model.moveToPosition(position);
			if(house == House.COMMONS) {
				debate = commonshelper.getId(model);
				guid = commonshelper.getGUID(model);
				title = commonshelper.getTitle(model);
				subject = commonshelper.getSubject(model);
				url = commonshelper.getURL(model);
				date = commonshelper.getDate(model);
			} else {
				debate = lordshelper.getId(model);
				guid = lordshelper.getGUID(model);
				title = lordshelper.getTitle(model);
				subject = lordshelper.getTitle(model);
				url = lordshelper.getURL(model);
				date = lordshelper.getDate(model);
			}
				
			content = subject;
			feedhelper = new PoliticsFeedDBHelper(acxt).open();

			if(today.compareTo(date) > 0) {
			  Log.v("PPP", "Today: " + today + " Debate: " + date);
			  if(chamber == Chamber.MAIN) {
			    new AlertDialog.Builder(acxt)
				.setTitle(title)
				.setMessage(subject)

                                .setNegativeButton("Cancel", null)
				.setNeutralButton("Read", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthing) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.UK);
						String date_ymd = sdf.format(date);
						String debate_url;
						
						feedhelper.close();
						Intent i = new Intent(Intent.ACTION_VIEW);
						if(house == House.COMMONS) {
							debate_url = "http://www.publications.parliament.uk/pa/cm201213/cmhansrd/cm" + date_ymd + "/debindx/" + date_ymd + "-x.htm";
						} else {
							debate_url = "http://www.publications.parliament.uk/pa/ld201213/ldhansrd/index/" + date_ymd + ".html";
						}
						i.setData(Uri.parse(debate_url));
						Log.v("PPP", "Launching browser with URL:" + url);
						startActivity(i);
					}}).show();
			  } else {
			    new AlertDialog.Builder(acxt)
				.setTitle(title)
				.setMessage(subject)

                                .setNegativeButton("Cancel", null)
				.setNeutralButton("Read", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthing) {


						// Insert into politicsfeed.
						if(house == House.COMMONS) {
							feedhelper.insert_commons_debate("", 0L, debate, true);
						} else {
							feedhelper.insert_lords_debate("", 0L, debate, true);
						}
						feedhelper.close();
						Toast.makeText(acxt, "Added", Toast.LENGTH_SHORT).show();

					}}).show();
			  }

			} else {
			  new AlertDialog.Builder(acxt)
				.setTitle(title)
				.setMessage(subject)

                                .setNegativeButton("Cancel", null)
				.setNeutralButton("Highlight", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthing) {


						// Insert into politicsfeed.
						if(house == House.COMMONS) {
							feedhelper.insert_commons_debate("", 0L, debate, true);
						} else {
							feedhelper.insert_lords_debate("", 0L, debate, true);
						}
						feedhelper.close();
						Toast.makeText(acxt, "Added to Highlights", Toast.LENGTH_SHORT).show();

					}
				})
				/*
				.setNeutralButton("View", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthing) {
						Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(sendIntent);
				}

				})
				*/

				/*
					TODO -- how to set reminderers ?
						before or after ?
				.setPositiveButton("Remind", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthing) {
						Toast.makeText(acxt, "Reminder - TODO", Toast.LENGTH_SHORT).show();
					}
				})
				*/
				.show();

/*
			Intent i = new Intent(cxt, DebateView.class);
			Bundle b = new Bundle();
			b.putString("guid", "");
			i.putExtras(b);
			startActivity(i);
*/

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v("PPP", "DebatesFragment - onResume");
/*
		if(dbadaptor == null) {
			dbadaptor = new DBAdaptor(cxt).open();
		}

		setList();
*/
	}

        @Override
        public void onPause() {
                super.onPause();
                Log.v("PPP", "DebatesFragment - onPause");
/*
		if(model != null) { model.close(); }

		if(commonshelper != null) { 
			commonshelper.close(); 
			commonshelper = null;
		}

		if(lordshelper != null) {
			lordshelper.close();
			lordshelper = null;
		}

		// Is this needed ? XXX
		if(commonsadaptor != null) { commonsadaptor = null; }

		if(dbadaptor != null) {
			dbadaptor.close();
			dbadaptor = null;
		}
*/
        }


	@Override
	public void onDestroy() {
		super.onDestroy();
		if(model != null) { model.close(); }
		if(lordshelper != null) { lordshelper.close(); }
		if(commonshelper != null) { commonshelper.close(); }
//		if(dbadaptor != null) { dbadaptor.close(); }
	}



    static class CommonsDebatesHolder {
	private TextView time = null;
    	private TextView committee = null;
    	private TextView subject = null;
    	private ImageView new_img = null;
	private View row = null;

	CommonsDebatesHolder(View row) {
		this.row = row;

		time = (TextView)row.findViewById(R.id.time);
		committee = (TextView)row.findViewById(R.id.committee);
		subject = (TextView)row.findViewById(R.id.subject);
		new_img = (ImageView)row.findViewById(R.id.new_img);
	}

	void populateFrom(Cursor c, CommonsDBHelper helper) {
		time.setText(helper.getTime(c));
		committee.setText(helper.getCommittee(c));
		subject.setText(helper.getSubject(c));

		if(helper.getNew(c)) {
			new_img.setVisibility(View.VISIBLE);
		}
			
			
	}

    }
    static class LordsDebatesHolder {
	private TextView time = null;
    	private TextView committee = null;
    	private TextView subject = null;
	private View row = null;

	LordsDebatesHolder(View row) {
		this.row = row;

		time = (TextView)row.findViewById(R.id.time);
		committee = (TextView)row.findViewById(R.id.committee);
		subject = (TextView)row.findViewById(R.id.subject);
	}

	void populateFrom(Cursor c, LordsDBHelper helper) {
		time.setText(helper.getTime(c));
		committee.setText(helper.getCommittee(c));
		subject.setText(helper.getSubject(c));
	}

    }

    class CommonsDebatesAdaptor extends CursorAdapter {

        CommonsDebatesAdaptor(Context context, Cursor c) {
		super(context, c);
		//Log.v("PPP", "Creating Commons Debates Adapter");
        }

	@Override
	public void bindView(View row, Context context, Cursor c) {
		CommonsDebatesHolder holder=(CommonsDebatesHolder)row.getTag();

		holder.populateFrom(c, commonshelper);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View row = LayoutInflater.from(getActivity()).inflate(R.layout.row_debate_commons, parent, false);
		CommonsDebatesHolder holder = new CommonsDebatesHolder(row);
		row.setTag(holder);
		return(row);
	}
    }

    class LordsDebatesAdaptor extends CursorAdapter {

        LordsDebatesAdaptor(Context context, Cursor c) {
		super(context, c);
		//Log.v("PPP", "Creating Lords Debates Adapter");
        }

	@Override
	public void bindView(View row, Context context, Cursor c) {
		LordsDebatesHolder holder=(LordsDebatesHolder)row.getTag();

		holder.populateFrom(c, lordshelper);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View row = LayoutInflater.from(getActivity()).inflate(R.layout.row_debate_lords, parent, false);
		LordsDebatesHolder holder = new LordsDebatesHolder(row);
		row.setTag(holder);
		return(row);
	}
    }

}


