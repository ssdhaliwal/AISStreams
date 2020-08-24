package elsu.ais.monitor;

public class TrackQueueMonitor extends Thread {
	private TrackWatcher watcher = null;
	private int latencyCleanupSpan = 5;
	private int latencyCleanupTime = 60000;

	public TrackQueueMonitor(TrackWatcher watcher) {
		this.watcher = watcher;
		
		this.latencyCleanupSpan = watcher.getLatencyCleanupSpan();
		this.latencyCleanupTime = watcher.getLatencyCleanupTime();
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				Thread.sleep(latencyCleanupSpan * latencyCleanupTime);
				
				watcher.checkQueueStatus();
			}
		} catch (Exception ex) {
			// log error for tracking
			System.out.println("track cleanup thread error; " + ex.getMessage());
		} finally {
		}
	}
}
