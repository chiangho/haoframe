package hao.framework.database.dao.sql;

import java.util.HashMap;
import java.util.Map;

import hao.framework.core.expression.HaoException;
import hao.framework.database.dao.sql.SQLCommon.SqlLinkOperator;
import hao.framework.database.dao.sql.SQLCommon.SqlOperator;
import hao.framework.database.entity.ColumnInfo;
import hao.framework.database.entity.EntityInfo;



/**
 * sql写操纵
 * @author chianghao
 *
 */
public class SQLWrite{

	
	/**
	 * 写操作的类型
	 * @author chianghao
	 *
	 */
	public enum SQLWriteAction{
		insert,
		update,
		delete;
	}
	
	private       Class<?>           clazz;
	private       SQLWriteAction    action;
	private       Map<String,Object> fields;
	private       SQLWhere           sQLWhere;
	private       EntityInfo         entityInfo;
	
	
	public SQLWrite addCondition(String fieldName, SqlOperator operator, Object value) throws HaoException {
		return addCondition(fieldName,operator,new Object[] {value},SqlLinkOperator.and);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SQLWrite addCondition(String fieldName,SqlOperator operator,Object value,SqlLinkOperator linkOperator) throws HaoException {
		return addCondition(fieldName,operator,new Object[] {value},linkOperator);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SQLWrite addCondition(String fieldName,Object value) throws HaoException {
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
	public SQLWrite addCondition(String fieldName,SqlOperator operator,Object[] values) throws HaoException {
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
	public SQLWrite addCondition(String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) throws HaoException {
		this.sQLWhere.addCondition(clazz, fieldName, operator, values, linkOperator);
		return this;
	}
	
	/**
	 * 添加元素
	 * @param field
	 * @param value
	 */
	public void addField(String field,Object value) {
		ColumnInfo p = this.entityInfo.getColumnInfo(field);
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
	
	public SQLWrite(Class<?> clazz,SQLWriteAction action){
		this.clazz       =   clazz;
		this.action      =   action;
		this.entityInfo  =   EntityInfo.getEntity(clazz);
		this.sQLWhere    =   new SQLWhere();
		this.fields      =   new HashMap<String,Object>();
	}
	
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}

	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}

	public SQLWriteAction getAction() {
		return action;
	}

	public void setAction(SQLWriteAction action) {
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

	public SQLWhere getSqlWhere() {
		return sQLWhere;
	}

	public void setSqlWhere(SQLWhere sQLWhere) {
		this.sQLWhere = sQLWhere;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	
}
