package jkademlia.protocol;

import java.math.BigInteger;

import jkademlia.exceptions.KademliaProtocolException;

public abstract class RPC implements KademliaProtocol {
	private BigInteger senderNodeID;

	private BigInteger destinationNodeID;

	private BigInteger RPCID;

	public BigInteger getSenderNodeID() {
		return this.senderNodeID;
	}

	public void setSenderNodeID(BigInteger node) throws KademliaProtocolException {
		senderNodeID = node;
	}

	public BigInteger getDestinationNodeID() {
		return this.destinationNodeID;
	}

	public void setDestinationNodeID(BigInteger node) throws KademliaProtocolException {
		destinationNodeID = node;
	}

	public BigInteger getRPCID() {
		return this.RPCID;
	}

	public void setRPCID(BigInteger RPCID) throws KademliaProtocolException {
		if (RPCID != null && RPCID.bitLength() > KademliaProtocol.RPC_ID_LENGTH * 8)
			throw new KademliaProtocolException("RPC ID must have " + (KademliaProtocol.RPC_ID_LENGTH * 8) + " bits, " + "found " + RPCID.bitLength() + " bits");
		else
			this.RPCID = RPCID;
	}

	public abstract String[][] getDataStructure();

	public abstract int getInfoLength();

	public abstract byte getType();

	public abstract boolean isRequest();      // 判断是客服端还是服务端

}
