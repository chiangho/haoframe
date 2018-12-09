package hao.framework.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import hao.framework.web.auth.AuthInterface;
import hao.framework.web.jwt.JwtConstants;



/**
 * spring 拦截器
 * @author chianghao
 *
 */
public class AuthorizationHandlerInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	AuthInterface auth;
	
	/**
	 * This implementation always returns {@code true}.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HandlerMethod handMethod = (HandlerMethod) handler;
		//通过className 和 method获取interface信息，如果获取不到则抛出异常
		String userid = auth.checkIntefacePermit(handMethod.getBeanType().getName(),handMethod.getMethod().getName(),request);//认证成功返回当前用户
		request.setAttribute(JwtConstants.get().getCURRENT_USER_NAME(), userid==null?"":userid);
		//没有获取到接口注解信息则表示无需验证可直接放行。
		return true;
	}

	
}
