package hao.framework.web.auth.shiro;

import java.io.Serializable;

public class Principal implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	
	private String access; // 登录名
	private String mobilePhone;//手机号码
	private String email;//邮件
	
	
	public Principal(
			String access,
			String mobilePhone,
			String email,
			String password
		) {
		this.access = access;
		this.mobilePhone = mobilePhone;
		this.email = email;
	}


	public String getAccess() {
		return access;
	}


	public void setAccess(String access) {
		this.access = access;
	}


	public String getMobilePhone() {
		return mobilePhone;
	}


	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	
}
