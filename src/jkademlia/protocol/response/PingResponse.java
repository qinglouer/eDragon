package jkademlia.protocol.response;

import jkademlia.protocol.RPC;

public class PingResponse extends RPC{

	public static final String[][] DATA_STRUCTURE = {};
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
		return PING;
	}

	@Override
	public boolean isRequest() {
		return false;
	}

}
