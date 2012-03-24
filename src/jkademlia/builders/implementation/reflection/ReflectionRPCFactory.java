package jkademlia.builders.implementation.reflection;

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
import jkademlia.tools.ReflectionTools;
import jkademlia.tools.ToolBox;

public class ReflectionRPCFactory extends RPCFactory{
	public RPC buildRPC(byte[] data) throws KademliaProtocolException {
        DataTools dataTools = ToolBox.getDataTools();
        ReflectionTools reflTools = ToolBox.getReflectionTools();
        RPC rpc = null;
       
        if(data.length < KademliaProtocol.TOTAL_AREA_LENGTH){
            throw new KademliaProtocolException("Expected at least " + KademliaProtocol.TOTAL_AREA_LENGTH + " bytes, found " + data.length);
        }       
        
        byte type = data[KademliaProtocol.TYPE_AREA];        
        
        byte response = data[KademliaProtocol.RESPONSE_AREA];
        
        if(response < 0){
            throw new KademliaProtocolException("Invalid response code: " + response);
        } else if (response == 0){
            switch (type) {
                case KademliaProtocol.FIND_VALUE: rpc = new FindValueRPC(); break; 
                case KademliaProtocol.FIND_NODE: rpc = new FindNodeRPC(); break;
                case KademliaProtocol.STORE: rpc = new StoreRPC(); break;
                case KademliaProtocol.PING: rpc = new PingRPC(); break;
                default: throw new KademliaProtocolException("Unknown RPC type: " + type);
            }
        } else {
            switch (type) {
                case KademliaProtocol.FIND_VALUE: rpc = new FindValueResponse(); break;    
                case KademliaProtocol.FIND_NODE: rpc = new FindNodeResponse(); break;
                case KademliaProtocol.STORE: rpc = new StoreResponse(); break;
                case KademliaProtocol.PING: rpc = new PingResponse(); break;
                default: throw new KademliaProtocolException("Unknown RPC type: " + type);
            }
        }
        
        if(data.length != rpc.getInfoLength()){
            throw new KademliaProtocolException("Constructing " + rpc.getClass().getName() + ": Expected " + rpc.getInfoLength() + " bytes, found " + data.length);
        }
               
        buildBasicInfo(data, rpc);
       
        String[][] structure = rpc.getDataStructure();
       
        try {   
            for(int i = 0, position = KademliaProtocol.TOTAL_AREA_LENGTH; i < structure.length; i++){
                String fieldName = structure[i][0];
                int value = reflTools.getFieldValue(structure[i][1], rpc);            
                byte[] extracted = dataTools.copyByteArray(data, position, value);
               
                if(extracted.length > 1){
                    reflTools.invokeSetter(fieldName, extracted, rpc);
                } else {
                    reflTools.invokeSetter(fieldName, extracted[0], rpc);
                }
            }
        } catch (NoSuchFieldException e){
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        return rpc;
    }

    private void buildBasicInfo(byte[] data, RPC rpc) throws KademliaProtocolException {
        DataTools dataTools = ToolBox.getDataTools();
        byte[] senderNodeID = dataTools.copyByteArray(
            data, 
            KademliaProtocol.SENDER_ID_AREA,
            KademliaProtocol.NODE_ID_LENGTH
        ); 
        byte[] rpcID = dataTools.copyByteArray(
            data, 
            KademliaProtocol.RPC_ID_AREA,
            KademliaProtocol.RPC_ID_LENGTH
        );
        byte[] destinationNodeID = dataTools.copyByteArray(
            data, 
            KademliaProtocol.DESTINATION_ID_AREA,
            KademliaProtocol.NODE_ID_LENGTH
        );
              
        rpc.setRPCID(new BigInteger(rpcID));
        rpc.setSenderNodeID(new BigInteger(senderNodeID));
        rpc.setDestinationNodeID(new BigInteger(destinationNodeID));
    }
}
