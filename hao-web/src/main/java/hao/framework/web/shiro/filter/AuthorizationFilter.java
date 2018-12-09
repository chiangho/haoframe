package hao.framework.web.shiro.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;

import hao.framework.web.auth.AuthInterface;


/**
 * 权限认证
 * @author chianghao
 */
public class AuthorizationFilter extends RolesAuthorizationFilter{

	@Autowired
	private AuthInterface authInterface;
	
	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws IOException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String requestAddress = uri;
		if(contextPath!=null&&!contextPath.equals("")) {
			requestAddress = uri.substring(contextPath.length());
		}
		Set<String> roles  = authInterface.queryApiRole(requestAddress);
		return super.isAccessAllowed(request, response, roles.toArray(new String[roles.size()]));
	}
	
}
