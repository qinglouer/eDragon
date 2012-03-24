package jkademlia.protocol.request;

import java.math.BigInteger;

import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.KademliaProtocol;
import jkademlia.protocol.RPC;

public class FindValueRPC extends RPC{

	public static final String[][] DATA_STRUCTURE ={
		{"key","NODE_ID_LENGTH"}
	};
	public BigInteger key;
	public static final int KEY_AREA = KademliaProtocol.TOTAL_AREA_LENGTH;
	public static final int TOTAL_AREA_LENGTH = KEY_AREA + NODE_ID_LENGTH;
	public BigInteger getKey(){
		return key;
	}
	
	public void setKey(BigInteger key) throws KademliaProtocolException{
		if(key == null){
			throw new KademliaProtocolException("Can not set key to null");
		}else if(key.bitLength()>KademliaProtocol.NODE_ID_LENGTH*8){
			throw new KademliaProtocolException("key must have" + KademliaProtocol.NODE_ID_LENGTH*8 +"bits, " + "found" + key.bitLength() + "bits");
		}else{
			this.key =key;
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
		return FIND_VALUE;
	}

	@Override
	public boolean isRequest() {
		return true;
	}

}
