package jkademlia.transfer.client;

//public class IPResourse implements Comparable<IPResourse> {
  public class IPResourse{
	
	private String ipAndCreditValue;
	public String ip;//此IPResourse的IP地址
	private String value;//此IPResourse的信任值，可能用到
	public int connection;//当前IPResourse正服务于几个socket连接;
	private boolean busy = false;//当前IPResourse是否繁忙，如果connection>=最大连接数，
								//值为true。作为从从IPResourseQueue中进出的标志状态
	
	public IPResourse(String ipAndCreditValue){
		this.ipAndCreditValue = ipAndCreditValue;
		int indexOfCredit = ipAndCreditValue.indexOf("@");
		this.ip = ipAndCreditValue.substring(0,indexOfCredit);
		String value =ipAndCreditValue.substring(indexOfCredit+1);
	}
	
	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	
	public int compareTo(IPResourse arg0) {
		return 0;
	}
}
