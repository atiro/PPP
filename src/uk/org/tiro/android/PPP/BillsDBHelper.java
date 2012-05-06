package uk.org.tiro.android.PPP;

import android.content.Context;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.ArrayList;


import android.util.Log;

class BillsDBHelper {
	static final String TITLE="title";
	static final String HOUSE="house";
	static final String STAGE="stage";
	static final String TYPE="type";
	static final String DESCRIPTION="description";
	static final String DATE="date";
	static final String GUID="guid";
	static final String URL="url";
	static final String NEW="new";
	static final String UPDATED="updated";

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

	public BillsDBHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public BillsDBHelper open() throws SQLException {
                Log.v("PPP", "Creating BillsDB Helper");
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.mDbHelper.close();
	}

	public void insert(Bill new_bill) {

		// Check doesn't already exist by comparing URL, if so see
		// if something has changed (moved a stage on)

		if(checkBillByGUID(new_bill.getGUID())) {
			Log.v("PPP", "Bill already exists, checking (guid: " + new_bill.getGUID());
			// Most obvious is change in stage. Then house?. 
			// TODO handle changes in year in GUID (permalink!?)
			Cursor c = getBillByGUID(new_bill.getGUID());
			c.moveToFirst();
			Stage old_stage = getStage(c);
			House old_house = getHouse(c);

			if(old_stage != new_bill.getStage()) {
				String [] args = {new_bill.getStage().toOrdinal(), new_bill.getGUID() };
				this.mDb.execSQL("UPDATE bills SET new = 1 AND updated = 1 AND stage = ? WHERE guid = ?", args);
				Log.v("PPP", "Bill changed stage, updating");
			}
			if(old_house != new_bill.getHouse()) {
				String [] args = {new_bill.getHouse().toOrdinal(), new_bill.getGUID() };
				this.mDb.execSQL("UPDATE bills SET new = 1 AND updated = 1 AND house = ? WHERE guid = ?", args);
				Log.v("PPP", "Bill changed house, updating");
			}

			// If neither, ignore.
		} else {
			// Add new bill
			Log.v("PPP", "New Bill, adding");

			Date raw = new Date();
			ContentValues cv = new ContentValues();

			cv.put(TITLE, new_bill.getTitle());
			cv.put(HOUSE, new_bill.getHouse().ordinal());
			cv.put(STAGE, new_bill.getStage().ordinal());
			cv.put(DESCRIPTION, new_bill.getDescription());
			cv.put(DATE, raw.getTime() / 1000);
			cv.put(GUID, new_bill.getGUID());
			cv.put(URL, new_bill.getURL());
			cv.put(NEW, 1);
			cv.put(UPDATED, 0);

			this.mDb.insert("bills", TITLE, cv);
		}
	}

	public Cursor getLatestBill(House house) {
		String [] args = {house.toOrdinal()};

		return(this.mDb.rawQuery("SELECT _id from bills WHERE house = ? ORDER BY date desc LIMIT 1", args));
	}

	public Cursor getAllBills(House house) {
		String [] args = {house.toOrdinal()};

		return(this.mDb.rawQuery("SELECT _id,title,house,stage,description,date,url from bills WHERE house = ? ORDER BY date desc", args));
	}

	public Cursor getBillByGUID(String guid) {
		String [] args = {guid};

		return(this.mDb.rawQuery("SELECT _id,title,house,stage,description,date,url from bills WHERE guid = ?", args));
	}

	public Cursor getBill(Integer bill_id) {
		String [] args = {bill_id.toString()};

		return(this.mDb.rawQuery("SELECT _id,title,house,stage,description,date,url from bills WHERE _id = ?", args));
	}

	public void markBillsOld() {
		this.mDb.execSQL("UPDATE bills SET new = 0");

		Log.v("PPP", "Bills now all marked as old");
	}


	public List<Integer> getBillsFiltered(String match, boolean ignore_case) {
		String[] matches = match.split("\\s+");
		String filter = new String('%' + matches[0] + '%');
		String [] args = {filter, filter};
		List<Integer> bills = new ArrayList<Integer>();
		Cursor c;
		String [] pat_matches;

		Log.v("PPP", "DB Filtering on " + matches[0]);

		if(ignore_case == false) {
			this.mDb.execSQL("PRAGMA case_sensitive_like = true");
		} 

		c = this.mDb.rawQuery("SELECT _id,title,description from bills WHERE title LIKE ? OR description LIKE ? AND new = 1 ORDER BY date desc", args);

		c.moveToFirst();

		// Remove first word as DB already confirmed for us
		// matches matches

		if(matches.length > 1) {
		    pat_matches = new String[matches.length - 1];
		    System.arraycopy(matches, 1, pat_matches, 0, matches.length - 1);
		} else {
		    pat_matches = matches;
		}

		 while(c.isAfterLast() == false) {
		  boolean matches_all = true;

		  if(matches.length > 1) {
		    String title = c.getString(1);
		    String desc = c.getString(2);
		    for(String mat: pat_matches) {
			Log.v("PPP", "Sub Filtering on " + mat);
			matches_all = false;
			Pattern pattern;
			if(ignore_case) {
			  pattern = Pattern.compile(".*" + mat + ".*", Pattern.CASE_INSENSITIVE);
			} else {
			  pattern = Pattern.compile(".*" + mat + ".*");
			}
			Log.v("PPP", "Title: " + title);
			if(pattern.matcher(title).matches()) {
				matches_all = true;
				continue;
			}
			Log.v("PPP", "Desc: " + desc);
			if(pattern.matcher(desc).matches()) {
				matches_all = true;
				continue;
			}
			Log.v("PPP", "Doesn't match");
			break;
		    }
		  }

		  if(matches_all == true) {
		    bills.add(c.getInt(0));
		  }

		  c.moveToNext();
		 }

		if(ignore_case == false) {
			this.mDb.execSQL("PRAGMA case_sensitive_like = false");
		} 

		return bills;
	}

	public Cursor getAllBillsFiltered(String match) {
		String filter = new String('%' + match + '%');
		String [] args = {filter, filter};

		return(this.mDb.rawQuery("SELECT _id,title,house,stage,description,date,url from bills WHERE title LIKE ? OR description LIKE ? ORDER BY date desc", args));

	}

	public Cursor getBillsByStage(House house, Stage stage) {
		String [] args = {house.toOrdinal(), stage.toOrdinal()};

		return(this.mDb.rawQuery("SELECT _id from bills WHERE house = ? AND stage = ?", args));
	}

	public Integer getBillsCount() {
		Integer count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) FROM bills", null);
		c.moveToFirst();

		count = c.getInt(0);

		return count;
	}

	private boolean checkBillByGUID(String guid) {
		String [] args = {guid};

		Log.v("PPP", "Checking existence of bill with guid: " + guid);

		Cursor r = this.mDb.rawQuery("SELECT _id from bills WHERE guid = ?", args);

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

	public House getHouse(Cursor c) {
		House house;

		house = House.values()[c.getInt(2)];

		return house;
	}

	public Stage getStage(Cursor c) {
		Stage stage;

		stage = Stage.values()[c.getInt(3)];

		return stage;
	}

	public String getDescription(Cursor c) {
		
		return(c.getString(4));
	}

	public Date getDate(Cursor c) {

		Long timestamp = c.getLong(5) * 1000;

		return(new Date(timestamp));
	}

	public String getURL(Cursor c) {
		return(c.getString(6));
	}

}
