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
import java.util.Locale;
import java.util.ArrayList;


import android.util.Log;

class LordsDBHelper {
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

	public LordsDBHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public LordsDBHelper open() throws SQLException {
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.mDbHelper.close();
	}

	public void insert(LordsDebate new_debate) {

		LordsDebate debate;

		ContentValues cv = new ContentValues();

		// Check doesn't already exist by comparing URL, if so see
		// if something has changed (moved a stage on)

		if(checkDebateByGUID(new_debate.getGUID())) {
			// Get bill
			//bill = getBill(new_bill.getURL());

			//updateBill(bill, new_bill);
		} else {
			// Add new bill

			Date raw = new_debate.getRawDate();
			String subject = null;

			cv.put(TITLE, new_debate.getTitle());
			cv.put(COMMITTEE, new_debate.getCommittee());
			subject = new_debate.getSubject();
			if(subject != null && subject.trim() != "") {
	  		  cv.put(SUBJECT, subject);
			} else {
			  cv.put(SUBJECT, "");
			}
			cv.put(LOCATION, new_debate.getLocation());
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

			this.mDb.insert("lords", TITLE, cv);
		}
	}

	public Cursor getLatestDebate() {
		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,date,guid,chamber,url from lords ORDER BY date desc LIMIT 1", null));
	}

	public Cursor getAllDebates() {
		return(this.mDb.rawQuery("SELECT _id,title,committee, subject,date,guid,chamber,url from lords ORDER BY date desc", null));
	}

        public List<Integer> getDebatesFiltered(String match, boolean ignore_case, boolean ignore_name) {
		String[] matches = match.split("\\s+");
                String filter = new String('%' + match + '%');
                String [] args = {filter, filter};
                String [] short_args = {filter};
                List<Integer> debates = new ArrayList<Integer>();
		Cursor c;
		String [] pat_matches;

                if(ignore_case == false) {
                        this.mDb.execSQL("PRAGMA case_sensitive_like = true");
                }

		if(ignore_name == false) {
 	               c = this.mDb.rawQuery("SELECT _id,title,subject from lords WHERE title LIKE ? OR subject LIKE ? ORDER BY date desc", args);
		} else {
 	               c = this.mDb.rawQuery("SELECT _id,subject from lords WHERE subject LIKE ? ORDER BY date desc", short_args);
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
                        break;
                    }
                  }

                  if(matches_all == true) {
                    debates.add(c.getInt(0));
                  }

                  c.moveToNext();
                 }

                if(ignore_case == false) {
                        this.mDb.execSQL("PRAGMA case_sensitive_like = false");
                }

                return debates;
        }

        public Cursor getDebatesChamber(Chamber chamber, Integer date) {
                String [] args = {chamber.toOrdinal()};
		String query;

		if(date < 0) {
			query = "SELECT _id,title,committee,subject,date,time,guid,chamber,url from lords WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d', 'now', '" + date + " day')) ORDER BY _id asc";
		} else if(date > 0) {
			query = "SELECT _id,title,committee,subject,date,time,guid,chamber,url from lords WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d', 'now', '+" + date + " day')) ORDER BY _id asc";
		} else {
                	query = "SELECT _id,title,committee,subject,date,time,guid,chamber,url from lords WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d')) ORDER BY _id asc";
		}


                return(this.mDb.rawQuery(query, args));
        }

	public Cursor getDebate(Integer id) {
		String [] args = {id.toString()};

		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,date,time,guid,chamber,url from lords WHERE _id = ?", args));
	}

	public Cursor getDebateByGUID(String guid) {
		String [] args = {guid};

		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,date,time,guid,chamber,url from lords WHERE guid = ?", args));
	}

	public void markChase(String guid) {
		String [] args = {guid};

		this.mDb.rawQuery("UPDATE lords SET chase = 1 WHERE guid = ?", args);
	}

	private boolean checkDebateByGUID(String guid) {
		String [] args = {guid};

		//Log.v("PPP", "Checking existence of lords debate with guid: " + guid);

		Cursor r = this.mDb.rawQuery("SELECT _id from lords WHERE guid = ?", args);

		if(r.getCount() > 0) {
			r.close();
			return true;
		} else {
			r.close();
			return false;
		}
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

	public Date getDate(Cursor c) {

		Long timestamp = c.getLong(4) * 1000;

		return(new Date(timestamp));
	}

        public Long getDateLong(Cursor c) {

                Long timestamp = c.getLong(4);

                return(timestamp);
	}

	public String getDateShort(Cursor c) {
                long timestamp = c.getLong(4) * 1000;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(timestamp);

                SimpleDateFormat df = new SimpleDateFormat();
                df.applyPattern("E, dd MMM yyyy");

                return(df.format(cal.getTime()));
	}

	public String getTime(Cursor c) {
		String time = c.getString(5);

		if(time == "") { return "-"; } else { return time; }
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

}
