package hao.framework.core.expression;

import hao.framework.core.CommonVar.SystemMsg;

/**
 * 封装报错信息
 * @author 蒋昊
 * @since 2014-09-27 18:17
 */
public class HaoException extends Exception{
	
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 错误编码，默认是成功。
	 */
	public String errorCode = "000000";
	/**
	 * 错误信息
	 */
	public String errorMsg  = "操作成功";
	
	public HaoException(SystemMsg msg) {
		this(msg.getCode(),msg.getMsg());
	}
	
	public HaoException(String code,String msg){
		super(msg);
		this.errorCode = code;
		this.errorMsg  = msg;
	}
}
