package bank2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 *  聊天类
 */
public class Talker extends Thread{

	private InputStream in;
	private OutputStream out;
	private DataInputStream dis;
	private DataOutputStream dos;
	private MsgListener listener;

	/**
	 * 
	 * @param socket
	 *            连接的Socket
	 * @param listener
	 *            监听消息的监听器
	 * @throws IOException
	 */
	public Talker(Socket socket, MsgListener listener) throws IOException {
		this.listener = listener;
		// 获取网络地址对象
		// socket.getInetAddress()  返回套接字所连接的地址。
		listener.onConnect(socket.getInetAddress());
		in = socket.getInputStream();
		out = socket.getOutputStream();
		dos=new DataOutputStream(out);
		dis=new DataInputStream(in);	
		// 启动线程，开始接受消息
		start();
	}
	public Talker(Socket socket) throws IOException{
		in = socket.getInputStream();
		out = socket.getOutputStream();
		dos=new DataOutputStream(out);
		dis=new DataInputStream(in);
		start();
	}

	/**
	 * 发送数据
	 */
	public String sendDate(String cardno, String pwd) throws IOException {
		dos.writeUTF("register");
		dos.writeUTF(cardno);
		dos.writeUTF(pwd);
		dos.flush();
		String ret=dis.readUTF();
		return ret;
	}

}

/**
 *	接收消息的监听器
 */
interface MsgListener {
	// 当接收到消息时执行的方法
	//public void onMessage(String msg);
	// 当连接成功时执行的方法
	public void onConnect(InetAddress addr);
}
