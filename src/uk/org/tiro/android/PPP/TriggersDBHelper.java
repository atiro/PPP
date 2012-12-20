package uk.org.tiro.android.PPP;

import android.content.Context;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import java.util.Date;
import android.text.format.Time;

import java.util.List;
import java.util.ArrayList;

import android.util.Log;


class TriggersDBHelper {
	static final String MATCH="match";
	static final String COMMONS="commons";
	static final String LORDS="lords";
	static final String BILLS="bills";
	static final String DRAFT_BILLS="draft_bills";
	static final String ACTS="acts";
	static final String STAT_INST="stat_inst";
	static final String DRAFT_STAT_INST="draft_stat_inst";
	static final String FREQ="freq";
	static final String NOTIFY="notify";
	static final String COUNT="count";
	static final String IGNORE_CASE="ignore_case";
	static final String IGNORE_NAME="ignore_name";
	static final String LAST="last";
	static final String ADDED="added";

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

	public TriggersDBHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public TriggersDBHelper open() throws SQLException {
		//Log.v("PPP", "Creating TriggersDB Helper");
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.mDbHelper.close();
	}


	public long insert(String trigger, boolean trigger_acts, boolean trigger_bills, boolean trigger_lords, boolean trigger_commons, boolean trigger_notify, boolean ignore_case, boolean ignore_name) {
		String[] args = {trigger};
		long trigger_id;

		ContentValues cv = new ContentValues();

		// TODO Check trigger doesn't already exist 

			cv.put(MATCH, trigger);

			if(trigger_bills) {
				cv.put(BILLS, 1);
			} else {
				cv.put(BILLS, 0);
			}

			if(trigger_acts) {
				cv.put(ACTS, 1);
			} else {
				cv.put(ACTS, 0);
			}

			if(trigger_lords) {
				cv.put(LORDS, 1);
			} else {
				cv.put(LORDS, 0);
			}

			if(trigger_commons) {
				cv.put(COMMONS, 1);
			} else {
				cv.put(COMMONS, 0);
			}

			// all TODO - no feed yet
			cv.put(DRAFT_BILLS, 0); 
			cv.put(STAT_INST, 0);
			cv.put(DRAFT_STAT_INST, 0);

			// What is this for again ?
			cv.put(FREQ, 1);

			if(trigger_notify) {
				cv.put(NOTIFY, 1);
			} else {
				cv.put(NOTIFY, 0);
			}

			if(ignore_case == true) {
				cv.put(IGNORE_CASE, 1);
			} else {
				cv.put(IGNORE_CASE, 0);
			}

			if(ignore_name == true) {
				cv.put(IGNORE_NAME, 1);
			} else {
				cv.put(IGNORE_NAME, 0);
			}

			cv.put(COUNT, 0);

			cv.put(LAST, 0);

			trigger_id = this.mDb.insert("triggers", MATCH, cv);
	//	}

		// Now return our new triggers id

		//Cursor c = this.mDb.rawQuery("SELECT _id from triggers WHERE match = ?", args);
		//c.moveToFirst();
		// trigger_id = c.getInt(0);

		return trigger_id;

	}

	public void updateLast(Long trigger_id) {
		String[] args  = { trigger_id.toString() };

		this.mDb.execSQL("UPDATE triggers SET last = strftime('%s', strftime('%Y-%m-%d')) WHERE _id = ?", args);
	}


	public Cursor getTriggers() {
		return(this.mDb.rawQuery("SELECT _id,match,last,acts,bills,lords,commons from triggers ORDER BY added desc", null));
	}

	public Cursor getBillTriggers() {
		return(this.mDb.rawQuery("SELECT _id,match,last from triggers WHERE bills = 1 ORDER BY match asc", null));
	}

	public Integer getBillTriggersCount() {
		Integer triggers_count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from triggers WHERE bills = 1", null);

		c.moveToFirst();

		triggers_count = c.getInt(0);

		c.close();

		return triggers_count;
	}

	public Cursor getActTriggers() {
		return(this.mDb.rawQuery("SELECT _id,match,last from triggers WHERE acts = 1 ORDER BY match asc", null));
	}

	public Integer getActTriggersCount() {
		Integer triggers_count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from triggers WHERE acts = 1", null);

		c.moveToFirst();

		triggers_count = c.getInt(0);

		c.close();

		return triggers_count;
	}

	public Cursor getSITriggers() {
		return(this.mDb.rawQuery("SELECT _id,match,last from triggers WHERE stat_inst = 1 ORDER BY match asc", null));
	}

	public Integer getSITriggersCount() {
		Integer triggers_count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from triggers WHERE stat_inst = 1", null);

		c.moveToFirst();

		triggers_count = c.getInt(0);

		c.close();

		return triggers_count;
	}
	
	public Cursor getDSITriggers() {
		return(this.mDb.rawQuery("SELECT _id,match,last from triggers WHERE draft_stat_inst = 1 ORDER BY match asc", null));
	}

	public Integer getDSITriggersCount() {
		Integer triggers_count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from triggers WHERE draft_stat_inst = 1", null);

		c.moveToFirst();

		triggers_count = c.getInt(0);

		c.close();

		return triggers_count;
	}

	public Cursor getDebateTriggers() {
		return(this.mDb.rawQuery("SELECT _id,match,last from triggers WHERE debates = 1 ORDER BY match asc", null));
	}

	public Integer getDebateTriggersCount() {
		Integer triggers_count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from triggers WHERE debates = 1", null);

		c.moveToFirst();

		triggers_count = c.getInt(0);

		c.close();

		return triggers_count;
	}

	public Cursor getCommitteeTriggers() {
		return(this.mDb.rawQuery("SELECT _id,match,last from triggers WHERE committees = 1 ORDER BY match asc", null));
	}

	public Integer getCommitteeTriggersCount() {
		Integer triggers_count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from triggers WHERE committees = 1", null);

		c.moveToFirst();

		triggers_count = c.getInt(0);

		c.close();

		return triggers_count;
	}

	public Integer getTriggersCount() {
		Integer triggers_count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from triggers", null);
		c.moveToFirst();

		triggers_count = c.getInt(0);

		c.close();

		return triggers_count ;
	}

	public Cursor getTriggersFiltered(String match) {
		String filter = new String('%' + match + '%');
		String [] args = {filter};

		return(this.mDb.rawQuery("SELECT _id,title from triggers WHERE match LIKE ? ORDER BY date desc", args));
	}

	private boolean checktriggerByMatch(String match) {
		String [] args = {match};

		//Log.v("PPP", "Checking existence of trigger with match: " + match);

		Cursor r = this.mDb.rawQuery("SELECT _id from triggers WHERE match = ?", args);

		if(r.getCount() > 0) {
			r.close();
			return true;
		} else {
			r.close();
			return false;
		}
	}
	
	public List<String> bill_triggers() {
		List<String> triggers = new ArrayList<String>();

		Cursor c = this.mDb.rawQuery("SELECT match from triggers WHERE bills = 1", null);

		c.moveToFirst();

		// Join results in string with '|'
		while(c.isAfterLast() == false) {
			triggers.add(c.getString(0));
			c.moveToNext();
		}

		c.close();

		return triggers;
	}

	public List<String> com_debates_triggers() {
		List<String> triggers = new ArrayList<String>();

		Cursor c = this.mDb.rawQuery("SELECT match from triggers WHERE commons = 1", null);

		c.moveToFirst();

		// Join results in string with '|'
		while(c.isAfterLast() == false) {
			triggers.add(c.getString(0));
			c.moveToNext();
		}

		c.close();

		return triggers;
	}

	public List<String> lords_debates_triggers() {
		List<String> triggers = new ArrayList<String>();

		Cursor c = this.mDb.rawQuery("SELECT match from triggers WHERE lords = 1", null);

		c.moveToFirst();

		// Join results in string with '|'
		while(c.isAfterLast() == false) {
			triggers.add(c.getString(0));
			c.moveToNext();
		}

		c.close();

		return triggers;
	}
			
	public void remove(Cursor c) {
		String id = c.getString(0);
		String [] args = {id};

		//Log.v("PPP", "Deleting trigger where id = " + id);

		this.mDb.delete("triggers", "_id = ?", args);

	}

	public String getID(Cursor c) {
		return(c.getString(0));
	}

	public String getMatch(Cursor c) {
		return(c.getString(1));
	}

	public String getLast(Cursor c) {
		long when = c.getLong(2) * 1000;

		if(when == 0) {
			return("No match yet");
		} else {
			Time last = new Time();
			last.set(when);

			return(last.format("%d %b %Y"));
		}
	}

	public String getTypes(Cursor c) {
		String active_types = "";
		List<String> types = new ArrayList<String>();

		if(c.getInt(3) == 1) {
			types.add("Acts");
		}
		if(c.getInt(4) == 1) {
			types.add("Bills");
		}
		if(c.getInt(5) == 1) {
			types.add("Lords");
		}
		if(c.getInt(6) == 1) {
			types.add("Commons");
		}

		for(String type: types) {
			active_types += type + ",";
		}

		//Log.v("PPP", "Active types: " + active_types);

		return active_types;
	}
		
}
