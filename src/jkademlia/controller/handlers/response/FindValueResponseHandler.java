package jkademlia.controller.handlers.response;

import java.math.BigInteger;
import java.util.List;

import jkademlia.controller.handlers.Controller;
import jkademlia.controller.handlers.Handler;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.facades.storage.DataManagerFacade;
import jkademlia.protocol.request.FindValueRPC;
import jkademlia.protocol.response.FindNodeResponse;
import jkademlia.protocol.response.FindValueResponse;
import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.KnowContacts;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.RPCBuffer;

import org.apache.log4j.Logger;

public class FindValueResponseHandler extends Handler<FindValueRPC> {
	private static Logger logger = Logger.getLogger(FindValueResponseHandler.class);

	private Status actualStatus;
	private KnowContacts contacts;

	public FindValueResponseHandler() {
		actualStatus = Status.NOT_STARTED;
	}

	public Status getStatus() {
		return actualStatus;
	}

	public KnowContacts getContacts() {
		return contacts;
	}

	public void setContacts(KnowContacts contacts) {
		this.contacts = contacts;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			actualStatus = Status.PROCESSING;
			RPCInfo<FindValueRPC> rpcInfo = getRPCInfo();
			FindValueRPC rpc = rpcInfo.getRPC();
			String key = rpc.getKey().toString(16);
			logger.info("Processing FindValue request for key " + key + " from " + rpcInfo.getIPAndPort());
			DataManagerFacade<String> storage = DataManagerFacade.getDataManager();
			//如果在本机上面找到的话，就会直接返回这个key的value。否则的话就会在自己的表里面去查找
			String value = storage.get(rpc.getKey());
			if (value != null) {
				logger.debug("Found value " + value + " for key " + key);
				logger.info("是在这个节点"+Controller.getMyID()+"上面找到的");
				FindValueResponse response = new FindValueResponse();
				response.setSenderNodeID(Controller.getMyID());
				response.setDestinationNodeID(rpc.getSenderNodeID());
				response.setRPCID(rpc.getRPCID());
				response.setValue(new BigInteger(value.getBytes()));
				// TODO: MultiPart values
				response.setPiece((byte) 1);
				response.setPieceTotal((byte) 1);

				RPCInfo<FindValueResponse> responseInfo = new RPCInfo<FindValueResponse>(response, rpcInfo.getIP(), rpcInfo.getPort());
				logger.info("Sending FindValueResponse to " + responseInfo.getIPAndPort());
				RPCBuffer.getSentBuffer().add(responseInfo);
			} else {
				int amount = Integer.parseInt(System.getProperty("jkademlia.contacts.findamount"));
				logger.debug("Found no value for key " + key + ", searching for " + amount + " closest nodes");
				List<KademliaNode> nodes = contacts.findClosestContacts(rpc.getKey(), amount);
				logger.debug("Found " + nodes.size() + " closest nodes to " + key);
				BigInteger myID = Controller.getMyID();
				RPCBuffer buffer = RPCBuffer.getSentBuffer();
				logger.info("Sending " + nodes.size() + " nodes to " + rpcInfo.getIPAndPort());
				for (KademliaNode node : nodes) {
					FindNodeResponse response = new FindNodeResponse();
					response.setSenderNodeID(myID);
					response.setDestinationNodeID(rpc.getSenderNodeID());
					response.setRPCID(rpc.getRPCID());
					response.setFoundNodeID(node.getNodeID());
					response.setIpAddress(node.getIpAddress());
					response.setPort(node.getPort());
					RPCInfo<FindNodeResponse> responseInfo = new RPCInfo<FindNodeResponse>(response, rpcInfo.getIP(), rpcInfo.getPort());
					logger.debug("Sending findNode response (node: " + node+ ")  to " + rpcInfo.getIPAndPort());
					buffer.add(responseInfo);
				}
			}
			actualStatus = Status.ENDED;
		} catch (KademliaProtocolException e) {
			logger.warn(e);
		}
	}

	public void clear() {
		this.actualStatus = Status.NOT_STARTED;
		this.setRPCInfo(null);
	}
}
