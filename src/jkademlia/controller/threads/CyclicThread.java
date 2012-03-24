package jkademlia.controller.threads;

import org.apache.log4j.Logger;

public abstract class CyclicThread extends Thread implements Pausable,HardStoppable{
	private boolean paused = false;
	private boolean dead = false;
	private long roundWait = 10;
	
	private Logger logger = Logger.getLogger(CyclicThread.class);
	
	protected abstract void cycleOperation() throws InterruptedException;
	protected abstract void finalize();
	
	protected CyclicThread(String name){
		super(name);	
	}
	
	public void run(){
		logger.debug("Start Thread");
		while(!isStoped()){
			try{
				if(isPaused()){
					synchronized(this){
						this.wait();
					}
				}else{
					this.cycleOperation();
					if(roundWait>0){
						synchronized(this){
							this.wait(roundWait);
						}
					}
				}
			}catch(InterruptedException e){
				logger.debug("Thread Interrupt");
				break;
			}
		}
		this.finalize();
	}

	private boolean isStoped() {
		return dead;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public void pauseThread() {
		logger.debug("pausing Thread");
		paused = true;	
	}

	@Override
	public void playThread() {
		logger.debug("playing Thread");
		paused = false;
		synchronized(this){
			this.notifyAll();
		}
		
	}

	@Override
	public void hardStopThread() {
		this.stopThread();
		this.interrupt();              //中断该线程
	}
		
	public void stopThread() {
		logger.debug("Stop Thread");
		dead = true;
	}

	public long getRoundWait(){
		return roundWait;
	}
	
	public void setRoundWait(long roundWait){
		if(roundWait >= 0){
			this.roundWait = roundWait;
		}
	}
}
