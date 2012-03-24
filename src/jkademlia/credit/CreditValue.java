package jkademlia.credit;

import jkademlia.data.DatabaseManager;

public class CreditValue {
	private String creditValue;
	
	public CreditValue(){
		creditValue = String.valueOf(DatabaseManager.getCredit());
	}
	
	public String getCreditValue(){
		return creditValue;
	}

}
