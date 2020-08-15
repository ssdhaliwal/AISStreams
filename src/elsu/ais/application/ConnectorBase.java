package elsu.ais.application;

import java.util.ArrayList;
import java.util.List;

import elsu.ais.resources.IMessageListener;

public abstract class ConnectorBase extends Thread {

	public void addListener(IMessageListener listener) {
		_listeners.add(listener);
		listener.onMessage("file", "client event listener added to notification list");
	}

	public void removeListener(IMessageListener listener) {
		listener.onMessage("file", "client event listener removed from notification list");
		_listeners.remove(listener);
	}

	public void clearListeners() {
		try {
			sendMessage("client event listener removed from notification list");
		} catch (Exception ex2) {
		}
		_listeners.clear();
	}

	public void sendError(String error) throws Exception {
		for (IMessageListener listener : _listeners) {
			listener.onError("file" + ", error, " + error);
		}
	}

	public void sendMessage(String message) throws Exception {
		for (IMessageListener listener : _listeners) {
			listener.onMessage("file", message);
		}
	}

	private List<IMessageListener> _listeners = new ArrayList<>();
}
