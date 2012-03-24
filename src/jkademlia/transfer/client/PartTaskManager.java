package jkademlia.transfer.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.*;

/*
 * ��PartTaskManager�����ɽ������ص�ip��Դ�ʹ����ص��ļ�Ƭ�������������������������̬�����������
 */
public class PartTaskManager implements Runnable{
	
	private Client ct = null;
	private ArrayBlockingQueue<Integer> partQueue = null;
	private ArrayBlockingQueue<String> ipQueue = null;
	public int partTaskNum = 0;
	public static int taskLimit=5;
	
//	public PartTaskManager(ClientTest ct,ArrayBlockingQueue<Integer> partQueue,ArrayBlockingQueue<IPResourse>ipQueue){
	public PartTaskManager(Client ct,ArrayBlockingQueue<Integer> partQueue,ArrayBlockingQueue<String> ipQueue){
		System.out.println("new����һ��PartTaskManagerʵ��");
		this.ct = ct;
		this.partQueue = partQueue;
		this.ipQueue = ipQueue;
	}

	public void run() {
		System.out.println("ԭʼ������г���"+partQueue.size());
		try{
			while(!ct.getTi().finished){
				if(!partQueue.isEmpty() && partTaskNum<=taskLimit){
					int partNum = partQueue.poll();
					if(ipQueue.isEmpty()){
						System.out.println("ipQueue����");
					}
					String resourse = ipQueue.take();//�˴���������InterrptedExcetion�쳣
    				new Thread(new FilePartTask(ct.getTi(),partNum,resourse,this)).start();
    				partTaskNum++;
					System.out.println("�����������ļ�Ƭ"+partNum+"���߳�");
				}
				Thread.sleep(1000);
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("������");
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
