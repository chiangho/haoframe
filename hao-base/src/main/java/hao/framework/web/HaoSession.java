package hao.framework.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public class HaoSession {
	
	static Logger log =LogManager.getLogger(HaoSession.class); // Logger.getLogger();
	
	private static final String USER_CACHE_KEY="USER_CACHE_KEY";
	private static final String USER_ID_CACHE_KEY="USER_ID_CACHE_KEY";
	private static final String USER_ROLE_CACHE_KEY="USER_ROLE_CACHE_KEY";
	private static final String USER_PERMISSION_CACHE_KEY="USER_PERMISSION_CACHE_KEY";
	
	private static Session getSession() {
		Subject subject = SecurityUtils.getSubject(); 
		try {
			return subject==null?null:subject.getSession();
		}catch(Exception e) {
			return null;
		}
	}
	
	public static void put(String key,Object value) {
		Session session = getSession();
		if(session == null) {
			log.error("向session中设置数据时异常，session为空或者为null");
			return;
		}
		session.setAttribute(key, value);
	}
	
	public static Object find(String key) {
		Session session = getSession();
		if(session == null) {
			log.error("从session中获取数据时异常，session为空或者为null");
			return null;
		}
		return session.getAttribute(key);
	}
	
	
	/***
	 * 设置用户信息
	 * @param bean
	 */
	public static <T> void putAccess(T bean) {
		put(USER_CACHE_KEY,bean);
	}

	
	/***
	 * 获取用户信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAccess() {
		if(find(USER_CACHE_KEY)==null) {
			return null;
		}
		T t = (T) find(USER_CACHE_KEY);
		return t;
	}
	
	
	/**
	 * 设置用户主键
	 * @param id
	 */
	public static void putAccessId(String id) {
		put(USER_ID_CACHE_KEY,id);
	}
	
	/**
	 * 获取用户主键
	 * @return
	 */
	public static String getAccessId() {
		return find(USER_ID_CACHE_KEY)==null?null:(String) find(USER_ID_CACHE_KEY);
	}
	
	/***
	 * 设置角色
	 * @param roles
	 */
	public static void putRoles(String[] roles) {
		put(USER_ROLE_CACHE_KEY,roles);
	}
	
	/***
	 * 获取角色
	 * @return
	 */
	public static String[] getRoles() {
		if(find(USER_ROLE_CACHE_KEY)==null) {
			return null;
		}
		return (String[]) find(USER_ROLE_CACHE_KEY);
	}
	
	/**
	 * 设置权限
	 * @param permissions
	 */
	public static void putPermissions(String[] permissions) {
		put(USER_PERMISSION_CACHE_KEY,permissions);
	}
	/**
	 * 获取权限
	 * @return
	 */
	public static String[] getPermissions() {
		if(find(USER_PERMISSION_CACHE_KEY)==null) {
			return null;
		}
		return (String[]) find(USER_PERMISSION_CACHE_KEY);
	}
	
}
