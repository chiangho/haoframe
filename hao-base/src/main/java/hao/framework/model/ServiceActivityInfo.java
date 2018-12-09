package hao.framework.model;

/***
 * 事务调用情况
 * @author 蒋昊
 *
 */
public class ServiceActivityInfo{
	
	
	private String userId;//用户主键
	private String beginTime;//执行开始时间   yyyy-MM-dd HH:mm:ss:SSS
	private String endTime;//执行结束时间   yyyy-MM-dd HH:mm:ss:SSS
	private String clazz;//执行类
	private String method;//执行方法
	private String code;//编号  clazz+method
	private int    end;//执行结果 1表示成功  0表示失败
	private Object[] params;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	
}
