package hao.framework.web.shiro;

import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.alibaba.druid.util.StringUtils;

import hao.framework.web.auth.AuthInterface;


public class AccountAuthorizationRealm extends AuthorizingRealm {

	private AuthInterface authInterface;
	
	public AuthInterface getAuthInterface() {
		return authInterface;
	}
	public void setAuthInterface(AuthInterface authInterface) {
		this.authInterface = authInterface;
	}
	/***
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		MemberPrincipal p = (MemberPrincipal) principals.getPrimaryPrincipal();
		Set<String> permits = this.authInterface.queryUserPermit(p.getAccess());
		Set<String> roles   = this.authInterface.queryUserRole(p.getAccess());
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		//设置许可
		if(permits!=null&&permits.size()>0) {
			authorizationInfo.addStringPermissions(permits);
		}
		//设置权限
		if(roles!=null&&roles.size()>0) {
			authorizationInfo.addRoles(roles);
		}
		return authorizationInfo;
	}

	/***
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//判断是否从本地的数据库认证
		MemberToken memberToken = (MemberToken) token;
		String password="";//authInterface.queryPassword(memberToken);
		if(StringUtils.isEmpty(password)) {
			throw new UnknownAccountException();
		}
		MemberPrincipal p = new MemberPrincipal(memberToken.getUsername(), memberToken.getMobilPhone(), memberToken.getEmail());
		return new SimpleAuthenticationInfo(p, password, getName());
	}
	

}
