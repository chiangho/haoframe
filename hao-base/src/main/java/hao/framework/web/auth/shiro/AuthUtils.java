package hao.framework.web.auth.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;

import hao.framework.core.CommonVar.SystemMsg;
import hao.framework.core.expression.HaoException;

/***
 * 认证工具类。提供认证服务
 * @author chianghao
 */
public class AuthUtils{
	/***
	 * 登录
	 * @param token
	 * @throws HaoException 
	 */
	public static void auth(MemberToken token) throws HaoException {
		Subject subject = SecurityUtils.getSubject();  
		try {
			subject.login(token);
		}catch(IncorrectCredentialsException e) {
			throw new HaoException(SystemMsg.auth_error.getCode(),"密码错误！");
		}catch (UnknownAccountException e) {
			throw new HaoException(SystemMsg.auth_error.getCode(),e.getMessage());
        }catch (AuthenticationException ae) {
        	throw new HaoException(SystemMsg.auth_error.getCode(),"认证异常！");
        } 
	}
	
	/***
	 * 登出
	 */
	public static void logout() {
		Subject subject = SecurityUtils.getSubject();  
		if(subject==null) {
			return;
		}
		subject.logout();
	}
	
}
