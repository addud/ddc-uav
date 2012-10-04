package uav.xml.parserconstructor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import uav.nc.usb.NCWayPoint;

import android.os.Environment;

public class Parser {
	private ArrayList<NCWayPoint> pointList;
	public Parser()
	{
		pointList = new ArrayList<NCWayPoint>();
	}
	public ArrayList<NCWayPoint> parse() throws XmlPullParserException, IOException
	{
		
		/*NCWayPoint[] wp_list; = { new NCWayPoint(598412841, 176488838, 1500),
				new NCWayPoint(598412856, 176489507, 1500) };*/	
	

    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    factory.setNamespaceAware(true);
    XmlPullParser xpp = factory.newPullParser();

    /*
    xpp.setInput( new StringReader (
   		 "<coordinates>" +
   		 "<coordinate><id>0</id><lat>59.84942800688396</lat><lng>17.681293487548828</lng><picture/><data/></coordinate>" +
   		 "<coordinate><id>1</id><lat>59.84718618343519</lat><lng>17.677860260009766</lng><picture/><data/></coordinate>" +
   		 "</coordinates>" ) );
    */
    xpp.setInput(new StringReader (getXMLfromSD()));
    int eventType = xpp.getEventType();
    String type="";
    NCWayPoint wyp=null;
    while (eventType != XmlPullParser.END_DOCUMENT) 
    {
     if(eventType == XmlPullParser.START_DOCUMENT) 
     {
     } 
     else if(eventType == XmlPullParser.START_TAG) 
     {
    	 if (xpp.getName().equalsIgnoreCase("coordinate"))
    	 {
    		 type="coordinate";
    		 wyp=new NCWayPoint(0,0,1500);
    	 }
    	 else if(xpp.getName().equalsIgnoreCase("lat"))
    	 {
    		 type="lat";
    	 }
    	 else if(xpp.getName().equalsIgnoreCase("lng"))
    	 {
    		 type="lng";
    	 }
    	 else if(xpp.getName().equalsIgnoreCase("picture"))
    	 {
    		 type="picture";
    	 }
    	 else if(xpp.getName().equalsIgnoreCase("data"))
    	 {
    		 type="data";
    	 }
    	 else if(xpp.getName().equalsIgnoreCase("altitude"))
    	 {
    		 type="altitude";
    	 }
    	 else if(xpp.getName().equalsIgnoreCase("tag"))
    	 {
    		 type="tag";
    	 }
    	 else if(xpp.getName().equalsIgnoreCase("holdtime"))
    	 {
    		 type="holdtime";
    	 }
    	 else if(xpp.getName().equalsIgnoreCase("toleranceradius"))
    	 {
    		 type="toleranceradius";
    	 }
    	 
     } 
     else if(eventType == XmlPullParser.TEXT) 
     {
    	 if (type.equalsIgnoreCase("lat"))
    	 {	
    		 //K=K+Integer.valueOf(xpp.getText().toString().replace(".", "").trim().substring(0, 9))+",";
    		 wyp.setLat(Integer.valueOf(xpp.getText().toString().replace(".", "").trim().substring(0, 9)));
    	 }
    	 else if(type.equalsIgnoreCase("lng"))
    	 {	
    		 //K=K+Integer.valueOf(xpp.getText().toString().replace(".", "").trim().substring(0, 9))+",";
    		 wyp.setLon(Integer.valueOf(xpp.getText().toString().replace(".", "").trim().substring(0, 9)));
    	 }
    	 else if (type.equalsIgnoreCase("picture"))
    	 {	
    		 wyp.setPicture(xpp.getText().toString());
    	 }
    	 else if (type.equalsIgnoreCase("data"))
    	 {	
    		 wyp.setData(xpp.getText().toString());
    	 }
    	 else if (type.equalsIgnoreCase("altitude"))
    	 {	
    		 wyp.setAlt(Integer.valueOf(xpp.getText().toString()));
    	 }
    	 else if (type.equalsIgnoreCase("tag"))
    	 {	
    		 wyp.setTag(xpp.getText().toString());
    	 }
    	 else if (type.equalsIgnoreCase("holdtime"))
    	 {	
    		 wyp.setHoldTime(Integer.valueOf(xpp.getText().toString()));
    	 }
    	 else if (type.equalsIgnoreCase("toleranceradius"))
    	 {	
    		 wyp.setHoldTime(Integer.valueOf(xpp.getText().toString()));
    		 pointList.add(wyp);
    	 }
     }
     eventType = xpp.next();
    }
    return pointList;
	}
	
	private String getXMLfromSD()
	{
	File sdcard = Environment.getExternalStorageDirectory();
	File file = new File(sdcard,"UAV.xml");
	StringBuilder text = new StringBuilder();
	try {
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    String line;

	    while ((line = br.readLine()) != null) {
	        text.append(line);
	        text.append('\n');
	    }
	}
	catch (IOException e) {
	    
	}
	return text.toString();
	}

}

