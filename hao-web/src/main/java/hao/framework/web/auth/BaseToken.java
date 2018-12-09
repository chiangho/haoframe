package hao.framework.web.auth;

import java.io.Serializable;

public class BaseToken implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String account;
	protected String password;
	
	public BaseToken(String account,String password) {
		this.account = account;
		this.password = password;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
