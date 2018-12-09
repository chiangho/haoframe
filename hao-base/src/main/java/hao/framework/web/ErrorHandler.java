package hao.framework.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ErrorHandler extends HttpServlet {
	/**
	 * 序列
	 */
	private static final long serialVersionUID = 1L;

	Logger log = LogManager.getLogger("__________捕获异常页面_________");
	
	// 处理 GET 方法请求的方法
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	// 处理 POST 方法请求的方法
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		String errorPage = request.getParameter("error_page");
		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null) {
			requestUri = "#";
		}
		log.info("\n"
				+ "状态:"+statusCode+"\n"
				+ "异常信息:"+(throwable==null?"":throwable.getMessage())+"\n"
				+ "异常请求:"+requestUri);
		
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		
		if(errorPage==null||errorPage.equals("")) {
			if (servletName == null) {
				servletName = "Unknown";
			}
			// 设置响应内容类型
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			String title = "Error/Exception 信息";
			String docType = "<!DOCTYPE html>\n";
			out.println(
					docType + "<html>\n" + "<head><title>" + title + "</title></head>\n" + "<body bgcolor=\"#f0f0f0\">\n");
			if (throwable == null && statusCode == null) {
				out.println("<h2>错误信息丢失</h2>");
				out.println("请返回 <a href=\"" + basePath + "\">主页</a>。");
			} else if (statusCode != null) {
				out.println("错误代码 : " + statusCode);
				out.println("请求 URI: " + requestUri + "<br><br>");
			} else {
				out.println("<h2>错误信息</h2>");
				out.println("Servlet Name : " + servletName + "</br></br>");
				out.println("异常类型 : " + throwable.getClass().getName() + "</br></br>");
				out.println("请求 URI: " + requestUri + "<br><br>");
				out.println("异常信息: " + throwable.getMessage());
			}
			out.println("</body>");
			out.println("</html>");
		}else {
			//			String backurl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+requestUri;
			//+"?back_url="+URLEncoder.encode(backurl,"UTF-8")
			//request.getRequestDispatcher(basePath+errorPage).include(request, response); 
			response.sendRedirect(basePath+errorPage);
		}
	}
}
