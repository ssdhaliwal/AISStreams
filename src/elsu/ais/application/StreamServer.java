package elsu.ais.application;

import java.util.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import elsu.support.ConfigLoader;
import elsu.base.IAISEventListener;
import elsu.parser.connector.ConnectorBase;
import elsu.parser.connector.StreamFileConnector;
import elsu.parser.connector.StreamSocketConnector;
import elsu.sentence.Sentence;
import elsu.sentence.SentenceBase;
import elsu.ais.monitor.TrackWatcher;
import elsu.ais.resources.ITrackListener;

public class StreamServer extends WebSocketServer implements IAISEventListener, ITrackListener {
	public StreamServer(InetSocketAddress address, ConfigLoader config, 
			ArrayList<ConnectorBase> connectors, TrackWatcher watcher) {
		super(address);
		
		initialize(config, connectors, watcher);
	}
	
	private void initialize(ConfigLoader config, ArrayList<ConnectorBase> connectors, TrackWatcher watcher) {
		setConnectionLostTimeout(0);

		this.connectors = connectors;
		this.watcher = watcher;

		// pull debug config info
		String webSocketDebug = config.getProperty("application.services.key.websocket.debug").toString();
		if (webSocketDebug.equals("true")) {
			StreamServer.isDebug = true;
		}

		try {
			String debugLevel = config.getProperty("application.services.key.processing.debug").toString();
			if (debugLevel.equals("debug")) {
				SentenceBase.logLevel = 6;
			} else if (debugLevel.equals("all")) {
				SentenceBase.logLevel = 6;
			} else if (debugLevel.equals("debug")) {
				SentenceBase.logLevel = 5;
			} else if (debugLevel.equals("info")) {
				SentenceBase.logLevel = 4;
			} else if (debugLevel.equals("warn")) {
				SentenceBase.logLevel = 3;
			} else if (debugLevel.equals("error")) {
				SentenceBase.logLevel = 2;
			} else if (debugLevel.equals("fatal")) {
				SentenceBase.logLevel = 1;
			} else if (debugLevel.equals("off")) {
				SentenceBase.logLevel = 0;
			}
		} catch (Exception exi) {
			System.out.println(getClass().getName() + ", initialize(), null, config item application.services.key.processing.debug not defined, debugLeve set to all");
		}

		// connect all monitors to the tracker
		this.watcher.registerListener(this);
		for (ConnectorBase monitor : this.connectors) {
			monitor.addListener(this);
		}
	}

	// websocket listeners
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println(
				"closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		if (message.equals("ping")) {
			System.out.println("alive message received from " + conn.getRemoteSocketAddress());
		} else {
			broadcast(message);
		}
	}

	@Override
	public void onMessage(WebSocket conn, ByteBuffer message) {
		System.out.println("received ByteBuffer from " + conn.getRemoteSocketAddress());
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send("{\"message\": \"Welcome to the server!\"}");
		broadcast("{\"message\": \"new connection: " + handshake.getResourceDescriptor() + "\"}");
		System.out.println("new connection to " + conn.getRemoteSocketAddress());
		
		/*
		ArrayList<String> statusList = watcher.getTrackPicture();
		for(String status : statusList) {
			conn.send("{\"message\": " + status + ", \"state\": \"add\"}");
		}
		*/
	}

	@Override
	public void onStart() {
		System.out.println("server started successfully");
	}

	// ais message stream connector listeners
	@Override
	public void onAISError(Exception ex, Object o, String message) {
		System.out.println("{\"message\": " + message + ", \"state\": \"error\"}");
	}

	@Override
	public void onAISComplete(Object o) {
		// broadcast("{\"message\": " + o + ", \"state\": \"new\"}");
		watcher.processTrack(((Sentence) o).getAISMessage());
	}

	@Override
	public void onAISUpdate(Object o) {
		// System.out.println(o.toString());
		// broadcast("{\"message\": " + o + ", \"state\": \"update\"}");
	}

	// track watcher listeners
	@Override
	public synchronized void onTrackError(Exception ex, String message) {
		System.out.println("track error; " + ex.getMessage() + "; "+ message);
		broadcast("{\"message\": " + message + ", \"state\": \"error\"}");
	}

	@Override
	public synchronized void onTrackRemove(String status) {
		if (SentenceBase.logLevel >= 4) {
			System.out.println("track remove; " + status);
		}
		
		broadcast("{\"message\": " + status + ", \"state\": \"remove\"}");
	}

	@Override
	public synchronized void onTrackAdd(String status) {
		if (SentenceBase.logLevel >= 4) {
			System.out.println("track add; " + status);
		}
		
		broadcast("{\"message\": " + status + ", \"state\": \"add\"}");
	}

	@Override
	public synchronized void onTrackUpdate(String status) {
		if (SentenceBase.logLevel >= 4) {
			System.out.println("track update; " + status);
		}
		
		broadcast("{\"message\": " + status + ", \"state\": \"update\"}");
	}

	public static void main(String[] args) {
		// load the app config
		ConfigLoader config = null;
		ConnectorBase connector = null;
		ArrayList<ConnectorBase> connectors = new ArrayList<>();

		try {
			config = new ConfigLoader("config/app.config", null);
			/*
			 * Map<String, Object> properties = config.getProperties();
			 * for(String key : properties.keySet()) { System.out.println(key +
			 * " - " + properties.get(key)); }
			 */

			String connectionList = config.getProperty("application.services.activeList").toString();
			String[] connections = connectionList.split(",");
			for (String connection : connections) {
				if (config.getProperty("application.services.service." + connection + ".attributes.key.type").toString().equals("file")) {
					connector = new StreamFileConnector(config, connection);
				} else {
					connector = new StreamSocketConnector(config, connection);
				}
				
				connector.start();
				connectors.add(connector);
			}

			// start the track monitor
			TrackWatcher watcher = new TrackWatcher(config);

			// start websocket server
			String _websocketHostUri = config.getProperty("application.services.key.websocket.host").toString();
			int _websocketHostPort = Integer
					.parseInt(config.getProperty("application.services.key.websocket.port").toString());

			WebSocketServer server = new StreamServer(new InetSocketAddress(_websocketHostUri, _websocketHostPort),
					config, connectors, watcher);
			server.run();

			// StreamServer server = new StreamServer(null, config, null, connectors, null);
		} catch (Exception ex) {
			System.out.println("elsu.ais.application, main(), " + "unknown, " + ex.getMessage());
		}
	}

	public static boolean isDebug = false;
	private TrackWatcher watcher = null;
	private ArrayList<ConnectorBase> connectors = new ArrayList<>();
}
