package uk.org.tiro.android.PPP;

// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseFeedParser implements FeedParser {

	static final String CHANNEL = "channel";
	static final String LINK = "link";
	static final String GUID = "guid";
	static final String TITLE = "title";
	static final String ITEM = "item";
	static final String CATEGORY = "category";
	static final String DESCRIPTION = "description";

	/* Atom Feeds */
	static final String FEED = "feed";
	static final String ENTRY = "entry";
	static final String SUMMARY = "summary";

	/* Calendar feeds */

	static final String PARLYCAL = "parlycal";
	static final String EVENT = "event";
	static final String CHAMBER = "chamber";
	static final String DATE = "date";
	static final String COMMITTEE = "comittee"; // sic
	static final String INQUIRY = "inquiry";
	static final String WITNESSES = "witnesses";
	static final String LOCATION = "location";
	static final String SUBJECT = "subject";
	static final String STARTTIME = "startTime";

	private final URL feedUrl;

	protected BaseFeedParser(String feedUrl) {
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	protected InputStream getInputStream() {
		try {
			return feedUrl.openConnection().getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

