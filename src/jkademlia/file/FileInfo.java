package jkademlia.file;

import java.io.File;

public class FileInfo {
	private File file;
	private String[] fileName;
	private String shareList;
	
	public FileInfo(){
		shareList = System.getProperty("edragon.sharelist");
	}
	
	public String[] getFileName(){
		file = new File(shareList); //共享文件夹目录
		fileName = file.list();
		
		return fileName;		
	}
	
	public String getFileSize(String stringFile ){
		File shareFile = new File(shareList + "/" + stringFile);
		System.out.println("!!!!!!"+shareFile);
		SizeOfFile sFile = new SizeOfFile(shareFile);
//		String sSize = sFile.getLongSize();
//		return sSize;
		return Long.toString(shareFile.length());
//		return sFile.getSizeFile();
	}
}
