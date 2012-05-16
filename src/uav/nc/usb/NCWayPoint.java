package uav.nc.usb;

public class NCWayPoint {

	/** time the MK should hold the position in seconds **/
	private int hold_time = 1;

	/**
	 * the radius around the waypoint in meters in which the point is seen as
	 * reached
	 **/
	private int tolerance_radius;

	/** the latitude in deg*10^-7 **/
	private int lat;

	/** the longitude in deg*10^-7 **/
	private int lon;

	/** the altitude in deg*10^-7 **/
	private int alt;

	/** TODO: find out what this means **/
	private int channel_event = 200;

	private Event event;

	private int status;

	private String tag;

	private String picture;
	private String data;

	public NCWayPoint(int lat, int lon, int alt) {
		this.setLat(lat);
		this.setLon(lon);
		this.setAlt(alt);
		this.setPicture("");
		this.setData("");
		this.setEvent(new Event(true));
		this.setStatus(1);
		this.setToleranceRadius(5);
		this.setTag(this.getLat() + "," + this.getLon());
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getData() {
		return picture;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getHoldTime() {
		return hold_time;
	}

	public void setHoldTime(int t) {
		hold_time = t;
	}

	public void setChannelEvent(int channel_event) {
		this.channel_event = channel_event;
	}

	public int getChannelEvent() {
		return channel_event;
	}

	public void setToleranceRadius(int tolerance_radius) {
		this.tolerance_radius = tolerance_radius;
	}

	public int getToleranceRadius() {
		return tolerance_radius;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getLat() {
		return lat;
	}

	public void setLon(int lon) {
		this.lon = lon;
	}

	public int getLon() {
		return lon;
	}

	public int getAlt() {
		return alt;
	}

	public void setAlt(int alt) {
		this.alt = alt;
	}
}
