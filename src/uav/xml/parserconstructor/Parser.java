package uav.xml.parserconstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import uav.nc.usb.NCWayPoint;
import android.os.Environment;

public class Parser {
	private ArrayList<NCWayPoint> pointList;

	public Parser() {
		pointList = new ArrayList<NCWayPoint>();
	}

	public ArrayList<NCWayPoint> parse() throws XmlPullParserException,
			IOException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(
				"<coordinates>"
						+ "<coordinate><id>0</id><lat>59.84138710000000</lat><lng>17.645552900000000</lng><picture/><data/></coordinate>"
						+ "<coordinate><id>0</id><lat>59.84117250000000</lat><lng>17.645402900000000</lng><picture/><data/></coordinate>"
						+ "<coordinate><id>0</id><lat>59.84137610000000</lat><lng>17.645132900000000</lng><picture/><data/></coordinate>"
						// +
						// "<coordinate><id>0</id><lat>59.84137100000000</lat><lng>17.645181900000000</lng><picture/><data/></coordinate>"
						+ "</coordinates>"));

		// xpp.setInput(new StringReader (getXMLfromSD()));
		int eventType = xpp.getEventType();
		String type = "";
		NCWayPoint wyp = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {
			} else if (eventType == XmlPullParser.START_TAG) {
				if (xpp.getName().equalsIgnoreCase("coordinate")) {
					type = "coordinate";
					wyp = new NCWayPoint(0, 0, 5000);
				} else if (xpp.getName().equalsIgnoreCase("lat")) {
					type = "lat";
				} else if (xpp.getName().equalsIgnoreCase("lng")) {
					type = "lng";
				}
			} else if (eventType == XmlPullParser.TEXT) {
				if (type.equalsIgnoreCase("lat")) {
					// K=K+Integer.valueOf(xpp.getText().toString().replace(".",
					// "").trim().substring(0, 9))+",";
					wyp.setLat(Integer.valueOf(xpp.getText().toString()
							.replace(".", "").trim().substring(0, 9)));
				} else if (type.equalsIgnoreCase("lng")) {
					// K=K+Integer.valueOf(xpp.getText().toString().replace(".",
					// "").trim().substring(0, 9))+",";
					wyp.setLon(Integer.valueOf(xpp.getText().toString()
							.replace(".", "").trim().substring(0, 9)));

					pointList.add(wyp);
				}
			}
			eventType = xpp.next();
		}
		return pointList;
	}

	private String getXMLfromSD() {
		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard, "UAV.xml");
		StringBuilder text = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} catch (IOException e) {
			// You'll need to add proper error handling here
		}
		return text.toString();
	}

}
