package uk.org.tiro.android.PPP;

import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Bill implements Comparable<Bill> {
	private House house;
	private Stage stage;
	private Type type;

	private String title = null;
	private String description = null;
	private String guid = null;
	private String url = null;

	private Date date = null;

	ThreadSafeSimpleDateFormat FORMATTER =
			new ThreadSafeSimpleDateFormat("yyyy-MMM-dd");

	public Bill(House house, Stage stage, Type type, String title, 
		String description, String url, String guid, Date date) {

		this.title = title;
		this.description = description;

		this.house = house;
		this.stage = stage;
		this.type = type;

		this.guid = guid;
		this.url = url;
		this.date = date;
	}

	public Bill() {
		this.title = "";
		this.description = "";

		this.house = House.NEITHER;
		this.stage = Stage.UNKNOWN;
		this.type = Type.UNKNOWN;

		this.guid = "";
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

	public void setGUID(String guid) {
		
		this.guid = guid;
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

	public void setDescription(String description) {

		this.description = description;
	}

	public String getDescription() {

		return description;
	}

	public void setHouse(String bill_house) {

		if(bill_house.equals("Commons")) {
			this.house = House.COMMONS;
		} else if(bill_house.equals("Lords")) {
			this.house = House.LORDS;
		} else {
			this.house = House.NEITHER;
		}
	}

	public House getHouse() {

		return house;
	}

	public void setStage(String bill_stage) {
		if(bill_stage.equals("2nd reading")) {
			stage = Stage.SECONDREADING;
		} else if(bill_stage.equals("1st reading")) {
			stage = Stage.FIRSTREADING;
		} else if(bill_stage.equals("Report stage")) {
			stage = Stage.REPORT;
		} else if(bill_stage.equals("Committee stage")) {
			stage = Stage.COMMITTEE;
		} else if(bill_stage.equals("3rd reading")) {
			stage = Stage.THIRDREADING;
		} else if(bill_stage.equals("Royal Assent")) {
			stage = Stage.ROYAL;
		} else if(bill_stage.equals("Commons Examiners")) {
			stage = Stage.EXAMINERS;
		} else if(bill_stage.equals("Lords Examiners")) {
			stage = Stage.EXAMINERS;
		} else if(bill_stage.equals("Consideration of Commons amendments")) {
			stage = Stage.CONSIDERATION;
		} else if(bill_stage.equals("Consideration of Lords amendments")) {
			stage = Stage.CONSIDERATION;
		} else if(bill_stage.equals("Second reading committee")) {
			stage = Stage.READING;
		} else {
			stage = Stage.UNKNOWN;
		}
	}

	public Stage getStage() {

		return stage;
	}

	public void setType(String bill_type) {

		if(bill_type.equals("Private Bill")) {
			type = type.PRIVATE;
		} else if(bill_type.equals("Private Members' Bill (under the Ten Minute Rule, SO No 23)")) {
			type = type.PMB_TEN;
		} else if(bill_type.equals("Private Members' Bill (Starting in the House of Lords)")) {
			type = type.PMB_LORDS;
		} else if(bill_type.equals("Private Members' Bill (Presentation Bill)")) {
			type = type.PMB_PRESENTATION;
		} else if(bill_type.equals("Private Members' Bill (Ballot Bill)")) {
			type = type.PMB_BALLOT;
		} else if(bill_type.equals("Government Bill")) {
			type = type.GOVERNMENT;
		} else if(bill_type.equals("Consolidation Bill")) {
			type = type.CONSOLIDATION; // Not seen in feed yet
		} else if(bill_type.equals("Hybrid Bill")) {
			type = type.HYBRID; // Not seen in feed yet
		} // cannot have catch-all else as category element
		  // also used for other information types, rather confusing.
	}

	public Type getType() {
		return type;
	}


	public Bill copy() {
		return new Bill(house,
				     stage,
				     type,
				     title,
				     description,
				     url,
				     guid,
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
		sb.append("Description: ");
		sb.append(description);
		sb.append("\n");
		sb.append("URL: ");
		sb.append(url);
		sb.append("\n");
		
		return sb.toString();

	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((url == null) ? 0: url.hashCode());

		result = prime * result + ((guid == null) ? 0: guid.hashCode());

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
		Bill other = (Bill) obj;
		if(date == null) {
			if(other.date != null)
				return false;
		} else if(!date.equals(other.date)) {
			return false;
		}

		if(description == null) {
			if(other.description != null)
				return false;
		} else if(!description.equals(other.description)) {
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

		if(house == null) {
			if(other.house != null)
				return false;
		} else if(house != other.house) {
			return false;
		}

		if(type == null) {
			if(other.type != null)
				return false;
		} else if(type != other.type) {
			return false;
		}

		if(stage == null) {
			if(other.stage != null)
				return false;
		} else if(stage != other.stage) {
			return false;
		}

		return true;
	}

	public int compareTo(Bill another) {

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

