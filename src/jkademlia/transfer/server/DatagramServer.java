package jkademlia.transfer.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import jkademlia.data.DatabaseManager;

public class DatagramServer extends Thread{
	private int port = 5001;
	private DatagramSocket ds = null;
	private byte[] buffer = null;
	private DatagramPacket dp = null;
	private boolean serverRunning = true;
	
	
	public DatagramServer(){
		try {
			ds = new DatagramSocket(port);
			System.out.println("UDP¿ªÊ¼¼àÌý...");
			buffer = new byte[1024];
			dp = new DatagramPacket(buffer,buffer.length);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		while(serverRunning){
			DataInputStream in = new DataInputStream(
					new ByteArrayInputStream(buffer));
			try{
				ds.receive(dp);
				int credit = in.readInt();
				DatabaseManager.updateCredit(credit);
				in.close();
			} catch(IOException e){
				e.printStackTrace();
				continue;
			}
		}
		
		
	}


}
