package hao.framework.web.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import hao.framework.core.utils.MD5;

/***
 * 校验密码
 * 默认md5 32位加密
 * @author chianghao
 */
public class CustomCredentialsMatcher extends HashedCredentialsMatcher{
	
	/**
	 * 算法名称
	 */
	private String algorithm;
	
	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/***
	 * 验证密码是否正确
	 */
	public boolean doCredentialsMatch(AuthenticationToken authcToken,AuthenticationInfo info) {
		if(this.algorithm==null||this.algorithm.equals("")) {
			this.algorithm = "MD5";
		}
		MemberToken token = (MemberToken) authcToken;//提供的钥匙密码
        String accountCredentials = info.getCredentials().toString();//数据库密码
        String comparePassword = "";
        if(this.algorithm.equals("MD5")) {
        	comparePassword = MD5.encode((new String(token.getPassword())));
        	if(comparePassword.toUpperCase().equals(accountCredentials.toUpperCase())) {
        		return true;
        	}
		}
		return false;
    }

}
