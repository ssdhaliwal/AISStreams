package elsu.ais.monitor;

import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormat;

import elsu.ais.base.AISLookupValues;
import elsu.ais.base.AISMessageBase;
import elsu.ais.messages.*;

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

		// return null; if not valid message for parsing
		if (status != null) {
			status.incUpdateCounter();
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

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);

				status.addPositionHistory(status);
				status.fromT1PositionReportClassA(message);
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

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);

				status.fromT5StaticAndVoyageRelatedData(message);
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

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);

				status.addPositionHistory(status);
				status.fromT18StandardClassBEquipmentPositionReport(message);
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

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);

				status.addPositionHistory(status);
				status.fromT19ExtendedClassBEquipmentPositionReport(message);
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

				watcher.updateTrackStatus(status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);

				status.addPositionHistory(status);
				status.fromT9StandardSARPositionReport(message);
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
		StringBuilder buffer = new StringBuilder();

		buffer.append("{");
		buffer.append("\"transponder\":\"" + getTransponderType() + "\"");
		buffer.append(", \"type\":" + getType());
		buffer.append(", \"typeText\":\"" + AISLookupValues.getMessageType(getType()) + "\"");
		buffer.append(", \"repeat\":" + getRepeat());
		buffer.append(", \"mmsi\":" + getMmsi());
		buffer.append(", \"status\":" + getStatus());
		buffer.append(", \"statusText\":\"" + AISLookupValues.getNavigationStatus(getStatus()) + "\"");
		buffer.append(", \"rateOfTurn\":" + getRateOfTurn());
		buffer.append(", \"speed\":" + getSpeed());
		buffer.append(", \"accuracy\":" + isAccuracy());
		buffer.append(", \"longitude\":" + getLongitude());
		buffer.append(", \"latitude\":" + getLatitude());
		buffer.append(", \"course\":" + getCourse());
		buffer.append(", \"heading\":" + getHeading());
		buffer.append(", \"second\":" + getSecond());
		buffer.append(", \"maneuver\":" + getManeuver());
		buffer.append(", \"maneuverText\":\"" + AISLookupValues.getManeuverIndicator(getManeuver()) + "\"");
		buffer.append(", \"raim\":" + isRaim());
		buffer.append(", \"radio\":" + getRadio());
		buffer.append(", \"commState\":" + getCommState());
		buffer.append(", \"commtech\":\"" + AISLookupValues.getCommunicationTechnology(getType()) + "\"");
		buffer.append(", \"aisVersion\":" + getAisVersion());
		buffer.append(", \"imo\":" + getImo());
		buffer.append(", \"callSign\":\"" + getCallSign().trim() + "\"");
		buffer.append(", \"shipName\":\"" + getShipName().trim() + "\"");
		buffer.append(", \"shipType\":" + getShipType());
		buffer.append(", \"shipTypeText\":\"" + AISLookupValues.getShipType(getShipType()) + "\"");
		buffer.append(", \"dimension\":" + getDimension());
		buffer.append(", \"epfd\":" + getEpfd());
		buffer.append(", \"epfdText\":\"" + AISLookupValues.getEPFDFixType(getEpfd()) + "\"");
		buffer.append(", \"month\":" + getMonth());
		buffer.append(", \"hour\":" + getHour());
		buffer.append(", \"day\":" + getDay());
		buffer.append(", \"minute\":" + getMinute());
		buffer.append(", \"draught\":" + getDraught());
		buffer.append(", \"destination\":\"" + getDestination().trim() + "\"");
		buffer.append(", \"dte\":" + getDte());
		buffer.append(", \"dteText\":\"" + AISLookupValues.getDte(getDte()) + "\"");
		buffer.append(", \"assignedMode\":" + getAssignedMode());
		buffer.append(", \"regional\":" + getRegional());
		buffer.append(", \"cs\":" + isCs());
		buffer.append(", \"display\":" + isDisplay());
		buffer.append(", \"dsc\":" + isDsc());
		buffer.append(", \"band\":" + isBand());
		buffer.append(", \"msg22\":" + isMsg22());
		buffer.append(", \"assigned\":" + isAssigned());
		buffer.append(", \"commFlag\":" + getCommFlag());
		buffer.append(", \"commFlagText\":\"" + AISLookupValues.getCommunicationFlag(getCommFlag()) + "\"");
		buffer.append(", \"positionHistory\":" + getPositionHistoryAsString());
		buffer.append(", \"createTime\":\"" + getCreateTime() + "\"");
		buffer.append(", \"updateCounter\":\"" + getUpdateCounter() + "\"");
		buffer.append(", \"period\":\"" + ISOPeriodFormat.standard().print(getPeriod()) + "\"");
		buffer.append("}");

		return buffer.toString();
	}

	public String toJSONArray() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("[");
		buffer.append("\"" + getTransponderType() + "\""); // 0
		buffer.append(", " + getType()); // 1
		buffer.append(", \"" + AISLookupValues.getMessageType(getType()) + "\""); // 2
		buffer.append(", " + getRepeat()); // 3
		buffer.append(", " + getMmsi()); // 4
		buffer.append(", " + getStatus()); // 5
		buffer.append(", \"" + AISLookupValues.getNavigationStatus(getStatus()) + "\""); // 6
		buffer.append(", " + getRateOfTurn()); // 7
		buffer.append(", " + getSpeed()); // 8
		buffer.append(", " + isAccuracy()); // 9
		buffer.append(", " + getLongitude()); // 10
		buffer.append(", " + getLatitude()); // 11
		buffer.append(", " + getCourse()); // 12
		buffer.append(", " + getHeading()); // 13
		buffer.append(", " + getSecond()); // 14
		buffer.append(", " + getManeuver()); // 15
		buffer.append(", \"" + AISLookupValues.getManeuverIndicator(getManeuver()) + "\""); // 16
		buffer.append(", " + isRaim()); // 17
		buffer.append(", " + getRadio()); // 18
		buffer.append(", " + getCommState()); // 19
		buffer.append(", \"" + AISLookupValues.getCommunicationTechnology(getType()) + "\""); // 20
		buffer.append(", " + getAisVersion()); // 21
		buffer.append(", " + getImo()); // 22
		buffer.append(", \"" + getCallSign().trim() + "\""); // 23
		buffer.append(", \"" + getShipName().trim() + "\""); // 24
		buffer.append(", " + getShipType()); // 25
		buffer.append(", \"" + AISLookupValues.getShipType(getShipType()) + "\""); // 26
		buffer.append(", " + getDimension()); // 27
		buffer.append(", " + getEpfd()); // 28
		buffer.append(", \"" + AISLookupValues.getEPFDFixType(getEpfd()) + "\""); // 29
		buffer.append(", " + getMonth()); // 30
		buffer.append(", " + getHour()); // 31
		buffer.append(", " + getDay()); // 32
		buffer.append(", " + getMinute()); // 33
		buffer.append(", " + getDraught()); // 34
		buffer.append(", \"" + getDestination().trim() + "\""); // 35
		buffer.append(", " + getDte()); // 36
		buffer.append(", \"" + AISLookupValues.getDte(getDte()) + "\""); // 37
		buffer.append(", " + getAssignedMode()); // 38
		buffer.append(", " + getRegional()); // 39
		buffer.append(", " + isCs()); // 40
		buffer.append(", " + isDisplay()); // 41
		buffer.append(", " + isDsc()); // 42
		buffer.append(", " + isBand()); // 43
		buffer.append(", " + isMsg22()); // 44
		buffer.append(", " + isAssigned()); // 45
		buffer.append(", " + getCommFlag()); // 46
		buffer.append(", \"" + AISLookupValues.getCommunicationFlag(getCommFlag()) + "\""); // 47
		buffer.append(", " + getPositionHistoryAsJSONArray()); // 48
		buffer.append(", \"" + getCreateTime() + "\""); // 49
		buffer.append(", " + getUpdateCounter()); // 50
		buffer.append(", \"" + ISOPeriodFormat.standard().print(getPeriod()) + "\""); // 51
		buffer.append("]");

		return buffer.toString();
	}
}
