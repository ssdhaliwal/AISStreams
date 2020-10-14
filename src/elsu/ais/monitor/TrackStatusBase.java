package elsu.ais.monitor;

import java.util.ArrayList;

import org.joda.time.Instant;
import org.joda.time.MutablePeriod;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import elsu.ais.messages.*;
import elsu.ais.messages.data.*;
import elsu.ais.resources.Utilities;
import elsu.sentence.SentenceBase;

public abstract class TrackStatusBase {

	public TrackStatusBase() {
	}

	public void fromT1PositionReportClassA(T1_PositionReportClassA message) {
		setTransponderType(message.getTransponderType());
		setType(message.getType());
		setRepeat(message.getRepeat());
		setMmsi(message.getMmsi());

		setStatus(message.getStatus());
		setRateOfTurn(message.getRateOfTurn());
		setSpeed(message.getSpeed());
		setAccuracy(message.isAccuracy());
		setLongitude(message.getLongitude());
		setLatitude(message.getLatitude());
		setCourse(message.getCourse());
		setHeading(message.getHeading());
		setSecond(message.getSecond());
		setManeuver(message.getManeuver());
		setRaim(message.isRaim());
		setRadio(message.getRadio());
		setCommState(message.getCommState());

		setPeriod();
	}

	public void fromT5StaticAndVoyageRelatedData(T5_StaticAndVoyageRelatedData message) {
		setType(message.getType());
		setRepeat(message.getRepeat());
		setMmsi(message.getMmsi());

		setAisVersion(message.getAisVersion());
		setImo(message.getImo());
		setCallSign(message.getCallSign());
		setShipName(message.getShipName());
		setShipType(message.getShipType());
		setDimension(message.getDimension());
		setEpfd(message.getEpfd());
		setMonth(message.getMonth());
		setDay(message.getDay());
		setHour(message.getHour());
		setMinute(message.getMinute());
		setDraught(message.getDraught());
		setDestination(message.getDestination());
		setDte(message.getDte());

		setPeriod();
	}

	public void fromT18StandardClassBEquipmentPositionReport(T18_StandardClassBEquipmentPositionReport message) {
		setTransponderType(message.getTransponderType());
		setType(message.getType());
		setRepeat(message.getRepeat());
		setMmsi(message.getMmsi());

		setSpeed(message.getSpeed());
		setAccuracy(message.isAccuracy());
		setLongitude(message.getLongitude());
		setLatitude(message.getLatitude());
		setCourse(message.getCourse());
		setHeading(message.getHeading());
		setSecond(message.getSecond());
		setRegional(message.getRegional());
		setCs(message.isCs());
		setDisplay(message.isDisplay());
		setDsc(message.isDsc());
		setBand(message.isBand());
		setMsg22(message.isMsg22());
		setAssigned(message.isAssigned());
		setRaim(message.isRaim());
		setCommFlag(message.getCommFlag());
		setRadio(message.getRadio());
		setCommState(message.getCommState());

		setPeriod();
	}

	public void fromT19ExtendedClassBEquipmentPositionReport(T19_ExtendedClassBEquipmentPositionReport message) {
		setTransponderType(message.getTransponderType());
		setType(message.getType());
		setRepeat(message.getRepeat());
		setMmsi(message.getMmsi());

		setSpeed(message.getSpeed());
		setAccuracy(message.isAccuracy());
		setLongitude(message.getLongitude());
		setLatitude(message.getLatitude());
		setCourse(message.getCourse());
		setHeading(message.getHeading());
		setSecond(message.getSecond());
		setRegional(message.getRegional());
		setShipName(message.getShipName());
		setShipType(message.getShipType());
		setDimension(message.getDimension());
		setEpfd(message.getEpfd());
		setRaim(message.isRaim());
		setDte(message.getDte());
		setAssignedMode(message.getAssignedMode());

		setPeriod();
	}

	public void fromT9StandardSARPositionReport(T9_StandardSARPositionReport message) {
		setType(message.getType());
		setRepeat(message.getRepeat());
		setMmsi(message.getMmsi());

		setAltitude(message.getAltitude());
		setSpeed(message.getSpeed());
		setAccuracy(message.isAccuracy());
		setLongitude(message.getLongitude());
		setLatitude(message.getLatitude());
		setCourse(message.getCourse());
		setSecond(message.getSecond());
		setAltitudeSensor(message.getAltitudeSensor());
		setDte(message.getDte());
		setAssigned(message.isAssigned());
		setRaim(message.isRaim());
		setCommFlag(message.getCommFlag());
		setRadio(message.getRadio());
		setCommState(message.getCommState());

		setPeriod();
	}

	public String getTransponderType() {
		return transponderType;
	}

	public String setTransponderType(String transponderType) {
		if (!transponderType.equals(this.transponderType)) {
			setUpdated(true);
		}
		return this.transponderType = transponderType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if (type != this.type) {
			setUpdated(true);
		}
		this.type = type;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		if (repeat != this.repeat) {
			setUpdated(true);
		}
		this.repeat = repeat;
	}

	public int getMmsi() {
		return mmsi;
	}

	public void setMmsi(int mmsi) {
		if (mmsi != this.mmsi) {
			setUpdated(true);
		}
		this.mmsi = mmsi;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		if (status != this.status) {
			setUpdated(true);
		}
		this.status = status;
	}

	public double getRateOfTurn() {
		return rateOfTurn;
	}

	public void setRateOfTurn(double rateOfTurn) {
		if (!Utilities.doubleCompare(rateOfTurn, this.rateOfTurn)) {
			setUpdated(true);
		}
		this.rateOfTurn = rateOfTurn;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		if (!Utilities.floatCompare(speed, this.speed)) {
			setUpdated(true);
		}
		this.speed = speed;
	}

	public boolean isAccuracy() {
		return accuracy;
	}

	public void setAccuracy(boolean accuracy) {
		if (accuracy != this.accuracy) {
			setUpdated(true);
		}
		this.accuracy = accuracy;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		if (!Utilities.floatCompare(longitude, this.longitude)) {
			setUpdated(true);
		}
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		if (!Utilities.floatCompare(latitude, this.latitude)) {
			setUpdated(true);
		}
		this.latitude = latitude;
	}

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		if (!Utilities.floatCompare(course, this.course)) {
			setUpdated(true);
		}
		this.course = course;
	}

	public int getHeading() {
		return heading;
	}

	public void setHeading(int heading) {
		if (heading != this.heading) {
			setUpdated(true);
		}
		this.heading = heading;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		if (second != this.second) {
			setUpdated(true);
		}
		this.second = second;
	}

	public int getManeuver() {
		return maneuver;
	}

	public void setManeuver(int maneuver) {
		if (maneuver != this.maneuver) {
			setUpdated(true);
		}
		this.maneuver = maneuver;
	}

	public boolean isRaim() {
		return raim;
	}

	public void setRaim(boolean raim) {
		if (raim != this.raim) {
			setUpdated(true);
		}
		this.raim = raim;
	}

	public int getRadio() {
		return radio;
	}

	public void setRadio(int radio) {
		if (radio != this.radio) {
			setUpdated(true);
		}
		this.radio = radio;
	}

	public CommunicationState getCommState() {
		return commState;
	}

	public void setCommState(CommunicationState commState) {
		this.commState = commState;
	}

	public int getAisVersion() {
		return aisVersion;
	}

	public void setAisVersion(int aisVersion) {
		if (aisVersion != this.aisVersion) {
			setUpdated(true);
		}
		this.aisVersion = aisVersion;
	}

	public int getImo() {
		return imo;
	}

	public void setImo(int imo) {
		if (imo != this.imo) {
			setUpdated(true);
		}
		this.imo = imo;
	}

	public String getCallSign() {
		return callSign;
	}

	public void setCallSign(String callSign) {
		String sCallSign = callSign.replace("@", "");
		if (!sCallSign.equals(this.callSign)) {
			setUpdated(true);
		}
		this.callSign = sCallSign;
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipName(String shipName) {
		String sShipName = shipName.replace("@", "");
		if (!sShipName.equals(this.shipName)) {
			setUpdated(true);
		}
		this.shipName = sShipName;
	}

	public int getShipType() {
		return shipType;
	}

	public void setShipType(int shipType) {
		if (shipType != this.shipType) {
			setUpdated(true);
		}
		this.shipType = shipType;
	}

	public VesselDimensions getDimension() {
		return dimension;
	}

	public void setDimension(VesselDimensions dimension) {
		try {
			this.dimension = dimension;
		} catch (Exception exi) {
		}
	}

	public int getEpfd() {
		return epfd;
	}

	public void setEpfd(int epfd) {
		if (epfd != this.epfd) {
			setUpdated(true);
		}
		this.epfd = epfd;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		if (month != this.month) {
			setUpdated(true);
		}
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		if (day != this.day) {
			setUpdated(true);
		}
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		if (hour != this.hour) {
			setUpdated(true);
		}
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		if (minute != this.minute) {
			setUpdated(true);
		}
		this.minute = minute;
	}

	public float getDraught() {
		return draught;
	}

	public void setDraught(float draught) {
		if (!Utilities.floatCompare(draught, this.draught)) {
			setUpdated(true);
		}
		this.draught = draught;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		String sDestination = destination.replace("@", "");
		if (!sDestination.equals(this.destination)) {
			setUpdated(true);
		}
		this.destination = sDestination;
	}

	public int getDte() {
		return dte;
	}

	public void setDte(int dte) {
		if (dte != this.dte) {
			setUpdated(true);
		}
		this.dte = dte;
	}

	public int getAssignedMode() {
		return assignedMode;
	}

	public void setAssignedMode(int assignedMode) {
		if (assignedMode != this.assignedMode) {
			setUpdated(true);
		}
		this.assignedMode = assignedMode;
	}

	public int getRegional() {
		return regional;
	}

	public void setRegional(int regional) {
		if (regional != this.regional) {
			setUpdated(true);
		}
		this.regional = regional;
	}

	public boolean isCs() {
		return cs;
	}

	public void setCs(boolean cs) {
		if (cs != this.cs) {
			setUpdated(true);
		}
		this.cs = cs;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		if (display != this.display) {
			setUpdated(true);
		}
		this.display = display;
	}

	public boolean isDsc() {
		return dsc;
	}

	public void setDsc(boolean dsc) {
		if (dsc != this.dsc) {
			setUpdated(true);
		}
		this.dsc = dsc;
	}

	public boolean isBand() {
		return band;
	}

	public void setBand(boolean band) {
		if (band != this.band) {
			setUpdated(true);
		}
		this.band = band;
	}

	public boolean isMsg22() {
		return msg22;
	}

	public void setMsg22(boolean msg22) {
		if (msg22 != this.msg22) {
			setUpdated(true);
		}
		this.msg22 = msg22;
	}

	public boolean isAssigned() {
		return assigned;
	}

	public void setAssigned(boolean assigned) {
		if (assigned != this.assigned) {
			setUpdated(true);
		}
		this.assigned = assigned;
	}

	public int getCommFlag() {
		return commFlag;
	}

	public void setCommFlag(int commFlag) {
		if (commFlag != this.commFlag) {
			setUpdated(true);
		}
		this.commFlag = commFlag;
	}

	public int getAltitudeSensor() {
		return altitudeSensor;
	}

	public void setAltitudeSensor(int altitudeSensor) {
		if (altitudeSensor != this.altitudeSensor) {
			setUpdated(true);
		}
		this.altitudeSensor = altitudeSensor;
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		if (altitude != this.altitude) {
			setUpdated(true);
		}
		this.altitude = altitude;
	}

	public ArrayList<TrackStatusPosition> getPostitionHistory() {
		return positionHistory;
	}

	public String getPositionHistoryAsString() {
		String result = "";
		
		try {
			// result = SentenceBase.objectMapper.writeValueAsString(this);
			ArrayNode node = TrackWatcher.objectMapper.createArrayNode();

			for(TrackStatusPosition trackPosition : getPostitionHistory()) {
				node.add(SentenceBase.objectMapper.readTree(trackPosition.toString()));
			}
			
			result = TrackWatcher.objectMapper.writeValueAsString(node);
			node = null;
		} catch (Exception exi) {
			result = "error, Sentence, " + exi.getMessage();
		}
		
		return result;
	}

	public String getPositionHistoryAsJSONArray() {
		String result = "";
		
		try {
			// result = SentenceBase.objectMapper.writeValueAsString(this);
			ArrayNode node = TrackWatcher.objectMapper.createArrayNode();

			for(TrackStatusPosition trackPosition : getPostitionHistory()) {
				node.add(SentenceBase.objectMapper.readTree(trackPosition.toJSONArray()));
			}
			
			result = TrackWatcher.objectMapper.writeValueAsString(node);
			node = null;
		} catch (Exception exi) {
			result = "error, Sentence, " + exi.getMessage();
		}
		
		return result;
	}
	
	public void addPositionHistory(TrackStatus status) {
		if (this.positionHistory.size() > 10) {
			this.positionHistory.remove(0);
		}
		
		this.positionHistory.add(TrackStatusPosition.fromTrackStatus(status));
	}
	
	public void clearPositionHistory() {
		positionHistory.clear();
	}
	
	public int getUpdateCounter() {
		return updateCounter;
	}
	
	public void incUpdateCounter() {
		this.updateCounter++;
	}
	
	public void resetUpdateCounter() {
		this.updateCounter = 0;
	}
	
	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public Instant getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Instant time) {
		this.createTime = time;
	}

	public void setCreateTime() {
		this.createTime = Instant.now();
	}

	public Instant getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime() {
		this.updateTime = Instant.now();
	}
	
	public MutablePeriod getPeriod() {
		return period;
	}
	
	public void setPeriod() {
		incUpdateCounter();
		setUpdateTime();
		this.period.setPeriod(getCreateTime(), getUpdateTime());
	}

	private String transponderType = "";

	private int type = 0;
	private int repeat = 0;
	private int mmsi = 0;
	private int status = 15;
	private double rateOfTurn = 0;
	private float speed = 102.3f;
	private boolean accuracy = false;
	private float longitude = 181f;
	private float latitude = 91f;
	private float course = 360.0f;
	private int heading = 511;
	private int second = 60;
	private int maneuver = 0;
	private boolean raim = false;
	private int radio = 0;
	private CommunicationState commState = null;

	private int aisVersion = 0;
	private int imo = 0;
	private String callSign = "";
	private String shipName = "";
	private int shipType = 0;
	private VesselDimensions dimension = null;
	private int epfd = 0;
	private int month = 0;
	private int day = 0;
	private int hour = 24;
	private int minute = 60;
	private float draught = 0;
	private String destination = "";
	private int dte = 1;
	private int assignedMode = 0;

	private int regional = 0;
	private boolean cs = false;
	private boolean display = false;
	private boolean dsc = false;
	private boolean band = false;
	private boolean msg22 = false;
	private boolean assigned = false;
	private int commFlag = 0;

	private int altitude = 4095;
	private int altitudeSensor = 0;

	private ArrayList<TrackStatusPosition> positionHistory = new ArrayList<TrackStatusPosition>();
	private int updateCounter = 0;
	private boolean updated = false;
	private boolean removed = false;
	private Instant createTime = Instant.now();
	private Instant updateTime = Instant.now();
	
	private MutablePeriod period = new MutablePeriod();
}
