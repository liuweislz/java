package bank;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
	
	public static void main(String[] args) throws IOException {
		
		new Client().start();
	}
private Scanner scanner=new Scanner(System.in);
DataInputStream dis;
DataOutputStream dos;
	
public void start() throws IOException{
		
		//
		Socket server=new Socket("172.16.40.20",8888);
		System.out.println("成功连接到服务器！");
		InetAddress addr=server.getInetAddress();
		System.out.println("服务器端主机地址："+addr.getHostAddress());
		System.out.println("服务器端IP地址："+Arrays.toString(addr.getAddress()));
		
		InputStream in=server.getInputStream();
		OutputStream out=server.getOutputStream();
		
		dis=new DataInputStream(in);
		dos=new DataOutputStream(out);
		
		boolean running=true;
		while(running){
			System.out.println("************************");
			System.out.println("******     1.开户      ******");
			System.out.println("******     2.存款      ******");
			System.out.println("******     3.取款      ******");
			System.out.println("******     4.转账      ******");
			System.out.println("******     0.退出      ******");
			System.out.println("************************");
			System.out.println("请输入命令：");
			String command=scanner.nextLine();
			switch(command){
			case "1":
				regiser();break;
			case "2":
				diposit();break;
			case "3":
				withdraw();break;
			case "4":
				transfer();break;
			case "0":
				System.out.println("bye");
				running=false;
			}
		  }	
		server.close();
		scanner.close();
		}
	//开户
	public void regiser() throws IOException{
		System.out.println("请输入你要注册的卡号：");
		String accountid=scanner.nextLine();
		System.out.println("请输入你要注册的密码：");
		String password=scanner.nextLine();
		dos.writeUTF("register");
		dos.writeUTF(accountid);
		dos.writeUTF(password);
		dos.flush();
		String ret=dis.readUTF();
		System.out.println(ret);
	}
	//存款
	public void diposit() throws IOException{
		System.out.println("请输入用户账号：");
		String cardno=scanner.nextLine();
		System.out.println("请输入存款金额：");
		float money=scanner.nextFloat();
		dos.writeUTF("diposit");
		dos.writeUTF(cardno);
		dos.writeFloat(money);
		dos.flush();
		String ret=dis.readUTF();
		System.out.println(ret);
	}
	//取款
	public void withdraw() throws IOException{
		System.out.println("请输入用户账号：");
		String cardno=scanner.nextLine();
		System.out.println("请输入取款金额：");
		float money=scanner.nextFloat();
		dos.writeUTF("withdraw");
		dos.writeUTF(cardno);
		dos.writeFloat(money);
		dos.flush();
		String ret=dis.readUTF();
		System.out.println(ret);
	}
	//转账
	public void transfer() throws IOException{
		System.out.println("请输入转账金额：");
		float money=scanner.nextFloat();
		System.out.println("请输入转出的账号：");
		String cardno1=scanner.next();
		System.out.println("请输入转入的账号：");
		String cardno2=scanner.next();
		dos.writeUTF("transfer");
		dos.writeUTF(cardno1);
		dos.writeUTF(cardno2);
		dos.writeFloat(money);
		dos.flush();
		String ret=dis.readUTF();
		System.out.println(ret);
	}
}
