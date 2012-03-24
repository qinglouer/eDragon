package jkademlia.controller.handlers.request;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jkademlia.controller.handlers.Controller;
import jkademlia.controller.handlers.RequestHandler;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.request.FindNodeRPC;
import jkademlia.protocol.response.FindNodeResponse;
import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.KnowContacts;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.RPCBuffer;
import jkademlia.structures.lists.ClosestNodes;

import org.apache.log4j.Logger;

public class FindNodeHandler extends RequestHandler<FindNodeResponse> {
	private static Logger logger = Logger.getLogger(FindNodeHandler.class);

	private BigInteger searchedNode;
	private String searchedNodeString;

	private BigInteger rpcID;

	private KademliaNode closestNode;
	private BigInteger distanceFromClosest;

	private Status actualStatus;
	private RPCBuffer outputBuffer;

	private KnowContacts contacts;

	private ClosestNodes results;

	private Set<BigInteger> queriedNodes;
	private int maxQueries;

	public FindNodeHandler() {
		actualStatus = Status.NOT_STARTED;
		distanceFromClosest = MAX_RANGE;
		searchedNode = null;
		searchedNodeString = null;
		rpcID = null;
		outputBuffer = RPCBuffer.getSentBuffer();
		queriedNodes = new HashSet<BigInteger>();
		maxQueries = Integer.parseInt(System.getProperty("jkademlia.findnode.maxqueries"));
	}

	public BigInteger getSearchedNode() {
		return searchedNode;
	}

	public void setSearchedNode(BigInteger searchedNode) {
		this.searchedNode = searchedNode;
		this.searchedNodeString = searchedNode != null ? searchedNode.toString(16) : null;
	}

	public KnowContacts getContacts() {
		return contacts;
	}

	public void setContacts(KnowContacts contacts) {
		this.contacts = contacts;
	}

	public Status getStatus() {
		synchronized (actualStatus) {
			return actualStatus;
		}
	}

	public BigInteger getRpcID() {
		return rpcID;
	}

	public void setRpcID(BigInteger rpcID) {
		this.rpcID = rpcID;
	}

	public void run() {
		actualStatus = Status.PROCESSING;
		//返回的结果是20个节点，
		results = new ClosestNodes(Integer.parseInt(System.getProperty("jkademlia.findnode.maxnodes")), searchedNode);
		logger.info("Looking for closest nodes to " + searchedNodeString);
		closestNode = contacts.findContact(searchedNode);
		if (closestNode != null)
			results.add(closestNode);
		int amount = Integer.parseInt(System.getProperty("jkademlia.contacts.findamount"));
		// 现在自己的k桶表里面查找节点，同时进行的是3个
		List<KademliaNode> closestNodes = contacts.findClosestContacts(searchedNode, amount);
		// 找到之后要对这些节点进行消息确认，发送请求
		if (closestNodes.size() > 0) {
			for (KademliaNode node : closestNodes)
				requestNode(rpcID, node);
		}
		actualStatus = Status.WAITING;
	}

	public void addResult(RPCInfo<FindNodeResponse> rpcInfo) {
		FindNodeResponse response = rpcInfo.getRPC();
		BigInteger found = response.getFoundNodeID();
		String foundNodeString = found.toString(16);
		try {
			if (queriedNodes.contains(found)) {
				logger.debug("Node " + foundNodeString + " already queried");
			} else if (queriedNodes.size() < maxQueries) {
				logger.debug("Found node " + foundNodeString);
				closestNode = new KademliaNode(found, response.getIpAddressInet(), response.getPortInteger());
				results.add(closestNode);
				BigInteger delta = found.xor(searchedNode);
				if (delta.compareTo(distanceFromClosest) < 0) {
					distanceFromClosest = delta;
					requestNode(rpcID, closestNode);
				}
			} else {
				logger.debug("Max queries reached, terminating find node");
				actualStatus = Status.ENDED;
			}
		} catch (UnknownHostException e) {
			logger.warn(e);
		}
	}

	private void requestNode(BigInteger rpcID, KademliaNode node) {
		this.requestNode(rpcID, node.getNodeID(), node.getIpAddress().getHostAddress(), node.getPort());
	}

	private void requestNode(BigInteger rpcID, BigInteger nodeID, String ip, int port) {
		FindNodeRPC rpc = new FindNodeRPC();
		try {
			logger.debug("Querying " + ip + ":" + port + " for the node " + searchedNodeString);
			rpc.setRPCID(rpcID);
			rpc.setDestinationNodeID(nodeID);
			rpc.setSearchedNodeID(searchedNode);
			rpc.setSenderNodeID(Controller.getMyID());
			RPCInfo<FindNodeRPC> rpcInfo = new RPCInfo<FindNodeRPC>(rpc, ip, port);
			outputBuffer.add(rpcInfo);
			queriedNodes.add(nodeID);
		} catch (KademliaProtocolException e) {
			logger.warn(e);
		}
	}

	public synchronized List<KademliaNode> getResults() {
		return results.toList();
	}

	public void clear() {
		actualStatus = Status.NOT_STARTED;
		searchedNode = null;
		searchedNodeString = null;
		closestNode = null;
		rpcID = null;
		distanceFromClosest = MAX_RANGE;
		results = null;
		queriedNodes.clear();
	}
}
