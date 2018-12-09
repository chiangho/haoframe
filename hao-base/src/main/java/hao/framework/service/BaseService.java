package hao.framework.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.core.expression.HaoException;
import hao.framework.db.dao.jdbc.JdbcDao;
import hao.framework.db.dao.mybatis.map.BaseDaoMapper;
import hao.framework.db.page.Page;
import hao.framework.db.page.PageHelper;
import hao.framework.db.sql.SqlSearch;
import hao.framework.utils.ClassUtils;
import hao.framework.utils.ConverterUtils;

public abstract class BaseService {

	@Autowired
    protected  BaseDaoMapper dao; 
	
	@Autowired
    protected  JdbcDao       jdbcDao;
	
	/**
	 * 更具某一个属性查询
	 * @param clazz
	 * @param fieldName
	 * @param value
	 * @return
	 * @throws HaoException
	 */
	protected final <T> T queryByField(Class<?> clazz,String fieldName,Object value) throws HaoException {
		SqlSearch sqlSearch = new SqlSearch(clazz);
		sqlSearch.addCondition(fieldName, value);
		return queryBean(sqlSearch);
	}
	/**
	 * 更具某一个属性查询
	 * @param clazz
	 * @param fieldName
	 * @param value
	 * @return
	 * @throws HaoException
	 */
	protected final <T> List<T> queryListByField(Class<?> clazz,String fieldName,Object value) throws HaoException {
		SqlSearch sqlSearch = new SqlSearch(clazz);
		sqlSearch.addCondition(fieldName, value);
		return queryList(sqlSearch);
	}
	
	
	/**
	 * 根据查询条件查询对象
	 * @param sqlSearch
	 * @return
	 * @throws HaoException
	 */
	protected final  <T> T queryBean(SqlSearch sqlSearch) throws HaoException {
		Map<String,Object> map = dao.queryBean(sqlSearch);
		T bean  = mapToBean(map,sqlSearch.getEntityInfo());
		return bean;
	}
	
	
	protected final <T> List<T> queryAll(Class<?> clazz) throws HaoException{
		SqlSearch sqlSearch = new SqlSearch(clazz);
		return queryList(sqlSearch);
	}
	/**
	 * 查询列表
	 * @param sqlSearch
	 * @return
	 * @throws HaoException
	 */
	protected final <T> List<T> queryList(SqlSearch sqlSearch) throws HaoException{
		List<Map<String,Object>> List = dao.queryList(sqlSearch);
		List<T> tList = new ArrayList<T>();
		for(Map<String,Object> map:List) {
			T bean  = mapToBean(map,sqlSearch.getEntityInfo());
			tList.add(bean);
		}
		return tList;
	}
	/***
	 * 分页查询
	 * @param page
	 * @param sqlSearch
	 * @return
	 * @throws HaoException
	 */
//	protected final <T> List<T> queryPageList(Page page,SqlSearch sqlSearch) throws HaoException{
//		PageHelper.setPage(BaseDaoMapper.class, "queryList", page);
//		return queryList(sqlSearch);
//	}
	@SuppressWarnings("unchecked")
	private <T> T mapToBean(Map<String,Object> map,EntityInfo entityInfo) throws HaoException {
		if(map==null) {
			return null;
		}
		try {
			T bean = (T) entityInfo.getClazz().newInstance();
			for(String key:map.keySet()) {
				Object tagValue = map.get(key);
				Property p = entityInfo.getProperty(key);
				Object value = null;
				if(p==null) {
					ClassUtils.setFieldValue(bean,key,tagValue);
				}else {
					value = toObjectValue(p,tagValue);
					ClassUtils.setFieldValue(bean,key,value);
				}
			}
			return bean;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	private Object toObjectValue(Property property ,Object value) {
		Class<?> fieldType = property.getFieldType();
		if(int.class.isAssignableFrom(fieldType)||Integer.class.isAssignableFrom(fieldType)){
			return ConverterUtils.objectToInt(value);
		}else if(fieldType.isAssignableFrom(Short.class)) {
			return ConverterUtils.objectToInt(value);
		}else if(fieldType.isAssignableFrom(Long.class)) {
			return ConverterUtils.objectToLong(value);
		}else if(fieldType.isAssignableFrom(Float.class)) {
			return ConverterUtils.objectToFloat(value);
		}else if(fieldType.isAssignableFrom(Double.class)) {
			return ConverterUtils.objectToDouble(value);
		}else if(fieldType.isAssignableFrom(BigDecimal.class)) {
			return ConverterUtils.objectToDecimal(value);
		}else if(fieldType.isAssignableFrom(Date.class)) {
			String formatter = property.getTimeFormat();
			if(formatter==null||formatter.equals("")) {
				formatter=SystemConfig.SYSTEM_TIME_FORMAT;
			}
			return ConverterUtils.objectToDate(value,formatter);
		}else if(fieldType.isAssignableFrom(char.class)) {
			return ConverterUtils.objectToChar(value);
		}else if(fieldType.isAssignableFrom(byte.class)) {
			return ConverterUtils.objectToByte(value);
		}else if(fieldType.isAssignableFrom(String.class)) {
			return ConverterUtils.objectToString(value);
		} else {
			return value;
		}
	}
	
}
