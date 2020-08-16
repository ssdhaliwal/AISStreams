package elsu.ais.monitor;

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
			status.clearIdleCounter();
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

				watcher.getTrackStatus().put(message.getMmsi(), status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

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

				watcher.getTrackStatus().put(message.getMmsi(), status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

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

				watcher.getTrackStatus().put(message.getMmsi(), status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

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

				watcher.getTrackStatus().put(message.getMmsi(), status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

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

				watcher.getTrackStatus().put(message.getMmsi(), status);
				status = (TrackStatus) status.clone();
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

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
		buffer.append(", \"updated\":" + isUpdated());
		buffer.append(", \"Removed\":" + isRemoved());
		buffer.append(", \"updateTime\":" + getUpdateTime());
		buffer.append("}");

		return buffer.toString();
	}

	public String toJSONArray() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("[");
		buffer.append("\"" + getTransponderType() + "\"");
		buffer.append(", " + getType());
		buffer.append(", \"" + AISLookupValues.getMessageType(getType()) + "\"");
		buffer.append(", " + getRepeat());
		buffer.append(", " + getMmsi());
		buffer.append(", " + getStatus());
		buffer.append(", \"" + AISLookupValues.getNavigationStatus(getStatus()) + "\"");
		buffer.append(", " + getRateOfTurn());
		buffer.append(", " + getSpeed());
		buffer.append(", " + isAccuracy());
		buffer.append(", " + getLongitude());
		buffer.append(", " + getLatitude());
		buffer.append(", " + getCourse());
		buffer.append(", " + getHeading());
		buffer.append(", " + getSecond());
		buffer.append(", " + getManeuver());
		buffer.append(", \"" + AISLookupValues.getManeuverIndicator(getManeuver()) + "\"");
		buffer.append(", " + isRaim());
		buffer.append(", " + getRadio());
		buffer.append(", " + getCommState());
		buffer.append(", \"" + AISLookupValues.getCommunicationTechnology(getType()) + "\"");
		buffer.append(", " + getAisVersion());
		buffer.append(", " + getImo());
		buffer.append(", \"" + getCallSign().trim() + "\"");
		buffer.append(", \"" + getShipName().trim() + "\"");
		buffer.append(", " + getShipType());
		buffer.append(", \"" + AISLookupValues.getShipType(getShipType()) + "\"");
		buffer.append(", " + getDimension());
		buffer.append(", " + getEpfd());
		buffer.append(", \"" + AISLookupValues.getEPFDFixType(getEpfd()) + "\"");
		buffer.append(", " + getMonth());
		buffer.append(", " + getHour());
		buffer.append(", " + getDay());
		buffer.append(", " + getMinute());
		buffer.append(", " + getDraught());
		buffer.append(", \"" + getDestination().trim() + "\"");
		buffer.append(", " + getDte());
		buffer.append(", \"" + AISLookupValues.getDte(getDte()) + "\"");
		buffer.append(", " + getAssignedMode());
		buffer.append(", " + getRegional());
		buffer.append(", " + isCs());
		buffer.append(", " + isDisplay());
		buffer.append(", " + isDsc());
		buffer.append(", " + isBand());
		buffer.append(", " + isMsg22());
		buffer.append(", " + isAssigned());
		buffer.append(", " + getCommFlag());
		buffer.append(", \"" + AISLookupValues.getCommunicationFlag(getCommFlag()) + "\"");
		buffer.append(", " + isUpdated());
		buffer.append(", " + isRemoved());
		buffer.append(", " + getUpdateTime());
		buffer.append("]");

		return buffer.toString();
	}
}
