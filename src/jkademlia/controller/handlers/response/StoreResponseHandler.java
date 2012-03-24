package jkademlia.controller.handlers.response;

import java.math.BigInteger;

import jkademlia.controller.handlers.Handler;
import jkademlia.facades.storage.DataManagerFacade;
import jkademlia.protocol.request.StoreRPC;

import org.apache.log4j.Logger;

public class StoreResponseHandler extends Handler<StoreRPC>{
	
	private static Logger logger = Logger.getLogger(StoreResponseHandler.class);
	private Status actualStatus;
	
	public StoreResponseHandler(){
		this.actualStatus = Status.NOT_STARTED;
	}
	
	public synchronized Status getStatus(){
		return actualStatus;
	}

	@Override
	public void clear() {
		this.actualStatus = Status.NOT_STARTED;
		setRPCInfo(null);	
	}

	@Override
	public void run() {
		StoreRPC rpc = getRPCInfo().getRPC();
		BigInteger key = rpc.getKey();
		String value = new String(rpc.getValue().toByteArray());
		if(key != null){
			this.actualStatus = Status.PROCESSING;
			DataManagerFacade<String> storage = DataManagerFacade.getDataManager();
			logger.info("saving value" + value +"with the key" + key.toString(16));
			String oldValue = storage.put(key, value);
			int i = storage.getSize();
			System.out.println("存储在本机上面的键值对一共有：" + i);
			System.out.println("存储的key" + key + "value是" + value);
			if(oldValue != null){
				logger.debug("previous Value overwritten：" +oldValue );
			}
			actualStatus = Status.ENDED;
		}else{
			throw new NullPointerException("Cannot processing a value with null key");
		}
	}
}
