package jkademlia.controller.threads.systemaccess;

import jkademlia.controller.handlers.Controller;

public class FindThread extends AccessThread{

	private byte[] key;
	private byte[] result;
	
	protected FindThread(ThreadGroup group, Controller controller,byte[] key) {
		super(group, controller);
        this.key = key;
        this.result = null;
	}

	@Override
	public void run() {
		result = this.controller.findValue(key);
	}
	
	public byte[] getValue(){
		return this.result;
	}

}
