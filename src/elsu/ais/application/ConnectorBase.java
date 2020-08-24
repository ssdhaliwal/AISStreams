package elsu.ais.application;

import elsu.base.IAISEventListener;
import elsu.sentence.SentenceFactory;

public abstract class ConnectorBase extends Thread {

	public ConnectorBase() {
	}

	public SentenceFactory getSentenceFactory() {
		return sentenceFactory;
	}
	
	public void addListener(IAISEventListener listener) {
		getSentenceFactory().addEventListener(listener);
	}

	public void removeListener(IAISEventListener listener) {
		getSentenceFactory().removeEventListener(listener);
	}

	public void sendError(String error) throws Exception {
		getSentenceFactory().notifyError(null, null, error);
	}

	public void sendMessage(String message) throws Exception {
		try {
			getSentenceFactory().parseSentence(message);
		} catch (Exception ex) {
			sendError("error processing message, " + message + ", " + ex.getMessage());
		}
	}

	private SentenceFactory sentenceFactory = new SentenceFactory();
}
