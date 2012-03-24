package jkademlia.file;

import java.io.File;
import java.math.BigDecimal;

public class SizeOfFile {
	// bt�ֽڲο���
	public static final long SIZE_BT = 1024L;
	// KB�ֽڲο���
	public static final long SIZE_KB = SIZE_BT * 1024L;
	// MB�ֽڲο���
	public static final long SIZE_MB = SIZE_KB * 1024L;
	// GB�ֽڲο���
	public static final long SIZE_GB = SIZE_MB * 1024L;
	// TB�ֽڲο���
	public static final long SIZE_TB = SIZE_GB * 1024L;

	private static final int SACLE = 2;

	// �ļ�����
	private File file;
	// �ļ���С����
	private long longSize;

	public SizeOfFile(File file) {
		this.file = file;
	}

	// �����ļ���С
	public String getSizeFile() throws RuntimeException{
		// ��ʼ���ļ���СΪ0
		this.longSize = 0;

		if (file.exists() && file.isFile()) {
			this.longSize = file.length();
			if (this.longSize >= 0 && this.longSize < SIZE_BT) {
				return this.longSize + "B";
			} else if (this.longSize >= SIZE_BT && this.longSize < SIZE_KB) {
				return this.longSize / SIZE_BT + "KB";
			} else if (this.longSize >= SIZE_KB && this.longSize < SIZE_MB) {
				return this.longSize / SIZE_KB + "MB";
			} else if (this.longSize >= SIZE_MB && this.longSize < SIZE_GB) {
				BigDecimal longs = new BigDecimal(Double.valueOf(this.longSize + "").toString());
				BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_MB + "").toString());
				String result = longs.divide(sizeMB, SACLE, BigDecimal.ROUND_HALF_UP).toString();
				// double result=this.longSize/(double)SIZE_MB;
				return result + "GB";
			} else {
				BigDecimal longs = new BigDecimal(Double.valueOf(this.longSize + "").toString());
				BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_GB + "").toString());
				String result = longs.divide(sizeMB, SACLE, BigDecimal.ROUND_HALF_UP).toString();
				return result + "TB";
			}
		} else {
			throw new RuntimeException("û���ҵ��ļ�!!!");
			// return null;
		}
	}

//	public String toString() throws RuntimeException {
//		try {
//			// ���ü����ļ���Ŀ¼��С����
//			getSizeFile();
//
//			if (this.longSize >= 0 && this.longSize < SIZE_BT) {
//				return this.longSize + "B";
//			} else if (this.longSize >= SIZE_BT && this.longSize < SIZE_KB) {
//				return this.longSize / SIZE_BT + "KB";
//			} else if (this.longSize >= SIZE_KB && this.longSize < SIZE_MB) {
//				return this.longSize / SIZE_KB + "MB";
//			} else if (this.longSize >= SIZE_MB && this.longSize < SIZE_GB) {
//				BigDecimal longs = new BigDecimal(Double.valueOf(this.longSize + "").toString());
//				BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_MB + "").toString());
//				String result = longs.divide(sizeMB, SACLE, BigDecimal.ROUND_HALF_UP).toString();
//				// double result=this.longSize/(double)SIZE_MB;
//				return result + "GB";
//			} else {
//				BigDecimal longs = new BigDecimal(Double.valueOf(this.longSize + "").toString());
//				BigDecimal sizeMB = new BigDecimal(Double.valueOf(SIZE_GB + "").toString());
//				String result = longs.divide(sizeMB, SACLE, BigDecimal.ROUND_HALF_UP).toString();
//				return result + "TB";
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			throw new RuntimeException(ex.getMessage());
//		}
//	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

//	public String getLongSize() throws RuntimeException {
//		try {
//			// ���ü����ļ���Ŀ¼��С����
//			getSizeFile();
//			return Double.valueOf(longSize).toString();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			throw new RuntimeException(ex.getMessage());
//		}
//	}
}