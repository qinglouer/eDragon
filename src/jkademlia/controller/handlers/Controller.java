package jkademlia.controller.handlers;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jkademlia.controller.handlers.Handler.Status;
import jkademlia.controller.handlers.request.FindNodeHandler;
import jkademlia.controller.handlers.request.FindValueHandler;
import jkademlia.controller.handlers.request.LoginHandler;
import jkademlia.controller.handlers.request.StoreHandler;
import jkademlia.controller.handlers.response.FindNodeResponseHandler;
import jkademlia.controller.handlers.response.FindValueResponseHandler;
import jkademlia.controller.handlers.response.PingResponseHandler;
import jkademlia.controller.handlers.response.StoreResponseHandler;
import jkademlia.controller.io.JKademliaDatagramSocket;
import jkademlia.controller.io.SingletonSocket;
import jkademlia.controller.threads.CyclicThread;
import jkademlia.facades.storage.DataManagerFacade;
import jkademlia.facades.user.NetLocation;
import jkademlia.facades.user.UserFacade;
import jkademlia.gui.EDragon;
import jkademlia.gui.KnowContactsModel;
import jkademlia.protocol.KademliaProtocol;
import jkademlia.protocol.RPC;
import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.KnowContacts;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.RPCBuffer;
import jkademlia.tools.SHADigester;
import jkademlia.tools.ThreadHashMap;
import jkademlia.tools.ToolBox;

import org.apache.log4j.Logger;

public class Controller extends CyclicThread implements UserFacade {

	private static Logger logger = Logger.getLogger(Controller.class);
	private static ThreadHashMap<BigInteger> myID;
	private ContactHandler knowContacts;
	private RPCBuffer inputBuffer;
	String[] result;

	private HashMap<BigInteger, RequestHandler> rpcIDMap;

	/**
	 *function:获取本机的IP和Port,并且散列得到ID
	 * 
	 * @return
	 */
	public static BigInteger getMyID() {
		if (myID == null) {
			myID = new ThreadHashMap<BigInteger>() {
				public BigInteger initialValue() {
					JKademliaDatagramSocket socket = SingletonSocket.getInstance();
					InetAddress ip = socket.getInetAddress() != null ? socket.getInetAddress() : socket.getLocalAddress();
					logger.info("本机的ip:" + ip.toString());
					Integer port = socket.getPort() != -1 ? socket.getPort() : socket.getLocalPort();
					logger.info("开放的端口：" + port.toString());
					String idString = ip.getHostAddress() + ":" + port;
					logger.debug("Generating ID for " + Thread.currentThread().getThreadGroup().getName() + " with address " + idString + "by SHADigester");
					BigInteger id = SHADigester.hash(idString);
					logger.debug("Generated ID " + id.toString(16));
					return id;
				}
			};
		}
		return myID.get();
	}

	public Controller() {
		super(ToolBox.getReflectionTools().generateThreadName(Controller.class));
		knowContacts = new ContactHandler(Controller.getMyID());
		inputBuffer = RPCBuffer.getReceivedBuffer();
		rpcIDMap = new HashMap<BigInteger, RequestHandler>();
		result = new String[20];
		super.setRoundWait(50);
	}

	public void run() {
		knowContacts.run();
		super.run();
	}

	protected void cycleOperation() throws InterruptedException {
		while (!inputBuffer.isEmpty()) {
			RPCInfo rpcInfo = inputBuffer.remove();
			RPC rpc = rpcInfo.getRPC();
			String ip = rpcInfo.getIP();
			Integer port = rpcInfo.getPort();
			try {
				KademliaNode senderNode = knowContacts.findContact(rpc.getSenderNodeID());
				if (senderNode == null) {
					String nodeIDString = rpc.getSenderNodeID().toString(16);
					logger.debug("Contact " + nodeIDString + " unknown to this system, adding to contact list");
					senderNode = new KademliaNode(rpc.getSenderNodeID(), ip, port);
					knowContacts.addContact(senderNode);
					} else {
					String nodeIDString = rpc.getSenderNodeID().toString(16);
					logger.debug("Contact " + nodeIDString + "  already known to this system, refreshing last access");
					senderNode.setLastAccess(System.currentTimeMillis());
				}
				logger.debug("Processing RPC of type " + rpc.getClass().getSimpleName());
				if (rpc.isRequest()) {
					switch (rpc.getType()) {
					case KademliaProtocol.PING:
						PingResponseHandler pingHandler = new PingResponseHandler();
						pingHandler.setRPCInfo(rpcInfo);
						pingHandler.run();
						break;
					case KademliaProtocol.STORE:
						StoreResponseHandler storeHandler = new StoreResponseHandler();
						storeHandler.setRPCInfo(rpcInfo);
						storeHandler.run();
						break;
					case KademliaProtocol.FIND_NODE:
						FindNodeResponseHandler findNodeHandler = new FindNodeResponseHandler();
						findNodeHandler.setRPCInfo(rpcInfo);
						findNodeHandler.setContacts(knowContacts);
						findNodeHandler.run();
						break;
					case KademliaProtocol.FIND_VALUE:
						FindValueResponseHandler findValueHandler = new FindValueResponseHandler();
						findValueHandler.setRPCInfo(rpcInfo);
						findValueHandler.setContacts(knowContacts);
						findValueHandler.run();
						break;
					}
				} else {
					RequestHandler handler;
					synchronized (rpcIDMap) {
						handler = rpcIDMap.get(rpc.getRPCID());
					}
					if (handler != null)
						handler.addResult(rpcInfo);
					else
						logger.warn("Received response of a request not requested by this node! (request id: " + rpc.getRPCID().toString(16) + ")");
				}
			} catch (UnknownHostException e) {
				logger.warn("Received packet from a invalid ip address: " + ip + ", discarting request");
			}
		}
	}

	public static BigInteger generateRPCID() {
		Long currentTime = System.currentTimeMillis(); 
		String myID = Controller.getMyID().toString(16);
		String rpcID = myID + currentTime;
		return SHADigester.hash(rpcID);
	}

	public void login(NetLocation anotherNode) {
		LoginHandler handler = new LoginHandler();
		BigInteger rpcID = generateRPCID();
		handler.setRpcID(rpcID);
		handler.setBootstrapNode(anotherNode);
		synchronized (rpcIDMap) {
			rpcIDMap.put(rpcID, handler);
		}
		LoginEnd task = new LoginEnd(handler, rpcIDMap, logger);
		Timer timer = new Timer(true);
		handler.run();
		//login的时间是10秒
		timer.schedule(task, Long.parseLong(System.getProperty("jkademlia.login.time")));
	}

	public void store(String key, String data) {
		this.store(key.getBytes(), data.getBytes());
	}

	public void store(byte[] key, byte[] data) {
		BigInteger bKey = new BigInteger(SHADigester.digest(key));
		BigInteger bData = new BigInteger(data);                   // 在这里面存储的只是ip，以二进制补码形式存在

		FindNodeHandler handler = new FindNodeHandler();
		BigInteger rpcID = generateRPCID();
		handler.setRpcID(rpcID);
		handler.setContacts(knowContacts);
		handler.setSearchedNode(bKey);
		synchronized (rpcIDMap) {
			rpcIDMap.put(rpcID, handler);
		}
		long maxWait = Long.parseLong(System.getProperty("jkademlia.findnode.maxwait"));
		long startTime = System.currentTimeMillis();
		handler.run();
		while (handler.getStatus() != Status.ENDED && System.currentTimeMillis() - startTime < maxWait) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.warn(e);
			}
		}
		synchronized (rpcIDMap) {
			rpcIDMap.remove(rpcID);
		}
		storeValueOnNodes(handler.getResults(), bKey, bData);
	}

	public String findValue(String key) {
		byte[] result = this.findValue(key.getBytes());
		return result != null ? new String(result) : null;
	}

	public byte[] findValue(byte[] data) {
		FindValueHandler handler = new FindValueHandler();
		BigInteger rpcID = generateRPCID();
		handler.setRpcID(rpcID);
		handler.setStorage(DataManagerFacade.getDataManager());
		handler.setContacts(knowContacts);
		handler.setValueKey(new BigInteger(SHADigester.digest(data)));
		synchronized (rpcIDMap) {
			rpcIDMap.put(rpcID, handler);
		}
		// 最长搜索时间是10秒钟
		long maxWait = Long.parseLong(System.getProperty("jkademlia.findvalue.maxwait"));
		long startTime = System.currentTimeMillis();
		handler.run();
		while (handler.getStatus() != Status.ENDED && System.currentTimeMillis() - startTime < maxWait) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.warn(e);
			}
		}
		synchronized (rpcIDMap) {
			rpcIDMap.remove(rpcID);
		} 
		handler.getResult();
		result = handler.getSearchResult();
		return handler.getResult() != null ? handler.getResult().toByteArray() : null;
	}
	
	public String[] getListResult(){
		return result;
	}

	private void storeValueOnNodes(Collection<KademliaNode> nodes, BigInteger key, BigInteger value) {
		StoreHandler storeHandler = new StoreHandler();
		for (KademliaNode node : nodes) {
			storeHandler.clear();
			storeHandler.setKey(key);
			storeHandler.setNode(node);
			storeHandler.setRpcID(generateRPCID());
			storeHandler.setValue(value);
			storeHandler.run();
		}
	}

	public KnowContacts getKnowContacts() {
		return knowContacts;
	}
	
	public List<KademliaNode> listKnowContacts() {
		List<KademliaNode> list = new ArrayList<KademliaNode>();
		KnowContacts contacts = knowContacts;
		synchronized (contacts) {
			for (KademliaNode node : contacts)
				list.add(node);
		}
		return list;
	}

	protected Logger getLogger() {
		return logger;
	}

	protected void finalize() {

	}
}

class LoginEnd extends TimerTask {
	private LoginHandler handler;
	private HashMap<BigInteger, RequestHandler> rpcIDMap;
	private Logger controllerLogger;

	protected LoginEnd(LoginHandler handler, HashMap<BigInteger, RequestHandler> rpcIDMap2, Logger controllerLogger) {
		this.handler = handler;
		this.rpcIDMap = rpcIDMap2;
		this.controllerLogger = controllerLogger;
	}

	public void run() {
		synchronized (rpcIDMap) {
			controllerLogger.debug("Finalizing login process");
			rpcIDMap.remove(handler.getRpcID());
		}
	}
}
