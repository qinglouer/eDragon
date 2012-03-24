package jkademlia.gui;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

public class KnowContactsModel extends AbstractTableModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6250189128088642766L;
	public String[] columnNames = { "ID", "CreditValue" };
	private Hashtable<BigInteger,String> contactsTable;
	private Enumeration<BigInteger> keys;
	private Enumeration<String> values;
	private String[][] aValue;

	public KnowContactsModel(Hashtable<BigInteger,String> contactsTable) {
		this.contactsTable = contactsTable;
		keys = contactsTable.keys();
		values = contactsTable.elements();
		aValue = new String[contactsTable.size()][columnNames.length];
		for(int i = 0;keys.hasMoreElements();i++){
			aValue[i][0] = keys.nextElement().toString(16);
		}
		for(int i = 0;values.hasMoreElements();i++){
			aValue[i][1] = values.nextElement();
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return contactsTable.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return aValue[rowIndex][columnIndex];
	}
	
	public String getColumnName(int column){
		return columnNames[column];
	}
}
