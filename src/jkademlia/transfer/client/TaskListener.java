package jkademlia.transfer.client;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TaskListener implements Runnable {

	public static final int PARTSIZE = 1024 * 1024 * 3;
	private TaskInfo ti = null;
	private Client c = null;
	

	public TaskListener(TaskInfo ti,Client c) {
		this.c = c;
		this.ti = ti;
	}

	public void run() {
		System.out.println("Client--->任务监听线程已经启动");
		
		//循环，直至所有分任务均完成
		for (int i = 1; i <= ti.getFilePartNum(); i++) {
			if (ti.getPartCompleted(i) == false)
				i--;
			continue;
		}
		System.out.println("Client--->所有文件片已经下载完成...");
		
		//TaskInfo中下载完成标记finished改为true
		ti.finished = true;
		
		System.out.println("Client--->进行文件合并...");
		TaskMerge();
	}


	private void TaskMerge() {
		File tempFile = null;
		File fileInfo = null;
		DataInputStream dis = null;
		RandomAccessFile raf = null;
		byte[] dataBuf = new byte[PARTSIZE];
		
		try {
			raf = new RandomAccessFile(ti.getSavePath()
					 + ti.getFileName(), "rws");
			for (int i = 1; i <= ti.getFilePartNum(); i++) {
				tempFile = new File(ti.getSavePath() + i + "_"
						+ ti.getFileName());
				dis = new DataInputStream(new FileInputStream(tempFile));
				try {
					int len = dis.read(dataBuf, 0, PARTSIZE);
					raf.write(dataBuf, 0, len);
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					dis.close();
					if (tempFile.exists()) {
						boolean bool = tempFile.delete();
						System.out.println(bool);
					} else {
						System.out.println("不存在");
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				dis.close();
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Client--->文件传输完毕...");

		
		//删除临时信息文件
		fileInfo = new File(ti.getFileInfoPath());
		fileInfo.delete();
		
		
		int response = showCreditWindow();
		updateCredit(response);

	}
	
	public int showCreditWindow(){
		
		Object[] options = {"不满意","一般","满意"};
		
		int response = JOptionPane.showOptionDialog(null,"评价","任务下载完成，请评价：",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
		
		return response-1;
	}
	
	private void updateCredit(int response){

		for(int i=0;i<c.ipAndCreditValue.size();i++){
			String ipAndCredit = c.ipAndCreditValue.get(i);
			int index = ipAndCredit.indexOf("@");
			String IP = ipAndCredit.substring(0,index);
			sendCredit(IP,response);
		}
		
		
	}
	
	private void sendCredit(String IP,int response){
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		/*
		 *int转字节数组
		 */
		byte[] credit = new byte[4];
		credit[3] = (byte)(response & 0xff);
		credit[2] = (byte)((response >> 8) & 0xff);
		credit[1] = (byte)((response >> 16) & 0xff);
		credit[0] = (byte)((response >> 24) & 0xff);
		try {
			DatagramPacket dp = new DatagramPacket(credit, credit.length
					,new InetSocketAddress(IP,5001));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
