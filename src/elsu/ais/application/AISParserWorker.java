package elsu.ais.application;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import elsu.common.CollectionUtils;
import elsu.sentence.SentenceFactory;

public class AISParserWorker implements Runnable {
	public AISParserWorker(String name, ConcurrentLinkedQueue<ArrayList<String>> messageQueue) {
		setName(name);
		this.messageQueue = messageQueue;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public SentenceFactory getSentenceFactory() {
		return sentenceFactory;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ArrayList<String> messages = null;
		while (!Thread.currentThread().isInterrupted())
		{
			try {
				messages = this.messageQueue.poll();
				if (messages != null) {
					getSentenceFactory().parseSentence(messages);
				}
				
				Thread.yield();
			} catch (Exception ex) {
				getSentenceFactory().notifyError(null, null, "error processing message, [" + CollectionUtils.ArrayListToString(messages) + "], " + ex.getMessage());
			}
		}
	}

	private String name = "";
	private ConcurrentLinkedQueue<ArrayList<String>> messageQueue = null;
	private SentenceFactory sentenceFactory = new SentenceFactory();
}
