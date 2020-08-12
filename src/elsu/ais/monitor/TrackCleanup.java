package elsu.ais.monitor;

import java.util.ArrayList;
import java.util.List;

import elsu.ais.resources.ITrackListener;
import elsu.support.ConfigLoader;

public class TrackCleanup extends Thread {
	public boolean _isShutdown = false;
	private boolean _isRunning = false;

	private List<ITrackListener> _listeners = new ArrayList<>();

	public TrackCleanup(ConfigLoader config) throws Exception {

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

	public void sendTrackRemove(String track) throws Exception {
		for (ITrackListener listener : _listeners) {
			listener.onTrackRemove(track);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// start the cleanup monitor
			if (_isRunning && !_isShutdown) {
				// this is to prevent socket to stay open after error
				try {
					while (_isRunning && !_isShutdown) {
					}
				} catch (Exception ex) {
					// log error for tracking
					_isRunning = false;
				} finally {
				}
			}
		} catch (Exception ex) {
			// log error for tracking
			System.out.println("track cleanup thread error; " + ex.getMessage());
		} finally {
			this._isShutdown = true;
			this._isRunning = false;

			// log message
			System.out.println("track cleanup thread shutdown.");
		}
	}
}
