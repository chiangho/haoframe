package hao.framework.web.form;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import hao.framework.core.CommonVar.SystemMsg;
import hao.framework.core.SpringContext;
import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.core.expression.HaoException;
import hao.framework.core.sequence.Seq;
import hao.framework.db.dao.jdbc.JdbcDao;
import hao.framework.utils.ClassUtils;
import hao.framework.utils.ConverterUtils;
import hao.framework.utils.StringUtils;
import hao.framework.web.form.validate.Validate;
import hao.framework.web.form.validate.ValidateInfo;
import hao.framework.web.form.validate.ValidateType;

/***
 * 表单提交
 * 
 * @author chianghao
 *
 */
public class Form {

	/***
	 * 参数分隔符
	 */
	private String params_split=",";
	/**
	 * 表单提交的参数
	 */
	private Map<String,String> paramMap = new HashMap<String,String>();
	
	/**
	 * 默认对象
	 */
	private Class<?> clazz;
	
	
	public Form(Class<?> clazz,HttpServletRequest request) throws HaoException {
		this.clazz = clazz;
		initFromDate(request);
	}


	/**
	 * 初始化表单数据
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	private void initFromDate(HttpServletRequest request) {
		Enumeration<String> em = request.getParameterNames();
		while (em.hasMoreElements()) {
			String paramName = em.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
                paramMap.put(paramName, paramValues[0]);
            } else {
                paramMap.put(paramName, StringUtils.arrayToStr(params_split, paramValues));
            }
		}
	}

	
	
	public void validate(String fieldName,ValidateType validateType) throws HaoException {
		validate(this.clazz,fieldName,validateType,null);
	}
	public void validate(String fieldName,ValidateType validateType,String[] params) throws HaoException {
		validate(this.clazz,fieldName,validateType,params);
	}
	public void validate(Class<?> clazz,String fieldName,ValidateType validateType) throws HaoException {
		validate(clazz,fieldName,validateType,null);
	}
	public void validate(Class<?> clazz,String fieldName,ValidateType validateType,String[] params) throws HaoException {
		ValidateInfo validateInfo = null;
		if(params==null) {
			validateInfo = new ValidateInfo(validateType);
		}else {
			validateInfo = new ValidateInfo(validateType,params);
		}
		Validate validate = new Validate(clazz,fieldName,validateInfo,this.paramMap,this.isInsertAction(clazz));
		boolean end = validate.check();
		if(end==false) {
			throw new HaoException("validate_error",validate.getWarnMeg());
		}
	}
	
	public Map<String,Object> getBeanMap(){
		return getBeanMap(this.clazz);
	}
	
	public Map<String,Object> getBeanMap(Class<?> clazz){
		EntityInfo entityInfo  = SystemConfig.getEntity(clazz);
		Map<String,Object> beanMap = new HashMap<String,Object>();
		for(Property p:entityInfo.getPropertys()) {
			String key = p.getName();
			String value= this.getParamValue(key);
			if(value!=null) {
				beanMap.put(key, value);
			}
		}
		return beanMap;
	}
	
	public <T> T getBean() throws HaoException {
		return getBean(this.clazz);
	}
	
	/**
	 * 获取bean对象
	 * 
	 * @return
	 * @throws HaoException 
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<?> clazz) throws HaoException {
		try {
			Object bean = clazz.newInstance();
			EntityInfo entityInfo = SystemConfig.getEntity(clazz);
			for(Property p:entityInfo.getPropertys()) {
				String field = p.getName();
				String value = this.getParamValue(field);
				if(value==null||value.equals("")) {
					continue;
				}
				Object tempValue = toObjectValue(p,value);
				if(tempValue==null) {
					throw new HaoException(SystemMsg.sub_form_change_data);
				}
				ClassUtils.setFieldValue(bean,field,tempValue);
			}
			return (T) bean;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/***
	 * 装换成对象值
	 * @return
	 */
	private Object toObjectValue(Property p,String value) {
		Class<?> fieldType = p.getFieldType();
		if(fieldType.isAssignableFrom(Integer.class)||fieldType.isAssignableFrom(int.class)) {
			return ConverterUtils.stringToInt(value);
		}else if(fieldType.isAssignableFrom(Long.class)||fieldType.isAssignableFrom(long.class)) {
			return ConverterUtils.stringToLong(value);
		}else if(fieldType.isAssignableFrom(Float.class)||fieldType.isAssignableFrom(float.class)) {
			return ConverterUtils.stringToFloat(value);
		}else if(fieldType.isAssignableFrom(Double.class)||fieldType.isAssignableFrom(double.class)) {
			return ConverterUtils.stringToDouble(value);
		}else if(fieldType.isAssignableFrom(Short.class)||fieldType.isAssignableFrom(short.class)) {
			return ConverterUtils.stringToDouble(value);
		}else if(fieldType.isAssignableFrom(BigDecimal.class)) {
			return ConverterUtils.stringToDecimal(value);
		}else if(fieldType.isAssignableFrom(Date.class)) {
			String formatter = p.getTimeFormat();
			if(formatter==null||formatter.equals("")) {
				formatter=SystemConfig.SYSTEM_TIME_FORMAT;
			}
			return ConverterUtils.stringToDate(value,formatter);
		}else if(fieldType.isAssignableFrom(char.class)) {
			return ConverterUtils.stringToChar(value);
		}else if(fieldType.isAssignableFrom(byte.class)) {
			return ConverterUtils.stringToByte(value);
		}else {
			//以上类型都不是则返回原值
			return value;
		}
	}
	
	/***
	 * 获取参数值
	 * @param name
	 * @return
	 */
	public String getParamValue(String name) {
		return this.paramMap.get(name);
	}

	/***
	 * 获取参数值的数组
	 * @param name
	 * @return
	 */
	public String[] getParamValues(String name) {
		String value = this.paramMap.get(name);
		return value.split(params_split);
	}

	public boolean isInsertAction() throws HaoException {
		return isInsertAction(this.clazz);
	}
	/***
	 * 是否插入操作
	 * @return
	 * @throws HaoException 
	 */
	public boolean isInsertAction(Class<?> clazz) throws HaoException {
		//判断本form是否含有此clazz的操作
		EntityInfo entity = SystemConfig.getEntity(clazz);
		if(entity==null) {
			throw new HaoException(SystemMsg.sub_form_not_exist_class);
		}
		Set<Property> propertys = entity.getPropertyIsKey();
		if(propertys==null||propertys.size()==0) {
			return true;
		}
		Map<String,String> keyValue = new HashMap<String,String>();
		for(Property p:propertys) {
			if(getParamValue(p.getName())==null||getParamValue(p.getName()).equals("")) {
				return true;
			}
			keyValue.put(p.getName(), getParamValue(p.getName()));
		}
		//数据库校验是否存在
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from "+entity.getTableName()+" where ");
		Vector<String> v = new Vector<String>();
		for(String key:keyValue.keySet()) {
			sb.append(" "+key+"=? and");
			v.add(keyValue.get(key));
		}
		JdbcDao dao = SpringContext.getBean(JdbcDao.class);
		String sql = sb.toString();
		if(sql.endsWith("and")) {
			sql = sql.substring(0,sql.lastIndexOf("and"));
		}
		int count = dao.queryInt(sql, v.toArray(new String[v.size()]));
		if(count==0) {
			return true;
		}
		return false;
	}

	public Map<String,String> setPrimaryValue() throws HaoException{
		return setPrimaryValue(this.clazz);
	} 
	/***
	 * 给主键设置值，
	 * @return 返回主键的值
	 * @throws HaoException 
	 */
	public Map<String,String> setPrimaryValue(Class<?> clazz) throws HaoException {
		EntityInfo entity =  SystemConfig.getEntity(clazz);
		if(entity==null) {
			throw new HaoException(SystemMsg.sub_form_not_exist_class);
		}
		Set<Property> propertys = entity.getPropertyIsKey();
		if(propertys!=null&&propertys.size()!=0) {
			Map<String,String> keyValue = new HashMap<String,String>();
			for(Property p:propertys) {
				if(getParamValue(p.getName())==null||getParamValue(p.getName()).equals("")) {
					String id = Seq.getNextId()+"";
					keyValue.put(p.getName(), id);
					//setFieldValue(p.getName(), id);
					paramMap.put(p.getName(), id);
				}
				keyValue.put(p.getName(), getParamValue(p.getName()));
			}
			return keyValue;
		}
		return null;
	}
	
	
	
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public void setParamValue(String key, String value) {
		this.paramMap.put(key, value);
	}
	
}
