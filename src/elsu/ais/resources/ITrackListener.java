package elsu.ais.resources;

public interface ITrackListener {
	void onTrackRemove(String track);
	void onTrackAdd(String track);
	void onTrackUpdate(String track);
}
