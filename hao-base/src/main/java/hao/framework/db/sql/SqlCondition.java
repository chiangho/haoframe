package hao.framework.db.sql;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.db.sql.SqlCommon.SqlLinkOperator;
import hao.framework.db.sql.SqlCommon.SqlOperator;

public class SqlCondition {
	
	
	private Class<?> clazz;
	private String fieldName;
	private SqlOperator operator;
	private Object[] values;
	private SqlLinkOperator linkOperator;
	private Property property;
	private EntityInfo entityInfo;
	
	/**
	 * 校验提交你的参数是否合法
	 * @return
	 */
	private void check(Class<?> clazz,String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) {
		
	}
	
	public SqlCondition(Class<?> clazz,String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) {
		check(clazz,fieldName,operator,values,linkOperator);
		this.clazz = clazz;
		this.fieldName = fieldName;
		this.operator = operator;
		this.values = values;
		this.linkOperator=linkOperator;
		this.entityInfo  = SystemConfig.getEntity(clazz);
		this.property = entityInfo.getProperty(fieldName);
		
	}
	
	
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public SqlOperator getOperator() {
		return operator;
	}
	public void setOperator(SqlOperator operator) {
		this.operator = operator;
	}
	public Object[] getValues() {
		return values;
	}
	public void setValues(Object[] values) {
		this.values = values;
	}
	public SqlLinkOperator getLinkOperator() {
		return linkOperator;
	}
	public void setLinkOperator(SqlLinkOperator linkOperator) {
		this.linkOperator = linkOperator;
	}
	public Property getProperty() {
		return property;
	}
	public void setProperty(Property property) {
		this.property = property;
	}
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}

	
	
}
