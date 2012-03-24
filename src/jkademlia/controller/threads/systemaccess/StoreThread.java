package jkademlia.controller.threads.systemaccess;

import jkademlia.controller.handlers.Controller;

public class StoreThread extends AccessThread {
	private byte[] key;
	private byte[] data;

	protected StoreThread(ThreadGroup group, Controller controller, byte[] key,byte[] data) {
		super(group, controller);
		this.key = key;
		this.data = data;
	}

	public void run() {
		this.controller.store(key, data);
	}
}
