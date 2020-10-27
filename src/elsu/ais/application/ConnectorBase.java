package elsu.ais.application;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import elsu.base.IAISEventListener;
import elsu.sentence.SentenceFactory;

public abstract class ConnectorBase extends Thread {

	public ConnectorBase() {
	}
	
	protected void initializeThreadPool(int max_threads) {
		setMaxThreads(max_threads);
		aisWorkers = new AISParserWorker[getMaxThreads()];
		
		// create thread pool for # of parsers
		ExecutorService workerPool = Executors.newFixedThreadPool(getMaxThreads());

		// create workers
		for(int i = 0; i < getMaxThreads(); i++) {
			getAISWorkers()[i] = new AISParserWorker("aisworker_" + i, getMessageQueue());
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
	
	public LinkedBlockingQueue<ArrayList<String>> getMessageQueue() {
		return messageQueue;
	}
	
	public void addListener(IAISEventListener listener) {
		for(int i = 0; i < getMaxThreads(); i++) {
			(getAISWorkers()[i]).getSentenceFactory().addEventListener(listener);
		}
	}

	public void removeListener(IAISEventListener listener) {
		for(int i = 0; i < getMaxThreads(); i++) {
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
	
	public int getMaxThreads() {
		return max_threads;
	}
	
	public void setMaxThreads(int max_threads) {
		this.max_threads = max_threads;
	}

	private int max_threads = 1;
	private ExecutorService workerPool = null;
	private AISParserWorker[] aisWorkers = null;
	private LinkedBlockingQueue<ArrayList<String>> messageQueue = new LinkedBlockingQueue<ArrayList<String>>();
	private SentenceFactory sentenceFactory = new SentenceFactory();
}
