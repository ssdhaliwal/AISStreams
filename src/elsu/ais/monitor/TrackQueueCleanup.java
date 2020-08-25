package elsu.ais.monitor;

import java.util.HashMap;
import java.util.List;


import elsu.ais.base.AISMessageBase;
import elsu.ais.resources.ITrackListener;

public class TrackQueueCleanup extends Thread {
	private List<ITrackListener> listeners = null;
	private HashMap<Integer, TrackStatus> trackStatusQ = null;
	private HashMap<Integer, TrackStatus> trackStatusHistory = null;

	public TrackQueueCleanup(HashMap<Integer, TrackStatus> statusHistory, 
			HashMap<Integer, TrackStatus> statusQ, List<ITrackListener> listeners) {
		this.trackStatusHistory = statusHistory;
		this.trackStatusQ = statusQ;
		this.listeners = listeners;
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
			TrackStatus status = null;
			
			// start the cleanup monitor
			for (Integer mmsi : trackStatusQ.keySet()) {
				Thread.yield();
				
				try {
					status = trackStatusQ.get(mmsi);

					sendTrackRemove(status.toJSONArray());
					status.setRemoved(true);
					
					status.clearPositionHistory();
					status.resetUpdateCounter();
					
					trackStatusHistory.put(mmsi, status);
				} catch (Exception ex) {
					System.out.println("track cleanup thread error - removal; " + ex.getMessage());
				}

				Thread.yield();
			}
		} catch (Exception ex) {
			// log error for tracking
			System.out.println("track cleanup thread error; " + ex.getMessage());
		} finally {
			trackStatusQ.clear();
		}
	}
}
