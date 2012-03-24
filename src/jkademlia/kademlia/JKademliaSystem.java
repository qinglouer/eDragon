package jkademlia.kademlia;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import jkademlia.controller.handlers.Controller;
import jkademlia.controller.io.SingletonSocket;
import jkademlia.controller.io.UDPReceiver;
import jkademlia.controller.io.UDPSender;
import jkademlia.controller.processors.RPCInputProcessor;
import jkademlia.controller.processors.RPCOutputProcessor;
import jkademlia.controller.threads.Pausable;
import jkademlia.controller.threads.Stoppable;
import jkademlia.controller.threads.systemaccess.AccessObject;
import jkademlia.facades.user.DetailedInfoFacade;
import jkademlia.facades.user.NetLocation;
import jkademlia.facades.user.UserFacade;
import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.KnowContacts;

import org.apache.log4j.Logger;

public class JKademliaSystem extends Thread implements Pausable, Stoppable,DetailedInfoFacade, UserFacade {

	private static Logger logger = Logger.getLogger(JKademliaSystem.class);

	private UDPReceiver receiver;
	private UDPSender sender;
	private RPCInputProcessor inputProcessor;
	private RPCOutputProcessor outputProcessor;
	private Controller controller;
	private AccessObject accessObject;
	private BigInteger systemID;
	private InetAddress ip;
	private int port;
	
	private boolean paused;
	private boolean running;
	
	public JKademliaSystem(){
		this.paused = false;
		this.running = false;
	}
	
	public JKademliaSystem(String name) {
		super(new ThreadGroup(name), name);
		this.paused = false;
		this.running = false;
	}
	public void login(NetLocation anotherNode) {
		this.accessObject.login(anotherNode);
	}

	public String findValue(String key) {
		return this.accessObject.findValue(key);
	}

	public byte[] findValue(byte[] data) {
		return this.accessObject.findValue(data);
	}

	public void store(String key, String data) {
		this.accessObject.store(key, data);
	}

	public void store(byte[] key, byte[] data) {
		this.accessObject.store(key, data);
	}

	public void run() {
		try{
			logger.info("Initializing " + this.getThreadGroup().getName());
			running = true;
			
			receiver = new UDPReceiver();
			sender = new UDPSender();
			receiver.start();
			sender.start();
			
			inputProcessor =  new RPCInputProcessor();
			outputProcessor = new RPCOutputProcessor();
			inputProcessor.start();
			outputProcessor.start();

			this.controller = new Controller();
			this.accessObject = new AccessObject(this.getThreadGroup(),controller);
			controller.start();

			systemID = Controller.getMyID();
			ip = SingletonSocket.getInstance().getLocalAddress();
			port = SingletonSocket.getInstance().getLocalPort();
			
			synchronized (this) {
				while (running) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
					if (isPaused()) {
						if (!controller.isPaused())
							controller.pauseThread();
						if (!inputProcessor.isPaused())
							inputProcessor.pauseThread();
						if (!outputProcessor.isPaused())
							outputProcessor.pauseThread();
						if (!receiver.isPaused())
							receiver.pauseThread();
						if (!sender.isPaused())
							sender.pauseThread();
					} else {
						if (receiver.isPaused())
							receiver.playThread();
						if (sender.isPaused())
							sender.playThread();
						if (inputProcessor.isPaused())
							inputProcessor.playThread();
						if (outputProcessor.isPaused())
							outputProcessor.playThread();
						if (controller.isPaused())
							controller.playThread();
					}
				}
			}

			logger.info("Stopping " + this.getThreadGroup().getName());

			controller.stopThread();

			inputProcessor.stopThread();
			outputProcessor.stopThread();

			receiver.stopThread();
			sender.stopThread();
			
			try {
				logger.debug("Joining with " + controller.getName());
				controller.join();
				logger.debug("Joining with " + inputProcessor.getName());
				inputProcessor.join();
				logger.debug("Joining with " + outputProcessor.getName());
				outputProcessor.join();
				logger.debug("Joining with " + sender.getName());
				sender.join();
				logger.debug("Joining with " + receiver.getName());
				receiver.join();
			} catch (InterruptedException e) {
				logger.warn(e);
			}
		}catch(SocketException e){
			logger.error(e);
		}

	}
	
	protected void finalize() throws Throwable {
		if (controller != null)
			controller.stopThread();
		if (inputProcessor != null)
			inputProcessor.stopThread();
		if (outputProcessor != null)
			outputProcessor.stopThread();
		if (receiver != null)
			receiver.stopThread();
		if (sender != null)
			sender.stopThread();
		super.finalize();
	}
	
	public boolean isPaused() {
		return paused;
	}

	public void pauseThread() {
		this.paused = true;
		synchronized (this) {
			notifyAll();
		}
	}

	public void playThread() {
		this.paused = false;
		synchronized (this) {
			notifyAll();
		}
	}

	public boolean isStopped() {
		return !running;
	}

	public void stopThread() {
		this.running = false;
		synchronized (this) {
			notifyAll();
		}
	}

	public InetAddress getIP() {
		return ip;
	}

	public int getPort() {
		return port;
	}
	
	public List<KademliaNode> listKnowContacts() {
		List<KademliaNode> list = new ArrayList<KademliaNode>();
		KnowContacts contacts = controller.getKnowContacts();
		synchronized (contacts) {
			for (KademliaNode node : contacts)
				list.add(node);
		}
		return list;
	}
	
	public Hashtable<BigInteger,String> tableKnowContacts(){
		KnowContacts contacts = controller.getKnowContacts();
		return contacts.getContactsTable();
	}
	
	
	/**
	 *function:通过调用此函数，能够得到所有含有文件的节点
	 *@return
	 */
	public List<String> listResult(){
		List<String> list = new ArrayList<String>();
		String[] result = controller.getListResult();
		for(int i=0;i<result.length;){
			if(result[i] != null){
			    list.add(result[i]);
			    i++;
			}else{
				i++;
			}
		}
		//测试用，打印出返回的节点。
		System.out.println("返回得到的节点信息，并在list中显示出来");
		return list;
	}

	public String toString() {
		return this.getName();
	}

	@Override
	public BigInteger getSystemID() {
		return systemID;
	}
}
