package jkademlia.controller.threads;

public interface Pausable {

	  public void pauseThread();

	  public void playThread();

	  public boolean isPaused();
}
