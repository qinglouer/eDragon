package jkademlia.structure.kademlia;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jkademlia.credit.CreditValue;

public class KademliaNode extends KademliaTreeNode{

	private BigInteger nodeID;
	private InetAddress ipAddress;
	private int port;
	private String creditValue;
	private long createTime;
	private long lastAccess;
	private CreditValue credit;
	
	public KademliaNode() {
		this.createTime = System.currentTimeMillis();   
	}
	
	public KademliaNode(BigInteger nodeID) {
		this.setNodeID(nodeID);
		this.createTime = System.currentTimeMillis();
		this.lastAccess = createTime;
	}

	public KademliaNode(BigInteger nodeID, String ip, int port)throws UnknownHostException {
		this(nodeID, InetAddress.getByName(ip), port);
	}

	public KademliaNode(BigInteger nodeID, InetAddress ip, int port) {
		this.setNodeID(nodeID);
		this.setIpAddress(ip);
		this.setPort(port);
		credit = new CreditValue();
		this.creditValue = credit.getCreditValue();
		this.createTime = System.currentTimeMillis();
		this.lastAccess = createTime;
	}
	
	public void setNodeID(BigInteger nodeID) {
		this.nodeID = nodeID;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getCreditValue(){
		return creditValue;
	}
	
	public void setCreditNumber(String creditValue){
		this.creditValue = creditValue;
	}

	public String getIpAndPort() {
		return ipAddress.getHostAddress() + ":" + port;
	}

	public BigInteger getNodeID() {
		return nodeID;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof KademliaNode) {
			KademliaNode node = (KademliaNode) obj;
			if (node.getNodeID() != null && this.getNodeID() != null)
				result = node.getNodeID().equals(this.getNodeID());
			else
				result = node.getNodeID() == null && this.getNodeID() != null;
		}
		return result;
	}

	public int hashCode() {
		return this.getNodeID().hashCode();
	}

	public String toString(){
		return "ID: " + idToString() + "creditValue: " + creditValue ;
	}
	private String idToString() {
		BigInteger id = this.getNodeID();
		StringBuffer idString = new StringBuffer(id.toString(16).toUpperCase());
		idString.ensureCapacity(21);
		if (id.compareTo(BigInteger.ZERO) >= 0)
			idString.insert(0, "+");
		for (int i = idString.length(); i <= 40; i++)
			idString.insert(1, "0");
		return idString.toString();
	}
}
