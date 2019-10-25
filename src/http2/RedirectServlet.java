package http2;

public class RedirectServlet extends HttpServlet{
 
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		/**
		 * 
		 */
		response.sendRedirect("index.html");
	}
 
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}

	
}
