package jkademlia.builders.implementation.reflection;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import jkademlia.builder.InputBuilder;
import jkademlia.protocol.RPC;
import jkademlia.tools.ToolBox;

public class ReflectionInputBuilderImp extends InputBuilder {
	public RPC buildRPC(Byte[] data) {
        return this.buildRPC(ToolBox.getDataTools().convertArray(data));
    }
    
    public RPC buildRPC(ByteBuffer data){
        RPC rpc = null;
        return rpc;
    }
    
    public RPC buildRPC(byte[] data){
        return this.buildRPC(ToolBox.getDataTools().convertArray(data));
    }

    public RPC buildRPC(DatagramPacket packet) {
        return this.buildRPC(packet.getData());
    }

}
