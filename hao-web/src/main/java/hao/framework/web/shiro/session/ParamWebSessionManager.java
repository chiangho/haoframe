package hao.framework.web.shiro.session;


import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ParamWebSessionManager extends DefaultSessionManager implements WebSessionManager {

    private static final Logger log = LoggerFactory.getLogger(ParamWebSessionManager.class);
    
    private String sessionIdName;
    
	public void setSessionIdName(String sessionIdName) {
		this.sessionIdName = sessionIdName;
	}
	@Override
	public boolean isServletContainerSessions() {
		// TODO Auto-generated method stub
		return false;
	}
    
	public String getSessionIdName() {
        String name = sessionIdName != null ? sessionIdName : null;
        if (name == null) {
            name = ShiroHttpSession.DEFAULT_SESSION_ID_NAME;
        }
        return name;
    }
	
	@Override
    public Serializable getSessionId(SessionKey key) {
        Serializable id = super.getSessionId(key);
        if (id == null && WebUtils.isWeb(key)) {
            ServletRequest request = WebUtils.getRequest(key);
            id = request.getParameter(getSessionIdName());
        }
        return id;
    }
	
	
	
	@Override
    protected void onStart(Session session, SessionContext context) {
        super.onStart(session, context);
        if (!WebUtils.isHttp(context)) {
            log.debug("SessionContext argument is not HTTP compatible or does not have an HTTP request/response " +
                    "pair. No session ID cookie will be set.");
            return;

        }
        HttpServletRequest request = WebUtils.getHttpRequest(context);
        request.removeAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_IS_NEW, Boolean.TRUE);
    }
	
	
	@Override
    protected void onExpiration(Session s, ExpiredSessionException ese, SessionKey key) {
        super.onExpiration(s, ese, key);
        onInvalidation(key);
    }

    @Override
    protected void onInvalidation(Session session, InvalidSessionException ise, SessionKey key) {
        super.onInvalidation(session, ise, key);
        onInvalidation(key);
    }

    private void onInvalidation(SessionKey key) {
        ServletRequest request = WebUtils.getRequest(key);
        if (request != null) {
            request.removeAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID);
        }
    }
    
//    protected Session createExposedSession(Session session, SessionContext context) {
//        if (!WebUtils.isWeb(context)) {
//            return super.createExposedSession(session, context);
//        }
//        ServletRequest request = WebUtils.getRequest(context);
//        ServletResponse response = WebUtils.getResponse(context);
//        SessionKey key = new WebSessionKey(session.getId(), request, response);
//        return new DelegatingSession(this, key);
//    }
//
//    protected Session createExposedSession(Session session, SessionKey key) {
//        if (!WebUtils.isWeb(key)) {
//            return super.createExposedSession(session, key);
//        }
//
//        ServletRequest request = WebUtils.getRequest(key);
//        ServletResponse response = WebUtils.getResponse(key);
//        SessionKey sessionKey = new WebSessionKey(session.getId(), request, response);
//        return new DelegatingSession(this, sessionKey);
//    }
	
}
