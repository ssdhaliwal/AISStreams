package elsu.ais.monitor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;

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
			latencyPurgeSpan = Integer
					.parseInt(config.getProperty("application.services.key.latency.purge.span").toString());
		} catch (Exception ex) {
			latencyPurgeSpan = 15;
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

		// start the cleaner thread
		TrackQueuePurge purger = new TrackQueuePurge(this);
		purger.start();
	}

	private void restoreTrackHistoryFromFile() {
		try {
			objectModule.addDeserializer(TrackStatus.class, new TrackStatusDeserializer());
			objectMapper.registerModule(objectModule);

			String line = "";
			ArrayList<String> input = null;
			input = FileUtils.readFileToList(getStatusPath() + "/trackStatus.log");

			TrackStatus status = null;
			for (int i = 0; i < input.size(); i++) {
				line = input.get(i);
				
				if (!line.isEmpty()) {
					try {
						status = objectMapper.readValue(line, TrackStatus.class);
						trackStatusMap.put(status.getMmsi(), status);
					} catch (Exception exi) {
						System.out.println(getClass().toString() + ", restoreTrackHistoryFromFile()-1, " + exi.getMessage() + ", " + line);
					}
				}
			}
		} catch (Exception exi) {
			System.out.println(getClass().toString() + ", restoreTrackHistoryFromFile(), " + exi.getMessage());
		}

		System.out.println(
				"TrackStatus/ total: " + trackStatusMap.size() + "/ loaded: " + getStatusPath() + "/trackStatus.log");
	}

	public void saveTrackHistoryToFile() {
		try {
			TrackStatus status = null;
			List<Object> output = new ArrayList<Object>();
			for (Integer mmsi : trackStatusMap.keySet()) {
				Thread.yield();

				try {
					status = trackStatusMap.get(mmsi);
					output.add(status.toString());
				} catch (Exception exi) {
				}
			}

			FileUtils.writeFile(getStatusPath() + "/trackStatus.log", output, true);
			System.out.println(
					"TrackStatus/ total: " + trackStatusMap.size() + "/ saved: " + getStatusPath() + "/trackStatus.log");
		} catch (Exception exi) {
			System.out.println(getClass().toString() + ", saveTrackHistoryToFile(), " + exi.getMessage());
		}
	}

	public void saveTrackPurgeToFile(ArrayList<TrackStatus> tracks) {
		try {
			TrackStatus status = null;
			List<Object> output = new ArrayList<Object>();
			for (int i = 0; i < tracks.size(); i++) {
				Thread.yield();

				try {
					status = tracks.get(i);
					output.add(status.toString());
				} catch (Exception exi) {
				}
			}

			FileUtils.writeFile(
					getStatusPath() + "/trackPurge_"
							+ new SimpleDateFormat("yyyyMMddHHmm").format(Calendar.getInstance().getTime()) + ".log",
					output, true);
		} catch (Exception exi) {
			System.out.println(getClass().toString() + ", saveTrackPurgeToFile(), " + exi.getMessage());
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
						System.out.println(getClass().toString() + ", processTrack(), " + "notification, "
								+ exi.getMessage() + ", " + message);
					}
				}
			}
		} catch (Exception ex) {
			try {
				sendTrackError(ex, message);
			} catch (Exception exi) {
				System.out.println(
						getClass().toString() + ", processTrack(), " + "parsing, " + exi.getMessage() + ", " + message);
			}
		}
	}

	public synchronized TrackStatus isActive(int mmsi) {
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

	public int getLatencyPurgeSpan() {
		return latencyPurgeSpan;
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
		return trackStatusMap;
	}

	public synchronized TrackStatus getTrackStatus(int key) {
		TrackStatus status = null;

		status = trackStatusMap.get(key);
		return status;
	}

	public synchronized void updateTrackStatus(TrackStatus status) {
		trackStatusMap.put(status.getMmsi(), status);
	}

	public synchronized ArrayList<String> getTrackPicture() {
		ArrayList<String> result = new ArrayList<String>();

		for (int mmsi : trackStatusMap.keySet()) {
			try {
				result.add((trackStatusMap.get(mmsi)).toJSONArray());
			} catch (Exception exi) {
			}
		}

		return result;
	}

	private int latencyCleanupSpan = 5;
	private int latencyCleanupTime = 60000;
	private int latencyPurgeSpan = 60;
	private int latencyPurgeDays = 5;
	private String statusPath = System.getProperty("user.dir") + "/config/status";

	public static SimpleModule objectModule = new SimpleModule();
	public static ObjectMapper objectMapper = new ObjectMapper();

	private List<ITrackListener> listeners = new ArrayList<>();

	private HashMap<Integer, TrackStatus> trackStatusMap = new HashMap<Integer, TrackStatus>();
}
