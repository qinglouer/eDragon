package jkademlia.controller.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.Observable;

/**
 * @author zhuzhengtao Email:zhuzhengtao520@gmail.com
 * 
 */
public class JKademliaDatagramSocket extends Observable {

	private DatagramSocket socket; // 用于接收和发送UDP的socket实例
	private byte[] buffer = new byte[1024]; // 接受数据包时,只需要提供一个缓冲区来安置接受到的数据

	public enum Action {
		CLOSE_SOCKET
	}

	/**
	 * function:通常用于客户端编程，没有特定的监听的端口，使用时，由操作系统临时分配端口（匿名端口）
	 * 
	 * @throws SocketException
	 */
	public JKademliaDatagramSocket() throws SocketException {
		try {
			socket = new DatagramSocket();
			// 发送包。。。
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 这是个非常有用的构建器，当一台机器拥有多于一个IP地址的时候， 由它创建的实例仅仅接收来自LocalAddress的报文
	 * 
	 * @param port
	 * @param laddr
	 * @throws SocketException
	 */
	public JKademliaDatagramSocket(int port, InetAddress localAddress) throws SocketException {
		socket = new DatagramSocket(port, localAddress);
	}

	/**
	 * function:创建实例，并固定监听Port端口报文，通常用于服务端
	 * 
	 * @param port
	 * @throws SocketException
	 */
	public JKademliaDatagramSocket(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	/**
	 *function:绑定监听地址
	 */
	public void bind(SocketAddress addr) throws SocketException {
		socket.bind(addr);
	}

	/**
	 *function:在应用程序退出的时候，通常会主动释放资源，关闭Socket，但是由于异常地退出可能造成资源无法回收。
	 * 所以，应该在程序完成时，主动使用此方法关闭 Socket，或在捕获到异常抛出后关闭Socket
	 */
	public void close() {
		socket.close();
	}

	/**
	 *function:实际上不是建立TCP意义上的连接，但是它只对指定主机和指定端口收发包
	 * 
	 * @param address
	 *@param port
	 *           return type:void
	 */
	public void connect(InetAddress address, int port) {
		socket.connect(address, port);
	}

	public void connect(SocketAddress addr) throws SocketException {
		socket.connect(addr);
	}

	public void disconnect() {
		socket.disconnect();
	}

	public boolean getBroadcast() throws SocketException {
		return socket.getBroadcast();
	}

	/**
	 *function:一个数据报socket可以处理多个客服端的输入和输出请求
	 * 
	 * @return
	 */
	public DatagramChannel getChannel() {
		return socket.getChannel();
	}

	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}

	public int getLocalPort() {
		return socket.getLocalPort();
	}

	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}

	/**
	 *function:只有当连接时才会返回远程的端口，否则返回-1
	 * 
	 * @return return type:int
	 */
	public int getPort() {
		return socket.getPort();
	}

	public int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}

	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}

	public boolean getReuseAddress() throws SocketException {
		return socket.getReuseAddress();
	}

	public int getSendBufferSize() throws SocketException {
		return socket.getSendBufferSize();
	}

	/**
	 *function:返回超时时间
	 * 
	 * @return
	 *@throws SocketException
	 *            return type:int
	 */
	public int getSoTimeout() throws SocketException {
		return socket.getSoTimeout();
	}

	public int getTrafficClass() throws SocketException {
		return socket.getTrafficClass();
	}

	public boolean isBound() {
		return socket.isBound();
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

	public boolean isConnected() {
		return socket.isConnected();
	}

	/**
	 *function:接收数据报文到dp中。receive方法产生一个“阻塞”(阻塞调用线程)。“阻塞”是一个专业名词，
	 * 它会产生一个内部循环，使程序暂停在这个地方，直到一个条件触发(数据报到达) 数据报的缓冲区应该足够大，以保存接收的数据
	 * 
	 * @param dp
	 *@throws IOException
	 *            return type:void
	 */
	public void receive(DatagramPacket dp) throws IOException {
		socket.receive(dp);
	}

	/**
	 *function:一旦创建了DatagramPocket和DatagramSocket,就可以通过将包传递给socket的send()方法发送该包
	 * 。 发送报文dp到目的地。
	 * 
	 * @param dp
	 *@throws IOException
	 *            return type:void
	 */
	public void send(DatagramPacket dp) throws IOException {
		socket.send(dp);
	}

	public void setBroadcast(boolean on) throws SocketException {
		socket.setBroadcast(on);
	}

	public void setReceiveBufferSize(int size) throws SocketException {
		socket.setReceiveBufferSize(size);
	}

	public void setReuseAddress(boolean on) throws SocketException {
		socket.setReuseAddress(on);
	}

	/**
	 *function:设置报文的缓冲长度
	 * 
	 * @param size
	 *@throws SocketException
	 *            return type:void
	 */
	public void setSendBufferSize(int size) throws SocketException {
		socket.setSendBufferSize(size);
	}

	/**
	 *function:设置超时时间，单位为毫秒,方法必须在bind()之后使用
	 * 
	 * @param timeout
	 *@throws SocketException
	 *            return type:void
	 */
	public void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}

	public void setTrafficClass(int tc) throws SocketException {
		socket.setTrafficClass(tc);
	}

	public final DatagramPacket send(String serverHost, int serverPort, byte[] bytes) throws IOException {
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(serverHost), serverPort);
		socket.send(dp);
		return dp;

	}

	public String receive(String serverHost, int serverPort) throws IOException {
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		socket.receive(dp);
		String info = new String(dp.getData(), 0, dp.getLength());
		return info;
	}

}
