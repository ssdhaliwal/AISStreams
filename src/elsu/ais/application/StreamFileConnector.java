package elsu.ais.application;

import java.util.*;

import elsu.common.GlobalStack;
import elsu.io.FileChannelTextWriter;
import elsu.io.FileRolloverPeriodicityType;
import elsu.support.ConfigLoader;
import elsu.ais.resources.IMessageListener;

public class StreamFileConnector extends Thread {

	public String _filename = "";
	public int _speed = 100;
	public boolean _isShutdown = false;
		
	private List<IMessageListener> _listeners = new ArrayList<>();

	public StreamFileConnector(ConfigLoader config, String connName, String filename, int speed) throws Exception {
		// load the config params
		_filename = config.getProperty("application.services.service." + connName + ".attributes.key.filename").toString();
		_speed = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.speed").toString());
		
		// override from constructor
		if (filename != "") {
			_filename = filename;
			_speed = speed;
		}
		
		System.out.println("client config loaded, " +
			"_filename: " + _filename + ", " +
			"_speed: " + _speed);
	}

	public void addListener(IMessageListener listener) {
		_listeners.add(listener);
		listener.onMessage("file", "client event listener added to notification list");
	}
	
	public void removeListener(IMessageListener listener) {
		listener.onMessage("file", "client event listener removed from notification list");
		_listeners.remove(listener);
	}
	
	public void clearListeners() {
		try {
			sendMessage("client event listener removed from notification list");
		} catch (Exception ex2) { }
		_listeners.clear();
	}

	public void sendError(String error) throws Exception {
		for (IMessageListener listener : _listeners) {
			listener.onError("file" + ", error, " + error);
		}
	}

	public void sendMessage(String message) throws Exception {
		for (IMessageListener listener : _listeners) {
			listener.onMessage("file", message);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// read the file in memory
			
			// until shutdown
			while (!_isShutdown) {
				// loop and send file data
			}
		} catch (Exception ex) {
			// log error for tracking
			try {
				sendError("client socket error, " + ex.getMessage());
			} catch (Exception ex2) {
				System.out.println("error, " + "file" + ", client socket error, " + ex2.getMessage());
			}
		} finally {
			this._isShutdown = true;

			// log message
			try {
				sendMessage("client socket closed - shutdown.");
			} catch (Exception ex2) {
				System.out.println("error, " + "file" + ", client socket error, " + ex2.getMessage());
			}
		}
	}
}
