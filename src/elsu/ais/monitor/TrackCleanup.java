package elsu.ais.monitor;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.*;

import elsu.ais.base.AISMessageBase;
import elsu.ais.resources.ITrackListener;

public class TrackCleanup extends Thread {
	public boolean isShutdown = false;
	private int cleanupTime = 60000;
	private int cleanupSpan = 5;
	private TrackWatcher watcher = null;

	private List<ITrackListener> listeners = new ArrayList<>();

	public TrackCleanup(int cleanupTime, int cleanupSpan, TrackWatcher watcher) {
		this.cleanupTime = cleanupTime;
		this.cleanupSpan = cleanupSpan;
		this.watcher = watcher;
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
			ArrayList<Integer> trackRemoval = new ArrayList<Integer>();
			TrackStatus status = null;
			
			// start the cleanup monitor
			if (!isShutdown) {
				// this is to prevent socket to stay open after error
				try {
					while (!isShutdown) {
						Thread.sleep(cleanupTime);
						
						// identify and update records
						synchronized(watcher) {
							try {
								for (Integer mmsi : watcher.getTrackStatus().keySet()) {
									status = watcher.getTrackStatus().get(mmsi);
									status.setIdleCounter();
									
									if (status.getIdleCounter() > cleanupSpan) {
										trackRemoval.add(status.getMmsi());
									}
								}
							} catch (Exception ex) {
								System.out.println("track cleanup thread error - selection; " + ex.getMessage());
							}
						}
						
						Thread.yield();
						
						synchronized(watcher) {
							try {
								for (Integer mmsi : trackRemoval) {
									status = watcher.getTrackStatus().get(mmsi);
									
									if (status.getIdleCounter() > cleanupSpan) {
										status.setRemoved(true);
										
										watcher.getTrackStatus().remove(mmsi);
										sendTrackRemove(status.toJSONArray());
									}
								}
							} catch (Exception ex) {
								System.out.println("track cleanup thread error - removal; " + ex.getMessage());
							}
						}
						
						trackRemoval.clear();
					}
				} catch (Exception ex) {
					// log error for tracking
					System.out.println("track cleanup thread error; " + ex.getMessage());
				} finally {
				}
			}
		} catch (Exception ex) {
			// log error for tracking
			System.out.println("track cleanup thread error; " + ex.getMessage());
		} finally {
			isShutdown = true;

			// log message
			System.out.println("track cleanup thread shutdown.");
		}
	}
}
