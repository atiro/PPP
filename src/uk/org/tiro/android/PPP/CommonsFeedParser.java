package uk.org.tiro.android.PPP;


// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/


import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.StartElementListener;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import org.xml.sax.Attributes;
import android.util.Xml;
import android.util.Xml.Encoding;

import android.util.Log;

public class CommonsFeedParser extends BaseFeedParser {

	static final String RSS = "rss";
	static final String PARLY_NS = "http://services.parliament.uk/ns/calendar/feeds";

	private Xml.Encoding encoding = Xml.Encoding.UTF_8; // Default

	public CommonsFeedParser(String feedUrl, String encoding) {
		super(feedUrl);
		if(encoding.equals("ISO-8859-1")) {
			this.encoding = Xml.Encoding.ISO_8859_1;
		}
	}

	public List<CommonsDebate> parse() {
		final CommonsDebate currentDebate = new CommonsDebate();
		RootElement root = new RootElement(RSS);
		final List<CommonsDebate> debates = new ArrayList<CommonsDebate>();

		Element channel = root.getChild(CHANNEL);
		Element item = channel.getChild(ITEM);


		item.setEndElementListener(new EndElementListener() {
			public void end() {
				CommonsDebate copy = currentDebate.copy();
			//	Log.v("PPP", "Added bill to list: " + copy.getTitle());
				currentDebate.clear();
				debates.add(copy);
			}
		});

		item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentDebate.setTitle(body);
			}
		});

		item.getChild(LINK).setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) {
                                currentDebate.setURL(body);
			}
		});

                item.getChild(GUID).setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) { 
                                currentDebate.setGUID(body);
                        }       
                });

		Element event = item.getChild(PARLY_NS, EVENT);

                event.getChild(PARLY_NS, CHAMBER).setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) { 
					//Log.v("ppp", "Setting chamber to :" + body);
					currentDebate.setChamber(body);
                        }       
                });

			// Typo or to void name clash ?
		event.getChild(PARLY_NS, COMMITTEE).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentDebate.setCommittee(body);
			}
		});

		event.getChild(PARLY_NS, SUBJECT).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentDebate.setSubject(body);
			}
		});

		event.getChild(PARLY_NS, LOCATION).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentDebate.setLocation(body);
			}
		});

		event.getChild(PARLY_NS, INQUIRY).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentDebate.setSubject(body);
			}
		});

		event.getChild(PARLY_NS, DATE).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				//Log.v("ppp", "Setting date to :" + body);
				currentDebate.setDate(body);
			}
		});
		event.getChild(PARLY_NS, STARTTIME).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				//Log.v("ppp", "Setting time to :" + body);
				currentDebate.setTime(body);
			}
		});

		try {
			Xml.parse(this.getInputStream(), this.encoding, root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return debates;
	}
}
