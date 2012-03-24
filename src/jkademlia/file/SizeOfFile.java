package jkademlia.file;

import java.io.File;
import java.math.BigDecimal;

public class SizeOfFile {
	// bt字节参考量
	public static final long SIZE_BT = 1024L;
	// KB字节参考量
	public static final long SIZE_KB = SIZE_BT * 1024L;
	// MB字节参考量
	public static final long SIZE_MB = SIZE_KB * 1024L;
	// GB字节参考量
	public static final long SIZE_GB = SIZE_MB * 1024L;
	// TB字节参考量
	public static final long SIZE_TB = SIZE_GB * 1024L;

	private static final int SACLE = 2;

	// 文件属性
	private File file;
	// 文件大小属性
	private long longSize;

	public SizeOfFile(File file) {
		this.file = file;
	}

	// 返回文件大小
	public String getSizeFile() throws RuntimeException{
		// 初始化文件大小为0
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
			throw new RuntimeException("没有找到文件!!!");
			// return null;
		}
	}

//	public String toString() throws RuntimeException {
//		try {
//			// 调用计算文件或目录大小方法
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
//			// 调用计算文件或目录大小方法
//			getSizeFile();
//			return Double.valueOf(longSize).toString();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			throw new RuntimeException(ex.getMessage());
//		}
//	}
}
