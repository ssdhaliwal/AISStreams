package elsu.ais.resources;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StreamQueueManager {
	private LinkedList<String> activeList = new LinkedList<String>();
	private LinkedList<String> getList = null;
	
	private LinkedList<LinkedList<String>> queueList = new LinkedList<LinkedList<String>>();
	
	public StreamQueueManager() {
		getList = activeList;
		queueList.add(activeList);
	}
	
	public List<String> getActiveList() {
		return this.activeList;
	}
	
	public LinkedList<LinkedList<String>> getQueueList() {
		return this.queueList;
	}
	
	public void add(String item) {
		activeList.add(item);
		
		if (activeList.size() > 50000) {
			activeList = new LinkedList<String>();
			System.out.println("new queue added, " + queueList.size());
			queueList.add(activeList);
			
			Thread.yield();
		}
	}
	
	public String get() {
		String result = "";
		
		if (getList == null) {
			try {
				System.out.println("queue assigned, " + queueList.size());
				synchronized (queueList) {
					getList = queueList.remove(0);
				}
			} catch (Exception exi) {
			}
		}
		
		try {
			synchronized(getList) {
				result = getList.remove(0);
			}

			if ((getList.size() == 0) && (queueList.size() > 1)) {
				System.out.println("queue processed");
				getList = null;
			}
		} catch (Exception exi) {
		}
		
		return result;
	}
}
