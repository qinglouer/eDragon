package jkademlia.controller.handlers;

import java.math.BigInteger;

import jkademlia.protocol.RPC;
import jkademlia.structure.kademlia.RPCInfo;

public abstract class RequestHandler<T extends RPC> extends Handler<T>{
	protected static final BigInteger MAX_RANGE = BigInteger.valueOf(2).pow(161);
	public abstract void addResult(RPCInfo<T> rpcInfo);
}
