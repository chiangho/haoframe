package hao.framework.web;



/***
 * 请求响应的状态
 * @author chianghao
 *
 */
public class RequestStatus {

	public enum REQUEST_STATUS{
		
		SUCCESS("000000","操作成功"),
		NO_PERMISSION("NO_PERMISSION","未获取许可"),
		ERROR("ERROR","操作失败！"),
		NO_AOUTH("NO_AOUTH","未认证！")
		;
		
		private String rtnCode;
		private String rtnMsg;
		
		private REQUEST_STATUS(String rtnCode,String rtnMsg) {
			this.rtnCode = rtnCode;
			this.rtnMsg  = rtnMsg;
		}
		
		
		public String getCode() {
			return rtnCode;
		}
		public String getMsg(){
			return rtnMsg;
		}
	}
	
}
