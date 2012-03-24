package jkademlia.transfer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;


public class SocketServer extends Thread {
	private int port = 5001;
	private Socket s = null;
	private ServerSocket ss = null;
	private boolean serverRunning = true;
	private ExecutorService executorService;
	private final int POOL_SIZE = 4;
	
	public SocketServer() {
		try {
			ss = new ServerSocket(port,50);
//			executorService =Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(serverRunning) {
			try {
				System.out.println("¿ªÊ¼¼àÌý...");
				s = ss.accept();
				SocketThread st = new SocketThread(s);
				st.run();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}

