package com.yc.net;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
	/**
	 *1. 实现屏幕录入发送的消息
	 *2. 可以无限制的发送消息
	 *3. 可以实现文件传输
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Scanner scanner=new Scanner(System.in);
		
		Socket server=new Socket("172.18.49.15",8848);
		System.out.println("成功连接到服务器！");
		InetAddress addr=server.getInetAddress();
		System.out.println("服务器端主机地址："+addr.getHostAddress());
		System.out.println("服务器端IP地址："+Arrays.toString(addr.getAddress()));
		
		InputStream in=server.getInputStream();
		OutputStream out=server.getOutputStream();
		
		Thread t1=new Thread(){
			public void run(){
				boolean running=true;
				while(running){
					System.out.print("我说：");
					String msg=scanner.nextLine();
					try {
						out.write(msg.getBytes());
						//文件： e/a.txt
						if(msg.startsWith("文件：")){
							String filename=msg.substring("文件：".length());
							sendFile(out,filename);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		};
		t1.start();
		
		Thread t2=new Thread(){
			public void run(){
				boolean running=true;
				while(running){	
					try {
						byte [] buffer=new byte[1024];
						int count  = in.read(buffer);
						String msg=new String(buffer,0,count);
						//文件：e:/a.txt
						if(msg.startsWith("文件：")){
							String filename=msg.substring("文件：".length());
							filename=filename.substring(filename.lastIndexOf("/")+1);
							saveFile(in,filename);
						}else{
							System.out.println("服务器端说："+msg);
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		t2.start();
		
		t1.join();
		t2.join();
		
		scanner.close();
		 
		server.close();
		
		
	}
	//保存文件
	static void saveFile(InputStream in, String filename) throws IOException {
		FileOutputStream fos=new FileOutputStream("d:/"+filename);
		try{
			byte [] buf=new byte[1024];
			int count;
			while((count=in.read(buf))>0){
				fos.write(buf,0,count);
			}
		}finally{
			fos.close();
		}
		System.out.println("文件保存成功："+"d:/"+filename);
}
	//发送文件
	static void sendFile(OutputStream out, String filename) throws IOException {
		 FileInputStream fis=new FileInputStream(filename);	
		 try {
			 byte [] buf=new byte[1024];
			 int count;
			 while((count=fis.read(buf))>0){
				 out.write(buf,0,count);
			 }
		}finally{
			fis.close();
		}
		System.out.println("文件发送成功"+"d:/"+filename);
	}
}
