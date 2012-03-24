package jkademlia.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
/**
 * @author zhuzhengtao  Email:zhuzhengtao520@gmail.com
 * �����ļ�ɢ��ժҪ
 */
public class SHADigester {
	private static Logger logger = Logger.getLogger(SHADigester.class);
	private static MessageDigest Digester;
	
	private static MessageDigest getDigester(){
		if(Digester == null){
			try{
				Digester = MessageDigest.getInstance("SHA-1");     //���ɰ�ȫɢ���㷨����
			}catch(NoSuchAlgorithmException e){
				logger.fatal(e);
				throw new RuntimeException(e);            //RuntimeException����Щ������Java�������������ʱ�׳����쳣�ĳ���   
			}
		}
		return Digester;
	}
	
	/**
	 *function:��ɹ�ϣ���������ô˷�����ժҪ������
	 *@param string
	 *@return
	 */
	public static byte[] digest(String string){
		byte[] data = string.getBytes();        //��String�ͽ���Ϊ�ֽ�����
		return getDigester().digest(data);
	}
	
	public static byte[] digest(byte[] data){
		return getDigester().digest(data);
	}
	
	/**
	 *function:���ص��ǲ��ɱ�����⾫�ȵ�����
	 *@param string
	 *@return
	 */
	public static BigInteger hash(String string){
		byte[] data = string.getBytes();
		byte[] hashed = getDigester().digest(data);
		return new BigInteger(hashed);
	}
	
	public static BigInteger hash(byte[] data){
		byte[] hashed = getDigester().digest(data);
		return new BigInteger(hashed);
	}
}
