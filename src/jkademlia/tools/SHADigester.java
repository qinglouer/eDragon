package jkademlia.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
/**
 * @author zhuzhengtao  Email:zhuzhengtao520@gmail.com
 * 计算文件散列摘要
 */
public class SHADigester {
	private static Logger logger = Logger.getLogger(SHADigester.class);
	private static MessageDigest Digester;
	
	private static MessageDigest getDigester(){
		if(Digester == null){
			try{
				Digester = MessageDigest.getInstance("SHA-1");     //生成安全散列算法对象
			}catch(NoSuchAlgorithmException e){
				logger.fatal(e);
				throw new RuntimeException(e);            //RuntimeException是那些可能在Java虚拟机正常运行时抛出的异常的超类   
			}
		}
		return Digester;
	}
	
	/**
	 *function:完成哈希操作，调用此方法后摘要被重置
	 *@param string
	 *@return
	 */
	public static byte[] digest(String string){
		byte[] data = string.getBytes();        //将String型解码为字节数组
		return getDigester().digest(data);
	}
	
	public static byte[] digest(byte[] data){
		return getDigester().digest(data);
	}
	
	/**
	 *function:返回的是不可变的任意精度的整数
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
