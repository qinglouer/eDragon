package jkademlia.builder.implementation;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import jkademlia.builder.OutputBuilder;
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

public class OutputBuilderImp extends OutputBuilder {
	public ByteBuffer buildData(RPC rpc) {
		DataTools tool = ToolBox.getDataTools();
        ByteBuffer data;
        
        if(rpc instanceof FindNodeRPC){
            FindNodeRPC castedRPC = (FindNodeRPC) rpc;
            data = ByteBuffer.allocate(FindNodeRPC.TOTAL_AREA_LENGTH);
            
            this.buildbasicInfo(data, rpc, KademliaProtocol.FIND_NODE, (byte)0);
           
            //获取缓冲区当前的位置 
            data.position(FindNodeRPC.SEARCHED_NODE_AREA);
            byte[] array = castedRPC.getSearchedNodeID().toByteArray();
            data.put(tool.formatByteArray(array, KademliaProtocol.NODE_ID_LENGTH));
        
        } else if(rpc instanceof FindValueRPC) {
            FindValueRPC castedRPC = (FindValueRPC) rpc;
            data = ByteBuffer.allocate(FindValueRPC.TOTAL_AREA_LENGTH);
            
            this.buildbasicInfo(data, rpc, KademliaProtocol.FIND_VALUE, (byte)0);
           
            data.position(FindValueRPC.KEY_AREA);
            byte[] array = castedRPC.getKey().toByteArray();
            data.put(tool.formatByteArray(array, KademliaProtocol.KEY_LENGTH));
        
        } else if(rpc instanceof PingRPC) {
            data = ByteBuffer.allocate(PingRPC.TOTAL_AREA_LENGTH);

            this.buildbasicInfo(data, rpc, KademliaProtocol.PING, (byte)0);
        
            
        } else if(rpc instanceof StoreRPC) {
            StoreRPC castedRPC = (StoreRPC) rpc;
            byte[] array;
            data = ByteBuffer.allocate(StoreRPC.TOTAL_AREA_LENGTH);

            this.buildbasicInfo(data, rpc, KademliaProtocol.STORE, (byte)0);
            
           
            data.position(StoreRPC.PIECE_AREA);
            data.put(castedRPC.getPiece());
            
            data.position(StoreRPC.PIECE_TOTAL_AREA);
            data.put(castedRPC.getPieceTotal());
           
            data.position(StoreRPC.KEY_AREA);
            array = castedRPC.getKey().toByteArray();
            data.put(tool.formatByteArray(array, KademliaProtocol.KEY_LENGTH));
            
            data.position(StoreRPC.VALUE_AREA);
            array = castedRPC.getValue().toByteArray();
            data.put(tool.formatByteArray(array, KademliaProtocol.VALUE_LENGTH));
            
        } else if(rpc instanceof FindNodeResponse) {
            FindNodeResponse castedRPC = (FindNodeResponse) rpc;
            byte[] array;
            //创建大小为90的字节缓冲区
            data = ByteBuffer.allocate(FindNodeResponse.TOTAL_AREA_LENGTH);
            
            this.buildbasicInfo(data, rpc, KademliaProtocol.FIND_NODE, (byte)1);
            
          
            data.position(FindNodeResponse.FOUND_NODE_AREA);
            array = castedRPC.getFoundNodeID().toByteArray();
            data.put(tool.formatByteArray(array, KademliaProtocol.NODE_ID_LENGTH));
            
            data.position(FindNodeResponse.IP_AREA);
            array = castedRPC.getIpAddress().toByteArray();
            data.put(tool.formatByteArray(array, FindNodeResponse.IP_ADDRESS_LENGTH));
            
            data.position(FindNodeResponse.PORT_AREA);
            array = castedRPC.getPort().toByteArray();
            data.put(tool.formatByteArray(array, FindNodeResponse.PORT_LENGTH));
            
        } else if(rpc instanceof FindValueResponse) {
            FindValueResponse castedRPC = (FindValueResponse) rpc;
            data = ByteBuffer.allocate(FindValueResponse.TOTAL_AREA_LENGTH);
         
            this.buildbasicInfo(data, rpc, KademliaProtocol.FIND_VALUE, (byte)1);
           
            data.position(FindValueResponse.PIECE_AREA);
            data.put(castedRPC.getPiece());
            
            data.position(FindValueResponse.PIECE_TOTAL_AREA);
            data.put(castedRPC.getPieceTotal());
            
            data.position(FindValueResponse.VALUE_AREA);
            byte[] array = castedRPC.getValue().toByteArray();
            data.put(tool.formatByteArray(array, KademliaProtocol.VALUE_LENGTH));
            
        } else if(rpc instanceof PingResponse) {
            data = ByteBuffer.allocate(PingResponse.TOTAL_AREA_LENGTH);
         
            this.buildbasicInfo(data, rpc, KademliaProtocol.PING, (byte)1);
      
        } else if(rpc instanceof StoreResponse) {
            data = ByteBuffer.allocate(StoreResponse.TOTAL_AREA_LENGTH);         
            this.buildbasicInfo(data, rpc, KademliaProtocol.STORE, (byte)1);
            
        } else {
            data = null;
        }
        return data;
	}
    
    public DatagramPacket buildPacket(RPC rpc, String ip, int port) throws UnknownHostException {
        return this.buildPacket(rpc, InetAddress.getByName(ip), port);
    }
    
	public DatagramPacket buildPacket(RPC rpc, InetAddress ip, int port) {
        ByteBuffer data = this.buildData(rpc);
        return new DatagramPacket(data.array(), data.array().length, ip, port);
	}
    
    private void buildbasicInfo(ByteBuffer data, RPC rpc, byte type, byte response){
        DataTools tool = ToolBox.getDataTools();
        byte[] array;
       
        //设置RPC类型在缓冲区中所占有的位置
        data.position(KademliaProtocol.TYPE_AREA);
        data.put(type);
        
       
        data.position(KademliaProtocol.RESPONSE_AREA);
        data.put(response);
        
       
        data.position(KademliaProtocol.RPC_ID_AREA);
        array = rpc.getRPCID().toByteArray();
        data.put(tool.formatByteArray(array, KademliaProtocol.RPC_ID_LENGTH));
        
       
        data.position(KademliaProtocol.SENDER_ID_AREA);
        array = rpc.getSenderNodeID().toByteArray();
        data.put(tool.formatByteArray(array, KademliaProtocol.NODE_ID_LENGTH));
        
       
        data.position(KademliaProtocol.DESTINATION_ID_AREA);
        array = rpc.getDestinationNodeID().toByteArray();
        data.put(tool.formatByteArray(array, KademliaProtocol.NODE_ID_LENGTH));
    }
}
