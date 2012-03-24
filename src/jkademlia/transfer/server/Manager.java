package jkademlia.transfer.server;

import java.util.Properties;
import java.io.*;
import java.net.DatagramSocket;
public class Manager {
	
	public static String SHARE_LIST;
	public Manager(){
		System.out.println("Manager启动了...");
		Properties pro = new Properties();
		File proFile = new File("jkademlia.properties");
		try {
			InputStream is = new FileInputStream(proFile);
			pro.load(is);
			SHARE_LIST = pro.getProperty("edragon.sharelist");
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		jkademlia.data.DatabaseManager.connectDatabase();//连接数据库
//		jkademlia.data.DatabaseManager.createTable();
		System.out.println("连接上数据库");
		
		start();
	}
	
	private SocketServer s;
	private DatagramServer ds;
	
	public void start(){
		s = new SocketServer();
		s.start();
		ds = new DatagramServer();
		ds.start();
	}
}
