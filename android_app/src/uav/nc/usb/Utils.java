package uav.nc.usb;

public final class Utils {

	public final static int parse_signed_int_2(int i1, int i2) {
		int res = (int) ((i2 << 8) | i1);
		if ((res & (1 << 15)) != 0)
			return -(res & (0xFFFF - 1)) ^ (0xFFFF - 1); // TODO check!
		else
			return res;
	}

	public final static int parse_unsigned_int_2(int i1, int i2) {
		return (int) ((i2 << 8) | i1);
	}

	public final static int parse_arr_4(int offset, int[] in_arr) {
		return ((in_arr[offset + 3] << 24) | (in_arr[offset + 2] << 16)
				| (in_arr[offset + 1] << 8) | (in_arr[offset + 0]));
	}

	public final static int parse_arr_2(int offset, int[] in_arr) {
		return (((in_arr[offset + 1] & 0xFF) << 8) | (in_arr[offset + 0] & 0xFF));
	}

	public final static void int32ToByteArr(int val, byte[] arr, int offset) {
		arr[offset] = (byte) ((0xFF) & (val));
		arr[offset + 1] = (byte) ((0xFF) & (val >> 8));
		arr[offset + 2] = (byte) ((0xFF) & (val >> 16));
		arr[offset + 3] = (byte) ((0xFF) & (val >> 24));
	}

	public final static boolean isBitSet(int val, int pos) {
		return (val & (1 << pos)) != 0;
	}

}
