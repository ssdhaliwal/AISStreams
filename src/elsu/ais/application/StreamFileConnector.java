package elsu.ais.application;

import java.util.*;

import elsu.common.FileUtils;
import elsu.common.GlobalStack;
import elsu.io.FileChannelTextWriter;
import elsu.io.FileRolloverPeriodicityType;
import elsu.support.ConfigLoader;
import elsu.ais.resources.IMessageListener;

public class StreamFileConnector extends ConnectorBase {

	public String _filename = "";
	public int _speed = 100;
	public boolean _isShutdown = false;

	public StreamFileConnector(ConfigLoader config, String connName, String filename, int speed) throws Exception {
		// load the config params else override from constructor
		if (connName != null) {
			_filename = config.getProperty("application.services.service." + connName + ".attributes.key.filename").toString();
			_speed = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.speed").toString());
		} else {
			_filename = filename;
			_speed = speed;
		}
		
		System.out.println("client config loaded, " +
			"_filename: " + _filename + ", " +
			"_speed: " + _speed);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// read the file in memory
			ArrayList<String> fileData = FileUtils.readFileToList(_filename);
			
			// until shutdown
			String line = "";
			int i = 0;
			while (!_isShutdown) {
				// loop and send file data
				if (i < fileData.size()) {
					line = fileData.get(i);

					// process the message and fire the events
					sendMessage(line);
				} else {
					i = -1;
				}
				
				Thread.sleep(_speed);
				i++;
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
