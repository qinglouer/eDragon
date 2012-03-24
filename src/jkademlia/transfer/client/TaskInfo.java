package jkademlia.transfer.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class  TaskInfo {
	private String fileName;
	private int filePartNum;
	private String savePath;
	private boolean[] parts;
	private DataOutputStream dos;
	private String fileInfoPath;
	public boolean finished = false;//标志所有文件片已经下载完成，finished置为true

	public String getFileInfoPath() {
		return fileInfoPath;
	}

	public void setFileInfoPath(String fileInfoPath) {
		this.fileInfoPath = fileInfoPath;
	}

	public int fileLength;
	public int receivedFileLength;
	public File fileInfo;



	public TaskInfo(String fileName,int fileLength,int filePartNum,String savePath,String fileInfoPath){
		this.fileName = fileName;
		this.filePartNum = filePartNum;
		this.savePath = savePath;
		this.fileInfoPath = fileInfoPath;
		parts = new boolean[filePartNum];
		this.fileLength = fileLength;
		fileInfo = new File(fileInfoPath);
		for(int i=0;i<filePartNum;i++){
			parts[i] = false;
		}
	}
	
	public synchronized void updateFileInfo(){
		try{
			dos = new DataOutputStream(new FileOutputStream(fileInfo));
			for(int i=0;i<filePartNum;i++){
				dos.writeBoolean(parts[i]);
			}
			dos.writeInt(this.receivedFileLength);
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(dos != null){
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized void setPartCompleted(int i){
		parts[i-1] = true;//实际的第i片相当于数组中的第i-1个变量
	}
	
	public boolean getPartCompleted(int i){
		return parts[i-1];
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFilePartNum() {
		return filePartNum;
	}

	public void setFilePartNum(int filePartNum) {
		this.filePartNum = filePartNum;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public boolean[] getParts() {
		return parts;
	}

	public void setParts(boolean[] parts) {
		this.parts = parts;
	}
	
	public int getFileLength() {
		return fileLength;
	}

	public void setFileLength(int fileLength) {
		this.fileLength = fileLength;
	}
}
