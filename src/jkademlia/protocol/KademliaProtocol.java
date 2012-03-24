package jkademlia.protocol;

public interface KademliaProtocol {

	public static final byte PING = 1;

	public static final byte STORE = 2;

	public static final byte FIND_NODE = 4;

	public static final byte FIND_VALUE = 8;

	public static final byte RPC_TYPE_AMOUNT = 4;

	public static final int NODE_ID_LENGTH = 20;

	public static final int KEY_LENGTH = 20;

	public static final int RPC_ID_LENGTH = 20;

	public static final int TYPE_AREA_LENGTH = 1;

	public static final int RESPONSE_AREA_LENGTH = 1;

	public static final int VALUE_LENGTH = 160;

	public static final int PIECE_LENGTH = 1;

	public static final int TYPE_AREA = 0;

	public static final int RESPONSE_AREA = TYPE_AREA + TYPE_AREA_LENGTH;

	public static final int RPC_ID_AREA = RESPONSE_AREA + RESPONSE_AREA_LENGTH;

	public static final int SENDER_ID_AREA = RPC_ID_AREA + RPC_ID_LENGTH;

	public static final int DESTINATION_ID_AREA = SENDER_ID_AREA + NODE_ID_LENGTH;

	public static final int TOTAL_AREA_LENGTH = DESTINATION_ID_AREA + NODE_ID_LENGTH;

	public static final int BUCKET_SIZE = 20;

	public String[][] getDataStructure();

	public int getInfoLength();
}
