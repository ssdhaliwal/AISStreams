package elsu.ais.monitor;

import java.util.Date;
import java.util.HashMap;

public class TrackMonitor {

	public TrackMonitor() {
	}

	public void addTrack(Object track) {
		// only process position reports and status voyage data
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

	private HashMap<Integer, TrackStatus> trackStatus = new HashMap<Integer, TrackStatus>();
	private HashMap<Integer, Date> trackLastReport = new HashMap<Integer, Date>();
}
