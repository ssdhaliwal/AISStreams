package elsu.ais.monitor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import elsu.ais.base.AISMessageBase;
import elsu.ais.messages.*;
import elsu.ais.resources.ITrackListener;
import elsu.support.ConfigLoader;

public class TrackMonitor {

	public TrackMonitor(ConfigLoader config) {
		// initialize track cleaner
	}

	public void registerListener(ITrackListener listener) {
		this.addListener(listener);
		getCleaner().addListener(listener);
	}

	public void processTrack(AISMessageBase message) {
		TrackStatus status = null;

		// only process position reports and status voyage data
		if (message.getClass().isInstance(T1_PositionReportClassA.class)) {
			status = TrackStatus.fromMessage(this, (T1_PositionReportClassA) message);
		} else if (message.getClass().isInstance(T5_StaticAndVoyageRelatedData.class)) {
			status = TrackStatus.fromMessage(this, (T5_StaticAndVoyageRelatedData) message);
		} else if (message.getClass().isInstance(T18_StandardClassBEquipmentPositionReport.class)) {
			status = TrackStatus.fromMessage(this, (T18_StandardClassBEquipmentPositionReport) message);
		} else if (message.getClass().isInstance(T19_ExtendedClassBEquipmentPositionReport.class)) {
			status = TrackStatus.fromMessage(this, (T19_ExtendedClassBEquipmentPositionReport) message);
		} else if (message.getClass().isInstance(T9_StandardSARPositionReport.class)) {
			status = TrackStatus.fromMessage(this, (T9_StandardSARPositionReport) message);
		}
		
		// check and notify on status
		
	}

	public TrackStatus isActive(int mmsi) {
		return getTrackStatus().get(mmsi);
	}

	public void sendTrackAdd(TrackStatus track) throws Exception {
		for (ITrackListener listener : _listeners) {
			listener.onTrackAdd(track);
		}
	}

	public void sendTrackUpdate(TrackStatus track) throws Exception {
		for (ITrackListener listener : _listeners) {
			listener.onTrackUpdate(track);
		}
	}

	public TrackStatus getTrackker() {
		return trackker;
	}

	public void setTrackker(TrackStatus trackker) {
		this.trackker = trackker;
	}

	public TrackCleanup getCleaner() {
		return cleaner;
	}

	public void setCleaner(TrackCleanup cleaner) {
		this.cleaner = cleaner;
	}

	public void addListener(ITrackListener listener) {
		_listeners.add(listener);
	}

	public void removeListener(ITrackListener listener) {
		_listeners.remove(listener);
	}

	public void clearListeners() {
		_listeners.clear();
	}

	public HashMap<Integer, TrackStatus> getTrackStatus() {
		return trackStatus;
	}

	public void setTrackStatus(HashMap<Integer, TrackStatus> trackStatus) {
		this.trackStatus = trackStatus;
	}

	public HashMap<Integer, Instant> getTrackLastReport() {
		return trackLastReport;
	}

	public void setTrackLastReport(HashMap<Integer, Instant> trackLastReport) {
		this.trackLastReport = trackLastReport;
	}

	private TrackStatus trackker = new TrackStatus();
	private TrackCleanup cleaner = new TrackCleanup();

	private List<ITrackListener> _listeners = new ArrayList<>();

	private HashMap<Integer, TrackStatus> trackStatus = new HashMap<Integer, TrackStatus>();
	private HashMap<Integer, Instant> trackLastReport = new HashMap<Integer, Instant>();
}
