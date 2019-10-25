package http3;

public class ForwardServlet extends HttpServlet{
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		//请求转发 与http协议无关
		/**
		 * request.setRequestURL("/index.html").forward(request,response);
		 */
		RequestDispatcher rd=request.getRequestDispatcher("/index.html");
		rd.forward(request,response);
	}
 
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}

	

}
