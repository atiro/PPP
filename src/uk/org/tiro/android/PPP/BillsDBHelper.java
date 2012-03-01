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
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		this.mDbHelper.close();
	}

	public void insert(Bill new_bill) {

		Bill bill;

		ContentValues cv = new ContentValues();

		// Check doesn't already exist by comparing URL, if so see
		// if something has changed (moved a stage on)

		if(checkBillByGuid(new_bill.getGUID())) {
			// Get bill
			//bill = getBill(new_bill.getURL());

			//updateBill(bill, new_bill);
		} else {
			// Add new bill

			Date raw = new_bill.getRawDate();

			cv.put(TITLE, new_bill.getTitle());
			cv.put(HOUSE, new_bill.getHouse().ordinal());
			cv.put(STAGE, new_bill.getStage().ordinal());
			cv.put(DESCRIPTION, new_bill.getDescription());
//			cv.put(DATE, raw.getTime() / 1000);
			cv.put(GUID, new_bill.getGUID());
			cv.put(URL, new_bill.getURL());
			cv.put(NEW, 1);

			this.mDb.insert("bills", TITLE, cv);
		}
	}

	public Cursor getLatestBill(House house) {
		String [] args = {house.toOrdinal()};

		return(this.mDb.rawQuery("SELECT _id from bills WHERE house = ? ORDER BY date desc LIMIT 1", args));
	}

	public Cursor getAllBills(House house) {
		String [] args = {house.toOrdinal()};

		return(this.mDb.rawQuery("SELECT _id,title,house,stage,description from bills WHERE house = ? ORDER BY date desc", args));
	}

	public Cursor getAllBillsFiltered(House house, String match) {
		String filter = new String('%' + match + '%');
		String [] args = {house.toOrdinal(), filter};

		return(this.mDb.rawQuery("SELECT _id,title,house,stage,description from bills WHERE house = ? AND title LIKE ? ORDER BY date desc", args));
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

	public Integer getAlertCount() {
		Integer count = -1;

		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) FROM bills WHERE relevant = 1 AND new = 1", null);

		c.moveToFirst();

		count = c.getInt(0);

		return count;
	}

	private boolean checkBillByGuid(String guid) {
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

	public String getGUID(Cursor c) {
		return(c.getString(7));
	}

}
