package hao.framework.web.form.validate;


/**
 * 认证类型
 * @author chianghao
 */
public enum ValidateType {

	REQUIRED("REQUIRED","不能为空","checkNotNull"),
	IDCODE("IDCODE","身份证验证","checkIDCode"),
	EMAIL("EMAIL","邮箱","checkEmail"),
	POSITIVE("POSITIVE","正数","checkPositive"),
	NEGATIVE("NEGATIVE","负数","checkNegative"),
	INTEGER("INTEGER","整数","checkInteger"),
	LENGTH("LENGTH","长度","checkLength"),
	ODD("ODD","奇数","checkOdd"),
	EVEN("EVEN","偶数","checkEven"),
	PRECISION("PRECISION","精度","checkPrecision"),
	DECIMAL("DECIMAL","小数","checkDecimal"),
	DATE("DATE","日期","checkDate"),
	BANKCARD("BANKCARD","银行卡","checkBankcard"),
	EQUALTO("EQUALTO","和某个字段一样","equalTo"),
	BETWEEN("BETWEEN","数值在什么之间","between"),
	ONLYONE("ONLYONE","数据唯一","onlyone"),
	URL("URL","http地址","checkUrl"),
	MOBILE_PHONE("MOBILE_PHONE","验证手机号码","checkMobilePhone")
	;
	
	private String code;
	private String title;
	private String function;
	
	
	private ValidateType(String code,String title,String function) {
		this.code = code;
		this.title = title;
		this.function = function;
	}
	


	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * 根据名字获取枚举类型
	 * @param typeCode
	 * @return
	 */
	public static ValidateType getValidateType(String typeCode) {
		for(ValidateType type:ValidateType.values()) {
			if(type.getCode().equals(typeCode)) {
				return type;
			}
		}
		return null;
	}
}
