package jkademlia.transfer;


/*
 * ��Manager�๦����Ϊҵ���߼��㣨�ļ����䣬��㷢�֣���ͼ�ν����(MainFrame)����֮����ϵ
 */

import java.util.ArrayList;
import java.util.HashMap;

import jkademlia.gui.EDragon;
import jkademlia.transfer.client.Client;

public class Manager implements Runnable{
	private EDragon eDragon = null;
	public ArrayList<Client> tasks = null;
	public HashMap<String, Integer> sizeMap; //Map�ļ����Ͷ�Ӧ����ʵ��
	
	public Manager(EDragon eDragon){

		this.eDragon = eDragon;
		tasks = new ArrayList<Client>();
		sizeMap = new HashMap<String,Integer>();
	}
	
	public void run(){
		Client aTask = null;
/*		for(int i=0;i<tasks.size();i++){
			aTask = tasks.get(i);
			int rowNo = mf.rowMap.get(aTask.getFileName());

			mf.defaultModel.setValueAt(aTask.getFileTransSpeed(), rowNo, 2);
			mf.defaultModel.notify();
		}*/
		while(true){
			int speedK;
			int speedM;
			
			for(int i=0;i<tasks.size();i++){
				aTask = tasks.get(i);
				sizeMap.put(aTask.getFileName(),aTask.getTi().receivedFileLength);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			
			}
			for(int i=0;i<tasks.size();i++){
				aTask = tasks.get(i);
				if(aTask.getTi().finished == false){
					speedK = (int)((aTask.getTi().receivedFileLength - sizeMap.get(aTask.getFileName()))/1024);
					//��ʾ�ٶ�ֵ
/*					if(speedK>1024){
						speedM = speedK/1024;
						mf.defaultModel.setValueAt(speedM+"MB/s", i, 2);//??��ʽ�������λС��
					}else{
						mf.defaultModel.setValueAt(speedK+"KB/s", i, 2);
					}*/
					eDragon.defaultModel.setValueAt(speedK+"KB/s", i, 2);
					//��ʾ����ֵ
					System.out.println(aTask.getTi().receivedFileLength+"--------=-=-=-=");
					System.out.println(aTask.getFileLength()+"--------=-=-=-=");
					eDragon.defaultModel.setValueAt((int)((double)aTask.getTi().receivedFileLength/aTask.getFileLength()*100) + "%",i,3);
					
					
				}else{
					tasks.remove(i);
					eDragon.defaultModel.removeRow(i);
				}
			}
		}
	}
	
	
	
	
}

