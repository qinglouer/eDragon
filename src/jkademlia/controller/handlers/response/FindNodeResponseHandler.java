package jkademlia.controller.handlers.response;

import java.math.BigInteger;
import java.util.List;

import jkademlia.controller.handlers.Controller;
import jkademlia.controller.handlers.Handler;
import jkademlia.controller.handlers.request.FindNodeHandler;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.request.FindNodeRPC;
import jkademlia.protocol.response.FindNodeResponse;
import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.KnowContacts;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.RPCBuffer;

import org.apache.log4j.Logger;

public class FindNodeResponseHandler extends Handler<FindNodeRPC> {
	private static Logger logger = Logger.getLogger(FindNodeHandler.class);

	private Status actualStatus;
	private KnowContacts contacts;

	public FindNodeResponseHandler() {
		this.actualStatus = Status.NOT_STARTED;
	}

	public KnowContacts getContacts() {
		return contacts;
	}

	public void setContacts(KnowContacts contacts) {
		this.contacts = contacts;
	}

	public void run() {
		actualStatus = Status.PROCESSING;
		try {
			RPCInfo<FindNodeRPC> rpcInfo = getRPCInfo();
			FindNodeRPC rpc = rpcInfo.getRPC();
			String searched = rpc.getSearchedNodeID().toString(16);
			logger.info("Processing FindNode request for node " + searched + " from " + rpcInfo.getIPAndPort());
			int amount = Integer.parseInt(System.getProperty("jkademlia.contacts.findamount"));
			List<KademliaNode> nodes = contacts.findClosestContacts(rpc.getSearchedNodeID(), amount);
			logger.debug("Found " + nodes.size() + " closest nodes to " + searched);
			BigInteger myID = Controller.getMyID();
			RPCBuffer buffer = RPCBuffer.getSentBuffer();
			logger.info("Sending " + nodes.size() + " nodes to " + rpcInfo.getIPAndPort());
			for (KademliaNode node : nodes) {
				FindNodeResponse response = new FindNodeResponse();
				response.setSenderNodeID(myID);
				response.setDestinationNodeID(rpc.getRPCID());
				response.setFoundNodeID(node.getNodeID());
				response.setIpAddress(node.getIpAddress());
				response.setRPCID(rpc.getRPCID());
				response.setPort(node.getPort());
				RPCInfo<FindNodeResponse> responseInfo = new RPCInfo<FindNodeResponse>(response, rpcInfo.getIP(), rpcInfo.getPort());
				logger.debug("Sending findNode response (node: " + node + ")  to " + rpcInfo.getIPAndPort());
				buffer.add(responseInfo);
			}
		} catch (KademliaProtocolException e) {
			logger.warn(e);
		}
		actualStatus = Status.ENDED;
	}

	public Status getStatus() {
		return actualStatus;
	}

	public void clear() {
		this.actualStatus = Status.NOT_STARTED;
		this.setRPCInfo(null);
	}
}
