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

public class BillsFeedParser extends BaseFeedParser {

	static final String RSS = "rss";

	private Xml.Encoding encoding = Xml.Encoding.UTF_8; // Default

	public BillsFeedParser(String feedUrl, String encoding) {
		super(feedUrl);
		if(encoding.equals("ISO-8859-1")) {
			this.encoding = Xml.Encoding.ISO_8859_1;
		}
	}

	public List<Bill> parse() {
		final Bill currentBill = new Bill();
		RootElement root = new RootElement(RSS);
		final List<Bill> bills = new ArrayList<Bill>();

		Element channel = root.getChild(CHANNEL);
		Element item = channel.getChild(ITEM);


		item.setStartElementListener(new StartElementListener() {
			public void start(Attributes attrib) {
				String stage = attrib.getValue("stage");
				if(stage != null) {
					currentBill.setStage(stage);
				}
			}
		});

		item.setEndElementListener(new EndElementListener() {
			public void end() {
				Bill copy = currentBill.copy();
			//	Log.v("PPP", "Added bill to list: " + copy.getTitle());
				bills.add(copy);
			}
		});

		item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentBill.setTitle(body);
			}
		});

		item.getChild(LINK).setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) {
                                currentBill.setURL(body);
			}
		});

                item.getChild(GUID).setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) { 
                                currentBill.setGUID(body);
                        }       
                });

                item.getChild(CATEGORY).setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) { 
				if(body.equals("Commons") || body.equals("Lords") || body.equals("Not assigned")) {
 	                               currentBill.setHouse(body);
				} else {
					currentBill.setType(body);
				}
                        }       
                });

		item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentBill.setDescription(body);
			}
		});


		try {
			Xml.parse(this.getInputStream(), this.encoding, root.getContentHandler());
		} catch (UnknownHostException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return bills;
	}
}
