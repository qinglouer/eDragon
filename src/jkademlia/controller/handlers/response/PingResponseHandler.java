package jkademlia.controller.handlers.response;

import jkademlia.controller.handlers.Controller;
import jkademlia.controller.handlers.Handler;
import jkademlia.exceptions.KademliaProtocolException;
import jkademlia.protocol.request.PingRPC;
import jkademlia.protocol.response.PingResponse;
import jkademlia.structure.kademlia.RPCInfo;
import jkademlia.structures.buffers.RPCBuffer;

import org.apache.log4j.Logger;

public class PingResponseHandler extends Handler<PingRPC> {

	private static Logger logger = Logger.getLogger(PingResponseHandler.class);
	private Status actualStatus;

	public PingResponseHandler() {
		this.actualStatus = Status.NOT_STARTED;
	}

	public synchronized Status getStatus() {
		return actualStatus;
	}

	@Override
	public void clear() {
		this.actualStatus = Status.NOT_STARTED;
		setRPCInfo(null);
	}

	@Override
	public void run() {
		RPCInfo<PingRPC> rpcInfo = getRPCInfo();
		if (getRPCInfo() != null) {
			try {
				actualStatus = Status.PROCESSING;
				logger.info("Processing Ping request from " + rpcInfo.getIPAndPort());
				PingRPC rpc = rpcInfo.getRPC();
				PingResponse response = new PingResponse();

				response.setDestinationNodeID(rpc.getDestinationNodeID());
				response.setRPCID(rpc.getRPCID());
				response.setSenderNodeID(Controller.getMyID());

				RPCInfo<PingResponse> responseInfo = new RPCInfo<PingResponse>(response, rpcInfo.getIP(), rpcInfo.getPort());

				logger.info("Sending PingResponse to " + rpcInfo.getIPAndPort());
				RPCBuffer.getSentBuffer().add(responseInfo);

				actualStatus = Status.ENDED;
			} catch (KademliaProtocolException e) {
				logger.warn(e);
			}
		} else
			throw new NullPointerException("Cannot proccess a ping request for a null rpcInfo");
	}

}
