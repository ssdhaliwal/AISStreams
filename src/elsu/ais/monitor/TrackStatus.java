package elsu.ais.monitor;

import java.time.Instant;
import elsu.ais.messages.*;

public class TrackStatus extends TrackStatusBase {

	public static TrackStatus fromMessage(TrackMonitor watcher, T1_PositionReportClassA message) {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT1PositionReportClassA(message);
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

				status.fromT1PositionReportClassA(message);
			}
		}
		return status;
	}

	public static TrackStatus fromMessage(TrackMonitor watcher, T5_StaticAndVoyageRelatedData message) {
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
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

				status.fromT5StaticAndVoyageRelatedData(message);
			}
		}
		return status;
	}

	public static TrackStatus fromMessage(TrackMonitor watcher, T18_StandardClassBEquipmentPositionReport message) {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT18StandardClassBEquipmentPositionReport(message);
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

				status.fromT18StandardClassBEquipmentPositionReport(message);
			}
		}
		return status;
	}

	public static TrackStatus fromMessage(TrackMonitor watcher, T19_ExtendedClassBEquipmentPositionReport message) {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT19ExtendedClassBEquipmentPositionReport(message);
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

				status.fromT19ExtendedClassBEquipmentPositionReport(message);
			}
		}
		
		synchronized(watcher) {
			watcher.getTrackLastReport().put(message.getMmsi(), Instant.now());
		}
		return status;
	}

	public static TrackStatus fromMessage(TrackMonitor watcher, T9_StandardSARPositionReport message) {
		TrackStatus status = null;

		// if exists; lock and update
		synchronized (watcher) {
			status = watcher.isActive(message.getMmsi());
		}

		if (status == null) {
			synchronized (TrackStatus.class) {
				status = new TrackStatus();
				status.fromT9StandardSARPositionReport(message);
			}
		} else {
			synchronized (status) {
				status.setUpdated(true);
				status.setUpdateTime();

				status.fromT9StandardSARPositionReport(message);
			}
		}
		return status;
	}

	public TrackStatus() {
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public Instant getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime() {
		this.updateTime = Instant.now();
	}

	public int getIdleCounter() {
		return idleCounter;
	}

	public void setIdleCounter() {
		this.idleCounter++;
	}

	public void clearIdleCounter() {
		this.idleCounter = 0;
	}

	private boolean updated = false;
	private Instant updateTime = Instant.now();
	private int idleCounter = 0;
}
