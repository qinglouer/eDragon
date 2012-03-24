package jkademlia.controller.threads;

public interface Stoppable {
	public void stopThread();

    public boolean isStopped();
}
