package hao.framework.web.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

import hao.framework.core.expression.HaoException;
import hao.framework.web.context.MSG;

/***
 * token
 * @author 蒋昊
 *
 */
public class MemberToken extends UsernamePasswordToken{
	
	private static final long serialVersionUID = 1577661986165098932L;
	/***
	 * 构建账号登录的钥匙
	 * @param access
	 * @param password
	 * @return
	 * @throws HaoException 
	 */
	public static MemberToken createAccessToken(String access,String mobilPhone,String email,String password) throws HaoException {
		if((access==null||access.equals(""))&&(mobilPhone==null||mobilPhone.equals(""))&&(email==null||email.equals(""))) {
			throw MSG.auth_missing_access.getException();
		}
		if(password==null||password.equals("")) {
			throw MSG.auth_missing_password.getException();
		}
		MemberToken token  = new MemberToken();
		if(access!=null&&!access.equals("")) {
			token.setUsername(access);
		}
		if(mobilPhone!=null&&!mobilPhone.equals("")) {
			token.setMobilPhone(mobilPhone);
		}
		if(email!=null&&!email.equals("")) {
			token.setEmail(email);
		}
		token.setPassword(password.toCharArray());
		return token;
	}
	
	/***
	 * 空构造函数
	 */
	private MemberToken() {
		
	}
	/**
	 * 更具账号密码构建
	 * @param access
	 * @param password
	 */
	private MemberToken(String access,String password) {
		super(access,password.toCharArray());
	}
	/**
	 * 手机
	 */
	public String mobilPhone;
	
	/**
	 * 电子邮件
	 */
	public String email;
	
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilPhone() {
		return mobilPhone;
	}

	public void setMobilPhone(String mobilPhone) {
		this.mobilPhone = mobilPhone;
	}
	
}
