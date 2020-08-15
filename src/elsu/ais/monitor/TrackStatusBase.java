package elsu.ais.monitor;

import elsu.ais.base.AISLookupValues;
import elsu.ais.messages.*;
import elsu.ais.messages.data.*;

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
	}

	public String getTransponderType() {
		return transponderType;
	}

	public String setTransponderType(String transponderType) {
		return this.transponderType = transponderType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public int getMmsi() {
		return mmsi;
	}

	public void setMmsi(int mmsi) {
		this.mmsi = mmsi;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getRateOfTurn() {
		return rateOfTurn;
	}

	public void setRateOfTurn(double rateOfTurn) {
		this.rateOfTurn = rateOfTurn;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public boolean isAccuracy() {
		return accuracy;
	}

	public void setAccuracy(boolean accuracy) {
		this.accuracy = accuracy;
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

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getManeuver() {
		return maneuver;
	}

	public void setManeuver(int maneuver) {
		this.maneuver = maneuver;
	}

	public boolean isRaim() {
		return raim;
	}

	public void setRaim(boolean raim) {
		this.raim = raim;
	}

	public int getRadio() {
		return radio;
	}

	public void setRadio(int radio) {
		this.radio = radio;
	}

	public CommunicationState getCommState() {
		return commState;
	}

	private void setCommState(CommunicationState commState) {
		this.commState = commState;
	}

	public int getAisVersion() {
		return aisVersion;
	}

	public void setAisVersion(int aisVersion) {
		this.aisVersion = aisVersion;
	}

	public int getImo() {
		return imo;
	}

	public void setImo(int imo) {
		this.imo = imo;
	}

	public String getCallSign() {
		return callSign;
	}

	public void setCallSign(String callSign) {
		this.callSign = callSign.replace("@", "");
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipName(String shipName) {
		this.shipName = shipName.replace("@", "");
	}

	public int getShipType() {
		return shipType;
	}

	public void setShipType(int shipType) {
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
		this.epfd = epfd;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public float getDraught() {
		return draught;
	}

	public void setDraught(float draught) {
		this.draught = draught;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination.replace("@", "");
	}

	public int getDte() {
		return dte;
	}

	public void setDte(int dte) {
		this.dte = dte;
	}

	public int getAssignedMode() {
		return assignedMode;
	}

	public void setAssignedMode(int assignedMode) {
		this.assignedMode = assignedMode;
	}

	public int getRegional() {
		return regional;
	}

	public void setRegional(int regional) {
		this.regional = regional;
	}

	public boolean isCs() {
		return cs;
	}

	public void setCs(boolean cs) {
		this.cs = cs;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean isDsc() {
		return dsc;
	}

	public void setDsc(boolean dsc) {
		this.dsc = dsc;
	}

	public boolean isBand() {
		return band;
	}

	public void setBand(boolean band) {
		this.band = band;
	}

	public boolean isMsg22() {
		return msg22;
	}

	public void setMsg22(boolean msg22) {
		this.msg22 = msg22;
	}

	public boolean isAssigned() {
		return assigned;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}

	public int getCommFlag() {
		return commFlag;
	}

	public void setCommFlag(int commFlag) {
		this.commFlag = commFlag;
	}

	public int getAltitudeSensor() {
		return altitudeSensor;
	}

	public void setAltitudeSensor(int altitudeSensor) {
		this.altitudeSensor = altitudeSensor;
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

	private String transponderType = "";

	private int type = 0;
	private int repeat = 0;
	private int mmsi = 0;
	private int status = 0;
	private double rateOfTurn = 0;
	private float speed = 0.0f;
	private boolean accuracy = false;
	private float longitude = 0f;
	private float latitude = 0f;
	private float course = 0.0f;
	private int heading = 0;
	private int second = 0;
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
	private int hour = 0;
	private int minute = 0;
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

	private int altitude = 0;
	private int altitudeSensor = 0;
}
