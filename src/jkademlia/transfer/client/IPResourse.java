package jkademlia.transfer.client;

//public class IPResourse implements Comparable<IPResourse> {
  public class IPResourse{
	
	private String ipAndCreditValue;
	public String ip;//��IPResourse��IP��ַ
	private String value;//��IPResourse������ֵ�������õ�
	public int connection;//��ǰIPResourse�������ڼ���socket����;
	private boolean busy = false;//��ǰIPResourse�Ƿ�æ�����connection>=�����������
								//ֵΪtrue����Ϊ�Ӵ�IPResourseQueue�н����ı�־״̬
	
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
