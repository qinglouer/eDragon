package jkademlia.transfer.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.*;

/*
 * 类PartTaskManager，将可进行下载的ip资源和待下载的文件片序号这两个队列连接起来，动态进行任务分配
 */
public class PartTaskManager implements Runnable{
	
	private Client ct = null;
	private ArrayBlockingQueue<Integer> partQueue = null;
	private ArrayBlockingQueue<String> ipQueue = null;
	public int partTaskNum = 0;
	public static int taskLimit=5;
	
//	public PartTaskManager(ClientTest ct,ArrayBlockingQueue<Integer> partQueue,ArrayBlockingQueue<IPResourse>ipQueue){
	public PartTaskManager(Client ct,ArrayBlockingQueue<Integer> partQueue,ArrayBlockingQueue<String> ipQueue){
		System.out.println("new出了一个PartTaskManager实例");
		this.ct = ct;
		this.partQueue = partQueue;
		this.ipQueue = ipQueue;
	}

	public void run() {
		System.out.println("原始任务队列长度"+partQueue.size());
		try{
			while(!ct.getTi().finished){
				if(!partQueue.isEmpty() && partTaskNum<=taskLimit){
					int partNum = partQueue.poll();
					if(ipQueue.isEmpty()){
						System.out.println("ipQueue空了");
					}
					String resourse = ipQueue.take();//此处阻塞，抛InterrptedExcetion异常
    				new Thread(new FilePartTask(ct.getTi(),partNum,resourse,this)).start();
    				partTaskNum++;
					System.out.println("启动了下载文件片"+partNum+"的线程");
				}
				Thread.sleep(1000);
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("结束了");
	}
	
	
/*	public BlockingQueue<Integer> getPartQueue() {
		return partQueue;
	}

	public BlockingQueue<IPResourse> getIpQueue() {
		return ipQueue;
	}*/
	public BlockingQueue<String> getIpQueue() {
		return ipQueue;
	}
	
}
