package elsu.ais.monitor;

import org.joda.time.Instant;

public class TrackStatusPosition {

	public static TrackStatusPosition fromTrackStatus(TrackStatus status) {
		TrackStatusPosition result = new TrackStatusPosition(status.getSpeed(), status.getLongitude(),
				status.getLatitude(), status.getCourse(), status.getHeading(), status.getUpdateTime());

		return result;
	}

	public TrackStatusPosition() {
	}

	public TrackStatusPosition(float speed, float longitude, float latitude, float course, int heading, 
			Instant updateTime) {
		this.speed = speed;
		this.longitude = longitude;
		this.latitude = latitude;
		this.course = course;
		this.heading = heading;
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("{");
		buffer.append("\"speed\":" + getSpeed());
		buffer.append(", \"longitude\":" + getLongitude());
		buffer.append(", \"latitude\":" + getLatitude());
		buffer.append(", \"course\":" + getCourse());
		buffer.append(", \"heading\":" + getHeading());
		buffer.append(", \"updateTime\":\"" + getUpdateTime() + "\"");
		buffer.append("}");
		
		return buffer.toString();
	}
	public String toJSONArray() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("{");
		buffer.append(getSpeed());
		buffer.append(", " + getLongitude());
		buffer.append(", " + getLatitude());
		buffer.append(", " + getCourse());
		buffer.append(", " + getHeading());
		buffer.append(", \"" + getUpdateTime() + "\"");
		buffer.append("}");
		
		return buffer.toString();
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		this.course = course;
	}

	public int getHeading() {
		return heading;
	}

	public void setHeading(int heading) {
		this.heading = heading;
	}

	public Instant getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime() {
		this.updateTime = Instant.now();
	}

	private float speed = 102.3f;
	private float longitude = 181f;
	private float latitude = 91f;
	private float course = 360.0f;
	private int heading = 511;

	private Instant updateTime = Instant.now();
}
