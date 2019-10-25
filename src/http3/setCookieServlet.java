package http3;

import java.io.PrintWriter;
/**
 *
 * @author ASUS
 *
 */
public class setCookieServlet extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html; charset=utf-8");
		
		PrintWriter pw=response.getWriter();
		
		pw.print("<h1>测试Cookie</h1>");
		
		Cookie cookie=new Cookie("username","zhangsan");
		response.addCookie(cookie);
		
		cookie=new Cookie("level","100");
		cookie.setPath("/page");
		response.addCookie(cookie);
		
		cookie=new Cookie("user","test");
		cookie.setPath("/user");
		response.addCookie(cookie);
		 
	}

 
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	
}
