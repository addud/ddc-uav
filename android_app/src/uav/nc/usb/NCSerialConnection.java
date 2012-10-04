package uav.nc.usb;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.Scanner;

import android.util.Log;

public class NCSerialConnection {
	private static final String SERIAL_COM_LOCK_BASE = "/data/local/serialcom/LCK..";
	private static final String TAG = "NCSerialConnection";

	private boolean running;
	private Process serialComProc;
	private InputStream in;
	private OutputStream out;
	private BufferedInputStream inbuf;

	private CharBuffer printBuffer;

	public NCSerialConnection() {
		// TODO Auto-generated constructor stub
		running = false;
		printBuffer = CharBuffer.allocate(1024);
		printBuffer.clear();
	}

	public void startSerialCom() throws IOException {
		if (running) {
			return;
		}
		Log.d(TAG, "startSerialCom");

		/* Start serial_com process for device /dev/ttyUSB0. */
		serialComProc = Runtime.getRuntime().exec("su");

		out = serialComProc.getOutputStream();
		DataOutputStream outs = new DataOutputStream(out);
		// outs.writeBytes("enable-usb-device" + "\n");
		// outs.flush();
		outs.writeBytes("enable-usb-host" + "\n");
		outs.flush();
		outs.writeBytes("serial_com -b57600 /dev/ttyUSB0" + "\n");
		outs.flush();

		in = serialComProc.getInputStream();
		inbuf = new BufferedInputStream(in);
		inbuf.skip(100000);

		running = true;
	}

	public void stopSerialCom() throws IOException {
		if (!running) {
			return;
		}

		/* Read the process PID from the lock file and kill the process . */
		Scanner s = new Scanner(new File(SERIAL_COM_LOCK_BASE + "ttyUSB0"));
		String pid = s.nextLine();
		Runtime.getRuntime().exec(new String[] { "kill", pid });

		serialComProc.destroy();
		in.close();
		out.close();
		inbuf.close();
		running = false;
	}

	public int receive() throws IOException {
		return inbuf.read();
	}

	public void send(byte[] data) throws IOException {
		out.write(data);
	}

	public int dataReady() throws IOException {
		return inbuf.available();
	}

	int readChar() {
		if (printBuffer.hasRemaining()) {
			return (int) printBuffer.get();
		} else {
			return -1;
		}
	}
}