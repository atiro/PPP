package uk.org.tiro.android.PPP;

import android.content.Context;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;


import android.util.Log;

class CommonsDBHelper {
	static final String TITLE="title";
	static final String SUBJECT="subject";
	static final String CHAMBER="chamber";
	static final String COMMITTEE="committee";
	static final String WITNESSES="witnesses";
	static final String LOCATION="location";
	static final String DATE="date";
	static final String TIME="time";
	static final String GUID="guid";
	static final String URL="url";
	static final String NEW="new";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DBAdaptor.DATABASE_NAME, null, DBAdaptor.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO
		}

	}

	public CommonsDBHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public CommonsDBHelper open() throws SQLException {
//		Log.v("PPP", "Creating CommonsDB Helper");
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.mDbHelper.close();
	}

	public void insert(CommonsDebate new_debate) {

		CommonsDebate debate;

		ContentValues cv = new ContentValues();

		// Check doesn't already exist by comparing URL, if so see
		// if something has changed (moved a stage on)

		if(checkDebateByGUID(new_debate.getGUID())) {
			// Getdebates  
			//debates= getBill(new_bill.getURL());

			//updateBill(bill, new_bill);
		} else {
			// Add new debate

			Date raw = new_debate.getRawDate();
			String subject = null;

			cv.put(TITLE, new_debate.getTitle());
			cv.put(COMMITTEE, new_debate.getCommittee());
			subject = new_debate.getSubject();
			if(subject != null && subject.trim() != "") {
	  		  cv.put(SUBJECT, subject);
			}
			cv.put(LOCATION, new_debate.getLocation());
			cv.put(WITNESSES, new_debate.getWitnesses());
			cv.put(CHAMBER, new_debate.getChamber().toOrdinal());
			cv.put(DATE, raw.getTime() / 1000);
			cv.put(GUID, new_debate.getGUID());
			String time = new_debate.getTime();
			if(time == "") {
				cv.put(TIME, " --- ");
			} else {
				cv.put(TIME, new_debate.getTime());
			}
			cv.put(URL, new_debate.getURL());
			cv.put(NEW, 1);

			this.mDb.insert("commons", TITLE, cv);
		}
	}

	public Cursor getLatestDebate() {
		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,date,guid,chamber,url from commons ORDER BY date desc LIMIT 1", null));
	}

	public Cursor getAllDebates() {
		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,date,guid,chamber,url from commons ORDER BY date desc", null));
	}

	public Cursor getAllDebatesFiltered(String match) {
		String filter = new String('%' + match + '%');
		String [] args = {filter};

		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,date,guid,chamber,url from commons WHERE title LIKE ? ORDER BY date desc", args));
	}


        public List<Integer> getDebatesFiltered(String match, boolean ignore_case, boolean ignore_name, Integer days_ahead) {
		String[] matches = match.split("\\s+");
                String filter = new String('%' + matches[0] + '%');
		String [] args = {filter, filter};
		String [] short_args = {filter};
                List<Integer> debates = new ArrayList<Integer>();
		Cursor c;
		String [] pat_matches;

		if(ignore_case == false) {
			//Log.v("PPP", "Enabling case sensitive likes");
			this.mDb.execSQL("PRAGMA case_sensitive_like = true");
		}


		if(ignore_name == false) {
		       String query = "SELECT _id,title,subject from commons WHERE title LIKE ? OR subject LIKE ? AND new = 1 AND date < strftime('%s', 'now', '+" + days_ahead + " day') ORDER BY date desc";
 	               c = this.mDb.rawQuery(query, args);
		} else {
		       String query = "SELECT _id,subject from commons WHERE subject LIKE ? AND new = 1 AND date < strftime('%s', 'now', '+" + days_ahead + " day') ORDER BY date desc";
 	               c = this.mDb.rawQuery(query, short_args);
		}

                c.moveToFirst();

                if(matches.length > 1) {
                    pat_matches = new String[matches.length - 1];
                    System.arraycopy(matches, 1, pat_matches, 0, matches.length - 1);
		} else {
		   pat_matches = matches;
		}

                 while(c.isAfterLast() == false) {
                  boolean matches_all = true;

                  if(matches.length > 1) {
                    for(String mat: pat_matches) {
                        matches_all = false;
                        Pattern pattern;
			if(ignore_case) {
			 pattern = Pattern.compile(".*" + mat + ".*", Pattern.CASE_INSENSITIVE);
			} else {
			 pattern = Pattern.compile(".*" + mat + ".*");
			}

			//Log.v("PPP", "Matching word: " + mat);

			if(ignore_name == false) {
                    	   String title = c.getString(1);
                    	   String subject = c.getString(2);

                           if(pattern.matcher(title).matches()) {
                                matches_all = true; 
                                continue;
                           }       

                           if(pattern.matcher(subject).matches()) {
                                matches_all = true; 
                                continue;
                           }
			} else {
                    	   String subject = c.getString(1);
                           if(pattern.matcher(subject).matches()) {
                                matches_all = true; 
                                continue;
                           }
			}

			//Log.v("PPP", "Didn't match " + mat);
                        break;
                    }
                  }

		  if(matches_all == true) {
			 //Log.v("PPP", "Adding debate");
 	                 debates.add(c.getInt(0));
		  }

                  c.moveToNext();
                 }

		if(ignore_case == false) {
			//Log.v("PPP", "Disabling case sensitive likes");
			this.mDb.execSQL("PRAGMA case_sensitive_like = false");
		}

		c.close();

                return debates;
        }

	public Cursor getDebatesChamber(Chamber chamber, Integer date) {
		String [] args = {chamber.toOrdinal()};
		String date_offset;
		String query;

		if(date < 0) {
			query = "SELECT _id,title,committee,subject,date,time,guid,chamber,url,new from commons WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d', 'now', '" + date + " day')) ORDER BY _id asc";
		} else if(date > 0) {
			query = "SELECT _id,title,committee,subject,date,time,guid,chamber,url,new from commons WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d', 'now', '+" + date + " day')) ORDER BY _id asc";
		} else {
			query = "SELECT _id,title,committee,subject,date,time,guid,chamber,url,new from commons WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d')) ORDER BY _id asc";
		}
			
		return(this.mDb.rawQuery(query, args));
	}

	public Cursor getDebateByGUID(String guid) {
		String [] args = {guid};

		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,date,time,guid,chamber,url from commons WHERE guid = ?", args));
	}

	public Cursor getDebate(Integer debate_id) {
                String [] args = {debate_id.toString()};

		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,date,time,guid,chamber,url from commons WHERE _id = ?", args));
	}

	public void markAllOld(Integer days_ahead) {

		/* Set the next X days articles as old */

		String query = "UPDATE commons SET new = 0 WHERE date < strftime('%s', 'now', '+" + days_ahead + " day')";
		this.mDb.execSQL(query);
	}

	public Integer countFutureDebates(Integer days_ahead) {
		Integer nf_count = -1;

		String query = "SELECT COUNT(*) FROM commons WHERE date >= strftime('%s', strftime('%Y-%m-%d', 'now')) AND date < strftime('%s', 'now', '+" + days_ahead + " day')";
		Cursor c = this.mDb.rawQuery(query, null);
		c.moveToFirst();
		nf_count = c.getInt(0);
		c.close();
		return nf_count;
	}

	/* Private functions */

	private boolean checkDebateByGUID(String guid) {
		String [] args = {guid};

		//Log.v("PPP", "Checking existence of commons debate with guid: " + guid);

		Cursor r = this.mDb.rawQuery("SELECT _id from commons WHERE guid = ?", args);

		if(r.getCount() > 0) {
			r.close();
			return true;
		} else {
			r.close();
			return false;
		}
	}

	/* Value helper functions */

	public int getId(Cursor c) {
		return(c.getInt(0));
	}
	
	public String getTitle(Cursor c) {
		return(c.getString(1));
	}

	public String getCommittee(Cursor c) {
		return(c.getString(2));
	}

	public String getSubject(Cursor c) {
		return(c.getString(3));
	}


	public String getDateShort(Cursor c) {

		long timestamp = c.getLong(4) * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);

                SimpleDateFormat df = new SimpleDateFormat();
                df.applyPattern("E, dd MMM yyyy");

		return(df.format(cal.getTime()));
	}

	public Date getDate(Cursor c) {

		Long timestamp = c.getLong(4) * 1000;

		return(new Date(timestamp));
	}

	public Long getDateLong(Cursor c) {

		Long timestamp = c.getLong(4);

		return(timestamp);
	}

	public String getTime(Cursor c) {
		String time = c.getString(5);
		return time;
	}

	public String getGUID(Cursor c) {
		String guid = c.getString(6);
		return guid;
	}

	public Chamber getChamber(Cursor c) {
		Chamber chamber;

		chamber = Chamber.values()[c.getInt(7)];
		
		return chamber;
	}

	public String getURL(Cursor c) {
		String url = c.getString(8);
		return url;
	}

	public Boolean getNew(Cursor c) {
		int new_debate = c.getInt(9);

		if(new_debate == 1) { 
			return true;
		} else {
			return false;

		}
	}
}
