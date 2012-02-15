package uk.org.tiro.android.PPP;

import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Act implements Comparable<Act> {
	private String title = null;
	private String summary = null;
	private String url = null;

	private Date date = null;

	ThreadSafeSimpleDateFormat FORMATTER =
			new ThreadSafeSimpleDateFormat("yyyy-MMM-dd");

	public Act(String title, String summary, String url, Date date) {

		this.title = title;
		this.summary= summary;

		this.url = url;
		this.date = date;
	}

	public Act() {
		this.title = "";
		this.summary = "";

		this.url = "";
		this.date = null;
	}

		
	public void setTitle(String title) {
		
		this.title = title;
	}

	public String getTitle() {

		return title;
	}

	public void setURL(String url) {

		this.url = url;
	}

	public String getURL() {

		return url;
	}

	public String getDate() {
		return FORMATTER.format(this.date);
	}
	
	public Date getRawDate() {
		return this.date;
	}


	public void setSummary(String summary) {

		this.summary= summary;
	}

	public String getSummary() {

		return summary;
	}

	public Act copy() {
		return new Act( title,
				     summary,
				     url,
				     date);

	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Title: ");
		sb.append(title);
		sb.append("\n");
		sb.append("Date: ");
		sb.append(this.getDate());
		sb.append("\n");
		sb.append("Summary: ");
		sb.append(this.summary);
		sb.append("\n");
		sb.append("URL: ");
		sb.append(this.url);
		sb.append("\n");
		
		return sb.toString();

	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((summary == null) ? 0 : summary.hashCode());
		result = prime * result + ((url == null) ? 0: url.hashCode());

		result = prime * result + ((title == null) ? 0 : title.hashCode());

		return result;
	}

	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if (obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Act other = (Act) obj;
		if(date == null) {
			if(other.date != null)
				return false;
		} else if(!date.equals(other.date)) {
			return false;
		}

		if(summary == null) {
			if(other.summary != null)
				return false;
		} else if(!summary.equals(other.summary)) {
			return false;
		}

		if(url == null) {
			if(other.url != null)
				return false;
		} else if(!url.equals(other.url)) {
			return false;
		}

		if(title == null) {
			if(other.title != null)
				return false;
		} else if(!title.equals(other.title)) {
			return false;
		}

		return true;
	}

	public int compareTo(Act another) {

		if(another == null) return 1;

		return another.date.compareTo(date);
	}


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

