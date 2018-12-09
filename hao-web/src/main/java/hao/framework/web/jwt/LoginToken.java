package hao.framework.web.jwt;

import hao.framework.core.expression.HaoException;
import hao.framework.web.auth.BaseToken;

/***
 * token
 * @author 蒋昊
 *
 */
public class LoginToken extends BaseToken{
	
	private static final long serialVersionUID = 1577661986165098932L;
	
	private String mobilPhone;
	private String email;
	

	public LoginToken(String account,String password) throws HaoException {
		super(account, password);
	}


	public String getMobilPhone() {
		return mobilPhone;
	}

	public void setMobilPhone(String mobilPhone) {
		this.mobilPhone = mobilPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
