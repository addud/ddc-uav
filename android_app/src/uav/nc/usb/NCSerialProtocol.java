package uav.nc.usb;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.util.Log;

public class NCSerialProtocol {

	public final static int DATA_IN_BUFF_SIZE = 2048;
	public final static int RX_TIMEOUT = -1;
	public final static int RX_CRC_ERROR = -2;
	public final static int RX_UNKNOWN = -3;
	private static final byte NAVI_SLAVE_ADDR = 2;
	private static final int NC_FLAG_TARGET_REACHED = 0x20;

	private static final String TAG = "NCSerialProtocol";

	private int EchoPattern;

	private NCSerialConnection sc;
	private boolean WP_reached = false;
	private int command_count = 0;
	private NCWayPoint current_wp;

	public NCSerialProtocol() {
		// TODO Auto-generated constructor stub
		sc = new NCSerialConnection();
	}

	public void init() throws IOException {
		sc.startSerialCom();

	}

	public void deinit() throws IOException {
		sc.stopSerialCom();
	}

	public int receive() throws IOException, InterruptedException {
		int timeout_counter = 0;
		boolean frame_detected = false;
		int ret = NCSerialProtocol.RX_UNKNOWN;

		ByteBuffer bytebuf = ByteBuffer
				.allocate(NCSerialProtocol.DATA_IN_BUFF_SIZE);

		bytebuf.clear();

		while (bytebuf.hasRemaining()) {

			if (this.dataReady() > 0) {

				byte c = '0';
				c = (byte) sc.receive();

				if (c != -1) {
					// Log.d(TAG, "rxc: " + (char) c);
					// publishProgress(((char) c) + "");

					if (c == '#') {
						frame_detected = true;
						Log.d(TAG, "frame_detected");
						// Log.d(TAG, "" + (char) c);
						bytebuf.put(c);
					} else if (c == '\r' && frame_detected) {
						frame_detected = false;
						// Log.d(TAG, "frame_closed");
						// Log.d(TAG, "" + (char) c);
						bytebuf.put(c);

						bytebuf.flip();

						byte[] tempbyte = new byte[bytebuf.remaining()];

						bytebuf.get(tempbyte);
						bytebuf.clear();
						ret = this.parseCommand(tempbyte, tempbyte.length);

						break;
					} else if (frame_detected) {
						bytebuf.put(c);
						// Log.d(TAG, "" + (char) c);
					}
				} else {
					// no more data from the NC
					Log.w(TAG, "No more data to read");
					break;
				}

			} else {
				if (timeout_counter > 10) {
					Log.w(TAG, "rx timeout");
					timeout_counter = 0;

					if (ret != NCSerialProtocol.RX_UNKNOWN) {
						return ret;
					}
					return NCSerialProtocol.RX_TIMEOUT;
				}
				timeout_counter++;
				Thread.sleep(100);
			}
		}
		return ret;
	}

	public int dataReady() throws IOException {
		return sc.dataReady();
	}

	public void sendWaypoint(int index, NCWayPoint wp) throws IOException {
		byte[] waypoint_struct = new byte[30];

		Log.d(TAG, "tx wp: " + wp.getLat() + " , " + wp.getLon());

		Utils.int32ToByteArr(wp.getLon(), waypoint_struct, 0);
		Utils.int32ToByteArr(wp.getLat(), waypoint_struct, 4);

		// alt
		Utils.int32ToByteArr(wp.getAlt(), waypoint_struct, 8);

		// status
		waypoint_struct[12] = (byte) ((0xFF) & (wp.getStatus()));

		// heading
		waypoint_struct[13] = (0xFF) & (0);
		waypoint_struct[14] = (0xFF) & (0);

		// tolerance
		waypoint_struct[15] = (byte) ((0xFF) & (wp.getToleranceRadius()));

		// holdtime
		waypoint_struct[16] = (byte) ((0xFF) & (wp.getHoldTime()));

		// event flag
		waypoint_struct[17] = (byte) ((0xFF) & (0));

		// index
		waypoint_struct[18] = (byte) ((0xFF) & (index));

		// type 19

		// channel event
		waypoint_struct[20] = (byte) ((0xFF) & (wp.getChannelEvent()));

		// 11 reserved

		byte[] send_buff = encodeCommand(NAVI_SLAVE_ADDR, 'w', waypoint_struct);

		current_wp = wp;
		WP_reached = false;

		sc.send(send_buff);
	}

	public void sendErrorTextRequest() throws IOException {
		byte[] bytes = {};

		byte[] send_buff = encodeCommand(NAVI_SLAVE_ADDR, 'e', bytes);

		log("tx: " + new String(send_buff));

		sc.send(send_buff);
	}

	public void sendSerialLinkTest(int EchoPattern) throws IOException {

		byte[] bytes = new byte[2];

		// log("echo: " + EchoPattern);

		bytes[0] = (byte) (EchoPattern & 0xFF);
		bytes[1] = (byte) (EchoPattern >> 8);

		// EchoPattern = bytes[1] << 8 | (bytes[0] & 0xFF);
		//
		// log("retecho: " + EchoPattern);

		byte[] send_buff = encodeCommand(NAVI_SLAVE_ADDR, 'z', bytes);

		sc.send(send_buff);
	}

	public void sendRequestWaypoint(byte wp_index) throws IOException {
		byte[] bytes = { wp_index };

		byte[] send_buff = encodeCommand(NAVI_SLAVE_ADDR, 'x', bytes);

		// log("tx command: x");
		// log("txdata: " + new String(bytes));

		sc.send(send_buff);
	}

	public void sendRequestOSDData(byte byte_interval) throws IOException {
		byte[] bytes = { byte_interval };

		byte[] send_buff = encodeCommand(NAVI_SLAVE_ADDR, 'o', bytes);

		// log("tx command: x");
		// log("txdata: " + new String(bytes));

		sc.send(send_buff);
	}

	public int getEchoPattern() {
		return EchoPattern;
	}

	public boolean isWPReached() {
		return WP_reached;
	}

	private void log(String str) {
		Log.d(TAG, str);
	}

	private int parseCommand(byte[] data, int len) {
		String s = new String();
		int off, latitude, longitude, altitude;

		log("rx: ----------------------");
		// log("rx: " + new String(data));

		// check crc
		int tmp_crc = 0;
		for (int i = 0; i < len - 3; i++)
			tmp_crc += (int) data[i];

		tmp_crc %= 4096;

		if (!((data[len - 3] == (char) (tmp_crc / 64 + '=')) && (data[len - 2] == (char) (tmp_crc % 64 + '=')))) {
			Log.w(TAG, "CRC error");
			// log("rx: CRC1: " + data[len - 3] + " CRC2: " + data[len - 2]
			// + " last: " + data[len - 1]);
			return NCSerialProtocol.RX_CRC_ERROR;
		}
		// end of c

		int[] decoded_data = decodeData(data, 3, len - 6);

		// slave_addr=data[1];
		log("rx command: " + (char) data[2] + " length: " + decoded_data.length);

		switch ((char) data[2]) {

		case 'W': // waypoint
			log("rx: nr of waypoints: " + decoded_data[0]);
			break;
		case 'w':

			off = 0;

			longitude = Utils.parse_arr_4(off + 0, decoded_data);
			latitude = Utils.parse_arr_4(off + 4, decoded_data);
			altitude = Utils.parse_arr_4(off + 8, decoded_data);

			log("longitude: " + longitude);
			log("latitude: " + latitude);

			command_count = 0;
			break;

		case 'e':
			for (int i = 0; i < decoded_data.length; i++) {
				s += " " + (char) decoded_data[i];
			}

			log("rx: " + s);
			break;

		case 'E':
			for (int i = 0; i < decoded_data.length; i++) {
				s += " " + (char) decoded_data[i];
			}

			log("rx: " + s);
			break;

		case 'O':
			off = 1;

			longitude = Utils.parse_arr_4(off + 0, decoded_data);
			latitude = Utils.parse_arr_4(off + 4, decoded_data);
			altitude = Utils.parse_arr_4(off + 8, decoded_data);

			log("rx current position: " + latitude + " , " + longitude);

			off = 14;

			int t_longitude = Utils.parse_arr_4(off + 0, decoded_data);
			int t_latitude = Utils.parse_arr_4(off + 4, decoded_data);
			int t_altitude = Utils.parse_arr_4(off + 8, decoded_data);

			log("rx target position: " + t_latitude + " , " + t_longitude);

			off = 31;

			longitude = Utils.parse_arr_4(off + 0, decoded_data);
			latitude = Utils.parse_arr_4(off + 4, decoded_data);
			altitude = Utils.parse_arr_4(off + 8, decoded_data);

			log("rx home position: " + latitude + " , " + longitude);

			log("rx waypoint index: " + decoded_data[49]);
			log("rx waypoints number: " + decoded_data[49]);
			// log("rx sattelites: " + decoded_data[50]);
			// log("rx holdtime: " + decoded_data[73]);

			int target_reached = decoded_data[68]
					& NCSerialProtocol.NC_FLAG_TARGET_REACHED;

			log("rx holdtime: " + decoded_data[73]);

			int target_deviation = Utils.parse_arr_2(27, decoded_data);
			log("rx deviation: " + target_deviation);

			// multiply by 10 because the distance read from the NC is in
			// decimeters (saw this in code, the struct on MK site says
			// centimeters)

			if ((target_reached > 0) && (t_latitude == current_wp.getLat())
					&& (t_longitude == current_wp.getLon())) {
				WP_reached = true;
			}

			break;

		case 'o':
			log("rx navi struct: " + decoded_data[0]);
			break;

		case 'X':
			log("rx: nr of waypoints: " + decoded_data[0]);

			if (decoded_data[0] == 0) {
				WP_reached = true;
			}

			log("rx: wp_index: " + decoded_data[1]);
			off = 2;

			longitude = Utils.parse_arr_4(off + 0, decoded_data);
			latitude = Utils.parse_arr_4(off + 4, decoded_data);
			altitude = Utils.parse_arr_4(off + 8, decoded_data);

			log("rx: longitude: " + longitude);
			log("rx: latitude: " + latitude);
			break;

		case 'x':

			log("command_count: " + command_count);

			if (command_count > 5) {
				WP_reached = true;
			} else {
				command_count++;
			}

			break;

		case 'z':
		case 'Z':
			off = 0;
			EchoPattern = Utils.parse_arr_2(off, decoded_data);

			break;

		default:
			log("other command");
			break;

		}

		log("command processing done");

		return data[2];
	}

	private byte[] encodeCommand(byte modul, char cmd, byte[] params) {

		log("--------tx: " + cmd + "--------");

		byte[] res = new byte[3 + (params.length / 3 + (params.length % 3 == 0 ? 0
				: 1)) * 4 + 3]; // 5=1*start_char+1*addr+1*cmd+2*crc + line
								// break

		res[0] = '#';
		res[1] = (byte) (modul + 'a');
		res[2] = (byte) cmd;

		for (int param_pos = 0; param_pos < (params.length / 3 + (params.length % 3 == 0 ? 0
				: 1)); param_pos++) {
			byte a = (param_pos * 3 < params.length) ? params[param_pos * 3]
					: 0;
			byte b = ((param_pos * 3 + 1) < params.length) ? params[param_pos * 3 + 1]
					: 0;
			byte c = ((param_pos * 3 + 2) < params.length) ? params[param_pos * 3 + 2]
					: 0;

			res[3 + param_pos * 4] = (byte) ((a >> 2) + '=');
			res[3 + param_pos * 4 + 1] = (byte) ('=' + (((a & 0x03) << 4) | ((b & 0xf0) >> 4)));
			res[3 + param_pos * 4 + 2] = (byte) ('=' + (((b & 0x0f) << 2) | ((c & 0xc0) >> 6)));
			res[3 + param_pos * 4 + 3] = (byte) ('=' + (c & 0x3f));
		}
		int tmp_crc = 0;

		for (int tmp_i = 0; tmp_i < res.length - 3; tmp_i++)
			tmp_crc += (int) res[tmp_i];

		tmp_crc %= 4096;

		res[res.length - 3] = (byte) ((char) (tmp_crc / 64 + '='));
		res[res.length - 2] = (byte) ((char) (tmp_crc % 64 + '='));
		res[res.length - 1] = (byte) ('\r');

		// log("tx: CRC1: " + res[res.length - 3] + " CRC2: "
		// + res[res.length - 2] + " last: " + res[res.length - 1]);
		return res;
	}

	private int[] decodeData(byte[] in_arr, int offset, int len) {
		int ptrIn = offset;
		int a, b, c, d, x, y, z;
		int ptr = 0;

		int[] out_arr = new int[len];

		while (len != 0) {
			a = 0;
			b = 0;
			c = 0;
			d = 0;
			try {
				a = in_arr[ptrIn++] - '=';
				b = in_arr[ptrIn++] - '=';
				c = in_arr[ptrIn++] - '=';
				d = in_arr[ptrIn++] - '=';
			} catch (Exception e) {
			}

			x = ((a << 2) | (b >> 4)) & 0xFF;
			y = ((b & 0x0f) << 4) | (c >> 2);
			z = ((c & 0x03) << 6) | d;

			if ((len--) != 0)
				out_arr[ptr++] = x;
			else
				break;
			if ((len--) != 0)
				out_arr[ptr++] = y;
			else
				break;
			if ((len--) != 0)
				out_arr[ptr++] = z;
			else
				break;
		}

		return out_arr;
	}
}
