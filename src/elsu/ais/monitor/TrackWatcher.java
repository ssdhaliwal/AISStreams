package elsu.ais.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.Instant;
import org.joda.time.Minutes;

import elsu.ais.base.AISMessageBase;
import elsu.ais.resources.ITrackListener;
import elsu.support.ConfigLoader;

public class TrackWatcher {

	public TrackWatcher(ConfigLoader config) {
		initialize(config);
	}

	private void initialize(ConfigLoader config) {
		try {
			latencyCleanupTime = Integer
					.parseInt(config.getProperty("application.services.key.latency.cleanup.time").toString());
		} catch (Exception ex) {
			latencyCleanupTime = 60000;
		}

		try {
			latencyCleanupSpan = Integer
					.parseInt(config.getProperty("application.services.key.latency.cleanup.span").toString());
		} catch (Exception ex) {
			latencyCleanupSpan = 5;
		}

		// initialize the queueMonitor
		queueMonitor = new TrackQueueMonitor(this);
		queueMonitor.start();
	}

	public void registerListener(ITrackListener listener) {
		this.addListener(listener);
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
		return getTrackStatus(mmsi);
	}

	public void sendTrackError(Exception ex, AISMessageBase message) throws Exception {
		try {
			for (ITrackListener listener : listeners) {
				listener.onTrackError(ex, message.toString());
			}
		} catch (Exception exi) {
			System.out.println("trackWatcher sendTrackError; " + exi.getMessage() + "; " + message);
		}
	}

	public void sendTrackAdd(TrackStatus track) throws Exception {
		try {
			for (ITrackListener listener : listeners) {
				listener.onTrackAdd(track.toJSONArray());
			}
		} catch (Exception exi) {
			System.out.println("trackWatcher sendTrackAdd; " + exi.getMessage() + "; " + track);
		}
	}

	public void sendTrackUpdate(TrackStatus track) throws Exception {
		try {
			for (ITrackListener listener : listeners) {
				listener.onTrackUpdate(track.toJSONArray());
			}
		} catch (Exception exi) {
			System.out.println("trackWatcher sendTrackUpdate; " + exi.getMessage() + "; " + track);
		}
	}

	public int getLatencyCleanupTime() {
		return latencyCleanupTime;
	}

	public int getLatencyCleanupSpan() {
		return latencyCleanupSpan;
	}

	public void addListener(ITrackListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ITrackListener listener) {
		listeners.remove(listener);
	}

	public void clearListeners() {
		listeners.clear();
	}

	public void checkQueueStatus() {
		synchronized (lockSearch) {
			if (getQueueId() == 1) {
				// start the cleaner thread
				TrackQueueCleanup cleaner = new TrackQueueCleanup(trackStatusHistory, trackStatusQ2, listeners);
				cleaner.start();

				trackStatusQ2 = new HashMap<Integer, TrackStatus>();
				setQueueId(2);
			} else {
				// start the cleaner thread
				TrackQueueCleanup cleaner = new TrackQueueCleanup(trackStatusHistory, trackStatusQ1, listeners);
				cleaner.start();

				trackStatusQ1 = new HashMap<Integer, TrackStatus>();
				setQueueId(1);
			}
		}
	}

	public int getQueueId() {
		return queueId;
	}

	public void setQueueId(int id) {
		this.queueId = id;
	}

	public TrackStatus getTrackStatus(int key) {
		TrackStatus status = null;

		synchronized (lockSearch) {
			if (getQueueId() == 1) {
				status = trackStatusQ1.get(key);
			} else {
				status = trackStatusQ2.get(key);
			}

			// if still null, see if in history pull and remove it
			if (status == null) {
				try {
					status = trackStatusHistory.remove(key);
					if (status != null) {
						status.setCreateTime();
					}
				} catch (Exception ex) {
					System.out.println("track history retrieval error; " + ex.getMessage());
				}
			}
		}

		return status;
	}

	public void updateTrackStatus(TrackStatus status) {
		synchronized (lockSearch) {
			if (getQueueId() == 1) {
				trackStatusQ1.put(status.getMmsi(), status);
				trackStatusQ2.remove(status.getMmsi());
			} else {
				trackStatusQ2.put(status.getMmsi(), status);
				trackStatusQ1.remove(status.getMmsi());
			}
		}
	}
	
	public ArrayList<String> getTrackPicture() {
		ArrayList<String> result = new ArrayList<String>();
		
		synchronized (lockSearch) {
			for(int mmsi : trackStatusQ1.keySet()) {
				result.add((trackStatusQ1.get(mmsi)).toJSONArray());
			}
			for(int mmsi : trackStatusQ2.keySet()) {
				result.add((trackStatusQ2.get(mmsi)).toJSONArray());
			}
		}
		
		return result;
	}

	private int latencyCleanupSpan = 5;
	private int latencyCleanupTime = 60000;

	private Object lockSearch = new Object();

	private List<ITrackListener> listeners = new ArrayList<>();

	private int queueId = 1;
	private HashMap<Integer, TrackStatus> trackStatusQ1 = new HashMap<Integer, TrackStatus>();
	private HashMap<Integer, TrackStatus> trackStatusQ2 = new HashMap<Integer, TrackStatus>();

	private HashMap<Integer, TrackStatus> trackStatusHistory = new HashMap<Integer, TrackStatus>();
	private TrackQueueMonitor queueMonitor = null;
}
