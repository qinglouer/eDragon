package jkademlia.controller.processors;

import java.net.DatagramPacket;

import jkademlia.builder.InputBuilder;
import jkademlia.controller.threads.CyclicThread;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.RPC;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.DatagramBuffer;
import jkademlia.structures.buffers.RPCBuffer;
import jkademlia.tools.ToolBox;

import org.apache.log4j.Logger;

public class RPCInputProcessor extends CyclicThread{
	
	private static Logger logger = Logger.getLogger(RPCInputProcessor.class);
	
	private InputBuilder rpcBuilder;
	private DatagramBuffer inputBuffer;
	private RPCBuffer outputBuffer;
	

	public RPCInputProcessor() {
		super(ToolBox.getReflectionTools().generateThreadName(RPCInputProcessor.class));
		this.rpcBuilder = InputBuilder.getInstance();
		this.inputBuffer = DatagramBuffer.getReceivedBuffer();
		this.outputBuffer = RPCBuffer.getReceivedBuffer();
		this.setRoundWait(50);	
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void cycleOperation() throws InterruptedException {
		while (!inputBuffer.isEmpty()) {
			DatagramPacket packet = (DatagramPacket) inputBuffer.remove();
			String ip = packet.getAddress().getHostAddress();
			Integer port = packet.getPort();
			logger.debug("Building RPC from " + ip + ":" + port);
			try {
				RPC rpc = rpcBuilder.buildRPC(packet);
				logger.debug("Built rpc of type "+ rpc.getClass().getSimpleName());
				outputBuffer.add(new RPCInfo(rpc, ip, port));
			} catch (KademliaProtocolException e) {
				logger.warn(e);
			}
		}
		
	}

	@Override
	protected void finalize() {
		
	}
	
	protected Logger getLogger() {
		return logger;
	}
}
