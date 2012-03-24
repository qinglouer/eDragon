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
	public final static int PARTSIZE = 1024 * 1024 * 3;// ��3M��С��Ƭ

	private String fileName;
	private int filePartNum;
	private String savePath;// �������ļ��ı���Ŀ¼
	private String fileInfoPath;
	private int fileTransSpeed;
	public List<String> ipAndCreditValue;

	private TaskInfo ti = null;// ����ÿ���ļ�Ƭ�����̵߳�������������TaskListener�߳�

	private ArrayBlockingQueue<Integer> partQueue;
//	private ArrayBlockingQueue<IPResourse> ipQueue;
	private ArrayBlockingQueue<String> ipQueue;

	private int fileSize;
	public EDragon eDragon;
	public File fileInfo;

	public Client(EDragon eDragon,List<String> ipAndCreditValue,String fileName,int fileSize) { // �����Ŀ������Ҫ��������ļ������ļ���С��IP��ַ�����ڵĹ������������Ϊ�ֶ�����

		System.out.println("new��һ��ClientTest��");
		this.eDragon = eDragon;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.ipAndCreditValue = ipAndCreditValue;
		// �������server����ʱ��Ƭ֮ǰӦ����֪�������ļ��Ĵ�С�����ֵ�Ƭ��,���޸�\
		filePartNum = (int) (fileSize / PARTSIZE + 1);
		System.out.println("�ļ���СΪ"+fileSize);
		System.out.println("�ļ�����"+filePartNum+"Ƭ");
		savePath = "D:\\";

		
		int index = fileName.lastIndexOf(".");
		fileInfoPath = savePath + fileName.substring(0, index) + ".info";// �õ���ʱ�ļ��Ĵ��λ��

		ti = new TaskInfo(fileName, fileSize, filePartNum, savePath,
				fileInfoPath);
		
		TaskListener tl = new TaskListener(ti,this);
		Thread t = new Thread(tl);
		System.out.println("TaskListener�Ѿ�	װ�ڽ�t�߳��У��ȴ�����");
		t.start();
		
		partQueue = new ArrayBlockingQueue<Integer>(500);// ʵ����ʢ���ļ�Ƭ��ŵĶ���
		ipQueue = new ArrayBlockingQueue<String>(20);// ʵ����ʢ��ip��Դ�Ķ���
		
		for(int i=0;i<ipAndCreditValue.size();i++){
			String ipAndCredit = ipAndCreditValue.get(i);
			int index_1 = ipAndCredit.indexOf("@");
			
			double credit = Double.parseDouble(ipAndCredit.substring(index_1+1, ipAndCredit.length()-1));
			System.out.println("�õ��ļ��ṩ��������֮Ϊ"+credit);
			
			/*
			 * �����ã������Ǽ�ӷ��ʿ���
			 */
			if(credit >= 0){
				
			/*
			 * �Ӽ�ӷ��ʿ��ƣ��ͻ�������������Ȩ��
			 */
//			double requiedCredit = Double.parseDouble(ipAndCredit.substring(0, index_1));
//			if(credit >= 0 && DatabaseManager.getCredit() >= requiedCredit){
				
				ipQueue.add(ipAndCreditValue.get(i));
			} else{
				System.out.println("�ļ��ṩ������ֵ̫��,���迼������");
			}
//			
			System.out.println("��ʼ��ʱ��"+ipAndCreditValue.get(i)+"����ip����");
		}
		System.out.println("���տ�ʼ����ǰ��ipQueue����"+ipQueue.size()+"��ip��Դ");
		
	}

	public void run() {
		// Ϊ��ʵ�ֶϵ�����������Ҫ����������Ϣ�ļ���ȡ�Ѿ����غ�δ���ص��ļ�Ƭ
		DataOutputStream dos = null;
		DataInputStream dis = null;
		fileInfo = new File(fileInfoPath);
		try {
			if (!fileInfo.exists()) {
				// System.out.println("��ʱ�ļ�������!");
				dos = new DataOutputStream(new FileOutputStream(fileInfo));
				for (int i = 1; i <= filePartNum; i++) {
					dos.writeBoolean(false);
					partQueue.add(i);
					System.out.println("��ʼ��ʱ,�ӽ�partQueue����һ��"+i);
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
						System.out.println("��ʼ��ʱ,�ӽ�partQueue����һ��"+i);
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
		
		// ����Ƭ��������̣߳���ip��Դ���ļ�Ƭ������ж�̬����
		System.out.println("��ʼ����PartTaskManager");
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
