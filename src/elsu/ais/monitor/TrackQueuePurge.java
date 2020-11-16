package elsu.ais.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.Days;
import org.joda.time.Instant;

import elsu.ais.base.AISMessageBase;
import elsu.ais.resources.ITrackListener;

public class TrackQueuePurge extends Thread {
	public TrackQueuePurge(TrackWatcher watcher) {
		this.watcher = watcher;
		this.trackStatusMap = watcher.getTrackStatus();
		this.listeners = watcher.getListeners();
		this.latencyPurgeSpan = watcher.getLatencyPurgeSpan();
		this.latencyCleanupTime = watcher.getLatencyCleanupTime();
		this.latencyPurgeDays = watcher.getLatencyPurgeDays(); 
	}

	public void sendTrackError(Exception ex, AISMessageBase message) throws Exception {
		for (ITrackListener listener : listeners) {
			listener.onTrackError(ex, message.toString());
		}
	}

	public void sendTrackRemove(String track) throws Exception {
		for (ITrackListener listener : listeners) {
			listener.onTrackRemove(track);
		}
	}

	@Override
	public void run() {
		try {
			ArrayList<TrackStatus> tracks = new ArrayList<TrackStatus>();

			while (!isInterrupted()) {
				Thread.sleep(latencyPurgeSpan * latencyCleanupTime);
				
				int purged = 0;
				TrackStatus status = null;
				
				// start the cleanup monitor
				try {
					tracks.clear();

					ArrayList<Integer> mmsiSet = null;
					synchronized(trackStatusMap) {
						mmsiSet = new ArrayList<>(trackStatusMap.keySet());
					}
					for (Integer mmsi : mmsiSet) {
						Thread.yield();
						
						try {
							status = trackStatusMap.get(mmsi);
							
							if (status != null) {
								if (Days.daysBetween(status.getUpdateTime(), Instant.now()).getDays() >= latencyPurgeDays) {
									tracks.add(trackStatusMap.remove(mmsi));
	
									purged++;
								}
							}
						} catch (Exception ex) {
							System.out.println(getClass().toString() + ", run(), " + "track purge, " + ex.getMessage());
						}
	
						Thread.yield();
					}
					
					System.out.println("TrackStatus/ total: " + trackStatusMap.size() + "/ purged: " + purged);
					if (tracks.size() > 0) {
						watcher.saveTrackPurgeToFile(tracks);
					}
				} catch (Exception ex) {
					System.out.println(getClass().toString() + ", run(), " + "track purge-2, " + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			// log error for tracking
			System.out.println(getClass().toString() + ", run(), " + "track purge-3, " + ex.getMessage());
		} finally {
		}
	}

	private TrackWatcher watcher = null;
	private List<ITrackListener> listeners = null;
	private HashMap<Integer, TrackStatus> trackStatusMap = null;
	private int latencyPurgeSpan = 15;
	private int latencyCleanupTime = 60000;
	private int latencyPurgeDays = 5; 
}
