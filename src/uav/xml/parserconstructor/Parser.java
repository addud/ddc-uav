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

	// 05-16 18:07:58.646: D/NCSerialProtocol(616): rx current position:
	// 598411852 , 176454225
	// 05-16 18:10:51.166: D/NCSerialProtocol(733): rx current position:
	// 598412117 , 176460251
	// 05-16 18:11:26.726: D/NCSerialProtocol(733): rx current position:
	// 598414271 , 176454996

	public ArrayList<NCWayPoint> parse() throws XmlPullParserException,
			IOException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(
				"<coordinates>"
						+ "<coordinate><id>0</id><lat>59.84118520000000</lat><lng>17.645422500000000</lng><picture/><data/></coordinate>"
						+ "<coordinate><id>0</id><lat>59.84121170000000</lat><lng>17.646025100000000</lng><picture/><data/></coordinate>"
						+ "<coordinate><id>0</id><lat>59.84142710000000</lat><lng>17.645499600000000</lng><picture/><data/></coordinate>"
						+ "<coordinate><id>0</id><lat>59.84118540000000</lat><lng>17.645422400000000</lng><picture/><data/></coordinate>"
						+ "<coordinate><id>0</id><lat>59.84121170000000</lat><lng>17.646025100000000</lng><picture/><data/></coordinate>"
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
					wyp = new NCWayPoint(0, 0, 1000);
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
