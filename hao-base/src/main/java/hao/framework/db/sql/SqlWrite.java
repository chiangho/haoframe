package hao.framework.db.sql;

import java.util.HashMap;
import java.util.Map;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.core.expression.HaoException;
import hao.framework.db.sql.SqlCommon.SqlLinkOperator;
import hao.framework.db.sql.SqlCommon.SqlOperator;

/**
 * sql写操纵
 * @author chianghao
 *
 */
public class SqlWrite{

	
	/**
	 * 写操作的类型
	 * @author chianghao
	 *
	 */
	public enum SqlWriterAction{
		insert,
		update,
		delete;
	}
	
	private       Class<?>           clazz;
	private       SqlWriterAction    action;
	private       Map<String,Object> fields;
	private       SqlWhere           sqlWhere;
	private       EntityInfo         entityInfo;
	
	
	public SqlWrite addCondition(String fieldName, SqlOperator operator, Object value) throws HaoException {
		return addCondition(fieldName,operator,new Object[] {value},SqlLinkOperator.and);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SqlWrite addCondition(String fieldName,SqlOperator operator,Object value,SqlLinkOperator linkOperator) throws HaoException {
		return addCondition(fieldName,operator,new Object[] {value},linkOperator);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SqlWrite addCondition(String fieldName,Object value) throws HaoException {
		return addCondition(fieldName,SqlOperator.equal,new Object[] {value},SqlLinkOperator.and);
	}
	/***
	 * 添加查询条件
	 * @param fieldName
	 * @param operator
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SqlWrite addCondition(String fieldName,SqlOperator operator,Object[] values) throws HaoException {
		return addCondition(fieldName,operator,values,SqlLinkOperator.and);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param operator
	 * @param value
	 * @return
	 * @throws HaoException 
	 */
	public SqlWrite addCondition(String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) throws HaoException {
		this.sqlWhere.addCondition(clazz, fieldName, operator, values, linkOperator);
		return this;
	}
	
	/**
	 * 添加元素
	 * @param field
	 * @param value
	 */
	public void addField(String field,Object value) {
		Property p = this.entityInfo.getProperty(field);
		if(p==null) {
			return;
		}
		if(!p.isDB()) {
			return;
		}
		if(value==null) {
			return;
		}
		fields.put(field, value);
	}
	
	public SqlWrite(Class<?> clazz,SqlWriterAction action){
		this.clazz       =   clazz;
		this.action      =   action;
		this.entityInfo  =   SystemConfig.getEntity(clazz);
		this.sqlWhere    =   new SqlWhere();
		this.fields      =   new HashMap<String,Object>();
	}
	
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}

	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}

	public SqlWriterAction getAction() {
		return action;
	}

	public void setAction(SqlWriterAction action) {
		this.action = action;
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setFields(Map<String, Object> fields) {
		if(fields==null) {
			return;
		}
		this.fields = fields;
	}

	public SqlWhere getSqlWhere() {
		return sqlWhere;
	}

	public void setSqlWhere(SqlWhere sqlWhere) {
		this.sqlWhere = sqlWhere;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	
}
