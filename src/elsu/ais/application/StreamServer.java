package elsu.ais.application;

import java.util.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.fasterxml.jackson.databind.ObjectMapper;

import elsu.support.ConfigLoader;
import elsu.base.IAISEventListener;
import elsu.sentence.SentenceFactory;
import elsu.ais.application.StreamClientConnector;
import elsu.ais.base.AISMessageBase;
import elsu.ais.monitor.TrackMonitor;
import elsu.ais.monitor.TrackStatus;
import elsu.ais.resources.IClientListener;
import elsu.ais.resources.ITrackListener;

public class StreamServer extends WebSocketServer implements IClientListener, IAISEventListener, ITrackListener {

	public StreamServer(InetSocketAddress address, ConfigLoader config, Object tracker,
			ArrayList<StreamClientConnector> connectors, TrackMonitor watcher) {
		super(address);

		this.config = config;
		this.connectors = connectors;
		this.watcher = watcher;

		// connect all monitors to the tracker
		getSentenceFactory().addEventListener(this);
		this.watcher.registerListener(this);
		for (StreamClientConnector monitor : this.connectors) {
			monitor.addListener(this);
		}
	}

	public SentenceFactory getSentenceFactory() {
		return sentenceFactory;
	}

	@Override
	public void onMessage(String siteId, String message) {
		try {
			getSentenceFactory().parseSentence(message);
		} catch (Exception ex) {
			System.out.println("message parsing error, " + ex.getMessage());
		}
	}

	@Override
	public boolean onError(String error) {
		System.out.println("client error, " + error);
		return false;
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
		broadcast(message);
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
	}

	@Override
	public void onStart() {
		System.out.println("server started successfully");
	}

	@Override
	public void onAISError(Exception ex, Object o, String message) {
		Thread.yield();
		broadcast("{\"message\": " + o + ", \"state\": \"error\"}");
	}

	@Override
	public void onAISComplete(Object o) {
		Thread.yield();
		// broadcast("{\"message\": " + o + ", \"state\": \"new\"}");
		watcher.processTrack((AISMessageBase) o);
	}

	@Override
	public void onAISUpdate(Object o) {
		Thread.yield();
		// broadcast("{\"message\": " + o + ", \"state\": \"update\"}");
	}

	@Override
	public void onTrackRemove(TrackStatus status) {
		System.out.println("track remove; " + status);
		broadcast("{\"message\": " + status + ", \"state\": \"remove\"}");
	}

	@Override
	public void onTrackAdd(TrackStatus status) {
		System.out.println("track add; " + status);
		broadcast("{\"message\": " + status + ", \"state\": \"add\"}");
	}

	@Override
	public void onTrackUpdate(TrackStatus status) {
		System.out.println("track update; " + status);
		broadcast("{\"message\": " + status + ", \"state\": \"update\"}");
	}

	public static void main(String[] args) {
		// load the app config
		ConfigLoader config = null;
		StreamClientConnector cmdConnector = null;
		ArrayList<StreamClientConnector> connectors = new ArrayList<>();

		try {
			config = new ConfigLoader("config/app.config", null);
			/*
			 * Map<String, Object> properties = config.getProperties();
			 * for(String key : properties.keySet()) { System.out.println(key +
			 * " - " + properties.get(key)); }
			 */

			// check if args passed
			String host = "", name = "", id = "";
			int port = 0;
			if ((args.length > 0) && (args.length == 4)) {
				host = args[0];
				port = Integer.valueOf(args[1]);
				name = args[2];
				id = args[3];

				cmdConnector = new StreamClientConnector(config, "", host, port, name, id);
				cmdConnector.start();

				connectors.add(cmdConnector);
			} else if (args.length > 0) {
				throw new Exception("invalid arguments: java -jar ./AISStreamServer.jar host%s port%i name%s id%s");
			}

			// if host is empty; then connect to default config
			if (host == "") {
				String connectionList = config.getProperty("application.services.activeList").toString();
				String[] connections = connectionList.split(",");
				for (String connection : connections) {
					cmdConnector = new StreamClientConnector(config, connection, "", 0, "", "");
					cmdConnector.start();

					connectors.add(cmdConnector);
				}
			}

			// start the track monitor
			TrackMonitor watcher = new TrackMonitor(config);

			// start websocket server
			String _websocketHostUri = config.getProperty("application.services.key.websocket.host").toString();
			int _websocketHostPort = Integer
					.parseInt(config.getProperty("application.services.key.websocket.port").toString());

			WebSocketServer server = new StreamServer(new InetSocketAddress(_websocketHostUri, _websocketHostPort),
					config, null, connectors, watcher);
			server.run();
		} catch (Exception ex) {
			System.out.println("error starting app, " + ex.getMessage());
		}
	}

	private ObjectMapper mapper = new ObjectMapper();
	private TrackMonitor watcher = null;
	private SentenceFactory sentenceFactory = new SentenceFactory();
	private ArrayList<StreamClientConnector> connectors = new ArrayList<>();
	private ConfigLoader config = null;
}
