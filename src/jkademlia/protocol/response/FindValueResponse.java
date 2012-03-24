package jkademlia.protocol.response;

import java.math.BigInteger;

import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.KademliaProtocol;
import jkademlia.protocol.RPC;

public class FindValueResponse extends RPC {
	public static final String[][] DATA_STRUCTURE = { { "piece", "PIECE_LENGTH" }, { "pieceTotal", "PIECE_LENGTH" }, { "value", "VALUE_LENGTH" } };

	public static final int PIECE_AREA = KademliaProtocol.TOTAL_AREA_LENGTH;

	public static final int PIECE_TOTAL_AREA = PIECE_AREA + PIECE_LENGTH;

	public static final int VALUE_AREA = PIECE_TOTAL_AREA + PIECE_LENGTH;

	public static final int TOTAL_AREA_LENGTH = VALUE_AREA + VALUE_LENGTH;

	private BigInteger value;

	private byte piece;

	private byte pieceTotal;

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) throws KademliaProtocolException {
		if (value == null) {
			throw new KademliaProtocolException("Cannot set value to null");
		} else if (value.bitLength() > KademliaProtocol.VALUE_LENGTH * 8) {
			throw new KademliaProtocolException("Value must have " + (KademliaProtocol.VALUE_LENGTH * 8) + " bits, " + "found " + value.bitLength() + " bits");
		} else {
			this.value = value;
		}
	}

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
		return false;
	}
}
