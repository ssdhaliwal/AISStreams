package elsu.ais.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.Days;
import org.joda.time.Instant;

import elsu.ais.base.AISMessageBase;
import elsu.ais.resources.ITrackListener;

public class TrackQueuePurge extends Thread {
	private TrackWatcher watcher = null;
	private List<ITrackListener> listeners = null;
	private ConcurrentHashMap<Integer, TrackStatus> trackStatus = null;
	private int latencyCleanupSpan = 5;
	private int latencyCleanupTime = 60000;
	private int latencyPurgeDays = 5; 

	public TrackQueuePurge(TrackWatcher watcher) {
		this.watcher = watcher;
		this.trackStatus = watcher.getTrackStatus();
		this.listeners = watcher.getListeners();
		this.latencyCleanupSpan = watcher.getLatencyCleanupSpan();
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
			while (!isInterrupted()) {
				Thread.sleep(latencyCleanupSpan * latencyCleanupTime);
				
				int purged = 0;
				TrackStatus status = null;
				
				// start the cleanup monitor
				try {
					for (Integer mmsi : trackStatus.keySet()) {
						Thread.yield();
						
						try {
							status = trackStatus.get(mmsi);
							
							if (status != null) {
								if (status.getUpdateCounter() == 0) {
									if (Days.daysBetween(status.getCreateTime(), Instant.now()).getDays() == latencyPurgeDays) {
										trackStatus.remove(mmsi);
		
										purged++;
									}
								}
							}
						} catch (Exception ex) {
							System.out.println(getClass().toString() + ", run(), " + "track purge, " + ex.getMessage());
						}
	
						Thread.yield();
					}
					
					System.out.println("TrackStatus/ total: " + trackStatus.size() + "/ purged: " + purged);
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
}
