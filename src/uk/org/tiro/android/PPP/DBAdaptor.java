package uk.org.tiro.android.PPP;

import android.content.Context;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;


import android.util.Log;

// Taken from http://stackoverflow.com/questions/4063510/multiple-table-sqlite-db-adapters-in-android

public class DBAdaptor {

	public static final String DATABASE_NAME = "ppp";

	public static final int DATABASE_VERSION = 1;

	private static final String CREATE_TABLE_BILLS =
		"CREATE TABLE bills (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, house INTEGER, stage INTEGER, description TEXT, date INTEGER, guid TEXT, url TEXT, new INTEGER, updated INTEGER);";

	private static final String CREATE_TABLE_ACTS =
		"CREATE TABLE acts (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, summary TEXT, date INTEGER, url TEXT, guid TEXT, new INTEGER, chase INTEGER);";

	private static final String CREATE_TABLE_LORDS =
		"CREATE TABLE lords (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, committee TEXT, subject TEXT, location TEXT, chamber INTEGER, witnesses TEXT, date INTEGER, time TEXT, url TEXT, guid TEXT, new INTEGER);";

	private static final String CREATE_TABLE_COMMONS =
		"CREATE TABLE commons (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, committee TEXT, subject TEXT, location TEXT, chamber INTEGER, witnesses TEXT, date INTEGER, time TEXT, url TEXT, guid TEXT, new INTEGER);";

	private static final String CREATE_TABLE_TRIGGERS = 
		"CREATE TABLE triggers (_id INTEGER PRIMARY KEY AUTOINCREMENT, match TEXT, commons INTEGER, lords INTEGER, bills INTEGER, draft_bills INTEGER, acts INTEGER, stat_inst INTEGER, draft_stat_inst INTEGER, freq INTEGER, notify INTEGER, ignore_case INTEGER, ignore_name INTEGER, count INTEGER, last INTEGER, added INTEGER);";

	private static final String CREATE_TABLE_PEOPLE = 
		"CREATE TABLE people (_id INTEGER PRIMARY KEY AUTOINCREMENT, person TEXT, notify INTEGER, count INTEGER, last INTEGER, added INTEGER);";

	private static final String CREATE_TABLE_POLITICSFEED =
		"CREATE TABLE politicsfeed (_id INTEGER PRIMARY KEY AUTOINCREMENT, match TEXT, trigger_id INTEGER, trigger_type INTEGER, item_id INTEGER, house INTEGER, highlight INTEGER, new INTEGER, read INTEGER, date INTEGER);";

//	private static final String UPDATE_TABLE_POLITICSFEED_V1 =
//		"ALTER TABLE politicsfeed ADD COLUMN notify_alert INTEGER DEFAULT 0; ALTER TABLE politicsfeed ADD COLUMN notify_monitor INTEGER DEFAULT 0; ALTER TABLE politicsfeed ADD COLUMN notify_person DEFAULT 0; ALTER TABLE politicsfeed ADD COLUMN notify_important INTEGER DEFAULT 0; ALTER TABLE politicsfeed ADD COLUMN notify_bill INTEGER DEFAULT 0; ALTER TABLE politicsfeed ADD COLUMN notify_future INTEGER DEFAULT 0;";

	private final Context context;
	private DatabaseHelper DBHelper;
	public SQLiteDatabase mDb;

	public DBAdaptor(Context ctx) {
		//Log.v("PPP", "Creating DBAdaptor");
		this.context = ctx;
		this.DBHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//	Log.v("PPP", "Creating tables");
			db.execSQL(CREATE_TABLE_COMMONS);
			db.execSQL(CREATE_TABLE_LORDS);
			db.execSQL(CREATE_TABLE_ACTS);
			db.execSQL(CREATE_TABLE_BILLS);
			db.execSQL(CREATE_TABLE_TRIGGERS);
			db.execSQL(CREATE_TABLE_PEOPLE);
			db.execSQL(CREATE_TABLE_POLITICSFEED);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// upgrades go here
			switch(oldVersion) {
			case 1:
				// db.execSQL(UPDATE_TABLE_POLITICSFEED_V1);
				break;
			case 2:
				break;
			}
		}
	}

	public DBAdaptor open() throws SQLException {
		//Log.v("PPP", "Opening DBAdaptor");
		this.mDb = this.DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.DBHelper.close();
	}
}
