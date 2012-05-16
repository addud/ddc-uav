package uav.nc.usb;

import java.io.IOException;
import java.util.List;

import android.util.Log;

public class UAVController extends Thread {
	public static final int DUMMY_ECHO_PATTERN = 0x0505;
	private static final String TAG = "UAVController";
	private static final int NUMBER_OF_RETRIES = 100;

	private boolean event_trigger = false;

	private List<NCWayPoint> wp_list;
	private NCSerialProtocol nc;
	private boolean event_finished = true;

	public UAVController(List<NCWayPoint> wp_list) {
		this.wp_list = wp_list;
		nc = new NCSerialProtocol();
	}

	public int init() {
		try {
			nc.init();
			return 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public int deinit() {
		try {
			nc.deinit();
			return 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public void run() {
		Log.d(TAG, "***********Start************");
		Log.d(TAG, "****************************");

		int retries;

		try {
			this.setEventFinished();

			Thread.sleep(500);
			while (nc.receive() != NCSerialProtocol.RX_TIMEOUT) {
				Thread.sleep(100);
			}

			// test communication
			for (retries = 0; retries < UAVController.NUMBER_OF_RETRIES; retries++) {
				nc.sendSerialLinkTest(retries);
				Thread.sleep(100);
				if (receiveCommand('Z', 'z')) {
					if (nc.getEchoPattern() != retries) {
						Log.e(TAG, "Unrecognized data format.");
						return;
					}
					break;
				}
			}

			if (handleTimeout(retries)) {
				return;
			}

			// send waypoints
			for (int wp_index = 0; wp_index < wp_list.size(); wp_index++) {

				for (retries = 0; retries < UAVController.NUMBER_OF_RETRIES; retries++) {
					nc.sendWaypoint(wp_index, wp_list.get(wp_index));
					if (receiveCommand('w', 'W')) {
						break;
					}
				}

				if (handleTimeout(retries)) {
					return;
				}

				int temp_count = 10;
				while (true) {

					if (wp_list.get(wp_index).getStatus() == 0) {
						break;
					}

					for (retries = 0; retries < UAVController.NUMBER_OF_RETRIES; retries++) {
						nc.sendRequestOSDData((byte) 255);
						if (receiveCommand('o', 'O')) {
							break;
						}
					}

					if (handleTimeout(retries)) {
						return;
					}

					boolean temp_wp_reached = nc.isWPReached();

					if (temp_count > 20) {
						temp_count = 0;
						temp_wp_reached = true;
					} else {
						temp_count++;
					}

					if (temp_wp_reached && this.isEventFinished()) {
						if (wp_list.get(wp_index).getEvent().isEvent()) {
							setEventTrigger();
						}
						break;
					}

					Thread.sleep(500);
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		Log.d(TAG, "return");
		return;
	}

	public synchronized boolean readAndClearEventTrigger() {

		while (!event_trigger) {
			try {
				Log.w(TAG, "sleeping");
				wait();
				Log.w(TAG, "wakeup");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		event_trigger = false;
		return event_trigger;
	}

	public synchronized void setEventFinished() {
		this.event_finished = true;
	}

	public synchronized void clearEventFinished() {
		this.event_finished = false;
	}

	private synchronized boolean isEventFinished() {
		return event_finished;
	}

	private synchronized void setEventTrigger() {
		this.clearEventFinished();
		event_trigger = true;
		notify();
	}

	private synchronized boolean isEventTrigger() {
		return event_trigger;
	}

	private boolean receiveCommand(char c1, char c2) throws IOException,
			InterruptedException {
		boolean received_command = false;
		while (true) {
			int command = nc.receive();
			if ((command == c1) || (command == c2)) {
				received_command = true;
				break;
			} else if ((command == NCSerialProtocol.RX_TIMEOUT)
					|| (command == NCSerialProtocol.RX_CRC_ERROR)) {
				break;
			}
		}
		return received_command;
	}

	private boolean handleTimeout(int retries) {
		if (retries >= UAVController.NUMBER_OF_RETRIES) {
			Log.e(TAG, "Serial communication timeout.");
			return true;
		}
		return false;
	}
};
