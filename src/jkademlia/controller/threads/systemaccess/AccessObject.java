package jkademlia.controller.threads.systemaccess;

import jkademlia.controller.handlers.Controller;
import jkademlia.facades.user.NetLocation;
import jkademlia.facades.user.UserFacade;

public class AccessObject implements UserFacade {

	protected Controller controller;
	protected ThreadGroup threadGroup;

	public AccessObject(ThreadGroup threadGroup, Controller controller) {
		this.controller = controller;
		this.threadGroup = threadGroup;
	}

	@Override
	public String findValue(String key) {
		byte[] result = this.findValue(key.getBytes());
		return result != null ? new String(result) : null;
	}

	@Override
	public byte[] findValue(byte[] key) {
		FindThread thread = new FindThread(threadGroup, controller, key);
		execSync(thread);
		return thread.getValue();
	}

	@Override
	public void login(NetLocation anotherNode) {
		LoginThread thread = new LoginThread(threadGroup, controller,anotherNode);
		execSync(thread);
	}

	@Override
	public void store(String key, String data) {
		this.store(key.getBytes(), data.getBytes());
	}

	@Override
	public void store(byte[] key, byte[] data) {
		StoreThread thread = new StoreThread(threadGroup, controller, key, data);
		execSync(thread);
	}

	private void execSync(Thread thread) {
		thread.start();
		while (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
		}
	}

}
