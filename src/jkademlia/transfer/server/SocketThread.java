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

	public final static int PARTSIZE = 1024 * 1024 * 3;// ��3M��С��Ƭ

	public final static int STATUS_INFO = 0x01; // �ļ�������״̬
	public final static int STATUS_CONTENT = 0x02; // �ļ����ݴ���״̬
	public final static int STATUS_EVALUATION = 0x03;// ����״̬
	public final static int STATUS_END = 0x04; // �������״̬

	public final static int CONNECT_COUNT = 0x05;
	public final static int CONNECT_SUCCESS = 0x06;

	public int fileTransStatus = 0x0; // �ļ�����״̬
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
			dis = new DataInputStream(s.getInputStream());// ���socket��������
			dos = new DataOutputStream(s.getOutputStream());// ���socket�������
			fileTransStatus = STATUS_INFO;
			dataBuf = new byte[PARTSIZE + 100];
		} catch (IOException e) {
			e.printStackTrace();
		}

		/**
		 * ���ӽ�����Server��connect_countֵ+1
		 */
		updateConnectValue(CONNECT_COUNT);

	}

	// public synchronized void run() {
	public void run() {
		synchronized (this) {
			while (taskRunning) {
				switch (fileTransStatus) {
				case STATUS_INFO:
				case STATUS_EVALUATION: { // ����case����һ�δ��룬ͨ��fileTransStatus��upPackage()�н�������
					// �����ļ�����Ƭ�����Ϣ
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
					// �����ļ�Ƭ����
					if (fileTransStatus == STATUS_CONTENT) {
						aFile = new File(Manager.SHARE_LIST + "\\" + fileName);
						System.out.println("�ͻ����������ص��ļ���Ϊ:" + Manager.SHARE_LIST
								+ "/" + fileName);
						try {
							fDis = new DataInputStream(new FileInputStream(
									aFile));
							fDis.skip((long) (filePartNum - 1) * PARTSIZE);
							System.out.println("���ļ���Ϊ" + fileName
									+ "���ļ��������ӣ����͸��ļ��ĵ�" + filePartNum
									+ "Ƭ����...");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						int length = readFromFile(dataBuf, 0, PARTSIZE);// lengthһ��ΪPARTSIZE��С���������һƬʱ����

						writeToSocket(doPackage(dataBuf, length));
						fileTransStatus = STATUS_EVALUATION;
					}
					break;
				}

				case STATUS_END: {
					System.out.println("Server--->��Ҫ����ļ����ݴ������");
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
				byte[] data = new byte[1000];// ��ʱ����ļ������ֽ�����
				dis.read(data, 0, fileNameLength);
				fileName = new String(data, 0, fileNameLength);
				System.out.println("Server--->�ļ���Ϊ" + fileName);
				filePartNum = dis.readInt();
				System.out.println("Server--->�ļ�Ƭ���Ϊ" + filePartNum);
				fileTransStatus = STATUS_CONTENT;
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		case STATUS_EVALUATION: {
			try {
				int evaluation = dis.readInt();
				System.out.println("Server�˽��ܵ������۽����Ϊ" + evaluation);
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
			bufDos.writeInt(length);// д���ļ�Ƭ��С
			bufDos.write(dataBuf, 0, length);// д���ļ�Ƭ����
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
		// System.out.println("value.dat�ļ����ڣ����޳�ʼ����ֵ");
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
		// System.out.println("���º�����ֵΪ"+newEvaluation);
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
		// System.out.println("value.dat�ļ����ڣ����޳�ʼ����ֵ");
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
		// System.out.println("���º�connect_countֵΪ"+newValue);
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
		// System.out.println("connect_success.dat�ļ����ڣ����޳�ʼ����ֵ");
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
		// System.out.println("���º�connect_successֵΪ"+newValue);
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
