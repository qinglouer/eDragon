package jkademlia.builder;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import jkademlia.builder.implementation.InputBuilderImp;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.RPC;

public abstract class InputBuilder {

	private static InputBuilder instance;

	public static InputBuilder getInstance() {
		if (instance == null)
			instance = new InputBuilderImp();
		return instance;
	}

	public abstract RPC buildRPC(byte[] data) throws KademliaProtocolException;

	public abstract RPC buildRPC(Byte[] data) throws KademliaProtocolException;

	public abstract RPC buildRPC(ByteBuffer data) throws KademliaProtocolException;

	public abstract RPC buildRPC(DatagramPacket packet) throws KademliaProtocolException;

}
