package jkademlia.protocol.request;

import jkademlia.protocol.RPC;

public class PingRPC extends RPC {
	public static final String[][] DATA_STRUCTURE = {};
	
	public String[][] getDataStructure(){
		return DATA_STRUCTURE;
	}
	
	public int getInfoLength(){
		return TOTAL_AREA_LENGTH;
	}
	
	public byte getType(){
		return PING;
	}
	
	@Override
	public boolean isRequest(){
		return true;
	}
}
