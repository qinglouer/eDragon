package jkademlia.controller.handlers;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.KnowContacts;

import org.apache.log4j.Logger;

public class ContactHandler extends KnowContacts implements Runnable{
	public static final long DEFAULT_REFRESH = 10000;
	public static final long DEFAULT_EXPIRE = 1000 * 60 * 10;

	private static final Logger logger = Logger.getLogger(ContactHandler.class);

	private Timer timer;
	private RefreshTask refreshTask;
	private long refreshPeriod;

	public ContactHandler(BigInteger myID) {
		super(myID);
		this.init();
	}

	public ContactHandler(int maxSize, BigInteger myID) {
		super(maxSize, myID);
		this.init();
	}

	public ContactHandler(BigInteger myID, int refreshPeriod, int nodeExpire) {
		super(myID);
		this.init(refreshPeriod, nodeExpire);
	}

	public ContactHandler(int maxSize, BigInteger myID, int refreshPeriod, int nodeExpire) {
		super(maxSize, myID);
		this.init(refreshPeriod, nodeExpire);
	}

	private void init() {
		String refreshPeriod = System.getProperty("jkademlia.contacts.refreshPeriod");
		String nodeExpire = System.getProperty("jkademlia.contacts.expire"); // 节点失效
		long intRefreshPeriod = refreshPeriod != null ? Long.parseLong(refreshPeriod) : DEFAULT_REFRESH;
		long intNodeExpire = nodeExpire != null ? Long.parseLong(nodeExpire) : DEFAULT_EXPIRE;
		this.init(intRefreshPeriod, intNodeExpire);
	}

	private void init(long refreshPeriod, long nodeExpire) {
		this.refreshTask = new RefreshTask(this, logger, nodeExpire);
		this.refreshPeriod = refreshPeriod;
		this.timer = new Timer(true);
	}

	public void run() {
		this.timer.schedule(refreshTask, refreshPeriod, refreshPeriod);
	}

	public void setNodeExpire(long expire) {
		this.refreshTask.setNodeExpire(expire);
	}
}

class RefreshTask extends TimerTask {
	private KnowContacts knowContacts;
	private Logger logger;
	private long nodeExpire;

	protected RefreshTask(KnowContacts knowContacts, Logger logger, long nodeExpire) {
		this.knowContacts = knowContacts;
		this.logger = logger;
		this.nodeExpire = nodeExpire;
	}

	protected void setNodeExpire(long nodeExpire) {
		this.nodeExpire = nodeExpire;
	}

	public void run() {
		logger.info("Refreshing contact list");
		//每隔一个小时就会将表里面的节点删除，除非此节点会重新发布
		long now = System.currentTimeMillis();
		if (knowContacts.getSize() > 0) {
			for (Iterator<KademliaNode> it = knowContacts.iterator(); it.hasNext();) {
				KademliaNode next = it.next();
				long delta = now - next.getLastAccess();
				if (delta >= nodeExpire) {
					logger.debug("Removing KadNode " + next.toString() + ", last accessed " + (delta / 1000) + " seconds ago");
					it.remove();
				}
			}
		}
	}
}
