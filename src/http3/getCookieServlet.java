package http3;

import java.io.PrintWriter;

public class getCookieServlet extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		
		PrintWriter pw=response.getWriter();
		
		pw.print("<h1>测试  获取Cookie值</h1>");
		Cookie[]cookies=request.getCookies();
		if(null!=cookies){
			for(Cookie c:cookies){
				pw.print(c.getName()+"="+c.getValue()+"<br>");
			}
		}
		 
	}

 
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	
}
