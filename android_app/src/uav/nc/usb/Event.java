package uav.nc.usb;

public class Event {

	public Event(boolean event) {
		this.event = event;
	}

	public boolean isEvent() {
		return event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}

	private boolean event = true;

}
