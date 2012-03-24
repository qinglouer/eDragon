package jkademlia.protocol.request;

import java.math.BigInteger;

import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.KademliaProtocol;
import jkademlia.protocol.RPC;

public class StoreRPC extends RPC {

	private byte piece;
	private byte pieceTotal;
	private BigInteger key;
	private BigInteger value;
	public String[][] DATA_STRUCTURE = { { "piece", "PIECE_LENGTH" },
			{ "pieceTotal", "PIECE_TOTAL_LENGTH" }, { "key", "KEY_LENGTH" },
			{ "value", "VALUE_LENGTH" } };

	public static final int PIECE_AREA = KademliaProtocol.TOTAL_AREA_LENGTH;

	public static final int PIECE_TOTAL_AREA = PIECE_AREA + PIECE_LENGTH;

	public static final int KEY_AREA = PIECE_TOTAL_AREA + PIECE_LENGTH;

	public static final int VALUE_AREA = KEY_AREA + KEY_LENGTH;

	public static final int TOTAL_AREA_LENGTH = VALUE_AREA + VALUE_LENGTH;

	public byte getPiece() {
		return piece;
	}

	public void setPiece(byte piece) {
		this.piece = piece;
	}

	public byte getPieceTotal() {
		return pieceTotal;
	}

	public void setPieceTotal(byte pieceTotal) {
		this.pieceTotal = pieceTotal;
	}

	public BigInteger getKey() {
		return key;
	}

	public void setKey(BigInteger key) throws KademliaProtocolException {
		if (key == null) {
			throw new KademliaProtocolException("Cannot set key to null");
		} else if (key.bitLength() > KademliaProtocol.NODE_ID_LENGTH * 8) // key的最小的二进制补码如果大于160位
		{
			throw new KademliaProtocolException("Key must have "
					+ (KademliaProtocol.NODE_ID_LENGTH * 8) + " bits, "
					+ "found " + key.bitLength() + " bits");
		} else {
			this.key = key;
		}
	}

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) throws KademliaProtocolException {
		if (value == null) {
			throw new KademliaProtocolException("Cannot set key to null");
		} else if (value.bitLength() > KademliaProtocol.VALUE_LENGTH * 8) {
			throw new KademliaProtocolException("value must have"
					+ (KademliaProtocol.VALUE_LENGTH * 8) + "bits, " + "found "
					+ value.bitLength() + " bits");
		} else {
			this.value = value;
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
		return STORE;
	}

	@Override
	public boolean isRequest() {
		return true;
	}

}
