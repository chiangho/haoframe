package hao.framework.web.context;

import hao.framework.core.expression.HaoException;

/**
 * 提示信息枚举
 * @author chianghao
 *
 */
public enum MSG {
	
	success("000000","操作成功"),
	faild("999999","操作失败"),
	no_authentication("no_authentication","未认证，请先登录认证"),
	no_authorization("no_authorization","未授权，不能操作"),
	auth_missing_password("missing_password","请填写密码"),
	auth_missing_access("missing_access","请填写账号");
	
	
	private String code;
	private String msg;
	
	private MSG(String code,String msg) {
		this.code = code;
		this.msg  = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public HaoException getException() {
		return new HaoException(this.getCode(),this.msg);
	}
	
}
