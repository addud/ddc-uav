package uav.ew.camapp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uav.mainapp.R;
import uav.mainapp.R.id;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Environment;
import android.util.Log;
import android.widget.FrameLayout;

public class CameraApp extends Activity {

	protected static final String TAG = "CameraApp";
	private Camera mCamera;
	private CameraPreview mPreview;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static ConditionVariable sync = new ConditionVariable();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cam_layout);
		// Used to get a new instance or the camera
		mCamera = getCameraInstance();
		// If the camera is not available, terminate the activity
		if (mCamera == null) {
			Log.d(TAG, "Camera is very busy right now");
			terminate(false);
		}
		mPreview = new CameraPreview(this, mCamera);
		// Create a preview needed for the camera object
		FrameLayout preview = (FrameLayout) findViewById(id.camera_preview);
		preview.addView(mPreview);
		// Take a picture
		new TakePictureTask().execute(null, null, null);
	}

	private class TakePictureTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... urls) {
			Log.d(TAG, "Wait");
			sync.block();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "Sleep threw an exception");
				e.printStackTrace();
			}
			mCamera.takePicture(null, null, mPicture);
			Log.w(TAG, "We're taking pictures, bastards!!!");
			sync.close();
			// cancel(true);
			return null;
		}

		protected void onPostExecute(Void result) {
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		if (c == null) {
			Log.d(TAG, "failed to get camera instance");
		}
		return c; // returns null if camera is unavailable
	}

	// Callback function to store the image file
	public PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			// Creating the file
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: ");
				return;
			}
			// Writing the image to the file
			try {
				BufferedOutputStream fos = new BufferedOutputStream(
						new FileOutputStream(pictureFile), data.length);
				// FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data, 0, data.length);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			} catch (Exception e) {
				Log.d(TAG, "Error writing file: " + e.getMessage());
			}
			// Shut down the activity
			terminate(true);
		}
	};

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// Making sure the SD-card is available before
		// writing to it
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}

		// If the SD-card is unavailable, return null
		if (!(mExternalStorageAvailable && mExternalStorageWriteable)) {
			return null;
		}
		// Determine the path to the folder where we want to store the image
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory() + File.separator
						+ "DCIM", "UAV_Sensor_data");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}
		return mediaFile;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			// Stop the preview
			mCamera.stopPreview();
			// release the camera for other applications
			mCamera.release();
			mCamera = null;
		}
	}

	// Overriding the onPause() method to release the camera
	@Override
	protected void onPause() {
		super.onPause();
		mCamera.stopPreview();
		releaseCamera(); // release the camera immediately on pause event
	}

	public void terminate(Boolean result) {
		Intent intent = new Intent();
		if (result) {
			setResult(RESULT_OK, intent);
		} else {
			setResult(RESULT_CANCELED, intent);
		}
		// Shutting down the application
		// also invoking the onPause method that
		// releases the camera instance
		finish();
	}
}
