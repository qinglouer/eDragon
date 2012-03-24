package jkademlia.controller.io;

import java.util.Observable;
import java.util.Observer;

import jkademlia.controller.threads.CyclicThread;

public abstract class UDPHandler extends CyclicThread implements Observer {
	public UDPHandler(String name) {
		super(name);
	}

	public void update(Observable o, Object arg) {
		JKademliaDatagramSocket socket = (JKademliaDatagramSocket) o;
		JKademliaDatagramSocket.Action action = (JKademliaDatagramSocket.Action) arg;
		if (socket == this.getSocket()) {
			if (action == JKademliaDatagramSocket.Action.CLOSE_SOCKET)
				this.interrupt();
		}
	}

	public void stopThread() {
		this.interrupt();
		getSocket().notifyObservers(JKademliaDatagramSocket.Action.CLOSE_SOCKET);
		super.stopThread();
	}

	protected abstract JKademliaDatagramSocket getSocket();
}