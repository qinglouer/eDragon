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

	private DatagramSocket socket; // ���ڽ��պͷ���UDP��socketʵ��
	private byte[] buffer = new byte[1024]; // �������ݰ�ʱ,ֻ��Ҫ�ṩһ�������������ý��ܵ�������

	public enum Action {
		CLOSE_SOCKET
	}

	/**
	 * function:ͨ�����ڿͻ��˱�̣�û���ض��ļ����Ķ˿ڣ�ʹ��ʱ���ɲ���ϵͳ��ʱ����˿ڣ������˿ڣ�
	 * 
	 * @throws SocketException
	 */
	public JKademliaDatagramSocket() throws SocketException {
		try {
			socket = new DatagramSocket();
			// ���Ͱ�������
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ���Ǹ��ǳ����õĹ���������һ̨����ӵ�ж���һ��IP��ַ��ʱ�� ����������ʵ��������������LocalAddress�ı���
	 * 
	 * @param port
	 * @param laddr
	 * @throws SocketException
	 */
	public JKademliaDatagramSocket(int port, InetAddress localAddress) throws SocketException {
		socket = new DatagramSocket(port, localAddress);
	}

	/**
	 * function:����ʵ�������̶�����Port�˿ڱ��ģ�ͨ�����ڷ����
	 * 
	 * @param port
	 * @throws SocketException
	 */
	public JKademliaDatagramSocket(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	/**
	 *function:�󶨼�����ַ
	 */
	public void bind(SocketAddress addr) throws SocketException {
		socket.bind(addr);
	}

	/**
	 *function:��Ӧ�ó����˳���ʱ��ͨ���������ͷ���Դ���ر�Socket�����������쳣���˳����������Դ�޷����ա�
	 * ���ԣ�Ӧ���ڳ������ʱ������ʹ�ô˷����ر� Socket�����ڲ����쳣�׳���ر�Socket
	 */
	public void close() {
		socket.close();
	}

	/**
	 *function:ʵ���ϲ��ǽ���TCP�����ϵ����ӣ�������ֻ��ָ��������ָ���˿��շ���
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
	 *function:һ�����ݱ�socket���Դ������ͷ��˵�������������
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
	 *function:ֻ�е�����ʱ�Ż᷵��Զ�̵Ķ˿ڣ����򷵻�-1
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
	 *function:���س�ʱʱ��
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
	 *function:�������ݱ��ĵ�dp�С�receive��������һ����������(���������߳�)������������һ��רҵ���ʣ�
	 * �������һ���ڲ�ѭ����ʹ������ͣ������ط���ֱ��һ����������(���ݱ�����) ���ݱ��Ļ�����Ӧ���㹻���Ա�����յ�����
	 * 
	 * @param dp
	 *@throws IOException
	 *            return type:void
	 */
	public void receive(DatagramPacket dp) throws IOException {
		socket.receive(dp);
	}

	/**
	 *function:һ��������DatagramPocket��DatagramSocket,�Ϳ���ͨ���������ݸ�socket��send()�������͸ð�
	 * �� ���ͱ���dp��Ŀ�ĵء�
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
	 *function:���ñ��ĵĻ��峤��
	 * 
	 * @param size
	 *@throws SocketException
	 *            return type:void
	 */
	public void setSendBufferSize(int size) throws SocketException {
		socket.setSendBufferSize(size);
	}

	/**
	 *function:���ó�ʱʱ�䣬��λΪ����,����������bind()֮��ʹ��
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
