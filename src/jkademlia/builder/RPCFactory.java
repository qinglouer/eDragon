package jkademlia.builder;

import jkademlia.builder.implementation.RPCFactoryImp;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.RPC;

public abstract class RPCFactory {
	private static Class<? extends RPCFactory> classInstance = RPCFactoryImp.class;

	public static RPCFactory newInstance() {
		RPCFactory instance = null;
		try {
			instance = classInstance.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static final void setRPCFactoryClass(String className) throws ClassNotFoundException {
		RPCFactory.setRPCFactoryClass((Class<? extends RPCFactory>) Class.forName(className));
	}

	public static final void setRPCFactoryClass(Class<? extends RPCFactory> clazz) {
		classInstance = clazz;
	}

	public abstract RPC buildRPC(byte[] data) throws KademliaProtocolException;
}
