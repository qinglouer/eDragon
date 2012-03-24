package jkademlia.exceptions;

public class KademliaProtocolException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1442802815600013384L;

	public KademliaProtocolException(String message, Throwable cause){
		super(message,cause);
	}
	
	public KademliaProtocolException(String message){
		super(message);
	}
	
	public KademliaProtocolException(Throwable cause){
		super(cause);
	}
}
