package hao.framework.web.form.validate;

/***
 * 验证信息
 * 
 * @author chianghao
 *
 */
public class ValidateInfo {

	private ValidateType type;
	
	private String[] params;
	
	/***
	 * 构造函数
	 * @param inputInfo
	 * @param type
	 */
	public ValidateInfo(ValidateType type,String[] params) {
		this.type = type;
		this.params = params;
	}
	
	/***
	 * 构造函数
	 * @param inputInfo
	 * @param type
	 */
	public ValidateInfo(ValidateType type) {
		this.type = type;
	}
	
	public ValidateType getType() {
		return type;
	}
	public void setType(ValidateType type) {
		this.type = type;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	
}
