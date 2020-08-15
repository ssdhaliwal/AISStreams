package elsu.ais.monitor;

import org.joda.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import elsu.ais.base.AISMessageBase;
import elsu.ais.resources.ITrackListener;
import elsu.support.ConfigLoader;

public class TrackWatcher {

	public TrackWatcher(ConfigLoader config) {
		// initialize track cleaner
	}

	public void registerListener(ITrackListener listener) {
		this.addListener(listener);
		getCleaner().addListener(listener);
	}

	public void processTrack(AISMessageBase message) {
		TrackStatus status = null;

		try {
			status = TrackStatus.fromMessage(this, message);

			// check and notify on status
			if (status != null) {
				try {
					if (status.isUpdated()) {
						sendTrackUpdate(status);
					} else {
						sendTrackAdd(status);
					}
				} catch (Exception ex) {
					try {
						sendTrackError(ex, message);
					} catch (Exception exi) {
						System.out.println("trackWatcher notification error; " + exi.getMessage() + "; " + message);
					}
				}
			}
		} catch (Exception ex) {
			try {
				sendTrackError(ex, message);
			} catch (Exception exi) {
				System.out.println("trackWatcher parsing error; " + exi.getMessage() + "; " + message);
			}
		}
	}

	public TrackStatus isActive(int mmsi) {
		return getTrackStatus().get(mmsi);
	}

	public void sendTrackError(Exception ex, AISMessageBase message) throws Exception {
		try {
			for (ITrackListener listener : _listeners) {
				listener.onTrackError(ex, message.toString());
			}
		} catch (Exception exi) {
			System.out.println("trackWatcher sendTrackError; " + exi.getMessage() + "; " + message);
		}
	}

	public void sendTrackAdd(TrackStatus track) throws Exception {
		try {
			for (ITrackListener listener : _listeners) {
				listener.onTrackAdd(track.toJSONArray());
			}
		} catch (Exception exi) {
			System.out.println("trackWatcher sendTrackAdd; " + exi.getMessage() + "; " + track);
		}
	}

	public void sendTrackUpdate(TrackStatus track) throws Exception {
		try {
			for (ITrackListener listener : _listeners) {
				listener.onTrackUpdate(track.toJSONArray());
			}
		} catch (Exception exi) {
			System.out.println("trackWatcher sendTrackUpdate; " + exi.getMessage() + "; " + track);
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
