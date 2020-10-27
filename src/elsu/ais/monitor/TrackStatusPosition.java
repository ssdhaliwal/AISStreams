package elsu.ais.monitor;

import org.joda.time.Instant;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import elsu.sentence.SentenceBase;

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
		String result = "";
		
		try {
			// result = SentenceBase.objectMapper.writeValueAsString(this);
			ObjectNode node = TrackWatcher.objectMapper.createObjectNode();

			node.put("speed", getSpeed());
			node.put("longitude", getLongitude());
			node.put("latitude", getLatitude());
			node.put("course", getCourse());
			node.put("heading", getHeading());
			node.put("updateTime", SentenceBase.formatEPOCHToUTC((int)(getUpdateTime().getMillis() / 1000)));
			
			result = TrackWatcher.objectMapper.writeValueAsString(node);
			node = null;
		} catch (Exception exi) {
			System.out.println(getClass().getName() + ", toString(), error, Sentence, " + exi.getMessage());
			result = "";
		}
		
		return result;
	}
	
	public String toJSONArray() {
		String result = "";
		
		try {
			// result = SentenceBase.objectMapper.writeValueAsString(this);
			ArrayNode node = SentenceBase.objectMapper.createArrayNode();

			node.add(getSpeed());
			node.add(getLongitude());
			node.add(getLatitude());
			node.add(getCourse());
			node.add(getHeading());
			node.add(SentenceBase.formatEPOCHToUTC((int)(getUpdateTime().getMillis() / 1000)));
			
			result = SentenceBase.objectMapper.writeValueAsString(node);
			node = null;
		} catch (Exception exi) {
			System.out.println(getClass().getName() + ", toJSONArray(), error, Sentence, " + exi.getMessage());
			result = "";
		}
		
		return result;
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
