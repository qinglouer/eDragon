package jkademlia.gui;

import java.util.List;

import javax.swing.AbstractListModel;

public class SearchListModel extends AbstractListModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5255768055077593529L;
	private List<String> result;
	private String[] result1;

	public SearchListModel(List<String> result){
		this.result = result;
	}
	
	public SearchListModel(String[] result1){
		this.result1 = result1;
	}
	public Object getElementAt(int index) {
		return result.get(index);
	}

	public int getSize() {
		return result.size();
	}
	
}
