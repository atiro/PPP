package uk.org.tiro.android.PPP;

import android.content.Context;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import java.util.Date;
import java.util.Calendar;

import android.util.Log;


class PoliticsFeedDBHelper {
	static final String MATCH="match";
	static final String TRIGGER_ID="trigger_id";
	static final String TRIGGER_TYPE="trigger_type";
	static final String ITEM_ID="item_id";
	static final String HOUSE="house";
	static final String HIGHLIGHT="highlight"; // Alert coming debate
	static final String READ="read";	   // Notify available to read
	static final String NEW="new";
	static final String DATE="date";	   // Date of debate

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private ActsDBHelper actshelper;
	private BillsDBHelper billshelper;
	private CommonsDBHelper commonshelper;
	private LordsDBHelper lordshelper;

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

	public PoliticsFeedDBHelper(Context ctx) {
		this.mCtx = ctx;
	}

	public PoliticsFeedDBHelper open() throws SQLException {
		//Log.v("PPP", "Creating PoliticsFeedDB Helper");
		this.mDbHelper = new DatabaseHelper(this.mCtx);
		this.mDb = this.mDbHelper.getWritableDatabase();

		commonshelper = new CommonsDBHelper(this.mCtx).open();
		lordshelper = new LordsDBHelper(this.mCtx).open();
		billshelper = new BillsDBHelper(this.mCtx).open();
		actshelper = new ActsDBHelper(this.mCtx).open();

		return this;
	}

	public void close() {
		actshelper.close();
		billshelper.close();
		commonshelper.close();
		lordshelper.close();
		this.mDbHelper.close();
	}


	public boolean checkExists(Integer item_id, House house) {
		String [] args = {item_id.toString(), house.toOrdinal()};

		Cursor c = this.mDb.rawQuery("SELECT _id FROM politicsfeed WHERE item_id = ? AND house = ?", args);
	
		if(c.getCount() > 0) {
			c.close();
			return true;
		} else {
			c.close();
			return false;
		}
	}


		
	public void insert_bill(String trigger, long trigger_id, Integer bill_id, boolean new_trigger) {

		ContentValues cv = new ContentValues();

	/*
		BillsDBHelper billshelper = new BillsDBHelper(this.mCtx).open();

		Cursor c = billshelper.getBill(bill_id);

		c.moveToFirst(); // TODO check got result

		 Create message
			New bill ' ' matched trigger' '
			Bill ' ' matching trigger' ' has moved to stage ''
			Bill ' ' matching trigger' ' has moved to house ''

		*/

		if(checkExists(bill_id, House.NEITHER) == false) {
			//Log.v("PPP", "Adding feed entry for bill : " + bill_id.toString());
		// TODO Check trigger doesn't already exist 
			cv.put(MATCH, trigger);
			// cv.put(BILL_ID, bill.getID());
			cv.put(ITEM_ID, bill_id);

			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			cv.put(DATE, c.getTimeInMillis() / 1000);
			cv.put(TRIGGER_ID, trigger_id);
			cv.put(TRIGGER_TYPE, Trigger.BILL.ordinal());
			cv.put(HOUSE, House.NEITHER.ordinal());
			cv.put(HIGHLIGHT, 0);
			cv.put(READ, 0);

			if(new_trigger) {
				cv.put(NEW, 1);
			} else {
				cv.put(NEW, 0);
			}
			// ADDED - getdate

			this.mDb.insert("politicsfeed", MATCH , cv);
		} else {
			//Log.v("PPP", "Entry already exists, skipping");
		}

	}

	public void insert_act(String trigger, long trigger_id, Integer act_id, boolean new_trigger) {

		ContentValues cv = new ContentValues();
		/*
		ActsDBHelper actshelper = new ActsDBHelper(this.mCtx).open();

		Log.v("PPP", "Adding feed entry for act : " + act_id.toString());
		Cursor curs = actshelper.getAct(act_id);

		curs.moveToFirst();
		*/

		
		// TODO Check trigger doesn't already exist 

		if(checkExists(act_id, House.NEITHER) == false) {

			cv.put(MATCH, trigger);
			cv.put(ITEM_ID, act_id);
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			cv.put(DATE, c.getTimeInMillis() / 1000);
			cv.put(TRIGGER_ID, trigger_id);
			cv.put(TRIGGER_TYPE, Trigger.ACT.ordinal());
			cv.put(HOUSE, House.NEITHER.ordinal());
			cv.put(HIGHLIGHT, 0);
			cv.put(READ, 0);
			if(new_trigger) {
				cv.put(NEW, 1);
			} else {
				cv.put(NEW, 0);
			}
			// ADDED - getdate

			this.mDb.insert("politicsfeed", MATCH, cv);
		}
	}

	public void insert_commons_debate(String trigger, long trigger_id, Integer commons_id, boolean new_trigger) {

		if(checkExists(commons_id, House.COMMONS) == false) {
		  Trigger trigger_type;

		  ContentValues cv = new ContentValues();

		  CommonsDBHelper commonshelper = new CommonsDBHelper(this.mCtx).open();

		//Log.v("PPP", "Adding feed entry for debate : " + commons_id.toString());
		Cursor curs = commonshelper.getDebate(commons_id);

		curs.moveToFirst(); // TODO check got result

		/* Create message
			New bill ' ' matched trigger ' '
			Bill ' ' matching trigger ' ' has moved to stage ''
			Bill ' ' matching trigger' ' has moved to house ''
		*/

		Chamber chamber = commonshelper.getChamber(curs);

		if(chamber == Chamber.MAIN) {
			trigger_type = Trigger.MAIN;
		} else if(chamber == Chamber.SELECT) {
			trigger_type = Trigger.SELECT;
		} else if(chamber == Chamber.WESTMINSTER) {
			trigger_type = Trigger.WESTMINSTER;
		} else {
			trigger_type = Trigger.GENERAL;
		}
		
		commonshelper.close();


		// TODO Check trigger doesn't already exist 

			cv.put(MATCH, trigger);
//			Calendar c = Calendar.getInstance();
//			c.set(Calendar.HOUR, 0);
//			c.set(Calendar.MINUTE, 0);
//			c.set(Calendar.SECOND, 0);
//			c.set(Calendar.MILLISECOND, 0);
//			cv.put(DATE, c.getTimeInMillis() / 1000);
			cv.put(DATE, commonshelper.getDateLong(curs));
			cv.put(TRIGGER_ID, trigger_id);
			cv.put(TRIGGER_TYPE, trigger_type.ordinal());
			cv.put(ITEM_ID, commons_id);
			cv.put(HOUSE, House.COMMONS.ordinal());
			cv.put(HIGHLIGHT, 0);
			cv.put(READ, 0);
			if(new_trigger) {
				cv.put(NEW, 1);
			} else {
				cv.put(NEW, 0);
			}
			// ADDED - getdate

			this.mDb.insert("politicsfeed", MATCH, cv);
		} else {
			//Log.v("PPP", "ALready in feed");
		}
	}

	public void insert_lords_debate(String trigger, long trigger_id, Integer lords_id, boolean new_trigger) {

		if(checkExists(lords_id, House.LORDS) == false) {
		Trigger trigger_type;
		ContentValues cv = new ContentValues();
		LordsDBHelper lordshelper = new LordsDBHelper(this.mCtx).open();

		//Log.v("PPP", "Adding feed entry for debate : " + lords_id.toString());
		Cursor curs = lordshelper.getDebate(lords_id);

		curs.moveToFirst(); // TODO check got result

		/* Create message
			New bill ' ' matched trigger ' '
			Bill ' ' matching trigger ' ' has moved to stage ''
			Bill ' ' matching trigger ' ' has moved to house ''
		*/

		Chamber chamber = lordshelper.getChamber(curs);

		if(chamber == Chamber.MAIN) {
			trigger_type = Trigger.MAIN;
		} else if(chamber == Chamber.SELECT) {
			trigger_type = Trigger.SELECT;
		} else {
			trigger_type = Trigger.GRAND;
		}
		

		lordshelper.close();

		// TODO Check trigger doesn't already exist 

			cv.put(MATCH, trigger);
			cv.put(TRIGGER_ID, trigger_id);
			cv.put(TRIGGER_TYPE, trigger_type.ordinal());
			cv.put(ITEM_ID, lords_id);
			cv.put(HOUSE, House.LORDS.ordinal());
//			Calendar c = Calendar.getInstance();
//			c.set(Calendar.HOUR, 0);
//			c.set(Calendar.MINUTE, 0);
//			c.set(Calendar.SECOND, 0);
//			c.set(Calendar.MILLISECOND, 0);
//			cv.put(DATE, c.getTimeInMillis() / 1000);
			cv.put(DATE, lordshelper.getDateLong(curs));
			cv.put(HIGHLIGHT, 0);
			cv.put(READ, 0);
			if(new_trigger) {
				cv.put(NEW, 1);
			} else {
				cv.put(NEW, 0);
			}

			this.mDb.insert("politicsfeed", MATCH, cv);
		} else {
			//Log.v("PPP", "Already exists, skipping");
		}
	}

	public Cursor getPoliticsFeed() {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			// Try to ensure we include bills/acts with today's
			// date by adding half-day leeway.
			Long today = (c.getTimeInMillis() / 1000) - 43200;
			String[] args = {today.toString()};
		return(this.mDb.rawQuery("SELECT _id, match, trigger_id, trigger_type, item_id, house, highlight, read, new, date from politicsfeed WHERE date >= ? ORDER BY DATE asc, _id asc", args));
	}

	public Integer getPoliticsFeedCount() {
		Integer nf_count = -1;
		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from politicsfeed", null);
		c.moveToFirst();
		nf_count = c.getInt(0);
		return nf_count ;
	}

/*
	public Cursor getpoliticsfeedFiltered(String match) {
		String filter = new String('%' + match + '%');
		String [] args = {filter};

		return(this.mDb.rawQuery("SELECT _id,title from triggers WHERE match LIKE ? ORDER BY date desc", args));
	}

	private boolean checkTriggerByMatch(String match) {
		String [] args = {match};

		Log.v("PPP", "Checking existence of trigger with match: " + match);

		Cursor r = this.mDb.rawQuery("SELECT _id from triggers WHERE match = ?", args);

		if(r.getCount() > 0) {
			r.close();
			return true;
		} else {
			r.close();
			return false;
		}
	}
*/
	
	public String getURL(Cursor c) {
		Trigger trigger = getTriggerType(c);
		String url = "";

		if(trigger == Trigger.ACT) {
			Cursor d = actshelper.getAct(getItemID(c));
			d.moveToFirst(); // TODO check got result
			url = actshelper.getURL(d);
		} else if (trigger == Trigger.BILL) {
			Cursor d = billshelper.getBill(getItemID(c));
			d.moveToFirst(); // TODO check got result
			url = billshelper.getURL(d);
		} else if (trigger == Trigger.MAIN) {
			House house = getHouseRaw(c);
			if(house == House.COMMONS) {
			  Cursor d = commonshelper.getDebate(getItemID(c));
			  d.moveToFirst();
			  url = commonshelper.getURL(d);
			} else {
			  Cursor d = lordshelper.getDebate(getItemID(c));
			  d.moveToFirst();
			  url = lordshelper.getURL(d);
			}
		} else if (trigger == Trigger.SELECT) {
			// Need to get house
			House house = getHouseRaw(c);
			if(house == House.COMMONS) {
			  Cursor d = commonshelper.getDebate(getItemID(c));
			  d.moveToFirst();
			  url = commonshelper.getURL(d);
			} else {
			  Cursor d = lordshelper.getDebate(getItemID(c));
			  d.moveToFirst();
			  url = lordshelper.getURL(d);
			}
		} else if (trigger == Trigger.WESTMINSTER) {
			Cursor d = commonshelper.getDebate(getItemID(c));
			d.moveToFirst();
			url = commonshelper.getURL(d);
		} else if (trigger == Trigger.GRAND) {
			Cursor d = lordshelper.getDebate(getItemID(c));
			d.moveToFirst();
			url = lordshelper.getURL(d);
		} else if (trigger == Trigger.GENERAL) {
			Cursor d = commonshelper.getDebate(getItemID(c));
			d.moveToFirst();
			url = commonshelper.getURL(d);
		}
			
		return url;
	}
			

	public String getMessage(Cursor c) {
		// Need to do some work here
		Trigger trigger = getTriggerType(c);
		String msg = "";

		if(trigger == Trigger.ACT) {
			Cursor d = actshelper.getAct(getItemID(c));
			d.moveToFirst(); // TODO check got result
			msg = actshelper.getTitle(d) + " is now an Act.";
		} else if (trigger == Trigger.BILL) {
			Cursor d = billshelper.getBill(getItemID(c));
			d.moveToFirst(); // TODO check got result
			msg = "'" + billshelper.getTitle(d) + "' now at " + billshelper.getStage(d).toString() + " stage.";
		} else if (trigger == Trigger.MAIN) {
			// Need to get house
			House house = getHouseRaw(c);
			if(house == House.COMMONS) {
			  Cursor d = commonshelper.getDebate(getItemID(c));
			  d.moveToFirst();
			  msg = "'" + commonshelper.getSubject(d) + "'";
			  msg += " will be debated at ";
			  msg += "'" + commonshelper.getTitle(d) + "'.";
			} else {
			  Cursor d = lordshelper.getDebate(getItemID(c));
			  d.moveToFirst();
			  msg = "'" + lordshelper.getSubject(d) + "'";
			  msg += " will be debated at ";
			  msg += "'" + lordshelper.getTitle(d) + "'.";
			}
		} else if (trigger == Trigger.SELECT) {
			// Need to get house
			House house = getHouseRaw(c);
			String subject;
			if(house == House.COMMONS) {
			  Cursor d = commonshelper.getDebate(getItemID(c));
			  d.moveToFirst();
			  subject = commonshelper.getSubject(d).trim();
			  if(subject == null || "".equals(subject)) {
			    msg = "Meeting of the ";
			  } else {
			    msg = "'" + commonshelper.getSubject(d) + "'";
			    msg += " will be discussed at the ";
			  }
			  msg += commonshelper.getTitle(d) + " committee.";
			} else {
			  Cursor d = lordshelper.getDebate(getItemID(c));
			  d.moveToFirst();
			  subject = lordshelper.getSubject(d).trim();
			  if(subject == null || "".equals(subject)) {
			    msg = "Meeting of the ";
			  } else {
			    msg = "'" + lordshelper.getSubject(d) + "'";
			    msg += " will be discussed at the ";
			  }
			  msg += lordshelper.getTitle(d) + " committee.";
			}
		} else if (trigger == Trigger.WESTMINSTER) {
			Cursor d = commonshelper.getDebate(getItemID(c));
			d.moveToFirst();
			msg = "'" + commonshelper.getSubject(d) + "'";
			msg += " will be debated.";
		} else if (trigger == Trigger.GRAND) {
			Cursor d = lordshelper.getDebate(getItemID(c));
			d.moveToFirst();
			msg = "'" + lordshelper.getTitle(d) + "'";
			msg += " will be debated.";
		} else if (trigger == Trigger.GENERAL) {
			Cursor d = commonshelper.getDebate(getItemID(c));
			d.moveToFirst();
			msg = "'" + commonshelper.getSubject(d) + "'";
			msg += " will be discussed by the ";
			msg += commonshelper.getCommittee(d) + ".";
		}

		// SI, Draft SI, Reports...
			
		return msg;
	}
	public void clearTrigger(String trigger_id) {
		String [] args = {trigger_id};

		//Log.v("PPP", "Clearing feed of trigger: " + trigger_id);

		this.mDb.delete("politicsfeed", "trigger_id = ?", args);
	}

	// Get all talks marked as wanted to be read and older than yesterday
	public Cursor getReady() {
		// Bit of a kludge, just assume anything from yesterday or before is online.
		// TODO need to make this work ith scroll-on-demand list
		return(this.mDb.rawQuery("SELECT _id, match, trigger_id, trigger_type, item_id, house, highlight, read, new, date from politicsfeed WHERE read = 1 AND date < strftime('%s', 'now', '-1 day') ORDER BY date desc LIMIT 50", null));
	}

	// Show talks from yesterday that are marked as wanting to be read
	public Integer getReadyCount() {
		Integer ready_count = -1;
		Cursor c = this.mDb.rawQuery("SELECT COUNT(*) from politicsfeed WHERE read = 1 AND date < strftime('%s', 'now', '-1 day') and date > strftime('%s', 'now', '-2 day') LIMIT 50", null);
		c.moveToFirst();
		ready_count = c.getInt(0);
		return ready_count ;
	}

	public void markReader(Integer debate_id) {
		String [] args = {debate_id.toString()};
		
		this.mDb.execSQL("UPDATE politicsfeed SET read = 1 WHERE _id = " + debate_id.toString());
		
	}

	public void markFeedOld() {
		this.mDb.execSQL("UPDATE politicsfeed SET new = 0");
	}

	public int getId(Cursor c) {
		
		return(c.getInt(0));
	}

	public String getMatch(Cursor c) {
		
		return(c.getString(1));
	}

	public int getTriggerID(Cursor c) {

		return(c.getInt(2));
	}

	public Trigger getTriggerType(Cursor c) {
		
		int type = c.getInt(3);
		
		return(Trigger.values()[type]);
	}

	public int getItemID(Cursor c) {

		return(c.getInt(4));
	}

	public House getHouseRaw(Cursor c) {
		return(House.values()[c.getInt(5)]);
	}

	public String getHouse(Cursor c) {
		int house = c.getInt(5);
		String name;
		if(House.values()[house] != House.NEITHER) {
			name = "(" + House.values()[house].toShort() + ")";
		} else {
			name = "";
		}
		
		return(name);
	}

	public int getHighlight(Cursor c) {

		return(c.getInt(6));
	}

	public int getRead(Cursor c) {

		return(c.getInt(7));
	}

	public int getNew(Cursor c) {

		return(c.getInt(8));
	}

	public int getDate(Cursor c) {

		return(c.getInt(9));
	}

	public Date getDateLong(Cursor c) {
                Long timestamp = c.getLong(9) * 1000;

                return(new Date(timestamp));
	}

	public void onDestroy() {
		actshelper.close();
		billshelper.close();
		commonshelper.close();
		lordshelper.close();
	}
}
