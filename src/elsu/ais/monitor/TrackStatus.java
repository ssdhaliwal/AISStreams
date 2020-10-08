package elsu.ais.monitor;

import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormat;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import elsu.ais.base.AISLookupValues;
import elsu.ais.base.AISMessageBase;
import elsu.ais.messages.*;
import elsu.sentence.SentenceBase;

public class TrackStatus extends TrackStatusBase implements Cloneable {

	public static TrackStatus fromMessage(TrackWatcher watcher, AISMessageBase message) throws Exception {
		TrackStatus status = null;

		// only process position reports and status voyage data
		if (message instanceof T1_PositionReportClassA) {
			status = TrackStatus.fromMessage(watcher, (T1_PositionReportClassA) message);
		} else if (message instanceof T5_StaticAndVoyageRelatedData) {
			status = TrackStatus.fromMessage(watcher, (T5_StaticAndVoyageRelatedData) message);
		} else if (message instanceof T18_StandardClassBEquipmentPositionReport) {
			status = TrackStatus.fromMessage(watcher, (T18_StandardClassBEquipmentPositionReport) message);
		} else if (message instanceof T19_ExtendedClassBEquipmentPositionReport) {
			status = TrackStatus.fromMessage(watcher, (T19_ExtendedClassBEquipmentPositionReport) message);
		} else if (message instanceof T9_StandardSARPositionReport) {
			status = TrackStatus.fromMessage(watcher, (T9_StandardSARPositionReport) message);
		}

		return status;
	}

	public static TrackStatus fromMessage(TrackWatcher watcher, T1_PositionReportClassA message) throws Exception {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT1PositionReportClassA(message);
				status.setUpdated(false);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.addPositionHistory(status);
				status.fromT1PositionReportClassA(message);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		}

		return status;
	}

	public static TrackStatus fromMessage(TrackWatcher watcher, T5_StaticAndVoyageRelatedData message)
			throws Exception {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT5StaticAndVoyageRelatedData(message);
				status.setUpdated(false);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.fromT5StaticAndVoyageRelatedData(message);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		}
		return status;
	}

	public static TrackStatus fromMessage(TrackWatcher watcher, T18_StandardClassBEquipmentPositionReport message)
			throws Exception {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT18StandardClassBEquipmentPositionReport(message);
				status.setUpdated(false);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.addPositionHistory(status);
				status.fromT18StandardClassBEquipmentPositionReport(message);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		}
		return status;
	}

	public static TrackStatus fromMessage(TrackWatcher watcher, T19_ExtendedClassBEquipmentPositionReport message)
			throws Exception {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT19ExtendedClassBEquipmentPositionReport(message);
				status.setUpdated(false);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.addPositionHistory(status);
				status.fromT19ExtendedClassBEquipmentPositionReport(message);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		}

		return status;
	}

	public static TrackStatus fromMessage(TrackWatcher watcher, T9_StandardSARPositionReport message) throws Exception {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT9StandardSARPositionReport(message);
				status.setUpdated(false);

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.addPositionHistory(status);
				status.fromT9StandardSARPositionReport(message);
				
				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		}
		return status;
	}

	public TrackStatus() {
	}

	public synchronized Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		String result = "";
		
		try {
			// result = SentenceBase.objectMapper.writeValueAsString(this);
			ObjectNode node = TrackWatcher.objectMapper.createObjectNode();

			node.put("transponder", getTransponderType()); // 0
			node.put("type", getType()); // 1
			node.put("typeText", AISLookupValues.getMessageType(getType())); // 2
			node.put("repeat", getRepeat()); // 3
			node.put("mmsi", getMmsi()); // 4
			node.put("status", getStatus()); // 5
			node.put("statusText", AISLookupValues.getNavigationStatus(getStatus())); // 6
			node.put("rateOfTurn", getRateOfTurn()); // 7
			node.put("speed", getSpeed()); // 8
			node.put("accuracy", isAccuracy()); // 9
			node.put("longitude", getLongitude()); // 10
			node.put("latitude", getLatitude()); // 11
			node.put("course", getCourse()); // 12
			node.put("heading", getHeading()); // 13
			node.put("second", getSecond()); // 14
			node.put("maneuver", getManeuver()); // 15
			node.put("maneuverText", AISLookupValues.getManeuverIndicator(getManeuver())); // 16
			node.put("raim", isRaim()); // 17
			node.put("radio", getRadio()); // 18
			node.set("commState", TrackWatcher.objectMapper.readTree(((getCommState() != null) ? getCommState().toString() : ""))); // 19
			node.put("commtech", AISLookupValues.getCommunicationTechnology(getType())); // 20
			node.put("aisVersion", getAisVersion()); // 21
			node.put("imo", getImo()); // 22
			node.put("callSign", getCallSign().trim()); // 23
			node.put("shipName", getShipName().trim()); // 24
			node.put("shipType", getShipType()); // 25
			node.put("shipTypeText", AISLookupValues.getShipType(getShipType())); // 26
			node.set("dimension", SentenceBase.objectMapper.readTree(((getDimension() != null) ? getDimension().toString() : ""))); // 27
			node.put("epfd", getEpfd()); // 28
			node.put("epfdText", AISLookupValues.getEPFDFixType(getEpfd())); // 29
			node.put("month", getMonth()); // 30
			node.put("hour", getHour()); // 31
			node.put("day", getDay()); // 32
			node.put("minute", getMinute()); // 33
			node.put("draught", getDraught()); // 34
			node.put("destination", getDestination().trim()); // 35
			node.put("dte", getDte()); // 36
			node.put("dteText", AISLookupValues.getDte(getDte())); // 37
			node.put("assignedMode", getAssignedMode()); // 38
			node.put("regional", getRegional()); // 39
			node.put("cs", isCs()); // 40
			node.put("display", isDisplay()); // 41
			node.put("dsc", isDsc()); // 42
			node.put("band", isBand()); // 43
			node.put("msg22", isMsg22()); // 44
			node.put("assigned", isAssigned()); // 45
			node.put("commFlag", getCommFlag()); // 46
			node.put("commFlagText", AISLookupValues.getCommunicationFlag(getCommFlag())); // 47
			node.set("positionHistory", SentenceBase.objectMapper.readTree(getPositionHistoryAsString())); // 48
			node.put("createTime", SentenceBase.formatEPOCHToUTC((int)(getCreateTime().getMillis() / 1000))); // 49
			node.put("updateCounter", getUpdateCounter()); // 50
			node.put("period", ISOPeriodFormat.standard().print(getPeriod())); // 51
			
			result = TrackWatcher.objectMapper.writeValueAsString(node);
			node = null;
		} catch (Exception exi) {
			result = "error, Sentence, " + exi.getMessage();
		}
		
		return result;
	}

	public String toJSONArray() {
		String result = "";
		
		try {
			// result = SentenceBase.objectMapper.writeValueAsString(this);
			ArrayNode node = TrackWatcher.objectMapper.createArrayNode();

			node.add(getTransponderType()); // 0
			node.add(getType()); // 1
			node.add(AISLookupValues.getMessageType(getType())); // 2
			node.add(getRepeat()); // 3
			node.add(getMmsi()); // 4
			node.add(getStatus()); // 5
			node.add(AISLookupValues.getNavigationStatus(getStatus())); // 6
			node.add(getRateOfTurn()); // 7
			node.add(getSpeed()); // 8
			node.add(isAccuracy()); // 9
			node.add(getLongitude()); // 10
			node.add(getLatitude()); // 11
			node.add(getCourse()); // 12
			node.add(getHeading()); // 13
			node.add(getSecond()); // 14
			node.add(getManeuver()); // 15
			node.add(AISLookupValues.getManeuverIndicator(getManeuver())); // 16
			node.add(isRaim()); // 17
			node.add(getRadio()); // 18
			node.add(TrackWatcher.objectMapper.readTree(((getCommState() != null) ? getCommState().toString() : ""))); // 19
			node.add(AISLookupValues.getCommunicationTechnology(getType())); // 20
			node.add(getAisVersion()); // 21
			node.add(getImo()); // 22
			node.add(getCallSign().trim()); // 23
			node.add(getShipName().trim()); // 24
			node.add(getShipType()); // 25
			node.add(AISLookupValues.getShipType(getShipType())); // 26
			node.add(SentenceBase.objectMapper.readTree(((getDimension() != null) ? getDimension().toString() : ""))); // 27
			node.add(getEpfd()); // 28
			node.add(AISLookupValues.getEPFDFixType(getEpfd())); // 29
			node.add(getMonth()); // 30
			node.add(getHour()); // 31
			node.add(getDay()); // 32
			node.add(getMinute()); // 33
			node.add(getDraught()); // 34
			node.add(getDestination().trim()); // 35
			node.add(getDte()); // 36
			node.add(AISLookupValues.getDte(getDte())); // 37
			node.add(getAssignedMode()); // 38
			node.add(getRegional()); // 39
			node.add(isCs()); // 40
			node.add(isDisplay()); // 41
			node.add(isDsc()); // 42
			node.add(isBand()); // 43
			node.add(isMsg22()); // 44
			node.add(isAssigned()); // 45
			node.add(getCommFlag()); // 46
			node.add(AISLookupValues.getCommunicationFlag(getCommFlag())); // 47
			node.add(SentenceBase.objectMapper.readTree(getPositionHistoryAsString())); // 48
			node.add(SentenceBase.formatEPOCHToUTC((int)(getCreateTime().getMillis() / 1000))); // 49
			node.add(getUpdateCounter()); // 50
			node.add(ISOPeriodFormat.standard().print(getPeriod())); // 51
			
			result = TrackWatcher.objectMapper.writeValueAsString(node);
			node = null;
		} catch (Exception exi) {
			result = "error, Sentence, " + exi.getMessage();
		}
		
		return result;
	}
}
