//package jkademlia.gui;
//
//import java.math.BigInteger;
//
//import jkademlia.kademlia.JKademliaSystem;
//
//public class ListNode {
//	private JKademliaSystem system;
//	
//	public ListNode(JKademliaSystem system){
//		this.system = system;
//	}
//	
//	public String toString(){
//		String ip = system.getIP().toString();
//		String port = system.getPort() + "";
//		String id = idToString(system.getSystemID());
//		String number = "hellokitty";
//		return  ip + ":" + port +"|" + id + "|" + number; 
//	}
//
//	private String idToString(BigInteger id) {
//		StringBuffer idString = new StringBuffer(id.toString(16).toUpperCase());
//		idString.ensureCapacity(21);
//		if (id.compareTo(BigInteger.ZERO) >= 0)
//			idString.insert(0, "+");
//		for (int i = idString.length(); i <= 40; i++)
//			idString.insert(1, "0");
//		return idString.toString();
//	}
//}
