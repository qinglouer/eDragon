package jkademlia.transfer.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.sql.rowset.spi.SyncResolver;
import javax.xml.transform.Source;

public class FilePartTask implements Runnable {

	public static final int PARTSIZE = 1024 * 1024 * 3;
	
	public static final int STATUS_INFO = 0x01;
	public static final int STATUS_EVALUATION = 0x02;

	private TaskInfo ti;
	private int currentPartNum;

	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private RandomAccessFile raf = null;
	
	private long startTime;
	private long estimatedTime;
	
	byte[] dataBuf;
	private String IP;
	
	private PartTaskManager ptManager = null;
//	private IPResourse resourse;
	private String resourse;

//	public FilePartTask(TaskInfo ti,  int currentPartNum,IPResourse resourse,PartTaskManager ptManager) {
	public FilePartTask(TaskInfo ti,  int currentPartNum,String resourse,PartTaskManager ptManager) {
		this.ti = ti;
		this.currentPartNum = currentPartNum;
		this.resourse = resourse;
		int index = resourse.indexOf("@");
		this.IP = resourse.substring(0,index);
		this.ptManager = ptManager;
		
		//ȡ��һ��resourse��Ҫ�ȶ���æ�̶Ƚ��д����������Ƿ�����ipQueue��
		//���resourse�дﵽ��3�����ӵı��ͣ�����busyֵΪtrue������ipQueue���Ƴ�
/*		if(resourse.connection == 2){
			resourse.setBusy(true);
			ptManager.getIpQueue().remove(resourse);
		}
		resourse.connection++;*/
	}

	public synchronized void run() {
		
		startTime = System.nanoTime();
		Socket s = null;
		try {
			s = new Socket(IP, 5001);
			System.out.println("Client--->������Server...");
			dos = new DataOutputStream(s.getOutputStream());// �����
			dis = new DataInputStream(s.getInputStream()); // ������
			writeToSocket(doPackage(STATUS_INFO));

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ��ʼ�������ݲ�д����ʱ�ļ�
		dataBuf = new byte[PARTSIZE + 100];
		try {
			int packageLength = dis.readInt();
			int len = dis.read(dataBuf, 0, packageLength);
			while (len < packageLength) {
				len = len + dis.read(dataBuf, len, packageLength - len);// ????
			}
			upPackage(dataBuf);
			writeToSocket(doPackage(STATUS_EVALUATION));
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
//		resourse.connection--;
		if(ptManager.getIpQueue().contains(resourse)){
			System.out.println("resourse�У����ü�..................");
		}else{
			System.out.println("resourse���¼ӵ�ipQueue����...................");
			ptManager.getIpQueue().add(resourse);
			
		}
		
		ptManager.partTaskNum--;

		

	}

	private void upPackage(byte[] dataBuf) {
		ByteArrayInputStream bais = new ByteArrayInputStream(dataBuf);
		DataInputStream dis = new DataInputStream(bais);
		try {
			int filePartLength = dis.readInt();
			byte[] filePartData = new byte[PARTSIZE];// ��ʱ����ļ������ֽ�����
			dis.read(filePartData, 0, filePartLength);
//			System.out.println("Client--->�ļ�Ƭ���ݶ�������,�±�д���ļ�...");
			raf = new RandomAccessFile(ti.getSavePath() + currentPartNum + "_"
					+ ti.getFileName(), "rws");
			int signLength = 0;
			int blockSize = 1024*10*2;
			while (signLength < filePartLength) {
				if((signLength + blockSize)<=filePartLength){
					raf.write(filePartData, signLength, blockSize);
					ti.receivedFileLength += blockSize;
				}else{
					raf.write(filePartData,signLength,(filePartLength-signLength));
					ti.receivedFileLength += (filePartLength-signLength);
				}
				signLength += blockSize;
			}
			
			
			System.out.println("Client--->�ļ�Ƭ�������...");
			ti.setPartCompleted(currentPartNum);//�����ļ�Ƭ����״̬����Ϊ�����
			ti.updateFileInfo();//����fileInfo��¼�ļ������ϵ���������
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
				bais.close();
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] doPackage(int type) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream bufDos = new DataOutputStream(buf);
		DataOutputStream baosDos = new DataOutputStream(baos);

		try{
			if(type == STATUS_INFO){
				bufDos.writeInt(ti.getFileName().length());// д���ļ�������
				bufDos.write(ti.getFileName().getBytes());// д���ļ����ַ�����
				bufDos.writeInt(currentPartNum); // д��Ҫ�����ļ�Ƭ���
				
				baosDos.writeInt(buf.toByteArray().length);
				baosDos.write(buf.toByteArray());
			}else if(type == STATUS_EVALUATION){
				estimatedTime = System.nanoTime()-startTime;
				long m = estimatedTime/1000000;
				System.out.println("���뼶ʱ���mΪ:"+m);
				long speed = (3*1024*1000)/m;
				int evaluation = (int)speed;//Լ������ֵ��Ϊƽ���ٶ�ֵȡ��
				System.out.println("�����ٶ�Ϊ:"+speed+"k/s");
				System.out.println("��������ֵΪ:"+evaluation);
				
				System.out.println("Clien�˿�ʼд����������");
				bufDos.writeInt(evaluation);//д�뱾�δ��������ֵ
				baosDos.writeInt(buf.toByteArray().length);
				baosDos.write(buf.toByteArray());
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return baos.toByteArray();
	}

	private void writeToSocket(byte[] buf) {
		try {
			dos.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
