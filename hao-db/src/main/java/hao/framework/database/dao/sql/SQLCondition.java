package hao.framework.database.dao.sql;

import hao.framework.database.dao.sql.SQLCommon.SqlLinkOperator;
import hao.framework.database.dao.sql.SQLCommon.SqlOperator;
import hao.framework.database.entity.ColumnInfo;
import hao.framework.database.entity.EntityInfo;

public class SQLCondition {
	
	
	private Class<?> clazz;
	private String fieldName;
	private SqlOperator operator;
	private Object[] values;
	private SqlLinkOperator linkOperator;
	private ColumnInfo columnInfo;
	private EntityInfo entityInfo;
	
//	private void check(Class<?> clazz,String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) {
//		
//	}
	
	
	public SQLCondition(Class<?> clazz,String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) {
		//check(clazz,fieldName,operator,values,linkOperator);
		this.clazz = clazz;
		this.fieldName = fieldName;
		this.operator = operator;
		this.values = values;
		this.linkOperator=linkOperator;
		this.entityInfo  = EntityInfo.getEntity(clazz);
		this.columnInfo = entityInfo.getColumnInfo(fieldName);
		
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
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}

	public ColumnInfo getColumnInfo() {
		return columnInfo;
	}

	public void setColumnInfo(ColumnInfo columnInfo) {
		this.columnInfo = columnInfo;
	}

	
	
}
