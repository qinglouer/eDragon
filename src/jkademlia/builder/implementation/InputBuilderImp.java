package jkademlia.builder.implementation;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import jkademlia.builder.InputBuilder;
import jkademlia.builder.RPCFactory;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.RPC;
import jkademlia.tools.ToolBox;

public class InputBuilderImp extends InputBuilder{

	private RPCFactory factory;

	public InputBuilderImp() {
		factory = RPCFactory.newInstance();
	}

	public RPC buildRPC(Byte[] data) throws KademliaProtocolException {
		return this.buildRPC(ToolBox.getDataTools().convertArray(data));
	}

	public RPC buildRPC(byte[] data) throws KademliaProtocolException {
		return this.buildRPC(ByteBuffer.wrap(data));
	}

	public RPC buildRPC(ByteBuffer data) throws KademliaProtocolException {
		return factory.buildRPC(data.array());
	}

	public RPC buildRPC(DatagramPacket packet) throws KademliaProtocolException {
		return this.buildRPC(packet.getData());
	}

}
