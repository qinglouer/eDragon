package jkademlia.builders.implementation.reflection;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import jkademlia.builder.OutputBuilder;
import jkademlia.protocol.RPC;

public class ReflectionOutputBuilderImp extends OutputBuilder {
	public ByteBuffer buildData(RPC rpc) {
        return null;
    }

    public DatagramPacket buildPacket(RPC rpc, String ip, int port) throws UnknownHostException {
        return this.buildPacket(rpc, InetAddress.getByName(ip), port);
    }
    
    public DatagramPacket buildPacket(RPC rpc, InetAddress ip, int port) {
        ByteBuffer data = this.buildData(rpc);
        return new DatagramPacket(data.array(), data.array().length, ip, port);
    }
}
