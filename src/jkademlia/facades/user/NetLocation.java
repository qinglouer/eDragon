package jkademlia.facades.user;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author scaler Email:zhuzhengtao520@gmail.com 节点类，每一个节点都拥有全网唯一的ip和指定的Port
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
	 *function:如果有‘/’字符串，则将其去掉
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
