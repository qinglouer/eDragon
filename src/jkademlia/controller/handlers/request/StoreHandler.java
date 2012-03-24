package jkademlia.controller.handlers.request;

import java.math.BigInteger;

import jkademlia.controller.handlers.Controller;
import jkademlia.controller.handlers.RequestHandler;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.request.StoreRPC;
import jkademlia.protocol.response.StoreResponse;
import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.RPCBuffer;

import org.apache.log4j.Logger;

public class StoreHandler extends RequestHandler<StoreResponse> {

	private static Logger logger = Logger.getLogger(StoreHandler.class);

	private KademliaNode node;
	private BigInteger key;
	private BigInteger rpcID;
	private BigInteger value;
	private RPCBuffer outputBuffer;
	private Status actualStatus;

	public StoreHandler() {
		this.node = null;
		this.key = null;
		this.rpcID = null;
		this.value = null;
		this.outputBuffer = RPCBuffer.getSentBuffer();
		this.actualStatus = Status.NOT_STARTED;
	}

	public KademliaNode getNode() {
		return node;
	}

	public void setNode(KademliaNode node) {
		this.node = node;
	}

	public BigInteger getKey() {
		return key;
	}

	public void setKey(BigInteger key) {
		this.key = key;
	}

	public BigInteger getRpcID() {
		return rpcID;
	}

	public void setRpcID(BigInteger rpcID) {
		this.rpcID = rpcID;
	}

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}

	@Override
	public void addResult(RPCInfo<StoreResponse> rpcInfo) {
		String rpcID = rpcInfo.getRPC().getRPCID().toString(16);
		logger.warn("Response to a store commmand not expected (response from " + rpcInfo.getIPAndPort() + " with rpcID " + rpcID + ")");
	}

	@Override
	public void clear() {
		this.node = null;
		this.key = null;
		this.rpcID = null;
		this.value = null;
		this.outputBuffer = RPCBuffer.getSentBuffer();
		this.actualStatus = Status.NOT_STARTED;

	}

	@Override
	public Status getStatus() {
		return actualStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jkademlia.controller.handlers.Handler#run()
	 * 把带有key即文件信息的节点发布到跟文件相匹配的节点上
	 */
	@Override
	public void run() {
		actualStatus = Status.PROCESSING; // 状态码
		String nodeID = this.node.getNodeID().toString(16);
		String keyString = this.key.toString(16);
		String ipAndPort = this.node.getIpAndPort();
		logger.info("Saving value " + value + " with key " + keyString + " on node " + nodeID + " (" + ipAndPort + ")");
		StoreRPC rpc = new StoreRPC();
		try {
			rpc.setDestinationNodeID(node.getNodeID());
			rpc.setSenderNodeID(Controller.getMyID());
			rpc.setRPCID(rpcID);
			rpc.setKey(this.key);
			rpc.setValue(value);
			rpc.setPiece((byte) 1);
			rpc.setPieceTotal((byte) 1);
			RPCInfo<StoreRPC> rpcInfo = new RPCInfo<StoreRPC>(rpc, node.getIpAddress().getHostAddress(), node.getPort());
			outputBuffer.add(rpcInfo);
		} catch (KademliaProtocolException e) {
			logger.warn(e);
		}
		actualStatus = Status.ENDED;
	}

}
