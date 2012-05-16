package uav.xml.parserconstructor;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
 
import org.xmlpull.v1.XmlSerializer;

import uav.nc.usb.NCWayPoint;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
 
public class Constructor {

    public Constructor(NCWayPoint[] wp_array) {

        //create a new file called "new.xml" in the SD card
        File newxmlfile = new File(Environment.getExternalStorageDirectory()+"/UAV.xml");
        try{
                newxmlfile.createNewFile();
        }catch(IOException e){
                Log.e("IOException", "exception in createNewFile() method");
        }
        FileOutputStream fileos = null;        
        try{
                fileos = new FileOutputStream(newxmlfile);
        }catch(FileNotFoundException e){
                Log.e("FileNotFoundException", "can't create FileOutputStream");
        }
        //XmlSerializer in order to write xml data
        XmlSerializer serializer = Xml.newSerializer();
        try {
                //we set the FileOutputStream as output for the serializer, using UTF-8 encoding
                        serializer.setOutput(fileos, "UTF-8");
                        //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
                        serializer.startDocument(null, Boolean.valueOf(true));
                        //set indentation option
                        //serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                        //start a tag called "root"
                        serializer.startTag(null, "coordinates");
                        //i indent code just to have a view similar to xml-tree
                        for(int i=0;i<wp_array.length;i++)
                        {
                        	serializer.startTag(null,"coordinate");
                                serializer.startTag(null, "id");
                                serializer.text(i+"");
                                serializer.endTag(null, "id");
                            
                                serializer.startTag(null, "lat");
                                serializer.text((Double.parseDouble(wp_array[i].getLat()+"")/Math.pow(10.0,7.0 ))+"");
                                serializer.endTag(null, "lat");
                                
                                serializer.startTag(null, "lng");
                                serializer.text((Double.parseDouble(wp_array[i].getLon()+"")/Math.pow(10.0,7.0 ))+"");
                                serializer.endTag(null, "lng");                                
                                
                                serializer.startTag(null, "picture");
                                serializer.text(wp_array[i].getPicture());
                                serializer.endTag(null, "picture");
                                serializer.text(wp_array[i].getData());
                                serializer.startTag(null, "data");
                                
                                serializer.endTag(null, "data");                              
                            serializer.endTag(null, "coordinate"); 
                        }
                        serializer.endTag(null, "coordinates");
                        serializer.endDocument();
                        //write xml data into the FileOutputStream
                        serializer.flush();
                        //finally we close the file stream
                        fileos.close();
                } catch (Exception e) {
                        Log.e("Exception","error occurred while creating xml file");
                }
    }
}