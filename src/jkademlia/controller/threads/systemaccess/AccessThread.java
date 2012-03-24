package jkademlia.controller.threads.systemaccess;

import jkademlia.controller.handlers.Controller;


public abstract class AccessThread extends Thread {

	protected Controller controller;
    protected ThreadGroup group;
    
    protected AccessThread(ThreadGroup group, Controller controller)
    {
        super(group, "AccessThread");
        this.controller = controller;
        this.group = group;
    }
    
    public abstract void run();
}
