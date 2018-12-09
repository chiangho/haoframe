package hao.framework.web.auth;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import hao.framework.core.expression.HaoException;

/**
 * 认证接口
 * @author chianghao
 *
 */
public interface AuthInterface {

	/**
	 * 查询用户的密码
	 * @param token
	 * @return
	 */
	public String queryPassword(BaseToken token);
	
	/**
	 * 查询用户获取的许可
	 * @param principal
	 * @return
	 */
	public Set<String> queryUserPermit(String account);
	
	/**
	 * 查询用户的角色
	 * @param principal
	 * @return
	 */
	public Set<String> queryUserRole(String account);
	
	/**
	 * 获取访问路径的需要的角色
	 * @param uri
	 * @return
	 */
	public Set<String> queryRouterRole(String uri);
	
	/**
	 * 获取访问路径的需要的角色
	 * @param uri
	 * @return
	 */
	public Set<String> queryApiRole(String api);
	
	/**
	 * 
	 * @param token
	 * @return
	 * @throws HaoException
	 */
	public String doAuthenticationInfo(BaseToken token) throws HaoException;
	
	/**
	 * 校验成功返回当前用户的userid ,如果不存在返回空
	 * @param id
	 * @param request
	 * @return
	 * @throws HaoException
	 */
	public String checkIntefacePermit(String className,String methodName, HttpServletRequest request) throws HaoException;

	
}
