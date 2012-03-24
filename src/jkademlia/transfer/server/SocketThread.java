package jkademlia.transfer.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import jkademlia.data.DatabaseManager;

public class SocketThread extends Thread {

	public final static int PARTSIZE = 1024 * 1024 * 3;// 按3M大小分片

	public final static int STATUS_INFO = 0x01; // 文件名传输状态
	public final static int STATUS_CONTENT = 0x02; // 文件内容传输状态
	public final static int STATUS_EVALUATION = 0x03;// 评价状态
	public final static int STATUS_END = 0x04; // 传输完毕状态

	public final static int CONNECT_COUNT = 0x05;
	public final static int CONNECT_SUCCESS = 0x06;

	public int fileTransStatus = 0x0; // 文件传输状态
	private byte[] dataBuf;
	private Socket s = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private DataInputStream fDis = null;

	private File aFile;
	private String fileName;
	private int filePartNum;
	private boolean taskRunning = true;

	public SocketThread(Socket s) {
		this.s = s;
		try {
			dis = new DataInputStream(s.getInputStream());// 获得socket的输入流
			dos = new DataOutputStream(s.getOutputStream());// 获得socket的输出流
			fileTransStatus = STATUS_INFO;
			dataBuf = new byte[PARTSIZE + 100];
		} catch (IOException e) {
			e.printStackTrace();
		}

		/**
		 * 连接建立，Server端connect_count值+1
		 */
		updateConnectValue(CONNECT_COUNT);

	}

	// public synchronized void run() {
	public void run() {
		synchronized (this) {
			while (taskRunning) {
				switch (fileTransStatus) {
				case STATUS_INFO:
				case STATUS_EVALUATION: { // 两个case共用一段代码，通过fileTransStatus在upPackage()中进行区分
					// 接受文件名和片序号信息
					try {
						int packageLength = dis.readInt();
						int len = dis.read(dataBuf, 0, packageLength);
						while (len < packageLength) {
							len = len
									+ dis.read(dataBuf, len, packageLength
											- len);//
						}
						upPackage(dataBuf);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				case STATUS_CONTENT: {
					// 发送文件片内容
					if (fileTransStatus == STATUS_CONTENT) {
						aFile = new File(Manager.SHARE_LIST + "\\" + fileName);
						System.out.println("客户端请求下载的文件名为:" + Manager.SHARE_LIST
								+ "/" + fileName);
						try {
							fDis = new DataInputStream(new FileInputStream(
									aFile));
							fDis.skip((long) (filePartNum - 1) * PARTSIZE);
							System.out.println("和文件名为" + fileName
									+ "的文件建立连接，发送该文件的第" + filePartNum
									+ "片内容...");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						int length = readFromFile(dataBuf, 0, PARTSIZE);// length一般为PARTSIZE大小，但遇最后一片时可能

						writeToSocket(doPackage(dataBuf, length));
						fileTransStatus = STATUS_EVALUATION;
					}
					break;
				}

				case STATUS_END: {
					System.out.println("Server--->所要求的文件内容传输完毕");
					taskRunning = false;
					updateConnectValue(CONNECT_SUCCESS);
					try {
						fDis.close();
						dos.close();
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				}

			}
		}
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void upPackage(byte[] dataBuf) {
		ByteArrayInputStream bais = new ByteArrayInputStream(dataBuf);
		DataInputStream dis = new DataInputStream(bais);

		switch (fileTransStatus) {
		case STATUS_INFO: {
			try {
				int fileNameLength = dis.readInt();
				byte[] data = new byte[1000];// 暂时存放文件名的字节数组
				dis.read(data, 0, fileNameLength);
				fileName = new String(data, 0, fileNameLength);
				System.out.println("Server--->文件名为" + fileName);
				filePartNum = dis.readInt();
				System.out.println("Server--->文件片序号为" + filePartNum);
				fileTransStatus = STATUS_CONTENT;
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		case STATUS_EVALUATION: {
			try {
				int evaluation = dis.readInt();
				System.out.println("Server端接受到了评价结果，为" + evaluation);
				updateTrustedValue(evaluation);
				fileTransStatus = STATUS_END;
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		}
	}

	private byte[] doPackage(byte[] dataBuf, int length) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream bufDos = new DataOutputStream(buf);
		DataOutputStream baosDos = new DataOutputStream(baos);

		try {
			bufDos.writeInt(length);// 写入文件片大小
			bufDos.write(dataBuf, 0, length);// 写入文件片数据
			baosDos.writeInt(buf.toByteArray().length);
			baosDos.write(buf.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	private int readFromFile(byte[] dataBuf, int off, int length) {
		int len = 0;
		try {
			len = fDis.read(dataBuf, off, length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return len;
	}

	private void writeToSocket(byte[] data) {
		try {
			dos.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void updateTrustedValue(int evaluation) {
		// DataOutputStream dos = null;
		// DataInputStream dis = null;
		//
		// File valueFile = new File("value.dat");
		// if(valueFile.exists()){
		// if(valueFile.length() == 0){
		// System.out.println("value.dat文件存在，但无初始信任值");
		// try {
		// dos = new DataOutputStream(new FileOutputStream(valueFile));
		// dos.writeInt(0);
		// dos.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// try {
		// dis = new DataInputStream(new FileInputStream(valueFile));
		// int oldEvaluation = dis.readInt();
		// int newEvaluation = (oldEvaluation+evaluation)/2;
		// dos = new DataOutputStream(new FileOutputStream(valueFile));
		// dos.writeInt(newEvaluation);
		// System.out.println("更新后信任值为"+newEvaluation);
		// dis.close();
		// dos.close();
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		DatabaseManager.updateSpeed(evaluation);

	}

	private void updateConnectValue(int i) {
		// DataOutputStream dos = null;
		// DataInputStream dis = null;
		// if(i == CONNECT_COUNT){
		// File connectCountFile = new File("connect_count.dat");
		// if(connectCountFile.exists()){
		// if(connectCountFile.length() == 0){
		// System.out.println("value.dat文件存在，但无初始信任值");
		// try {
		// dos = new DataOutputStream(new FileOutputStream(connectCountFile));
		// dos.writeInt(0);
		// dos.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// try {
		// dis = new DataInputStream(new FileInputStream(connectCountFile));
		// int oldValue = dis.readInt();
		// int newValue = oldValue+1;
		// dos = new DataOutputStream(new FileOutputStream(connectCountFile));
		// dos.writeInt(newValue);
		// System.out.println("更新后connect_count值为"+newValue);
		// dis.close();
		// dos.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }else{
		// File connectSuccessFile = new File("connect_success.dat");
		// if(connectSuccessFile.exists()){
		// if(connectSuccessFile.length() == 0){
		// System.out.println("connect_success.dat文件存在，但无初始信任值");
		// try {
		// dos = new DataOutputStream(new FileOutputStream(connectSuccessFile));
		// dos.writeInt(0);
		// dos.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// try {
		// dis = new DataInputStream(new FileInputStream(connectSuccessFile));
		// int oldValue = dis.readInt();
		// int newValue = oldValue+1;
		// dos = new DataOutputStream(new FileOutputStream(connectSuccessFile));
		// dos.writeInt(newValue);
		// System.out.println("更新后connect_success值为"+newValue);
		// dis.close();
		// dos.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// }
		if (i == CONNECT_COUNT) {
			DatabaseManager.addConnectSuccess();
		} else {
			DatabaseManager.addTransferSuccess();
		}
	}
}
