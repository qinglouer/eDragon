package jkademlia.gui;

import javax.swing.table.AbstractTableModel;

import jkademlia.file.FileInfo;

public class ShareFileModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4106522291901059493L;
	
	private String[] columnNames ;
	private FileInfo fileInfo;
	private Object[][] data;
	private String[] fileNames;
	
	public ShareFileModel(String[] fileNames,String[] columnNames){
		this.columnNames = columnNames;
		this.fileNames = fileNames;
		fileInfo = new FileInfo();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return fileNames.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		data = new String[fileNames.length][2];
		for(int i =0;i<fileNames.length;i++){
			data[i][0] = fileNames[i];
		}
		for(int i =0; i<fileNames.length;i++){
			data[i][1] = fileInfo.getFileSize(fileNames[i]);
		}		
		return data[rowIndex][columnIndex];
	}
	
	public String getColumnName(int column){
		return columnNames[column];
	}
}
