package jkademlia.controller.handlers.request;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jkademlia.controller.handlers.Controller;
import jkademlia.controller.handlers.RequestHandler;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.facades.storage.DataManagerFacade;
import jkademlia.protocol.RPC;
import jkademlia.protocol.request.FindValueRPC;
import jkademlia.protocol.response.FindNodeResponse;
import jkademlia.protocol.response.FindValueResponse;
import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.KnowContacts;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.RPCBuffer;

import org.apache.log4j.Logger;

public class FindValueHandler extends RequestHandler<FindValueResponse> {
	private static Logger logger = Logger.getLogger(FindValueHandler.class);

	private Status actualStatus;
	private BigInteger rpcID;

	private BigInteger valueKey;
	private String valueKeyString;

	private BigInteger result;

	private BigInteger distanceFromClosest;

	private DataManagerFacade storage;
	private KnowContacts contacts;

	private RPCBuffer outputBuffer;

	private Set<BigInteger> queriedNodes;
	private int maxQueries;
	
	private String[] searchResult;
	private int i;

	public FindValueHandler() {
		actualStatus = Status.NOT_STARTED;
		valueKey = null;
		storage = null;
		contacts = null;
		outputBuffer = RPCBuffer.getSentBuffer();
		result = null;
		searchResult = new String[20];
		i = 0;
		distanceFromClosest = MAX_RANGE;
		queriedNodes = new HashSet<BigInteger>();
		maxQueries = Integer.parseInt(System.getProperty("jkademlia.findvalue.maxqueries"));
	}

	public BigInteger getValueKey() {
		return valueKey;
	}

	public void setValueKey(BigInteger valueKey) {
		this.valueKey = valueKey;
		this.valueKeyString = valueKey.toString(16);
	}

	public DataManagerFacade getStorage() {
		return storage;
	}

	public void setStorage(DataManagerFacade storage) {
		this.storage = storage;
	}

	public KnowContacts getContacts() {
		return contacts;
	}

	public void setContacts(KnowContacts contacts) {
		this.contacts = contacts;
	}

	public BigInteger getRpcID() {
		return rpcID;
	}

	public void setRpcID(BigInteger rpcID) {
		this.rpcID = rpcID;
	}

	public void run() {
		actualStatus = Status.PROCESSING;
		logger.info("Looking for value with key " + valueKeyString);
		String fromMap = (String) storage.get(valueKey);
		if (fromMap == null) {
			logger.debug("Value with key " + valueKeyString + " not found localy, looking for it on the network");
			int amount = Integer.parseInt(System.getProperty("jkademlia.contacts.findamount"));
			List<KademliaNode> closestNodes = contacts.findClosestContacts(valueKey, amount);
			if (closestNodes.size() > 0) {
				for (KademliaNode node : closestNodes)
					requestNode(rpcID, node);
			}
			actualStatus = Status.WAITING;
		} else {
			result = new BigInteger(fromMap.getBytes());
			logger.info("Value with key " + valueKeyString + " found locally");
			actualStatus = Status.ENDED;
		}
	}

	private void requestNode(BigInteger rpcID, KademliaNode node) {
		this.requestNode(rpcID, node.getNodeID(), node.getIpAddress().getHostAddress(), node.getPort());
	}

	private void requestNode(BigInteger rpcID, BigInteger nodeID, String ip, int port) {
		FindValueRPC rpc = new FindValueRPC();
		try {
			logger.debug("Querying node " + nodeID.toString(16) + " on " + ip + ":" + port + " for the value with key " + valueKeyString);
			rpc.setRPCID(rpcID);
			rpc.setDestinationNodeID(nodeID);
			rpc.setSenderNodeID(Controller.getMyID());
			rpc.setKey(valueKey);
			RPCInfo<FindValueRPC> rpcInfo = new RPCInfo<FindValueRPC>(rpc, ip, port);
			outputBuffer.add(rpcInfo);
			queriedNodes.add(nodeID);
		} catch (KademliaProtocolException e) {
			logger.warn(e);
		}
	}

	public void addResult(RPCInfo rpcInfo) {
		RPC response = rpcInfo.getRPC();
		if (response instanceof FindNodeResponse) {
			try {
				FindNodeResponse findNodeResponse = (FindNodeResponse) response;
				BigInteger foundNode = findNodeResponse.getFoundNodeID();
				String foundNodeString = foundNode.toString(16);
				if (queriedNodes.contains(foundNode)) {
					logger.debug("Found node" + foundNodeString + " instead of value. Node already queried");
				} else if (queriedNodes.size() < maxQueries) {
					logger.debug("Found node " + foundNodeString + " instead of value. Querying node");
					KademliaNode node = new KademliaNode(foundNode, findNodeResponse.getIpAddressInet(), findNodeResponse.getPortInteger());
					BigInteger delta = foundNode.xor(valueKey);
					if (delta.compareTo(distanceFromClosest) < 0) {
						distanceFromClosest = delta;
						requestNode(rpcID, node);
					}
				} else {
					logger.debug("Max queries reached, aborting find value");
					actualStatus = Status.ENDED;
				}
			} catch (UnknownHostException e) {
				logger.warn(e);
			}
		} else if (response instanceof FindValueResponse) {
			FindValueResponse findValueResponse = (FindValueResponse) response;
			logger.info("Value with key " + valueKeyString + " found on node " + response.getSenderNodeID().toString(16) + " on " + rpcInfo.getIPAndPort());
			result = findValueResponse.getValue();
			searchResult[i] = new String(result.toByteArray());
			i++;
		    System.out.println(new String(result.toByteArray()));
			actualStatus = Status.ENDED;
		}
	}
	
	public List<String> ListResult(){
		List<String> list = new ArrayList<String>();
		for(String sResult : searchResult)
			list.add(sResult);
		return list;
	}
	public String[] getSearchResult(){
		return searchResult;
	}
	
	public int getResultNumber(){
		return i;
	}

	public synchronized BigInteger getResult() {
		return result;
	}

	public Status getStatus() {
		synchronized (actualStatus) {
			return actualStatus;
		}
	}

	@Override
	public void clear() {
		actualStatus = Status.NOT_STARTED;
		valueKey = null;
		storage = null;
		contacts = null;
		outputBuffer = null;
		result = null;
		searchResult = new String[20];
		i = 0;
		distanceFromClosest = MAX_RANGE;
		queriedNodes.clear();
	}
}
