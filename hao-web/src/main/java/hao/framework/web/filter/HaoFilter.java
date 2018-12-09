package hao.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import hao.framework.web.context.RequestContext;

/***
 * 框架过滤器,配置在过滤器链的最后一个
 * @author chianghao
 *
 */
public class HaoFilter implements Filter{

	private FilterConfig config;
	
	public FilterConfig getConfig() {
		return config;
	}
	public void setConfig(FilterConfig config) {
		this.config = config;
	}

	/***
	 * 销毁
	 */
	@Override
	public void destroy() {

	}
	
	/***
	 * 过滤
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		String path = req.getContextPath();
		String basePath = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+path+"/";
		req.setAttribute("domain", basePath);
		RequestContext.initModelMap();
		RequestContext.setHttpRequest(req);
		RequestContext.setHttpResponse(resp);
		chain.doFilter(request, response);
		
		RequestContext.destroyModelMap();
		RequestContext.destroyHttpRequest();
		RequestContext.destroyHttpResponse();
	}

	/***
	 * 初始化
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

}
