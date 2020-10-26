package elsu.ais.application;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import elsu.base.IAISEventListener;
import elsu.common.CollectionUtils;
import elsu.sentence.SentenceFactory;

public abstract class ConnectorBase extends Thread {

	public ConnectorBase() {
		initialize();
	}
	
	private void initialize() {
		// create thread pool for # of parsers
		ExecutorService workerPool = Executors.newFixedThreadPool(10);

		// create workers
		for(int i = 0; i < 10; i++) {
			getAISWorkers()[i] = new AISParserWorker("aisworker_" + i, messageQueue);
			workerPool.execute(getAISWorkers()[i]);
		}
	
		workerPool.shutdown();
	}
	
	public ExecutorService getWorkerPool() {
		return workerPool;
	}
	
	public AISParserWorker[] getAISWorkers() {
		return aisWorkers;
	}
	
	public ConcurrentLinkedQueue<ArrayList<String>> getMessageQueue() {
		return messageQueue;
	}
	
	public void addListener(IAISEventListener listener) {
		for(int i = 0; i < 10; i++) {
			(getAISWorkers()[i]).getSentenceFactory().addEventListener(listener);
		}
	}

	public void removeListener(IAISEventListener listener) {
		for(int i = 0; i < 10; i++) {
			(getAISWorkers()[i]).getSentenceFactory().removeEventListener(listener);
		}
	}

	public void sendError(String error) throws Exception {
		sentenceFactory.notifyError(null, null, error);
	}

	public void sendMessage(ArrayList<String> messages) throws Exception {
		messageQueue.add(messages);
	}

	public void sendMessage(String message) throws Exception {
		try {
			sentenceFactory.parseSentence(message);
		} catch (Exception ex) {
			sendError("error processing message, " + message + ", " + ex.getMessage());
		}
	}

	private ExecutorService workerPool = null;
	private AISParserWorker[] aisWorkers = null;
	private ConcurrentLinkedQueue<ArrayList<String>> messageQueue = new ConcurrentLinkedQueue<ArrayList<String>>();
	private SentenceFactory sentenceFactory = new SentenceFactory();
}
