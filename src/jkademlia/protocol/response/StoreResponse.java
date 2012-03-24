package jkademlia.protocol.response;

import jkademlia.protocol.RPC;

public class StoreResponse extends RPC {

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
		return STORE;
	}

	@Override
	public boolean isRequest() {
		return false;
	}

}
