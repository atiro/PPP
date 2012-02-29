package uk.org.tiro.android.PPP;

import android.content.Context;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import java.util.Date;

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
	static final String RELEVANT="relevant";

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

		if(checkDebateByGuid(new_debate.getGUID())) {
			// Getdebates  
			//debates= getBill(new_bill.getURL());

			//updateBill(bill, new_bill);
		} else {
			// Add new debate

			Date raw = new_debate.getRawDate();

			cv.put(TITLE, new_debate.getTitle());
			cv.put(COMMITTEE, new_debate.getCommittee());
			cv.put(SUBJECT, new_debate.getSubject());
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
			cv.put(RELEVANT, 0);

			this.mDb.insert("commons", TITLE, cv);
		}
	}

	public Cursor getLatestDebate() {
		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,location,chamber,url,date from commons ORDER BY date desc LIMIT 1", null));
	}

	public Cursor getAllDebates() {
		return(this.mDb.rawQuery("SELECT _id,title,committee, subject,location,chamber,url,date from commons ORDER BY date desc", null));
	}

	public Cursor getAllDebatesFiltered(String match) {
		String filter = new String('%' + match + '%');
		String [] args = {filter};

		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,location,chamber,url,date from commons WHERE title LIKE ? ORDER BY date desc", args));
	}
	public Cursor getTodayDebatesChamber(Chamber chamber) {
		String [] args = {chamber.toOrdinal()};

		Log.v("PPP", "Querying debates from chamber " + args[0]);
		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,location,chamber,url,date,time from commons WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d')) ORDER BY _id asc", args));
	}

	public Cursor getTomorrowsDebatesChamber(Chamber chamber) {
		String [] args = {chamber.toOrdinal()};

		Log.v("PPP", "Querying tomorrows debates from chamber " + args[0]);
		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,location,chamber,url,date,time from commons WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d', 'now', '+1 day')) ORDER BY _id asc", args));
	}

	public Cursor getYesterdaysDebatesChamber(Chamber chamber) {
		String [] args = {chamber.toOrdinal()};

		Log.v("PPP", "Querying yesterdays debates from chamber " + args[0]);
		return(this.mDb.rawQuery("SELECT _id,title,committee,subject,location,chamber,url,date,time from commons WHERE chamber = ? AND date = strftime('%s', strftime('%Y-%m-%d', 'now', '-1 day')) ORDER BY _id asc", args));
	}

	public Integer getAlertCount() {
		Integer count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) FROM commons WHERE relevant = 1 AND new = 1", null);

		c.moveToFirst();

		count = c.getInt(0);

		return count;
	}

	private boolean checkDebateByGuid(String guid) {
		String [] args = {guid};

		Log.v("PPP", "Checking existence of commons debate with guid: " + guid);

		Cursor r = this.mDb.rawQuery("SELECT _id from commons WHERE guid = ?", args);

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

	public String getLocation(Cursor c) {
		return(c.getString(4));
	}

	public Chamber getChamber(Cursor c) {
		Chamber chamber;

		chamber = Chamber.values()[c.getInt(5)];
		
		return chamber;
	}

	public String getURL(Cursor c) {
		return(c.getString(6));
	}

	public Date getDate(Cursor c) {

		Long timestamp = c.getLong(7) * 1000;

		return(new Date(timestamp));
	}

	public String getTime(Cursor c) {
		String time = c.getString(8);

		return time;
	}
}
