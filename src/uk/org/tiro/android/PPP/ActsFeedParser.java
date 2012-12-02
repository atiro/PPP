package uk.org.tiro.android.PPP;


// Code taken from IBM Developerworks
// http://www.ibm.com/developerworks/opensource/library/x-android/


import java.util.ArrayList;
import java.util.List;
import java.net.UnknownHostException;

import android.sax.Element;
import android.sax.StartElementListener;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import org.xml.sax.Attributes;
import android.util.Xml;
import android.util.Xml.Encoding;

import android.util.Log;

public class ActsFeedParser extends BaseFeedParser {

	static final String FEED  = "feed";
	static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";

	private Xml.Encoding encoding = Xml.Encoding.UTF_8; // Default

	public ActsFeedParser(String feedUrl, String encoding) {
		super(feedUrl);
		if(encoding.equals("ISO-8859-1")) {
			this.encoding = Xml.Encoding.ISO_8859_1;
		}
	}

	public List<Act> parse() {
		final Act currentAct = new Act();
		RootElement root = new RootElement(ATOM_NAMESPACE, FEED);
		final List<Act> acts = new ArrayList<Act>();

		Element item = root.getChild(ATOM_NAMESPACE, ENTRY);

		item.setEndElementListener(new EndElementListener() {
			public void end() {
				//Log.v("PPP", "End of Act: " + currentAct.getTitle());
				Act copy = currentAct.copy();
				// Log.v("PPP", "Added act to list: " + copy.getTitle());
				acts.add(copy);
			}
		});

		item.getChild(ATOM_NAMESPACE, ID).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentAct.setGUID(body);
                                currentAct.setURL(body);
			}
		});
		item.getChild(ATOM_NAMESPACE, TITLE).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentAct.setTitle(body);
			}
		});

/*		item.getChild(ATOM_NAMESPACE, LINK).setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) {
                                currentAct.setURL(body);
			}
		});
		*/

		item.getChild(ATOM_NAMESPACE, SUMMARY).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentAct.setSummary(body);
			}
		});


		// TODO Date

		try {
			Xml.parse(this.getInputStream(), this.encoding, root.getContentHandler());
		} catch (UnknownHostException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return acts;
	}
}
