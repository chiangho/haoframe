package hao.framework.web.shiro.session;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

/**
 * session工具包
 * @author chianghao
 *
 */
public class SessionUtil {
	
	/**
	 * 获取当前用户
	 * @return
	 */
	public static Subject getCurrentUser() {
		return SecurityUtils.getSubject();
	}
	
	/**
	 * 获取session
	 * @return
	 */
	public static Session getSession() {
		return getCurrentUser().getSession();
	}
	
	/**
	 * 设置
	 * @param key
	 * @param value
	 */
	public static void put(String key,Object value) {
		getSession().setAttribute(key, value);
	}
	
	/**
	 * 获取
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		return getSession().getAttribute(key);
	}
	/**
	 * 移除
	 * @param key
	 */
	public static void remove(String key) {
		getSession().removeAttribute(key);
	}
	
}
