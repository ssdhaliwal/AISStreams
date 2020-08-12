package elsu.ais.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import elsu.ais.base.AISMessageBase;
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
	
	public void processTrack(AISMessageBase track) {
		// only process position reports and status voyage data
		// check if mmsi exists, then update
		// if new mmsi, add track
	}
	
	private void addTrack(AISMessageBase track) {
		
	}
	
	private void updateTrack(AISMessageBase track) {
		
	}
	
	public TrackStatus isActive(AISMessageBase track) {
		return null;
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

	public HashMap<Integer, Date> getTrackLastReport() {
		return trackLastReport;
	}

	public void setTrackLastReport(HashMap<Integer, Date> trackLastReport) {
		this.trackLastReport = trackLastReport;
	}

	private TrackStatus trackker = new TrackStatus();
	private TrackCleanup cleaner = new TrackCleanup();

	private List<ITrackListener> _listeners = new ArrayList<>();
	
	private HashMap<Integer, TrackStatus> trackStatus = new HashMap<Integer, TrackStatus>();
	private HashMap<Integer, Date> trackLastReport = new HashMap<Integer, Date>();
}
