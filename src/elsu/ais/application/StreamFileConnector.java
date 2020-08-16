package elsu.ais.application;

import java.util.*;

import elsu.common.FileUtils;
import elsu.common.GlobalStack;
import elsu.io.FileChannelTextWriter;
import elsu.io.FileRolloverPeriodicityType;
import elsu.support.ConfigLoader;
import elsu.ais.resources.IMessageListener;

public class StreamFileConnector extends ConnectorBase {

	public String filename = "";
	public int speed = 100;
	public boolean isShutdown = false;

	public StreamFileConnector(ConfigLoader config, String connName, String filename, int speed) throws Exception {
		// load the config params else override from constructor
		if (connName != null) {
			this.filename = config.getProperty("application.services.service." + connName + ".attributes.key.filename").toString();
			this.speed = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.speed").toString());
		} else {
			this.filename = filename;
			this.speed = speed;
		}
		
		System.out.println("client config loaded, " +
			"filename: " + filename + ", " +
			"speed: " + speed);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// read the file in memory
			ArrayList<String> fileData = FileUtils.readFileToList(filename);
			
			// until shutdown
			String line = "";
			int i = 0;
			while (!isShutdown) {
				// loop and send file data
				if (i < fileData.size()) {
					line = fileData.get(i);

					// process the message and fire the events
					sendMessage(line);
				} else {
					i = -1;
				}
				
				Thread.sleep(speed);
				i++;
			}
		} catch (Exception ex) {
			// log error for tracking
			try {
				sendError("file, " + ex.getMessage());
			} catch (Exception ex2) {
				System.out.println("error, " + "file" + ", " + ex2.getMessage());
			}
		} finally {
			isShutdown = true;

			// log message
			try {
				sendMessage("file closed - shutdown.");
			} catch (Exception ex2) {
				System.out.println("error, " + "file" + ", " + ex2.getMessage());
			}
		}
	}
}
