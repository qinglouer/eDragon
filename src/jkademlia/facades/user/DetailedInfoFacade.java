package jkademlia.facades.user;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.List;

import jkademlia.structure.kademlia.KademliaNode;

public interface DetailedInfoFacade {

//	public long countReceivedPackets();
//
//    public long countSentPackets();
//
//    public long countReceivedRPCs();
//
//    public long countReceivedRPCs(byte type);
//
//    public long countSentRPCs();
//
//    public long countSentRPCs(byte type);
    
    public BigInteger getSystemID();
    
    public List<KademliaNode> listKnowContacts();
    
    public InetAddress getIP();
    
    public int getPort();
}
