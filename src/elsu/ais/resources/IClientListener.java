package elsu.ais.resources;

public interface IClientListener {
	void onMessage(String siteId, String message);
    boolean onError(String error);
}
