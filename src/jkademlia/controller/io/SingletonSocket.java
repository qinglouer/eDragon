package jkademlia.controller.io;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import jkademlia.tools.ThreadHashMap;

/**
 * @author scaler  Email:zhuzhengtao520@gmail.com
 *防止编译器为我们自动同步一个默认的构造器，所以构造器要设为private
 *单子模式
 */
public class SingletonSocket{
	
    private static Integer actualPort = null;

    private static ThreadHashMap<JKademliaDatagramSocket> tlSocket = new ThreadHashMap<JKademliaDatagramSocket>(){
        public JKademliaDatagramSocket initialValue(){
            JKademliaDatagramSocket socket = null;
            try{
                if(actualPort == null){
                    String startPort = System.getProperty("jkademlia.socket.startPort");       //开始端口4000
                    actualPort = Integer.parseInt(startPort);
                }
                socket = new JKademliaDatagramSocket(actualPort, InetAddress.getLocalHost());
            } catch (UnknownHostException e){
                e.printStackTrace();
            } catch (SocketException e){
                e.printStackTrace();
            }
            return socket;
        }
    };

    private SingletonSocket(){
    }

    public static JKademliaDatagramSocket getInstance(){
        return tlSocket.get();
    }
}