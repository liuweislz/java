package http3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Processer {
	//定义Servlet容器
	private HashMap<String, HttpServlet> servletContainer=new HashMap<>();
	{
		//添加一个响应重定向Servlet
		servletContainer.put("/redirect.s", new RedirectServlet());
		//添加一个请求转发Servlet
		servletContainer.put("/forward.s", new ForwardServlet());
		//添加一个写入Servlet
		servletContainer.put("/hello.s", new HelloServlet());
		//添加一个CookieServlet
		servletContainer.put("/cookie.s", new setCookieServlet());
		//获取Cookie值
		servletContainer.put("/user/getcookie.s", new getCookieServlet());
	}

	public void process(Socket socket){
		InputStream in;
		OutputStream out;
		try {
			in=socket.getInputStream();
			out=socket.getOutputStream();
			byte [] buf=new byte[1024];
			int count;
			count=in.read(buf);
			String content=new String(buf,0,count);
			System.out.println(content);
			parseRequest(content);
			//解析请求报文
			HttpServletRequest request=parseRequest(content);
			HttpServletResponse response=new HttpServletResponse(request, out);
			/**
			 * 静态请求：对应一个html,js,css
			 * 动态请求：hello.s
			 * 非法404请求  即没有物理文件也没有虚拟的地址
			 * 
			 */
			//判断文件是否存在
			String rootPath="D:/apache-tomcat-8.0.51/webapps/photo";
			String webPath=request.getRequestURL();
			//判断访问文件是否存在
			String diskPath=rootPath+webPath;
			if(new File(diskPath).exists()==true){
				//静态请求直接commit
				
			}else if(servletContainer.containsKey(webPath)){
				//127.0.0.1:8080/hello.s
				//判断虚拟地址中有没有该地址（Servlet容器中找）
				//动态请求先由servlet处理
				HttpServlet servlet=servletContainer.get(webPath);
				servlet.service(request, response);
			}else{
				//404  改写资源文件为404.html
				response.setStatus(404, "Not Found");
				request.setRequestURL("/404.html");
			}
			response.commit();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private HttpServletRequest parseRequest(String content) {
		 HttpServletRequest request=new HttpServletRequest(content);
		 return request;
	}
}
