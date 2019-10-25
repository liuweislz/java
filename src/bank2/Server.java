package bank2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;


/**
 * 
 * @author ASUS
 *
 */
public class Server {
	
	bankDAO dao=new bankDAO();
	private DataOutputStream dos;
	public static void main(String[] args) throws IOException {
		new Server().start();
	}
	
	public void start() throws IOException{
		
		//创建套接字服务器
		ServerSocket server=new ServerSocket(8888);
		System.out.println("服务器启动完成，监听窗口：8888");
		
		boolean running=true;
		while(running){
			//
			Socket client=server.accept();
			//创建线程处理业务
			new Thread(){
				
			public void run(){
				InetAddress addr=client.getInetAddress();
				System.out.println("客户端主机地址："+addr.getHostAddress());
				System.out.println("客户端IP地址："+Arrays.toString(addr.getAddress()));
			    try{
				InputStream in=client.getInputStream();
				OutputStream out=client.getOutputStream();
				boolean running=true;
				while(running){
					/**
					 * 业务约定--》协议
					 * 
					 */
					DataInputStream dis=new DataInputStream(in);
					dos=new DataOutputStream(out);
					try{
					String command=dis.readUTF();
					switch(command){
					case "register":
						 try {
							regiser(dis.readUTF(), dis.readUTF());
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					case "diposit":
						try {
							diposit(dis.readUTF(), dis.readFloat());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case "withdraw":
						try {
							withdraw(dis.readUTF(), dis.readFloat());
						} catch (SQLException e) {
							e.printStackTrace();
						}
					case "transfer":
						try {
							transfer(dis.readUTF(), dis.readUTF(),dis.readFloat());
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					}catch(EOFException e){
						break;
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			}
		}.start();
	}
	
	}
	//开户
	public void regiser(String cardno,String password) throws SQLException, IOException{
		System.out.println(cardno+"   "+password);
		int i=dao.add(Integer.parseInt(cardno), password);
		if(i>0){
			dos.writeUTF("开户成功");
			dos.flush();
		}else{
			dos.writeUTF("开户失败");
		}
	}
	//存款
	public void diposit(String cardno,float money) throws IOException, SQLException{
		System.out.println(cardno+"  存款："+money);
		int i=dao.update(Integer.parseInt(cardno), money);
		if(i>0){
		dos.writeUTF("存款成功");
		dos.flush();
		}else{
			dos.writeUTF("存款失败");
		}
	}
	//取款
	public void withdraw(String cardno,float money) throws SQLException, IOException{
		System.out.println(cardno+"  取款："+money);
		int i=dao.update(Integer.parseInt(cardno), -money);
		if(i>0){
			dos.writeUTF("取款成功");
			dos.flush();
		}else{
			dos.writeUTF("取款失败");
		}
	}
	//转账
	public void transfer(String cardno1,String cardno2,float money) throws NumberFormatException, SQLException, IOException{
		System.out.println("用户    "+cardno1+"转账给     "+cardno2+"   "+money);
		int i=dao.transfer(Integer.parseInt(cardno1), Integer.parseInt(cardno2), money);
		if(i>0){
			dos.writeUTF("转账成功");
		}else{
			dos.writeUTF("转账失败");
		}
		dos.flush();
	}
}
