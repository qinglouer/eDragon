package jkademlia.structures.buffers;

import java.util.concurrent.ArrayBlockingQueue;

import jkademlia.controller.threads.ThreadGroupLocal;
import jkademlia.structure.kademlia.RPCInfo;

public class RPCBuffer extends ArrayBlockingQueue<RPCInfo> {

	private static final long serialVersionUID = -8615797442007125195L;

	public RPCBuffer(int capacity) {
		super(capacity, true);
	}

	private static ThreadGroupLocal<RPCBuffer> receivedBuffer;

	private static ThreadGroupLocal<RPCBuffer> sentBuffer;

	public static RPCBuffer getSentBuffer() {
		if (sentBuffer == null) {
			sentBuffer = new ThreadGroupLocal<RPCBuffer>() {
				public RPCBuffer initialValue() {
					Integer size = Integer.parseInt(System.getProperty("jkademlia.rpcbuffer.output.size"));
					return new RPCBuffer(size);
				}
			};
		}
		return sentBuffer.get();
	}

	public static RPCBuffer getReceivedBuffer() {
		if (receivedBuffer == null) {
			receivedBuffer = new ThreadGroupLocal<RPCBuffer>() {
				public RPCBuffer initialValue() {
					Integer size = Integer.parseInt(System.getProperty("jkademlia.rpcbuffer.input.size"));
					return new RPCBuffer(size);
				}
			};
		}
		return receivedBuffer.get();
	}
}
