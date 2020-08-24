package elsu.ais.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import elsu.common.GlobalStack;
import elsu.io.FileChannelTextWriter;
import elsu.io.FileRolloverPeriodicityType;
import elsu.support.ConfigLoader;

public class StreamNetworkConnector extends ConnectorBase {

	public String hostUri = "";
	public int hostPort = 0;
	public int noDataTimeout = 30000;
	public int retryWaitTime = 5000;
	public String localStoreMask = "%s%s%s.txt";
	public String localStorePath = "";
	public int rolloverFrequency = 5;
	public FileRolloverPeriodicityType rolloverPeriodicity = FileRolloverPeriodicityType.DAY;
	public String siteId = "306";
	public String siteName = "SITESVR1";
	public boolean isShutdown = false;
	
	private boolean isRunning = false;
	private boolean isMonitorRunning = false;
	private long recordCounter = 0L;
	private long monitorRecordCounter = 0L;
	private Socket clientSocket = null;
	private String fileMask = "";
	private volatile FileChannelTextWriter messageWriter = null;
		
	public StreamNetworkConnector(ConfigLoader config, String connName,
			String host, int port, String name, String id) throws Exception {
		super();
		
		// load the config params else override from constructor
		if (connName != null) {
			hostUri = config.getProperty("application.services.service." + connName + ".attributes.key.site.host").toString();
			hostPort = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.site.port").toString());
			noDataTimeout = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.monitor.noDataTimeout").toString());
			retryWaitTime = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.monitor.idleTimeout").toString());
			localStoreMask = config.getProperty("application.services.service." + connName + ".attributes.key.localStore.mask").toString();
			localStorePath = config.getProperty("application.services.service." + connName + ".attributes.key.localStore.directory").toString();
			rolloverFrequency = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.log.rollover.frequency").toString());
			rolloverPeriodicity = FileRolloverPeriodicityType.valueOf(config.getProperty("application.services.service." + connName + ".attributes.key.log.rollover.periodicity").toString());
			siteId = config.getProperty("application.services.service." + connName + ".attributes.key.site.id").toString();
			siteName = config.getProperty("application.services.service." + connName + ".attributes.key.site.name").toString();
		} else {
			hostUri = host;
			hostPort = port;
			siteName = name;
			siteId = id;
		}
		
		System.out.println("client config loaded, " +
			"hostUri: " + hostUri + ", " +
			"hostPort: " + hostPort + ", " +
			"noDataTimeout: " + noDataTimeout + ", " +
			"retryWaitTime: " + retryWaitTime + ", " +
			"localStoreMask: " + localStoreMask + ", " +
			"localStorePath: " + localStorePath + ", " +
			"rolloverFrequency: " + rolloverFrequency + ", " +
			"rolloverPeriodicity: " + rolloverPeriodicity + ", " +
			"siteId: " + siteId + ", " +
			"siteName: " + siteName );
		
		// initialize the file writer
		fileMask = String.format(localStoreMask, siteId, "%s", "%s");

		// clear all old *CS.txt files, old messages from parent service
		// are invalid and should not be processed
		// 20150314 ssd added mkdirs to prevent errors in processing
		new File(localStorePath + "incomming").mkdirs();
		// FileUtils.deleteFiles(getLocalStoreDirectory() + "incomming\\",
		// String.format(getFileMask(), ".*", getSiteName() + "CS"), false);

		// open the writer channels; don't use equipment id it is included in
		// the message in the file
		messageWriter = new FileChannelTextWriter(String.format(fileMask, "%s", "MSG"),
				localStorePath + "incomming/", rolloverPeriodicity);
		messageWriter.setRolloverFrequency(rolloverFrequency);
	}

	public void sendError(String error) throws Exception {
		messageWriter.write(error);
		super.sendError(error);
	}

	public void sendMessage(String message) throws Exception {
		messageWriter.write(message + GlobalStack.LINESEPARATOR);
		super.sendMessage(message);
	}

	@Override
	public void run() {
		try {
			Thread tMonitor = null;

			while (!isShutdown) {
				// if socket is not running, try to start it
				if (!isRunning) {
					try {
						// create socket to the equipment
						clientSocket = new Socket(hostUri, hostPort);
						isRunning = true;
					} catch (Exception exi) {
						sendError("client socket error, " + exi.getMessage());
					} finally {
						if (!isRunning && !isShutdown) {
							try {
								Thread.sleep(retryWaitTime);
							} catch (Exception exi) {
							}
						}
					}
				}

				// if thread monitor is not running, try to start it
				if (!isMonitorRunning && !isShutdown) {
					monitorRecordCounter = 0L;

					// start no data monitor thread
					tMonitor = new Thread(new Runnable() {
						@Override
						public void run() {
							isMonitorRunning = true;
							try {
								sendMessage("client monitor started...");
							} catch (Exception ex2) {
								System.out.println("error, " + siteId + ", client monitor start error, " + ex2.getMessage());
							}

							while (isRunning && isMonitorRunning && !isShutdown) {
								try {
									// yield processing to other threads
									// for specified
									// time, any exceptions are ignored
									try {
										Thread.sleep(noDataTimeout);
									} catch (Exception exi) {
									}

									// check the data count
									if (monitorRecordCounter == 0L) {
										// force close the connections and
										// restart
										try {
											sendError("client monitor error, no data received, resetting connection...");
											clientSocket.close();
										} catch (Exception exi) {
										} finally {
											clientSocket = null;
											isRunning = false;
										}
									}
								} catch (Exception exi) {
									isMonitorRunning = false;
									try {
										sendError("client monitor error, " + exi.getMessage());
									} catch (Exception ex) {
										System.out.println("error, " + siteId + ", client monitor run error, " + ex.getMessage());
									}
								}
							}
						}
					});

					// start the thread to create connection for the service.
					tMonitor.start();
				}

				// start the connection data collection
				if (isRunning && !isShutdown) {
					// local parameter for reader thread access, passes the
					// socket in stream
					final BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

					// local parameter for reader thread access, passes the
					// socket out
					// stream
					final PrintWriter out = new PrintWriter(
							new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

					// this is to prevent socket to stay open after error
					try {
						while (isRunning && !isShutdown) {
							// read line from the socket stream
							String line = in.readLine();

							// increment record trackers
							recordCounter++;
							monitorRecordCounter++;
							
							// process the message and fire the events
							sendMessage(line);
						}
					} catch (Exception ex) {
						// log error for tracking
						isRunning = false;
						sendError("client collector error, " + ex.getMessage());
					} finally {
						// close out all open in/out streams.
						try {
							try {
								out.flush();
							} catch (Exception exi) {
							}
							out.close();
						} catch (Exception exi) {
						}
						try {
							in.close();
						} catch (Exception exi) {
						}
					}
				}
			}
		} catch (Exception ex) {
			// log error for tracking
			try {
				sendError("client socket error, " + ex.getMessage());
			} catch (Exception ex2) {
				System.out.println("error, " + siteId + ", client socket error, " + ex2.getMessage());
			}
		} finally {
			isShutdown = true;
			isRunning = false;

			// close local writer
			if (messageWriter != null) {
				try {
					messageWriter.close();
				} catch (Exception exi) {
				}
			}

			// log message
			try {
				sendMessage("client socket closed - shutdown.");
			} catch (Exception ex2) {
				System.out.println("error, " + siteId + ", client socket error, " + ex2.getMessage());
			}
		}
	}
}
