package http2;

import java.io.PrintWriter;

public class HelloServlet extends HttpServlet{

 
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		
		PrintWriter pw=response.getWriter();
		
		pw.print("<h1>hello world</h1>");
		 
	}

 
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	

}
