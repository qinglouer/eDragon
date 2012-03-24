package jkademlia.builder.implementation;

import java.math.BigInteger;

import jkademlia.builder.RPCFactory;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.KademliaProtocol;
import jkademlia.protocol.RPC;
import jkademlia.protocol.request.FindNodeRPC;
import jkademlia.protocol.request.FindValueRPC;
import jkademlia.protocol.request.PingRPC;
import jkademlia.protocol.request.StoreRPC;
import jkademlia.protocol.response.FindNodeResponse;
import jkademlia.protocol.response.FindValueResponse;
import jkademlia.protocol.response.PingResponse;
import jkademlia.protocol.response.StoreResponse;
import jkademlia.tools.DataTools;
import jkademlia.tools.ToolBox;

public class RPCFactoryImp extends RPCFactory{

	@Override
	public RPC buildRPC(byte[] data) throws KademliaProtocolException {
		RPC rpc = null;
		byte type = data[KademliaProtocol.TYPE_AREA];
		System.out.println(type);
		
		byte response = data[KademliaProtocol.RESPONSE_AREA];
		
		if(response == 0){
			switch(type){
			case KademliaProtocol.PING:
				rpc = this.buildPingRPC(data);
				 break;
            case KademliaProtocol.STORE:
                rpc = this.buildStoreRPC(data);
                break;
            case KademliaProtocol.FIND_NODE:
                rpc = this.buildFindNodeRPC(data);
                break;
            case KademliaProtocol.FIND_VALUE:
                rpc = this.buildFindValueRPC(data);
                break;
            default:
                throw new KademliaProtocolException("没有这个类型的RPC: " + type);
            }
        } else if (response > 0) {
            switch (type) {
            case KademliaProtocol.PING:
                rpc = this.buildPingResponse(data);
                break;
            case KademliaProtocol.STORE:
                rpc = this.buildStoreResponse(data);
                break;
            case KademliaProtocol.FIND_NODE:
                rpc = this.buildFindNodeResponse(data);
                break;
            case KademliaProtocol.FIND_VALUE:
                rpc = this.buildFindValueResponse(data);
                break;
            default:
                throw new KademliaProtocolException("没有这个类型的RPC:" + type);
            }
        } else {
            throw new KademliaProtocolException("Invalid response code: " + response);
        }

        return rpc;
    }

    private PingRPC buildPingRPC(byte[] data) throws KademliaProtocolException {
        PingRPC rpc = new PingRPC();
        buildBasicInfo(data, rpc);
        return rpc;
    }

    private StoreRPC buildStoreRPC(byte[] data) throws KademliaProtocolException {
        DataTools tools = ToolBox.getDataTools();
        StoreRPC rpc = new StoreRPC();
        buildBasicInfo(data, rpc);
        rpc.setPiece(data[StoreRPC.PIECE_AREA]);
        rpc.setPieceTotal(data[StoreRPC.PIECE_TOTAL_AREA]);
        rpc.setKey(new BigInteger(tools.copyByteArray(data, StoreRPC.KEY_AREA, StoreRPC.KEY_LENGTH)));
        rpc.setValue(new BigInteger(tools.copyByteArray(data, StoreRPC.VALUE_AREA, StoreRPC.VALUE_LENGTH)));
        return rpc;
    }

    private FindNodeRPC buildFindNodeRPC(byte[] data) throws KademliaProtocolException {
        DataTools tools = ToolBox.getDataTools();
        FindNodeRPC rpc = new FindNodeRPC();
        buildBasicInfo(data, rpc);
        rpc.setSearchedNodeID(new BigInteger(tools.copyByteArray(data, FindNodeRPC.SEARCHED_NODE_AREA, StoreRPC.NODE_ID_LENGTH)));
        return rpc;
    }

    private FindValueRPC buildFindValueRPC(byte[] data) throws KademliaProtocolException {
        DataTools tools = ToolBox.getDataTools();
        FindValueRPC rpc = new FindValueRPC();
        buildBasicInfo(data, rpc);
        rpc.setKey(new BigInteger(tools.copyByteArray(data, FindValueRPC.KEY_AREA, FindValueRPC.KEY_LENGTH)));
        return rpc;
    }

    private PingResponse buildPingResponse(byte[] data) throws KademliaProtocolException {
        PingResponse rpc = new PingResponse();
        buildBasicInfo(data, rpc);
        return rpc;
    }

    private StoreResponse buildStoreResponse(byte[] data) throws KademliaProtocolException {
        StoreResponse rpc = new StoreResponse();
        buildBasicInfo(data, rpc);
        return rpc;
    }

    private FindNodeResponse buildFindNodeResponse(byte[] data) throws KademliaProtocolException {
        DataTools tools = ToolBox.getDataTools();
        FindNodeResponse rpc = new FindNodeResponse();
        buildBasicInfo(data, rpc);
        rpc.setFoundNodeID(new BigInteger(tools.copyByteArray(data, FindNodeResponse.FOUND_NODE_AREA, FindNodeResponse.NODE_ID_LENGTH)));
        rpc.setIpAddress(new BigInteger(tools.copyByteArray(data, FindNodeResponse.IP_AREA, FindNodeResponse.IP_ADDRESS_LENGTH)));
        rpc.setPort(new BigInteger(tools.copyByteArray(data, FindNodeResponse.PORT_AREA, FindNodeResponse.PORT_LENGTH)));
        return rpc;
    }

    private FindValueResponse buildFindValueResponse(byte[] data) throws KademliaProtocolException {
        DataTools tools = ToolBox.getDataTools();
        FindValueResponse rpc = new FindValueResponse();
        buildBasicInfo(data, rpc);
        rpc.setPiece(data[FindValueResponse.PIECE_AREA]);
        rpc.setPieceTotal(data[FindValueResponse.PIECE_TOTAL_AREA]);
        rpc.setValue(new BigInteger(tools.copyByteArray(data, FindValueResponse.VALUE_AREA, FindValueResponse.VALUE_LENGTH)));
        return rpc;
    }

    private void buildBasicInfo(byte[] data, RPC rpc) throws KademliaProtocolException {
        DataTools tools = ToolBox.getDataTools();
        byte[] senderNodeID = tools.copyByteArray(
            data, 
            KademliaProtocol.SENDER_ID_AREA,
            KademliaProtocol.NODE_ID_LENGTH
        ); 
        byte[] rpcID = tools.copyByteArray(
            data, 
            KademliaProtocol.RPC_ID_AREA,
            KademliaProtocol.RPC_ID_LENGTH
        );
        byte[] destinationID = tools.copyByteArray(
            data, 
            KademliaProtocol.DESTINATION_ID_AREA,
            KademliaProtocol.NODE_ID_LENGTH
        );
        
        rpc.setRPCID(new BigInteger(rpcID));
        rpc.setSenderNodeID(new BigInteger(senderNodeID));
        rpc.setDestinationNodeID(new BigInteger(destinationID));
    }
}
