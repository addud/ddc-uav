package uav.xml.parserconstructor;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageToBase64 {
	private String ImageName;
	public ImageToBase64(String ImageName)
	{
		this.ImageName=ImageName;
		
	}
	public String convert()
	{
		Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/"+this.ImageName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
		byte[] b = baos.toByteArray(); 
		return b.toString();
	}
}

