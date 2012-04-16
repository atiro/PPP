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
import java.util.ArrayList;


import android.util.Log;

class ActsDBHelper {
	static final String TITLE="title";
	static final String SUMMARY="summary";
	static final String DATE="date";
	static final String URL="url";
	static final String NEW="new";
	static final String CHASE="chase";

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

	public ActsDBHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public ActsDBHelper open() throws SQLException {
		Log.v("PPP", "Creating ActsDB Helper");
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.mDbHelper.close();
	}


	public void insert(Act new_act) {

		Act act;

		ContentValues cv = new ContentValues();

		// Check doesn't already exist by comparing URL, if so see
		// if something has changed (moved a stage on)

		if(checkActByGUID(new_act.getGUID())) {
			// TODO - What to do if already exists ?
		} else {
			// Add new act

			Date raw = new_act.getRawDate();

			cv.put(TITLE, new_act.getTitle());
			cv.put(SUMMARY, new_act.getSummary());
//			cv.put(DATE, raw.getTime() / 1000);
			cv.put(URL, new_act.getURL());
			cv.put(NEW, 1);
			cv.put(CHASE, 0); //TODO check if of interest

			this.mDb.insert("acts", TITLE, cv);
		}
	}

	public Cursor getLatestAct() {
		return(this.mDb.rawQuery("SELECT _id,title,summary,date,url from acts ORDER BY date desc LIMIT 1", null));
	}

	public Cursor getNewActs() {
		return(this.mDb.rawQuery("SELECT _id,title,summary,date,url from acts WHERE new = 1 ORDER BY date desc", null));
	}

	public Cursor getAllActs() {
		return(this.mDb.rawQuery("SELECT _id,title,summary,date,url from acts ORDER BY date desc", null));
	}

	public Integer getAllActsCount() {
		Integer acts_count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from acts", null);

		c.moveToFirst();

		acts_count = c.getInt(0);

		return acts_count ;
	}

	public int getNewActsCount() {
		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from acts WHERE new = 1", null);
		return(c.getInt(0));
	}


        public List<Integer> getActsFiltered(String match) {
                String filter = new String('%' + match + '%');
                String [] args = {filter, filter};
                List<Integer> acts = new ArrayList<Integer>();

                Cursor c = this.mDb.rawQuery("SELECT _id from acts WHERE title LIKE ? OR summary LIKE ? AND new = 1 ORDER BY date desc", args);
                c.moveToFirst();

                 while(c.isAfterLast() == false) {
                        acts.add(c.getInt(0));
                        c.moveToNext();
                 }

                return acts;
        }

	public Cursor getAllActsFiltered(String match) {
		String filter = new String('%' + match + '%');
		String [] args = {filter};

		return(this.mDb.rawQuery("SELECT _id,title,summary,date,url from acts WHERE title LIKE ? ORDER BY date desc", args));
	}

	public Cursor getChaseActs() {
		return(this.mDb.rawQuery("SELECT _id,title,summary,date,url from acts WHERE chase = 1 ORDER BY date desc", null));
	}

	public int getChasedActsCount() {
		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from acts WHERE chase = 1", null);
		return(c.getInt(0));
	}

	public Cursor getChasedActsFiltered(String match) {
		String filter = new String('%' + match + '%');
		String [] args = {filter};

		return(this.mDb.rawQuery("SELECT _id,title,summary,date,url from acts WHERE title LIKE ? AND chase = 1 ORDER BY date desc", args));
	}


        public Cursor getAct(Integer act_id) {
                String [] args = {act_id.toString()};

                return(this.mDb.rawQuery("SELECT _id,title,summary,date,url from acts WHERE _id = ?", args));

        }

        public void markActsOld() {
                this.mDb.execSQL("UPDATE acts SET new = 0");
        }

	private boolean checkActByGUID(String guid) {
		String [] args = {guid};

		Log.v("PPP", "Checking existence of act with guid: " + guid);

		Cursor r = this.mDb.rawQuery("SELECT _id from acts WHERE guid = ?", args);

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

	public String getSummary(Cursor c) {
		
		return(c.getString(2));
	}

	public Date getDate(Cursor c) {

		Long timestamp = c.getLong(3) * 1000;

		return(new Date(timestamp));
	}

	public String getURL(Cursor c) {
		return(c.getString(4));
	}

	public String getGUID(Cursor c) {
		return(c.getString(7));
	}

}
