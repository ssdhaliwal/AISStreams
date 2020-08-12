package elsu.ais.resources;

import elsu.ais.monitor.TrackStatus;

public interface ITrackListener {
	void onTrackRemove(TrackStatus track);
	void onTrackAdd(TrackStatus track);
	void onTrackUpdate(TrackStatus track);
}
