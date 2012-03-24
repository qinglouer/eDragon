package jkademlia.protocol.request;

import java.math.BigInteger;

import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.KademliaProtocol;
import jkademlia.protocol.RPC;

/**
 * @author scaler  Email:zhuzhengtao520@gmail.com
 *本操作的接受者返回更接近目标ID的K个节点信息
 */
public class FindNodeRPC extends RPC {
	
	public static final String[][] DATA_STRUCTURE = {
		{"searchedNodeID","NODE_ID_LENGTH"}
	};
	
	public static final int SEARCHED_NODE_AREA = KademliaProtocol.TOTAL_AREA_LENGTH;
	//加上要查找的节点
	public static final int TOTAL_AREA_LENGTH = SEARCHED_NODE_AREA + NODE_ID_LENGTH;
	
	private BigInteger searchedNodeID;
	
	public BigInteger getSearchedNodeID(){
		return searchedNodeID;
	}

	public void setSearchedNodeID(BigInteger searchedNodeID)throws KademliaProtocolException{
		if(searchedNodeID == null){
			throw new KademliaProtocolException("Cannot set searched Node ID to null");
		}else if(searchedNodeID.bitLength() > NODE_ID_LENGTH * 8){
			throw new KademliaProtocolException("SearchedNodeID must have " + KademliaProtocol.NODE_ID_LENGTH * 8 + " bits, " + "found " + searchedNodeID.bitLength() + " bits");
		}else{
			this.searchedNodeID = searchedNodeID;
		}
	}
	@Override
	public String[][] getDataStructure() {
		return DATA_STRUCTURE;
	}

	@Override
	public int getInfoLength() {
		return TOTAL_AREA_LENGTH;
	}

	@Override
	public byte getType() {
		return FIND_NODE;
	}

	@Override
	public boolean isRequest() {
		return true;
	}
}
