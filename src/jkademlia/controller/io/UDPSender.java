package jkademlia.controller.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import jkademlia.structures.buffers.DatagramBuffer;
import jkademlia.tools.ToolBox;

import org.apache.log4j.Logger;

public class UDPSender extends UDPHandler {
	private static Logger logger = Logger.getLogger(UDPSender.class);

	private JKademliaDatagramSocket socket;
	private DatagramBuffer buffer;

	public UDPSender() throws SocketException {
		this(SingletonSocket.getInstance(), DatagramBuffer.getSentBuffer());
	}

	public UDPSender(JKademliaDatagramSocket socket, DatagramBuffer buffer) throws SocketException {
		super(ToolBox.getReflectionTools().generateThreadName(UDPSender.class));
		if (socket == null)
			throw new NullPointerException("socket is null!");
		if (buffer == null)
			throw new NullPointerException("buffer is null!");
		this.socket = socket;
		this.buffer = buffer;
		this.socket.addObserver(this);
	}

	protected void cycleOperation() {
		if (!socket.isClosed()) {
			try {
				while (!buffer.isEmpty()) {
					DatagramPacket packet = (DatagramPacket) buffer.remove();
					socket.send(packet);
					logger.debug("Sent packet to " + packet.getSocketAddress());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected JKademliaDatagramSocket getSocket() {
		return socket;
	}

	protected void finalize() {
		if (socket != null)
			socket.close();
		logger.debug("Thread finalized");
	}

	protected Logger getLogger() {
		return logger;
	}
}
