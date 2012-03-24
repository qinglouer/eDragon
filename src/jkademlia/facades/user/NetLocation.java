package jkademlia.facades.user;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author scaler Email:zhuzhengtao520@gmail.com �ڵ��࣬ÿһ���ڵ㶼ӵ��ȫ��Ψһ��ip��ָ����Port
 */
public class NetLocation {

	private InetAddress ip;
	private Integer port;

	public NetLocation(String ip, Integer port) throws UnknownHostException {
		this(InetAddress.getByName(ip), port);
	}

	public NetLocation(InetAddress ip, Integer port) {
		this.ip = ip;
		this.port = port;
	}

	public InetAddress getIP() {
		return ip;
	}

	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	/**
	 *function:����С�/���ַ���������ȥ��
	 * 
	 * @return
	 */
	public String getStringIP() {
		String ipAddress = ip.toString();
		if (ipAddress.indexOf('/') == 0)
			ipAddress = ipAddress.substring(1);
		return ipAddress;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String toString() {
		return this.getStringIP() + ":" + this.getPort();
	}
}
