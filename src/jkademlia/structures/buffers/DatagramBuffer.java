package jkademlia.structures.buffers;

import java.net.DatagramPacket;
import java.util.concurrent.ArrayBlockingQueue;

import jkademlia.controller.threads.ThreadGroupLocal;

public class DatagramBuffer extends ArrayBlockingQueue<DatagramPacket> {

	private static final long serialVersionUID = 1445845374635392774L;
	public static ThreadGroupLocal<DatagramBuffer> receiverBuffer;
	public static ThreadGroupLocal<DatagramBuffer> sendBuffer;
	
	public static DatagramBuffer getSentBuffer(){
		if(sendBuffer == null){
			sendBuffer = new ThreadGroupLocal<DatagramBuffer>(){
				public DatagramBuffer initialValue(){
					Integer size = Integer.parseInt(System.getProperty("jkademlia.datagrambuffer.output.size"));
					return new DatagramBuffer(size);
				}
			};
		}
		return sendBuffer.get();
	}
	
	public static DatagramBuffer getReceivedBuffer(){
		if(receiverBuffer == null){
			receiverBuffer = new ThreadGroupLocal<DatagramBuffer>(){
				public DatagramBuffer initialValue(){
					Integer size = Integer.parseInt(System.getProperty("jkademlia.datagrambuffer.input.size"));
					return new DatagramBuffer(size);
				}
			};
		}
		return receiverBuffer.get();
	}

	/**
     *给定固定容量的有界缓存区，容量是20，访问策略是公平策略，即对等待的生产者线程和使用者线程进行排序时FIFO.
     * @param capacity
     */
	protected DatagramBuffer(int capacity) {
		super(capacity,true);
	}

}
