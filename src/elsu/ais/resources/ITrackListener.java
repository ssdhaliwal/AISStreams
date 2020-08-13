package elsu.ais.resources;

public interface ITrackListener {
	void onTrackError(Exception ex, String message);
	void onTrackRemove(String track);
	void onTrackAdd(String track);
	void onTrackUpdate(String track);
}
