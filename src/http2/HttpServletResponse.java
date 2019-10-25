package http2;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

public class HttpServletResponse {
	//web.xml解析器
	private static WebXmlParser webXmlParser=new WebXmlParser("web.xml");
	
	private HttpServletRequest request;
	private OutputStream out;
	
	//状态码字段
	private int status=200;
	private String message="OK";
	
	private HashMap<String, String> headerMap=new HashMap<>();
	
	public HttpServletResponse(HttpServletRequest request,OutputStream out){
		super();
		this.request=request;
		this.out=out;
	}
	
	public void commit()throws IOException{
		
		String suffix=request.getRequestURL().substring(request.getRequestURL().lastIndexOf(".")+1);
		if(headerMap.containsKey("Content-Type")==false){
			//从web.xml文件中获取contentType，代替之前的硬编码判断
			String contentType=webXmlParser.getContentType(suffix);
			setContentType(contentType);
		}	
		String responseStr="HTTP/1.1 "+status+" "+message+"\r\n";
		//responseStr+="Content-Type:"+contentType+"\r\n";
		//写头域信息
		for(Entry<String, String> entry:headerMap.entrySet()){
			responseStr+=entry.getKey()+":"+entry.getValue()+"\r\n";
		}
		responseStr+="\r\n";   //CRLF空行
		out.write(responseStr.getBytes());
		//响应重定向不需要写body
		if(status<300 || status>399){
			if(caw.toString().isEmpty()){
				String rootPath="D:/apache-tomcat-8.0.51/webapps/photo";
				String filePath=request.getRequestURL();
				//根据请求路径返回对应的html文件
				//判断访问文件是否存在
				//如果访问不存在，返回404.html
				String diskPath=rootPath+filePath;
				if(new File(diskPath).exists()==false){
					diskPath=rootPath+"/404.html";
				}
				FileInputStream fis=new FileInputStream(diskPath);
				
				byte [] buf=new byte[1024];
				int count;
				//向浏览器发送报文
				while((count=fis.read(buf))>0){
					out.write(buf,0,count);
				}
				fis.close();
			}else{
				out.write(caw.toString().getBytes());
			}

		}	
	}
	//设置结果码
	public void setStatus(int status,String message){
		this.status=status;
		this.message=message;
	}
	//响应重定向
	public void sendRedirect(String webPath) {
		 /**
		  * 响应结果码
		  * 1XX:接受请求，继续处理
		  * 2XX:正常响应 200
		  * 3XX:响应重定向 301,302
		  * 4XX:浏览器端错误 404,405
		  * 5XX:服务器端错误
		  */
		this.setStatus(301,"Redirect");
		this.addHeader("Location",webPath);
		
	}

	public void addHeader(String key, String value) {
		 this.headerMap.put(key, value);
	}
	//设置响应类型
	public void setContentType(String contentType){
		 this.headerMap.put("Content-Type", contentType);
	}

	CharArrayWriter caw=new CharArrayWriter();
	PrintWriter pw=new PrintWriter(caw);
	public PrintWriter getWriter() {
		return pw;
	}
	
}
