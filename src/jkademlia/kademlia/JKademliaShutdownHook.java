package jkademlia.kademlia;

import org.apache.log4j.Logger;

public class JKademliaShutdownHook extends Thread {
	private static Logger logger = Logger.getLogger(JKademlia.class);

	private Thread jkademliaThread;

	private JKademlia jkademlia;

	public JKademliaShutdownHook(Thread jkademliaThread, JKademlia jkademlia) {
		this.jkademliaThread = jkademliaThread;
		this.jkademlia = jkademlia;
	}

	public void run() {
		logger.debug("Shutdown hook called");
		if (jkademliaThread.isAlive()) {
			if (!jkademlia.isStarted()) {
				logger.warn("System not started, interrupting");
				jkademliaThread.interrupt();
			} else {
				logger.info("Stopping JKademlia");
				jkademlia.stopThread();
			}
			while (jkademliaThread.isAlive())
				try {
					jkademliaThread.join();
				} catch (InterruptedException e) {

				}
		}
		logger.debug("Finished Shutdown hook");
	}
}
