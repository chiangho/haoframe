package hao.framework.web.auth.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import hao.framework.core.CommonVar.SystemMsg;
import hao.framework.core.HaoProperty;
import hao.framework.core.expression.HaoException;
import hao.framework.service.LocalAuthServer;



public class AccountAuthorizationRealm extends AuthorizingRealm {


	@Autowired
	LocalAuthServer localAuthServer;
	
	/***
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		return null;
	}

	/***
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//判断是否从本地的数据库认证
		if(HaoProperty.getValue("auth_type").equals("local")){
			MemberToken memberToken = (MemberToken) token;
			String password="";
			try {
				password = this.localAuthServer.queryPassword(memberToken);
			} catch (HaoException e) {
				throw new UnknownAccountException(e.errorMsg);
			}
			if(password==null||password.equals("")) {
				throw new UnknownAccountException(SystemMsg.auth_no_find_access.getMsg());
			}
			Principal p = new Principal(memberToken.getUsername(), memberToken.getMobilPhone(), memberToken.getEmail(), password);
			return new SimpleAuthenticationInfo(p, password, getName());
		}else{
			throw new UnknownAccountException(SystemMsg.auth_no_find_auth_type.getMsg());// 用户不存在
		}
	}
	

}
