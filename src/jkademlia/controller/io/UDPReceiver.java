package jkademlia.controller.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import jkademlia.structures.buffers.DatagramBuffer;
import jkademlia.tools.ToolBox;

import org.apache.log4j.Logger;

public class UDPReceiver extends UDPHandler {
	private static Logger logger = Logger.getLogger(UDPReceiver.class);

	private JKademliaDatagramSocket socket;
	private DatagramBuffer buffer;

	public UDPReceiver() throws SocketException {
		this(SingletonSocket.getInstance(), DatagramBuffer.getReceivedBuffer());
	}

	public UDPReceiver(JKademliaDatagramSocket socket, DatagramBuffer buffer)throws SocketException {
		super(ToolBox.getReflectionTools().generateThreadName(UDPReceiver.class));
		if (socket == null)
			throw new NullPointerException("socket is null");
		if (buffer == null)
			throw new NullPointerException("buffer is null");
		this.socket = socket;
		this.buffer = buffer;
		this.socket.addObserver(this);
	}

	protected void cycleOperation() {
		DatagramPacket packet = new DatagramPacket(new byte[256], 256);
		try {
			socket.receive(packet);
			logger.debug("Received packet from " + packet.getSocketAddress());
			if (buffer.remainingCapacity() == 0)
				logger.warn("Received buffer at max capacity! Discarting packet");
			else {
				buffer.add(packet);
			}
		} catch (SocketException e) {
			if (!socket.isClosed() || !this.isInterrupted())
				logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	protected JKademliaDatagramSocket getSocket() {
		return this.socket;
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
