package uk.org.tiro.android.PPP;

// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public abstract class Item implements Comparable<Item> {
	ThreadSafeSimpleDateFormat FORMATTER =
		new ThreadSafeSimpleDateFormat("yyyy-MMM-dd");

	private String title;
	private String url;
	private String guid;
	private String description;
	private Date date;
	private House house;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title.trim();
	}

	public String getURL() {
		return url;
	}
	
	public void setURL(String url) {
		this.url= url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description.trim();
	}

	public String getDate() {
		return FORMATTER.format(this.date);
	}
	
	public Date getRawDate() {
		return this.date;
	}

	public void setDate(String date) {

//		try {
//			this.date = FORMATTER.parse(date.trim());	
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
	}

	abstract public Item copy();
	abstract public String toString();
	abstract public int hashCode();
	abstract public boolean equals(Object obj);
	// abstract public int compareTo(Classtype);

	// Thread safe version from 
	// http://www.codefutures.com/weblog/andygrove/2007/10/simpledateformat-and-thread-safety.html

	public class ThreadSafeSimpleDateFormat {
		private DateFormat df;

		public ThreadSafeSimpleDateFormat(String format) {
			this.df = new SimpleDateFormat(format);
		}

		public synchronized String format(Date date) {
			return df.format(date);
		}

		public synchronized Date parse(String string) throws ParseException {
			return df.parse(string);
		}
	}

}
