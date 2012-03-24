package jkademlia.transfer.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import jkademlia.data.DatabaseManager;
import jkademlia.gui.EDragon;

public class Client implements Runnable{
	public final static int PARTSIZE = 1024 * 1024 * 3;// 按3M大小分片

	private String fileName;
	private int filePartNum;
	private String savePath;// 待下载文件的保存目录
	private String fileInfoPath;
	private int fileTransSpeed;
	public List<String> ipAndCreditValue;

	private TaskInfo ti = null;// 储存每个文件片下载线程的完成情况，辅助TaskListener线程

	private ArrayBlockingQueue<Integer> partQueue;
//	private ArrayBlockingQueue<IPResourse> ipQueue;
	private ArrayBlockingQueue<String> ipQueue;

	private int fileSize;
	public EDragon eDragon;
	public File fileInfo;

	public Client(EDragon eDragon,List<String> ipAndCreditValue,String fileName,int fileSize) { // 最后项目整合中要填进包括文件名，文件大小，IP地址等在内的构造参数，现在为手动设置

		System.out.println("new出一个ClientTest来");
		this.eDragon = eDragon;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.ipAndCreditValue = ipAndCreditValue;
		// 在向外界server申请时间片之前应该已知道整个文件的大小和所分的片数,待修改\
		filePartNum = (int) (fileSize / PARTSIZE + 1);
		System.out.println("文件大小为"+fileSize);
		System.out.println("文件分了"+filePartNum+"片");
		savePath = "D:\\";

		
		int index = fileName.lastIndexOf(".");
		fileInfoPath = savePath + fileName.substring(0, index) + ".info";// 得到临时文件的存放位置

		ti = new TaskInfo(fileName, fileSize, filePartNum, savePath,
				fileInfoPath);
		
		TaskListener tl = new TaskListener(ti,this);
		Thread t = new Thread(tl);
		System.out.println("TaskListener已经	装在进t线程中，等待启动");
		t.start();
		
		partQueue = new ArrayBlockingQueue<Integer>(500);// 实例化盛放文件片序号的队列
		ipQueue = new ArrayBlockingQueue<String>(20);// 实例化盛放ip资源的队列
		
		for(int i=0;i<ipAndCreditValue.size();i++){
			String ipAndCredit = ipAndCreditValue.get(i);
			int index_1 = ipAndCredit.indexOf("@");
			
			double credit = Double.parseDouble(ipAndCredit.substring(index_1+1, ipAndCredit.length()-1));
			System.out.println("得到文件提供方的信任之为"+credit);
			
			/*
			 * 测试用，不考虑间接访问控制
			 */
			if(credit >= 0){
				
			/*
			 * 加间接访问控制，客户端主动检测访问权限
			 */
//			double requiedCredit = Double.parseDouble(ipAndCredit.substring(0, index_1));
//			if(credit >= 0 && DatabaseManager.getCredit() >= requiedCredit){
				
				ipQueue.add(ipAndCreditValue.get(i));
			} else{
				System.out.println("文件提供方信任值太低,不予考虑下载");
			}
//			
			System.out.println("初始化时候"+ipAndCreditValue.get(i)+"进入ip队列");
		}
		System.out.println("最终开始下载前，ipQueue中有"+ipQueue.size()+"个ip资源");
		
	}

	public void run() {
		// 为了实现断点续传，这里要读入下载信息文件获取已经下载和未下载的文件片
		DataOutputStream dos = null;
		DataInputStream dis = null;
		fileInfo = new File(fileInfoPath);
		try {
			if (!fileInfo.exists()) {
				// System.out.println("临时文件不存在!");
				dos = new DataOutputStream(new FileOutputStream(fileInfo));
				for (int i = 1; i <= filePartNum; i++) {
					dos.writeBoolean(false);
					partQueue.add(i);
					System.out.println("初始化时,加进partQueue队列一个"+i);
				}
			}else {
				dis = new DataInputStream(new FileInputStream(fileInfo));
				boolean sign;
				for (int i = 1; i <= filePartNum; i++) {
					sign = dis.readBoolean();
					if (sign == true) {
						this.ti.setPartCompleted(i);
					} else {
						partQueue.add(i);
						System.out.println("初始化时,加进partQueue队列一个"+i);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dos != null)
					dos.close();
				if (dis != null)
					dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		// 启动片任务调动线程，对ip资源和文件片任务进行动态管理
		System.out.println("开始启动PartTaskManager");
		new Thread(new PartTaskManager(this, partQueue, ipQueue)).start();

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileLength() {
		return fileSize;
	}

	public void setFileLength(int fileLength) {
		this.fileSize = fileLength;
	}

	public TaskInfo getTi() {
		return ti;
	}

	public void setTi(TaskInfo ti) {
		this.ti = ti;
	}

	public int getFileTransSpeed() {
		return fileTransSpeed;
	}
	

	public void setFileTransSpeed(int fileTransSpeed) {
		this.fileTransSpeed = fileTransSpeed;
	}

}
