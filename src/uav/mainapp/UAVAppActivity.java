package uav.mainapp;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import uav.ew.camapp.CameraApp;
import uav.nc.usb.NCWayPoint;
import uav.nc.usb.UAVController;
import uav.xml.parserconstructor.Parser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class UAVAppActivity extends Activity {
	public static final int CAMERA_ACIVITY_ID = 1;
	protected static final String TAG = "UAVAppActivity";

	private static Context context;

	private UAVController uav;

	private int count = 0;

	public static Context getAppContext() {
		return context;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// new Initializer().execute((Void) null);

		context = getApplicationContext();

		// KeyguardManager mKeyGuardManager = (KeyguardManager)
		// getSystemService(KEYGUARD_SERVICE);
		// KeyguardLock mLock = mKeyGuardManager
		// .newKeyguardLock("activity_classname");
		// mLock.disableKeyguard();

		// Button actButton = (Button) findViewById(id.button_act);
		//
		// actButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // Call the CameraApp
		// Intent intent = new Intent(UAVAppActivity.this, CameraApp.class);
		// startActivityForResult(intent, CAMERA_ACIVITY_ID);
		// }
		// });

		/*
		 * This is the waypoints struct. The parameter given to the class must
		 * resemble this
		 */

		ArrayList<NCWayPoint> wp_list = new ArrayList<NCWayPoint>();
		try {
			wp_list = (new Parser()).parse();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// for obtaining coordinates, Constructor class can be used
		// new uav.xml.parserconstructor.Constructor(wp_list);

		/*
		 * This is an array of events that follow to reaching the given
		 * waypoints.
		 * 
		 * Currently it is implemented as an array of flags for triggering the
		 * camera capture
		 */

		NCWayPoint invalid_wp = new NCWayPoint(0, 0, 0);
		invalid_wp.setStatus(0);

		wp_list.add(0, invalid_wp);

		uav = new UAVController(wp_list);

		uav.init();

		new Thread(uav).start();

		new Thread(new Runnable() {
			public void run() {
				Intent intent = new Intent(UAVAppActivity.this, CameraApp.class);
				for (;;) {
					uav.readAndClearEventTrigger();
					intent.putExtra("wp_tag", uav.getCurrentWP().getTag());
					startActivityForResult(intent, CAMERA_ACIVITY_ID);
				}

			}
		}).start();

		Log.d(TAG, "onCreate");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		uav.deinit();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_ACIVITY_ID) {
			if (resultCode == RESULT_OK) {
				uav.setEventFinished();
				Log.w(TAG, "Picture taken" + (++count));
			} else if (resultCode == RESULT_CANCELED) {
				Log.d(TAG, "Camera is used somewhere else");
			} else {
				Log.d(TAG, "Unknown resultCode from CameraApp");
			}
		}
	}
}