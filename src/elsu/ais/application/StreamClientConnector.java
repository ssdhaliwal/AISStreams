package elsu.ais.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import elsu.common.GlobalStack;
import elsu.io.FileChannelTextWriter;
import elsu.io.FileRolloverPeriodicityType;
import elsu.support.ConfigLoader;
import elsu.ais.resources.IClientListener;

public class StreamClientConnector extends Thread {

	public String _hostUri = "";
	public int _hostPort = 0;
	public int _noDataTimeout = 30000;
	public int _retryWaitTime = 5000;
	public String _localStoreMask = "%s_%s_%s.txt";
	public String _localStorePath = "";
	public int _rolloverFrequency = 5;
	public FileRolloverPeriodicityType _rolloverPeriodicity = FileRolloverPeriodicityType.DAY;
	public String _siteId = "306";
	public String _siteName = "SITESVR1";
	public boolean _isShutdown = false;
	
	private boolean _isRunning = false;
	private boolean _isMonitorRunning = false;
	private long _recordCounter = 0L;
	private long _monitorRecordCounter = 0L;
	private Socket _clientSocket = null;
	private String _fileMask = "";
	private volatile FileChannelTextWriter _messageWriter = null;
		
	private List<IClientListener> _listeners = new ArrayList<>();

	public StreamClientConnector(ConfigLoader config, String connName,
			String host, int port, String name, String id) throws Exception {
		// load the config params
		_hostUri = config.getProperty("application.services.service." + connName + ".attributes.key.site.host").toString();
		_hostPort = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.site.port").toString());
		_noDataTimeout = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.monitor.noDataTimeout").toString());
		_retryWaitTime = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.monitor.idleTimeout").toString());
		_localStoreMask = config.getProperty("application.services.service." + connName + ".attributes.key.localStore.mask").toString();
		_localStorePath = config.getProperty("application.services.service." + connName + ".attributes.key.localStore.directory").toString();
		_rolloverFrequency = Integer.parseInt(config.getProperty("application.services.service." + connName + ".attributes.key.log.rollover.frequency").toString());
		_rolloverPeriodicity = FileRolloverPeriodicityType.valueOf(config.getProperty("application.services.service." + connName + ".attributes.key.log.rollover.periodicity").toString());
		_siteId = config.getProperty("application.services.service." + connName + ".attributes.key.site.id").toString();
		_siteName = config.getProperty("application.services.service." + connName + ".attributes.key.site.name").toString();
		
		// override from constructor
		if (connName == "") {
			_hostUri = host;
			_hostPort = port;
			_siteName = name;
			_siteId = id;
		}
		
		System.out.println("client config loaded, " +
			"hostUri: " + _hostUri + ", " +
			"_hostPort: " + _hostPort + ", " +
			"_noDataTimeout: " + _noDataTimeout + ", " +
			"_retryWaitTime: " + _retryWaitTime + ", " +
			"_localStoreMask: " + _localStoreMask + ", " +
			"_localStorePath: " + _localStorePath + ", " +
			"_rolloverFrequency: " + _rolloverFrequency + ", " +
			"_rolloverPeriodicity: " + _rolloverPeriodicity + ", " +
			"_siteId: " + _siteId + ", " +
			"_siteName: " + _siteName );
		
		// initialize the file writer
		_fileMask = String.format(_localStoreMask, _siteId, "%s", "%s");

		// clear all old *_CS.txt files, old messages from parent service
		// are invalid and should not be processed
		// 20150314 ssd added mkdirs to prevent errors in processing
		new File(_localStorePath + "incomming").mkdirs();
		// FileUtils.deleteFiles(getLocalStoreDirectory() + "incomming\\",
		// String.format(getFileMask(), ".*", getSiteName() + "_CS"), false);

		// open the writer channels; don't use equipment id it is included in
		// the message in the file
		_messageWriter = new FileChannelTextWriter(String.format(_fileMask, "%s", "MSG"),
				_localStorePath + "incomming/", _rolloverPeriodicity);
		this._messageWriter.setRolloverFrequency(_rolloverFrequency);
	}

	public void addListener(IClientListener listener) {
		_listeners.add(listener);
		listener.onMessage(_siteId, "client event listener added to notification list");
	}
	
	public void removeListener(IClientListener listener) {
		listener.onMessage(_siteId, "client event listener removed from notification list");
		_listeners.remove(listener);
	}
	
	public void clearListeners() {
		try {
			sendMessage("client event listener removed from notification list");
		} catch (Exception ex2) { }
		_listeners.clear();
	}

	public boolean sendError(String error) throws Exception {
		boolean retryConnection = false;

		this._messageWriter.write(error);
		for (IClientListener listener : _listeners) {
			retryConnection = listener.onError(this._siteId + ", error, " + error);
		}

		return retryConnection;
	}

	public void sendMessage(String message) throws Exception {
		_messageWriter.write(message + GlobalStack.LINESEPARATOR);
		
		for (IClientListener listener : _listeners) {
			listener.onMessage(_siteId, message);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			boolean retryConnection = false;
			Thread tMonitor = null;

			while (!_isShutdown) {
				// if socket is not running, try to start it
				if (!_isRunning) {
					try {
						// create socket to the equipment
						_clientSocket = new Socket(_hostUri, _hostPort);
						_isRunning = true;
					} catch (Exception exi) {
						retryConnection = sendError("client socket error, " + exi.getMessage());
					} finally {
						if (!_isRunning && retryConnection) {
							try {
								Thread.sleep(_retryWaitTime);
							} catch (Exception exi) {
							}
						}
					}
				}

				// if thread monitor is not running, try to start it
				if (!_isMonitorRunning && !_isShutdown) {
					_monitorRecordCounter = 0L;

					// start no data monitor thread
					tMonitor = new Thread(new Runnable() {
						@Override
						public void run() {
							_isMonitorRunning = true;
							try {
								sendMessage("client monitor started...");
							} catch (Exception ex2) {
								System.out.println("error, " + _siteId + ", client monitor start error, " + ex2.getMessage());
							}

							while (_isRunning && _isMonitorRunning && !_isShutdown) {
								try {
									// yield processing to other threads
									// for specified
									// time, any exceptions are ignored
									try {
										Thread.sleep(_noDataTimeout);
									} catch (Exception exi) {
									}

									// check the data count
									if (_monitorRecordCounter == 0L) {
										// force close the connections and
										// restart
										try {
											sendError("client monitor error, no data received, resetting connection...");
											_clientSocket.close();
										} catch (Exception exi) {
										} finally {
											_clientSocket = null;
											_isRunning = false;
										}
									}
								} catch (Exception exi) {
									_isMonitorRunning = false;
									try {
										sendError("client monitor error, " + exi.getMessage());
									} catch (Exception ex) {
										System.out.println("error, " + _siteId + ", client monitor run error, " + ex.getMessage());
									}
								}
							}
						}
					});

					// start the thread to create connection for the service.
					tMonitor.start();
				}

				// start the connection data collection
				if (_isRunning && !_isShutdown) {
					// local parameter for reader thread access, passes the
					// socket in stream
					final BufferedReader in = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream()));

					// local parameter for reader thread access, passes the
					// socket out
					// stream
					final PrintWriter out = new PrintWriter(
							new BufferedWriter(new OutputStreamWriter(_clientSocket.getOutputStream())));

					// this is to prevent socket to stay open after error
					try {
						while (_isRunning && !_isShutdown) {
							// read line from the socket stream
							String line = in.readLine();

							// increment record trackers
							_recordCounter++;
							_monitorRecordCounter++;
							
							// process the message and fire the events
							sendMessage(line);
						}
					} catch (Exception ex) {
						// log error for tracking
						_isRunning = false;
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
				System.out.println("error, " + _siteId + ", client socket error, " + ex2.getMessage());
			}
		} finally {
			this._isShutdown = true;
			this._isRunning = false;

			// close local writer
			if (_messageWriter != null) {
				try {
					_messageWriter.close();
				} catch (Exception exi) {
				}
			}

			// log message
			try {
				sendMessage("client socket closed - shutdown.");
			} catch (Exception ex2) {
				System.out.println("error, " + _siteId + ", client socket error, " + ex2.getMessage());
			}
		}
	}
}
