package jkademlia.transfer;


/*
 * 此Manager类功能是为业务逻辑层（文件传输，结点发现）和图形界面层(MainFrame)两层之间搭建联系
 */

import java.util.ArrayList;
import java.util.HashMap;

import jkademlia.gui.EDragon;
import jkademlia.transfer.client.Client;

public class Manager implements Runnable{
	private EDragon eDragon = null;
	public ArrayList<Client> tasks = null;
	public HashMap<String, Integer> sizeMap; //Map文件名和对应任务实例
	
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
					//显示速度值
/*					if(speedK>1024){
						speedM = speedK/1024;
						mf.defaultModel.setValueAt(speedM+"MB/s", i, 2);//??格式化输出两位小数
					}else{
						mf.defaultModel.setValueAt(speedK+"KB/s", i, 2);
					}*/
					eDragon.defaultModel.setValueAt(speedK+"KB/s", i, 2);
					//显示进度值
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

