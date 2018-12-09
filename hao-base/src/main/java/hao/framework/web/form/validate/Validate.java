package hao.framework.web.form.validate;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import hao.framework.core.CommonVar.SystemMsg;
import hao.framework.core.SpringContext;
import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.core.expression.HaoException;
import hao.framework.db.dao.jdbc.JdbcDao;
import hao.framework.utils.ConverterUtils;
import hao.framework.utils.IDCardUtil;
import hao.framework.utils.ValidateUtils;

/***
 * 校验借口
 * @author chianghao
 *
 */
public class Validate {
	
	public static final String 	VALIDATELENGTH_COMPARISON_EQUAL="EQUAL";
	public static final String 	VALIDATELENGTH_COMPARISON_GREATER_EQUAL="GREATER_EQUAL";
	public static final String 	VALIDATELENGTH_COMPARISON_GREATER="GREATER";
	public static final String 	VALIDATELENGTH_COMPARISON_LESS_EQUAL="LESS_EQUAL";
	public static final String 	VALIDATELENGTH_COMPARISON_LESS="LESS";
	
	Class<?> clazz;
	String fieldName;
	ValidateInfo validateInfo;
	Map<String,String> paramMap;
	EntityInfo  entityInfo;
	String content;
	ValidateType type;
	Property property;
	boolean isInsert;
	
	public Validate(Class<?> clazz,String fieldName,ValidateInfo validateInfo,Map<String,String> paramMap,boolean isInsert) {
		this.clazz          =   clazz;
		this.fieldName      =   fieldName;
		this.validateInfo   =   validateInfo;
		this.paramMap       =   paramMap;
		
		this.content        =   paramMap.get(fieldName);
		this.entityInfo     =   SystemConfig.getEntity(clazz);
		this.type           =   validateInfo.getType();
		this.property       =   entityInfo.getProperty(fieldName);
		this.isInsert       = isInsert;
	}
	
	
	public ValidateInfo getValidateInfo() {
		return validateInfo;
	}
	public void setValidateInfo(ValidateInfo validateInfo) {
		this.validateInfo = validateInfo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ValidateType getType() {
		return type;
	}
	public void setType(ValidateType type) {
		this.type = type;
	}
	
	/**
	 * 校验http地址
	 * @param content
	 * @return
	 */
	public boolean checkUrl() {
		return ValidateUtils.checkUrl(content);
	}
	
	/**
	 * 校验银行卡
	 * @param content
	 * @return
	 */
	public boolean checkBankcard() {
		return ValidateUtils.checkBankcard(content);
	}

	/**
	 * 校验是否是小数
	 * @param content
	 * @return
	 */
	public boolean checkDecimal() {
		return ValidateUtils.checkDecimal(content);
	}

	/**
	 * 校验奇数
	 * @param content
	 * @return
	 */
	public boolean checkEven() {
		return ValidateUtils.checkEven(content);
	}

	/**
	 * 校验偶数
	 * @param content
	 * @return
	 */
	public boolean checkOdd() {
		return ValidateUtils.checkOdd(content);
	}

	/**
	 * 校验整数
	 * @param content
	 * @return
	 */
	public boolean checkInteger() {
		// TODO Auto-generated method stub
		return ValidateUtils.checkInteger(content);
	}
	/**
	 * 校验负数
	 * @param content
	 * @return
	 */
	public boolean checkNegative() {
		return ValidateUtils.checkNegative(content);
	}
	/**
	 * 校验正数
	 * @param content
	 * @return
	 */
	public boolean checkPositive() {
		return ValidateUtils.checkPositive(content);
	}
	/**
	 * 校验邮箱
	 * @param content
	 * @return
	 */
	public boolean checkEmail() {
		return ValidateUtils.checkEmail(content);
	}
	/**
	 * 校验手机
	 * @param content
	 * @return
	 */
	public boolean checkMobilePhone(String content) {
		return ValidateUtils.checkMobile(content);
	}
	/**
	 * 校验身份证
	 * @param content
	 * @return
	 */
	public boolean checkIDCode() {
		return IDCardUtil.isIDCard(content);
	}
	
	/**
	 * 校验长度
	 * @return
	 * @throws HaoException
	 */
	public boolean checkLength() throws HaoException {
		String comparisonType = this.validateInfo.getParams()[0];
		String _length = this.validateInfo.getParams()[1];
		int length = (int) ConverterUtils.stringToInt(_length);
		if(comparisonType==null||comparisonType.equals("")) {
			throw new HaoException("NO_PARAM_EQUEA_VALUE", "缺少参数【比对类型】");
		}
		if(length==0) {
			throw new HaoException("NO_PARAM_EQUEA_VALUE", "缺少参数【比对长度】");
		}
		if(content==null) {
			return false;
		}
		if(comparisonType.equals(VALIDATELENGTH_COMPARISON_EQUAL)) {
			if(content.length()==length) {
				return true;
			}
		}
		if(comparisonType.equals(VALIDATELENGTH_COMPARISON_GREATER_EQUAL)) {
			if(content.length()>=length) {
				return true;
			}
		}
		if(comparisonType.equals(VALIDATELENGTH_COMPARISON_GREATER)) {
			if(content.length()>length) {
				return true;
			}
		}
		if(comparisonType.equals(VALIDATELENGTH_COMPARISON_LESS_EQUAL)) {
			if(content.length()<=length) {
				return true;
			}
		}
		if(comparisonType.equals(VALIDATELENGTH_COMPARISON_LESS)) {
			if(content.length()<length) {
				return true;
			}
		}
		return false;
	}
	/***
	 * 校验是否在两个数之间
	 * @return
	 */
	public boolean between() {
		if(content==null||content.equals("")) {
			return false;
		}
		BigDecimal lowNum = new BigDecimal(validateInfo.getParams()[0]);
		BigDecimal upNum = new BigDecimal(validateInfo.getParams()[1]);
		try {
			BigDecimal tag = new BigDecimal(content);
			if(!(tag.compareTo(lowNum)==-1||tag.compareTo(upNum)==1)) {
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/***
	 * 判断是否是唯一的
	 * @return
	 * @throws HaoException
	 */
	public boolean onlyone() throws HaoException {
		JdbcDao   dao           =  SpringContext.getBean(JdbcDao.class);
		Set<Property> propertys =  entityInfo.getPropertyIsKey();
		Property      p         =  entityInfo.getProperty(fieldName);        
		if(!p.isDB()) {
			throw new HaoException(SystemMsg.validate_field_not_column);
		}
		if(propertys==null||propertys.size()==0) {
			//检查主键不存在
			String sql = "select count(1) from "+entityInfo.getTableName()+" where "+p.getColumnName()+"=?";
			int count = dao.queryInt(sql,new Object[] {paramMap.get(p.getName())});
			if(count==0) {
				return true;
			}
		}else {
			//判断是否是主键，如果是主键不需要校验
			for(Property _p:propertys) {
				if(p.getColumnName().equals(_p.getColumnName())) {
					return true;
				}
			}
			//通过主键判断更新还是插入
			StringBuffer sb = new StringBuffer();
			boolean isInsert= false;
			for(Property _p:propertys) {
				if(paramMap.get(_p.getName())==null||paramMap.get(_p.getName()).equals("")) {
					isInsert = true;
					break;
				}
			}
			if(isInsert) {
				String sql = "select count(1) from "+entityInfo.getTableName()+" where "+p.getColumnName()+"=?";
				int count = dao.queryInt(sql,new Object[] {paramMap.get(p.getName())});
				if(count==0) {
					return true;
				}
			}else {
				Vector<String> v = new Vector<String>();
				sb.append("select count(1) from "+entityInfo.getTableName()+" where "+p.getColumnName()+"=?  and");
				v.add(paramMap.get(p.getName()));
				for(Property _p:propertys) {
					sb.append("  "+_p.getField().getName()+"!=? and");
					v.add(paramMap.get(_p.getName()));
				}
				String sql = sb.toString();
				if(sql.endsWith("and"));
				sql = sql.substring(0,sql.lastIndexOf("and"));
				int count = dao.queryInt(sql,v.toArray(new String[v.size()]));
				if(count==0) {
					return true;
				}
			}
				
		}
		return false;
	}
	
	/**
	 * 校验精度
	 * @return
	 */
	public boolean checkPrecision() {
		return false;
	}
	
	/***
	 * 校验是否一致
	 * @return
	 * @throws HaoException
	 */
	public boolean equalTo() throws HaoException {
		String tagFieldName = this.validateInfo.getParams()[0];
		String value = this.paramMap.get(tagFieldName);
		if(value==null) {
			throw new HaoException("NO_PARAM_EQUEA_VALUE", "目标值为null");
		}
		if(content==null||content.equals("")) {
			return false;
		}
		if(content.equals(value)) {
			return true;
		}
		return false;
	}

	public boolean checkNotNull() {
		if(this.content==null||this.content.equals("")) {
			return false;
		}
		return true;
	}
	
	/***
	 * 校验日期是否符合规定格式
	 * @return
	 * @throws HaoException
	 */
	public boolean checkDate() throws HaoException {
		String format = this.validateInfo.getParams()[0];
		if(format==null||format.equals("")) {
			throw new HaoException("LOST_DATE_FORMAT","缺少日期格式【时间格式】");
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.parse(content);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	
   /**
    * 获取警告信息
    * @return
    */
   public String getWarnMeg() {
		switch(type) {
		case IDCODE:
			return this.property.getTitle()+"请填写合法的身份证号码";
		case MOBILE_PHONE:
			return this.property.getTitle()+"请填写合法的手机号码";
		case EMAIL:
			return this.property.getTitle()+"请填写合法的邮件";
		case POSITIVE:
			return this.property.getTitle()+"请填写正数";
		case NEGATIVE:
			return this.property.getTitle()+"请填写负数";
		case INTEGER:
			return this.property.getTitle()+"请填写整数";
		case DECIMAL:
			return this.property.getTitle()+"请填写小数";
		case BANKCARD:
			return this.property.getTitle()+"请填写合法的银行卡号";
		case URL:
			return this.property.getTitle()+"请填写合法的http或者https协议地址";
		case PRECISION:
			return this.property.getTitle()+"请填写符合精度（"+this.validateInfo.getParams()[0]+"）的数值！";
		case DATE:
			return this.property.getTitle()+"时间格式不对，请填写如下格式【"+this.validateInfo.getParams()[0]+"】";
		case BETWEEN:
			return this.property.getTitle()+"请填写"+this.validateInfo.getParams()[0]+"到"+this.validateInfo.getParams()[1]+"之间的数值";
		case EVEN:
			return this.property.getTitle()+"请填写偶数";
		case EQUALTO:
			return this.property.getTitle()+"和"+this.validateInfo.getParams()[0]+"值不一致，请重新填写";
		case LENGTH:
			String comparisonType = this.validateInfo.getParams()[0];
			String _length = this.validateInfo.getParams()[1];
			int length = (int) ConverterUtils.stringToInt(_length);
			if(comparisonType.equals(VALIDATELENGTH_COMPARISON_EQUAL)) {
				return this.property.getTitle()+"长度等于"+length;
			}
			if(comparisonType.equals(VALIDATELENGTH_COMPARISON_GREATER_EQUAL)) {
				return this.property.getTitle()+"长度大于等于"+length;
			}
			if(comparisonType.equals(VALIDATELENGTH_COMPARISON_GREATER)) {
				return this.property.getTitle()+"长度大于"+length;
			}
			if(comparisonType.equals(VALIDATELENGTH_COMPARISON_LESS_EQUAL)) {
				return this.property.getTitle()+"长度小于等于"+length;
			}
			if(comparisonType.equals(VALIDATELENGTH_COMPARISON_LESS)) {
				return this.property.getTitle()+"长度小于"+length;
			}
			return "";
		case ODD:
			return this.property.getTitle()+"请填写奇数";
		case ONLYONE:
			return this.property.getTitle()+"数据库中必须唯一";
		case REQUIRED:
			return this.property.getTitle()+"不能为空！";
		default:
			return "";
		}
	}
	
    public boolean check() throws HaoException {
		String functionName = this.type.getFunction();
		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod(functionName);
		}catch(Exception e){
			e.printStackTrace();
			throw new HaoException(SystemMsg.auth_no_find_check_method);
		}
		if(method==null) {
			throw new HaoException(SystemMsg.auth_no_find_check_method);
		}
		try {
			Object value = null;
			value =  method.invoke(this);
			return value==null?false:(boolean) value;
		}catch(Exception e) {
			throw new HaoException(SystemMsg.auth_error.getCode(),e.getMessage());	
		}
	}
	
}
