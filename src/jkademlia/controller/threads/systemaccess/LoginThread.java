package jkademlia.controller.threads.systemaccess;

import jkademlia.controller.handlers.Controller;
import jkademlia.facades.user.NetLocation;

public class LoginThread extends AccessThread {

	private NetLocation anotherNode;

	protected LoginThread(ThreadGroup group, Controller controller, NetLocation anotherNode) {
		super(group, controller);
		this.anotherNode = anotherNode;
	}

	public void run() {
		controller.login(anotherNode);
	}
}
