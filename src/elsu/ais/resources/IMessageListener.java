package elsu.ais.resources;

public interface IMessageListener {
	void onMessage(String siteId, String message);
    void onError(String error);
}
