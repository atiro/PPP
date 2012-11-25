package uk.org.tiro.android.PPP;

import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CommonsDebate implements Comparable<CommonsDebate> {
	private Chamber chamber;

	private String title = null;
	private String committee = null;
	private String subject = null;
	private String guid = null;
	private String url = null;
	private String location = null;
	private String witnesses = null;

	private Date date = null;
	private String time = null;

	ThreadSafeSimpleDateFormat FORMATTER =
			new ThreadSafeSimpleDateFormat("yyyy-MM-dd");

	public CommonsDebate(Chamber chamber, String title, String committee,
			String subject, String guid, String location, String url, Date date, String time, String witnesses ) {

		this.title = title;
		this.committee = committee;

		this.chamber = chamber;
		this.subject = subject;
		this.location = location;
		this.witnesses = witnesses;

		this.guid = guid;
		this.url = url;
		this.date = date;
		this.time = time;

	}

	public CommonsDebate() {
		this.title = "";
		this.committee = "";

		this.chamber = Chamber.OTHER;

		this.guid = "";
		this.location = "";
		this.url = "";
		this.witnesses = "";

		this.date = null;
		this.time = "";
	}

		
	public void clear() {
		this.title = "";
		this.committee = "";
		this.chamber = Chamber.OTHER;

		this.guid = "";
		this.location = "";
		this.url = "";
		this.witnesses = "";

		this.date = null;
		this.time = "";
	}

	public void setLocation(String location) {
		
		this.location = location;
	}

	public void setTime(String time) {
		this.time = time.substring(0, time.lastIndexOf(':')).replaceAll(":", ".");
	}

	public String getTime() {
		return time;
	}
		
	public String getLocation() {

		return location;
	}

	public void setTitle(String title) {
		
		this.title = title;
	}

	public String getTitle() {

		return title;
	}

	public void setSubject(String subject) {
		
		this.subject = subject;
	}

	public String getSubject() {

		return subject;
	}

	public void setURL(String url) {

		this.url = url;
	}

	public String getURL() {

		return url;
	}

	public void setGUID(String guid) {
		
		this.guid = guid;
	}

	public void setDate(String date) {

		// TODO parse it

		// this.date = date;

		try {
			this.date = FORMATTER.parse(date.trim());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

	public String getDate() {
		return FORMATTER.format(this.date);
	}
	
	public Date getRawDate() {
		return this.date;
	}


	public String getGUID() {

		return guid;
	}

	public void setCommittee(String committee) {

		this.committee = committee;
	}

	public String getCommittee() {

		return committee;
	}

	public void setWitnesses(String witnesses) {

		this.witnesses = witnesses;
	}

	public String getWitnesses() {

		return witnesses;
	}

	public void setChamber(String chamber) {

		if(chamber.equals("Main Chamber")) {
			this.chamber = Chamber.MAIN;
		} else if(chamber.equals("Select Committee")) {
			this.chamber = Chamber.SELECT;
		} else if(chamber.equals("Westminster Hall")) {
			this.chamber = Chamber.WESTMINSTER;
		} else if(chamber.equals("General Committee")) {
			this.chamber = Chamber.GENERAL;
		} else {
			this.chamber = Chamber.OTHER;
		}
	}

	public Chamber getChamber() {
		return chamber;
	}


	public CommonsDebate copy() {
		return new CommonsDebate(chamber,
				     title,
				     committee,
				     subject,
				     guid,
				     location,
				     url,
				     date,
				     time,
				     witnesses);

	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Title: ");
		sb.append(title);
		sb.append("\n");
		sb.append("Date: ");
		sb.append(this.getDate());
		sb.append("\n");
		sb.append("Subject: ");
		sb.append(subject);
		sb.append("\n");
		sb.append("URL: ");
		sb.append(url);
		sb.append("\n");
		
		return sb.toString();

	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((date == null)? 0 : date.hashCode());
		result = prime * result + ((subject== "") ? 0 : subject.hashCode());
		result = prime * result + ((url == "") ? 0: url.hashCode());

		result = prime * result + ((guid == "") ? 0: guid.hashCode());

		result = prime * result + ((title == "") ? 0 : title.hashCode());
		result = prime * result + ((title == "") ? 0 : witnesses.hashCode());
		result = prime * result + ((time == "") ? 0 : time.hashCode());

		return result;
	}

	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if (obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		CommonsDebate other = (CommonsDebate) obj;
		if(date == null) {
			if(other.date != null)
				return false;
		} else if(!date.equals(other.date)) {
			return false;
		}

		if(subject== null) {
			if(other.subject!= null)
				return false;
		} else if(!subject.equals(other.subject)) {
			return false;
		}

		if(url == null) {
			if(other.url != null)
				return false;
		} else if(!url.equals(other.url)) {
			return false;
		}

		if(guid == null) {
			if(other.guid != null)
				return false;
		} else if(!guid.equals(other.guid)) {
			return false;
		}

		if(title == null) {
			if(other.title != null)
				return false;
		} else if(!title.equals(other.title)) {
			return false;
		}

		if(location == null) {
			if(other.location != null)
				return false;
		} else if(!location.equals(other.location)) {
			return false;
		}

		if(committee == null) {
			if(other.committee != null)
				return false;
		} else if(!committee.equals(other.committee)) {
			return false;
		}

		if(witnesses == null) {
			if(other.witnesses != null)
				return false;
		} else if(!witnesses.equals(other.witnesses)) {
			return false;
		}

		if(time == null) {
			if(other.time != null)
				return false;
		} else if(!time.equals(other.time)) {
			return false;
		}

		return true;
	}

	public int compareTo(CommonsDebate another) {

		if(another == null) return 1;

		return another.date.compareTo(date);
	}


	public class ThreadSafeSimpleDateFormat {
		private DateFormat df;

		public ThreadSafeSimpleDateFormat(String format) {
			this.df = new SimpleDateFormat(format);
			// Avoid problems with British Summer Time 
			this.df.setTimeZone(TimeZone.getTimeZone("GMT"));
		}

		public synchronized String format(Date date) {
			return df.format(date);
		}

		public synchronized Date parse(String string) throws ParseException {
			return df.parse(string);
		}
	}

}

