package hao.framework.web.auth.shiro;



import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hao.framework.web.RequestStatus.REQUEST_STATUS;



/***
 * 如果没有登录并且是json格式请求那么返回json格式的报文
 * @author Administrator
 *
 */
public class HaoAuthenticationFilter extends FormAuthenticationFilter{

	private static final Logger log = LoggerFactory.getLogger(HaoAuthenticationFilter.class);
	
	/***
	 * 验证是否登录
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request,ServletResponse response, Object mappedValue) {
		// TODO Auto-generated method stub
		return super.isAccessAllowed(request, response, mappedValue);
		//return super.isAccessAllowed(request, response, mappedValue)&&HaoSession.getAccess()!=null;
	}

	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		String path = req.getContextPath();
		String basePath = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+path+"/";
		if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }
                //allow them to see the login page ;)
                return true;
            }
        } else {
        	if (log.isTraceEnabled()) {
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [" + getLoginUrl() + "]");
            }
        	if(log.isDebugEnabled()) {
    			log.debug("未授权或者未登录,请求信息为=====》"+req.getRequestURI());
    		}
        	
            String loginUrl = getLoginUrl();
            if(isAjaxRequest(req)) {
            	saveRequest(request);
            	JSONObject json = new JSONObject();
    			json.put("rtnCode",REQUEST_STATUS.NO_AOUTH.getCode());
    			json.put("rtnMsg",REQUEST_STATUS.NO_AOUTH.getMsg());
    			json.put("loginUrl", loginUrl);
    			json.put("domain", basePath);
    			resp.setHeader("Content-type", "text/html;charset=UTF-8");
    			resp.setCharacterEncoding("UTF-8");
    			PrintWriter out = resp.getWriter();
    			out.print(json.toString());
    			out.flush();
    			out.close();
            }else {
            	this.setLoginUrl(loginUrl);
            	saveRequestAndRedirectToLogin(request, response);
            }
            return false;
        }
    }
	
	
	protected boolean isAjaxRequest(HttpServletRequest httpRequest) {
		String accept = httpRequest.getHeader("accept");
		String contentType = httpRequest.getHeader("Content-Type");
		String xReqWith = httpRequest.getHeader("X-Requested-With");
		
		boolean isAjax =  (accept != null && accept.contains("application/json"))
				|| (contentType != null && contentType
						.contains("application/json"))
				|| (xReqWith != null && xReqWith.contains("XMLHttpRequest"));
		
		//判断是否是easyui 请求 ，easyui请求按照ajax请求对待
		if(isAjax==false) {
			String easyuiframe = httpRequest.getParameter("easyui_frame");
			if(easyuiframe != null&&easyuiframe.equals("1")) {
				isAjax = true;
			}
		}
		
		return isAjax;
	}
	
}
