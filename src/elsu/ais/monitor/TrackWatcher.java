package elsu.ais.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import elsu.ais.base.AISMessageBase;
import elsu.ais.resources.ITrackListener;
import elsu.common.FileUtils;
import elsu.support.ConfigLoader;

public class TrackWatcher {

	public TrackWatcher(ConfigLoader config) {
		initialize(config);
	}

	private void initialize(ConfigLoader config) {
		try {
			latencyCleanupTime = Integer
					.parseInt(config.getProperty("application.services.key.latency.cleanup.time").toString());
		} catch (Exception ex) {
			latencyCleanupTime = 60000;
		}

		try {
			latencyCleanupSpan = Integer
					.parseInt(config.getProperty("application.services.key.latency.cleanup.span").toString());
		} catch (Exception ex) {
			latencyCleanupSpan = 5;
		}
		
		try {
			latencyPurgeDays = Integer
					.parseInt(config.getProperty("application.services.key.latency.purge.days").toString());
		} catch (Exception ex) {
			latencyPurgeDays = 5;
		}

		// load existing prior history if available
		new File(getStatusPath()).mkdirs();
		restoreTrackHistoryFromFile();

		// start the cleaner thread
		TrackQueueCleanup cleaner = new TrackQueueCleanup(this);
		cleaner.start();
	}
	
	private void restoreTrackHistoryFromFile() {
		try {
			objectModule.addDeserializer(TrackStatus.class, new TrackStatusDeserializer());
			objectMapper.registerModule(objectModule);
			
			ArrayList<String> input = null;
			input = FileUtils.readFileToList(getStatusPath()+"/trackStatus.log");
			
			TrackStatus status = null;
			for (int i = 0; i < input.size(); i++) {
				status = objectMapper.readValue(input.get(i), TrackStatus.class);
				trackStatus.put(status.getMmsi(), status);
			}
		} catch (Exception exi) {
			System.out.println(getClass().toString() + ", restoreTrackHistoryFromFile(), " + exi.getMessage());
		}

		System.out.println("TrackStatus/ total: " + trackStatus.size() + "/ loaded: " + getStatusPath()+"/trackStatus.log");
	}
	
	public void saveTrackHistoryToFile() {
		try {
			TrackStatus status = null;
			List<Object> output = new ArrayList<Object>();
			for (Integer mmsi : trackStatus.keySet()) {
				Thread.yield();
				
				try {
					status = trackStatus.get(mmsi);
					output.add(status.toString());
				} catch (Exception exi) {
				}
			}
			
			FileUtils.writeFile(getStatusPath()+"/trackStatus.log", output, true);
			System.out.println("TrackStatus/ total: " + trackStatus.size() + "/ saved: " + getStatusPath()+"/trackStatus.log");
		} catch (Exception exi) {
			System.out.println(getClass().toString() + ", saveTrackHistoryToFile(), " + exi.getMessage());
		}
	}

	public void registerListener(ITrackListener listener) {
		this.addListener(listener);
	}

	public void processTrack(AISMessageBase message) {
		TrackStatus status = null;

		try {
			status = TrackStatus.fromMessage(this, message);

			// check and notify on status
			if (status != null) {
				try {
					if (status.isUpdated()) {
						sendTrackUpdate(status);
					} else {
						sendTrackAdd(status);
					}
				} catch (Exception ex) {
					try {
						sendTrackError(ex, message);
					} catch (Exception exi) {
						System.out.println(getClass().toString() + ", processTrack(), " + "notification, " + exi.getMessage() + ", " + message);
					}
				}
			}
		} catch (Exception ex) {
			try {
				sendTrackError(ex, message);
			} catch (Exception exi) {
				System.out.println(getClass().toString() + ", processTrack(), " + "parsing, " + exi.getMessage() + ", " + message);
			}
		}
	}

	public TrackStatus isActive(int mmsi) {
		return getTrackStatus(mmsi);
	}

	public void sendTrackError(Exception ex, AISMessageBase message) throws Exception {
		try {
			for (ITrackListener listener : listeners) {
				listener.onTrackError(ex, message.toString());
			}
		} catch (Exception exi) {
			System.out.println(getClass().toString() + ", sendTrackError(), " + exi.getMessage() + ", " + message);
		}
	}

	public void sendTrackAdd(TrackStatus track) throws Exception {
		try {
			for (ITrackListener listener : listeners) {
				listener.onTrackAdd(track.toJSONArray());
			}
		} catch (Exception exi) {
			System.out.println(getClass().toString() + ", sendTrackAdd(), " + exi.getMessage() + ", " + track);
		}
	}

	public void sendTrackUpdate(TrackStatus track) throws Exception {
		try {
			for (ITrackListener listener : listeners) {
				listener.onTrackUpdate(track.toJSONArray());
			}
		} catch (Exception exi) {
			System.out.println(getClass().toString() + ", sendTrackUpdate(), " + exi.getMessage() + ", " + track);
		}
	}

	public int getLatencyCleanupTime() {
		return latencyCleanupTime;
	}

	public int getLatencyCleanupSpan() {
		return latencyCleanupSpan;
	}

	public int getLatencyPurgeDays() {
		return latencyPurgeDays;
	}
	
	public String getStatusPath() {
		return statusPath;
	}
	
	public List<ITrackListener> getListeners() {
		return listeners;
	}

	public void addListener(ITrackListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ITrackListener listener) {
		listeners.remove(listener);
	}

	public void clearListeners() {
		listeners.clear();
	}
	
	public HashMap<Integer, TrackStatus> getTrackStatus() {
		return trackStatus;
	}

	public TrackStatus getTrackStatus(int key) {
		TrackStatus status = null;

		synchronized (lockSearch) {
			status = trackStatus.get(key);
		}

		return status;
	}

	public void updateTrackStatus(TrackStatus status) {
		synchronized (lockSearch) {
			trackStatus.put(status.getMmsi(), status);
		}
	}
	
	public ArrayList<String> getTrackPicture() {
		ArrayList<String> result = new ArrayList<String>();
		
		for(int mmsi : trackStatus.keySet()) {
			try {
				result.add((trackStatus.get(mmsi)).toJSONArray());
			} catch (Exception exi) {}
		}
		
		return result;
	}

	private int latencyCleanupSpan = 5;
	private int latencyCleanupTime = 60000;
	private int latencyPurgeDays = 5;
	private String statusPath = System.getProperty("user.dir") + "/config/status";

	private Object lockSearch = new Object();
	public static SimpleModule objectModule = new SimpleModule();
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	private List<ITrackListener> listeners = new ArrayList<>();

	private HashMap<Integer, TrackStatus> trackStatus = new HashMap<Integer, TrackStatus>();
}
