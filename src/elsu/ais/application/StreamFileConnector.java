package elsu.ais.application;

import java.util.*;
import java.util.regex.Matcher;

import elsu.common.CollectionUtils;
import elsu.common.FileUtils;
import elsu.common.GlobalStack;
import elsu.sentence.SentenceBase;
import elsu.support.ConfigLoader;

public class StreamFileConnector extends ConnectorBase {

	public String filename = "";
	public int speed = 100;
	public boolean isShutdown = false;
	private long recordCounter = 0L;
	private long lifetimeCounter = 0L;
	public boolean positionReportsOnly = true;

	public StreamFileConnector(ConfigLoader config, String connName, String filename, int speed) throws Exception {
		super();

		// load the config params else override from constructor
		if (connName != null) {
			this.filename = config.getProperty("application.services.service." + connName + ".attributes.key.filename")
					.toString();
			this.speed = Integer.parseInt(config
					.getProperty("application.services.service." + connName + ".attributes.key.speed").toString());

			if (config
					.getProperty(
							"application.services.service." + connName + ".attributes.key.site.position.reports.only")
					.toString().equals("true")) {
				positionReportsOnly = true;
			}
			;
		} else {
			this.filename = filename;
			this.speed = speed;
		}

		System.out.println(getClass().toString() + ", StreamFileConnector(), " + "client config loaded, " + "filename: "
				+ filename + ", " + "speed: " + speed);
	}

	public void sendMessage(ArrayList<String> messages) throws Exception {
		recordCounter++;
		super.sendMessage(messages);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// read the file in memory
			ArrayList<String> fileData = FileUtils.readFileToList(filename);

			// until shutdown
			String line = null, sentence = "", message[] = null;
			ArrayList<String> messages = new ArrayList<String>();
			ArrayList<String> lines = new ArrayList<String>();
			Matcher hMatch = null;
			int fileSize = fileData.size(), i = 0, systemGCCounter = 0, lineSize = 0;
			while (!Thread.currentThread().isInterrupted() && !isShutdown) {
				// loop and send file data
				if (i < fileSize) {
					if (lineSize == 0) {
						line = fileData.get(i);
					} else {
						line = lines.remove(0);
						lineSize--;
					}

					// if line is an array; separate it and parse it
					if (line.startsWith("[") && line.endsWith("]")) {
						line = line.substring(1, line.length() - 1);

						lines.clear();
						for (String l : line.split(" ")) {
							if (!l.isEmpty()) {
								if (l.endsWith(",")) {
									l = l.substring(0, l.length());
								}
								lines.add(l);
							}
						}
						
						lineSize = lines.size();
					} else if ((line != null) && (!line.isEmpty())) {
						// increment record trackers
						lifetimeCounter++;

						// process the message and fire the events
						try {
							if (positionReportsOnly) {
								if (line.matches("(?s).*!..VD[OM].*")) {
									hMatch = SentenceBase.messageVDOPattern.matcher(line);
									while (hMatch.find()) {
										sentence = hMatch.group(0);

										// if complete message
										message = sentence.split(",");
										if (message[1].equals(message[2])) {
											messages.add(sentence);

											if (messages.size() == Integer.valueOf(message[1])) {
												sendMessage(messages);
												messages = new ArrayList<String>();
											} else {
												sendError("partial fragment, pending queue cleared, ["
														+ CollectionUtils.ArrayListToString(messages) + "]");
												messages = new ArrayList<String>();
											}
										} else if (Integer.valueOf(message[2]) == 1) {
											if (messages.size() > 0) {
												sendError("partial fragment, pending queue cleared, ["
														+ CollectionUtils.ArrayListToString(messages) + "]");
												messages = new ArrayList<String>();
											}
											messages.add(sentence);
										}
									}
								}
							} else {
								sendMessage(line);
							}

							systemGCCounter++;
							if (systemGCCounter >= 50000) {
								systemGCCounter = 0;
								System.gc();

								System.out.println(">> queue count / " + getMessageQueue().size() + " of "
										+ recordCounter + " (" + lifetimeCounter + ") <<");
							}

							Thread.yield();
						} catch (Exception exi) {
							sendError("client collector error, sending message, (" + line + "), " + exi.getMessage());
						}
					}
				} else {
					i = -1;
				}

				Thread.sleep(speed);
				if (lineSize == 0) {
					i++;
				}
			}
		} catch (Exception ex) {
			// log error for tracking
			try {
				sendError("file, " + ex.getMessage());
			} catch (Exception exi) {
				System.out.println(getClass().toString() + ", run(), " + "file connector, " + exi.getMessage());
			}
		} finally {
			isShutdown = true;

			// log message
			try {
				sendMessage("file closed - shutdown.");
			} catch (Exception ex2) {
				System.out.println(getClass().toString() + ", run(), " + "file connector-2, " + ex2.getMessage());
			}
		}
	}
}
