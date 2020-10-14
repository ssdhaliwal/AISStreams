package elsu.ais.application;

import java.util.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import elsu.support.ConfigLoader;
import elsu.base.IAISEventListener;
import elsu.sentence.Sentence;
import elsu.ais.application.StreamNetworkConnector;
import elsu.ais.monitor.TrackWatcher;
import elsu.ais.resources.ITrackListener;

public class StreamServer extends WebSocketServer implements IAISEventListener, ITrackListener {
	public StreamServer(InetSocketAddress address, ConfigLoader config, Object tracker,
			ArrayList<ConnectorBase> connectors, TrackWatcher watcher) {
		super(address);
		
		setConnectionLostTimeout(0);

		this.config = config;
		this.connectors = connectors;
		this.watcher = watcher;

		// pull debug config info
		String webSocketDebug = config.getProperty("application.services.key.websocket.debug").toString();
		if (webSocketDebug.equals("true")) {
			this.debug = true;
		}

		// connect all monitors to the tracker
		this.watcher.registerListener(this);
		for (ConnectorBase monitor : this.connectors) {
			monitor.addListener(this);
		}
	}

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

	@Override
	public synchronized void onTrackError(Exception ex, String message) {
		System.out.println("track error; " + ex.getMessage() + "; "+ message);
		broadcast("{\"message\": " + message + ", \"state\": \"error\"}");
	}

	@Override
	public synchronized void onTrackRemove(String status) {
		if (this.debug) {
			System.out.println("track remove; " + status);
		}
		
		broadcast("{\"message\": " + status + ", \"state\": \"remove\"}");
	}

	@Override
	public synchronized void onTrackAdd(String status) {
		if (this.debug) {
			System.out.println("track add; " + status);
		}
		
		broadcast("{\"message\": " + status + ", \"state\": \"add\"}");
	}

	@Override
	public synchronized void onTrackUpdate(String status) {
		if (this.debug) {
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

			// check if args passed
			String host = "", name = "", id = "";
			int port = 0, speed = 0;
			if ((args.length > 0) && (args.length == 4)) {
				host = args[0];
				port = Integer.valueOf(args[1]);
				name = args[2];
				id = args[3];

				connector = new StreamNetworkConnector(config, null, host, port, name, id);
				connector.start();

				connectors.add(connector);
			} else if ((args.length > 0) && (args.length == 2)) {
				host = args[0];
				speed = Integer.valueOf(args[1]);

				connector = new StreamFileConnector(config, null, host, speed);
				connector.start();

				connectors.add(connector);
			} else if (args.length > 0) {
				throw new Exception("invalid arguments: \n\njava -jar ./AISStreamServer.jar host%s port%i name%s id%s\n(or)\njava -jar ./AISStreamServer.jar file_full_path%s speed_ms%i");
			}

			// if host is empty; then connect to default config
			if (host == "") {
				String connectionList = config.getProperty("application.services.activeList").toString();
				String[] connections = connectionList.split(",");
				for (String connection : connections) {
					if (config.getProperty("application.services.service." + connection + ".attributes.key.type").toString().equals("file")) {
						connector = new StreamFileConnector(config, connection, "", 0);
					} else {
						connector = new StreamNetworkConnector(config, connection, "", 0, "", "");
					}
					
					connector.start();
					connectors.add(connector);
				}
			}

			// start the track monitor
			TrackWatcher watcher = new TrackWatcher(config);

			// start websocket server
			String _websocketHostUri = config.getProperty("application.services.key.websocket.host").toString();
			int _websocketHostPort = Integer
					.parseInt(config.getProperty("application.services.key.websocket.port").toString());

			WebSocketServer server = new StreamServer(new InetSocketAddress(_websocketHostUri, _websocketHostPort),
					config, null, connectors, watcher);
			server.run();

			// StreamServer server = new StreamServer(null, config, null, connectors, null);
		} catch (Exception ex) {
			System.out.println("elsu.ais.application, main(), " + "unknown, " + ex.getMessage());
		}
	}

	private boolean debug = false;
	private TrackWatcher watcher = null;
	private ArrayList<ConnectorBase> connectors = new ArrayList<>();
	private ConfigLoader config = null;
}
