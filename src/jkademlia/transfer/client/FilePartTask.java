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
		
		//取出一个resourse后，要先对起繁忙程度进行处理，决定其是否留在ipQueue中
		//如果resourse中达到了3个连接的饱和，则置busy值为true，并从ipQueue中移除
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
			System.out.println("Client--->连接上Server...");
			dos = new DataOutputStream(s.getOutputStream());// 输出流
			dis = new DataInputStream(s.getInputStream()); // 输入流
			writeToSocket(doPackage(STATUS_INFO));

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 开始接收数据并写入临时文件
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
			System.out.println("resourse有，不用加..................");
		}else{
			System.out.println("resourse重新加到ipQueue中了...................");
			ptManager.getIpQueue().add(resourse);
			
		}
		
		ptManager.partTaskNum--;

		

	}

	private void upPackage(byte[] dataBuf) {
		ByteArrayInputStream bais = new ByteArrayInputStream(dataBuf);
		DataInputStream dis = new DataInputStream(bais);
		try {
			int filePartLength = dis.readInt();
			byte[] filePartData = new byte[PARTSIZE];// 暂时存放文件名的字节数组
			dis.read(filePartData, 0, filePartLength);
//			System.out.println("Client--->文件片内容读进来了,下边写入文件...");
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
			
			
			System.out.println("Client--->文件片传输完成...");
			ti.setPartCompleted(currentPartNum);//将此文件片下载状态设置为已完成
			ti.updateFileInfo();//更新fileInfo记录文件，供断点续传调用
			
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
				bufDos.writeInt(ti.getFileName().length());// 写入文件名长度
				bufDos.write(ti.getFileName().getBytes());// 写入文件名字符数组
				bufDos.writeInt(currentPartNum); // 写入要传的文件片序号
				
				baosDos.writeInt(buf.toByteArray().length);
				baosDos.write(buf.toByteArray());
			}else if(type == STATUS_EVALUATION){
				estimatedTime = System.nanoTime()-startTime;
				long m = estimatedTime/1000000;
				System.out.println("毫秒级时间差m为:"+m);
				long speed = (3*1024*1000)/m;
				int evaluation = (int)speed;//约定评估值即为平均速度值取整
				System.out.println("传输速度为:"+speed+"k/s");
				System.out.println("返回评估值为:"+evaluation);
				
				System.out.println("Clien端开始写入评估数据");
				bufDos.writeInt(evaluation);//写入本次传输的评估值
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
