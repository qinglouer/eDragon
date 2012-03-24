package jkademlia.controller.handlers.request;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.HashSet;

import jkademlia.controller.handlers.Controller;
import jkademlia.controller.handlers.RequestHandler;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.facades.user.NetLocation;
import jkademlia.protocol.request.FindNodeRPC;
import jkademlia.protocol.response.FindNodeResponse;
import jkademlia.structure.kademlia.KademliaNode;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.RPCBuffer;

import org.apache.log4j.Logger;

public class LoginHandler extends RequestHandler<FindNodeResponse> {
	private static Logger logger = Logger.getLogger(LoginHandler.class);

	private Status actualStatus;

	private NetLocation bootstrapNode;                // �����ڵ�
	private BigInteger rpcID;
	private RPCBuffer outputBuffer;

	private HashSet<BigInteger> queriedNodes;          

	public LoginHandler() {
		actualStatus = Status.NOT_STARTED;
		outputBuffer = RPCBuffer.getSentBuffer();
		bootstrapNode = null;
		rpcID = null;
		queriedNodes = new HashSet<BigInteger>();
	}

	public NetLocation getBootstrapNode() {
		return bootstrapNode;
	}

	public void setBootstrapNode(NetLocation bootstrapNode) {
		this.bootstrapNode = bootstrapNode;
	}

	public BigInteger getRpcID() {
		return rpcID;
	}

	public void setRpcID(BigInteger rpcID) {
		this.rpcID = rpcID;
	}

	public void run() {
		actualStatus = Status.PROCESSING;       //����״̬��
		logger.info("��ʼ��½���̣���½�ڵ��ǣ� " + bootstrapNode.getIP().getHostAddress());
		requestNode(rpcID, BigInteger.ZERO, bootstrapNode.getIP().getHostAddress(), bootstrapNode.getPort());
	}

	private void requestNode(BigInteger rpcID, KademliaNode node) {
		this.requestNode(rpcID, node.getNodeID(), node.getIpAddress().getHostAddress(), node.getPort());
	}

	private void requestNode(BigInteger rpcID, BigInteger nodeID, String ip, int port) {
		FindNodeRPC rpc = new FindNodeRPC();
		try {
			BigInteger myID = Controller.getMyID();
			logger.debug("���� " + ip + ":" + port + " �����Լ��Ľڵ�(" + myID.toString(16) + ")");
			rpc.setRPCID(rpcID);
			rpc.setDestinationNodeID(nodeID);
			rpc.setSearchedNodeID(myID);               // ���Լ�ID����FIND_NODE����
			rpc.setSenderNodeID(myID);
			RPCInfo<FindNodeRPC> rpcInfo = new RPCInfo<FindNodeRPC>(rpc, ip, port);
			queriedNodes.add(nodeID);            //��ӷ��ʹ��Ľڵ�
			outputBuffer.add(rpcInfo);             // ����outputBuffer�����rpcInfoʱ��RPCOutputProcessor�ͱ�������
		} catch (KademliaProtocolException e) {
			logger.warn(e);
		}
	}

	public void addResult(RPCInfo<FindNodeResponse> rpcInfo) {
		FindNodeResponse response = rpcInfo.getRPC();
		BigInteger found = response.getFoundNodeID();
		String foundNodeString = found.toString(16);
		try {
			if (!found.equals(Controller.getMyID())) {
				if (queriedNodes.contains(found)) {
					logger.debug("Node " + foundNodeString + " already queried");
					logger.debug("Querying node " + foundNodeString);
				} else {
					logger.debug("Querying node " + foundNodeString);
					KademliaNode node = new KademliaNode(found, response.getIpAddressInet(), response.getPortInteger());
					requestNode(rpcID, node);
				}
			}
		} catch (UnknownHostException e) {
			logger.warn(e);
		}
	}

	public Status getStatus() {
		return actualStatus;
	}

	public void clear() {
		actualStatus = Status.NOT_STARTED;
		outputBuffer = RPCBuffer.getSentBuffer();
		bootstrapNode = null;
		rpcID = null;
		queriedNodes.clear();
	}
}
