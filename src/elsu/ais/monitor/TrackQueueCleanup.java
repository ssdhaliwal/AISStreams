package elsu.ais.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import elsu.ais.base.AISMessageBase;
import elsu.ais.resources.ITrackListener;

public class TrackQueueCleanup extends Thread {
	public TrackQueueCleanup(TrackWatcher watcher) {
		this.watcher = watcher;
		this.trackStatusMap = watcher.getTrackStatus();
		this.listeners = watcher.getListeners();
		this.latencyCleanupSpan = watcher.getLatencyCleanupSpan();
		this.latencyCleanupTime = watcher.getLatencyCleanupTime();
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

				int latent = 0, reset = 0;
				TrackStatus status = null;

				// start the cleanup monitor
				try {
					for (Integer mmsi : trackStatusMap.keySet()) {
						Thread.yield();

						try {
							status = trackStatusMap.get(mmsi);

							if (status != null) {
								if (status.getUpdateCounter() == 0) {
									sendTrackRemove(status.toJSONArray());
									status.setRemoved(true);

									status.clearPositionHistory();
									status.resetUpdateCounter();

									latent++;
								} else {
									reset++;
									status.resetUpdateCounter();
								}
							}
						} catch (Exception ex) {
							System.out
									.println(getClass().toString() + ", run(), " + "track cleanup, " + ex.getMessage());
						}

						Thread.yield();
					}

					System.out.println(
							"TrackStatus/ total: " + trackStatusMap.size() + "/ latent: " + latent + "/ reset: " + reset);
					watcher.saveTrackHistoryToFile();
				} catch (Exception ex) {
					System.out.println(getClass().toString() + ", run(), " + "track cleanup-2, " + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			// log error for tracking
			System.out.println(getClass().toString() + ", run(), " + "track cleanup-3, " + ex.getMessage());
		} finally {
		}
	}

	private TrackWatcher watcher = null;
	private List<ITrackListener> listeners = null;
	private HashMap<Integer, TrackStatus> trackStatusMap = null;
	private int latencyCleanupSpan = 5;
	private int latencyCleanupTime = 60000;
}
