package hao.framework.database.dao.sql;

import java.util.ArrayList;

import hao.framework.core.expression.HaoException;
import hao.framework.database.dao.sql.SQLCommon.SqlLinkOperator;
import hao.framework.database.dao.sql.SQLCommon.SqlOperator;
import hao.framework.database.entity.ColumnInfo;
import hao.framework.database.entity.EntityInfo;


public class SQLWhere {
	
	private ArrayList<SQLCondition> conditions;
	
	//private Map<String,Object> params;
	
	
	public SQLWhere() {
		//this.params = new HashMap<String,Object>();
		this.conditions = new ArrayList<SQLCondition>();
	}
	public ArrayList<SQLCondition> getConditions() {
		return conditions;
	}
	public void setConditions(ArrayList<SQLCondition> conditions) {
		this.conditions = conditions;
	}
//	public Map<String, Object> getParams() {
//		return params;
//	}
//	public void setParams(Map<String, Object> params) {
//		this.params = params;
//	}


	public void addCondition(Class<?> clazz,String fieldName,Object value) throws HaoException {
		addCondition(clazz,fieldName,SqlOperator.equal,new Object[] {value},SqlLinkOperator.and);
	}
	public void addCondition(Class<?> clazz,String fieldName,Object value,SqlLinkOperator linkOperator) throws HaoException {
		addCondition(clazz,fieldName,SqlOperator.equal,new Object[] {value},linkOperator);
	}
	public void addCondition(Class<?> clazz,String fieldName,SqlOperator operator,Object value) throws HaoException {
		addCondition(clazz,fieldName,operator,new Object[] {value},SqlLinkOperator.and);
	}
	public void addCondition(Class<?> clazz,String fieldName,SqlOperator operator,Object value,SqlLinkOperator linkOperator) throws HaoException {
		addCondition(clazz,fieldName,operator,new Object[] {value},linkOperator);
	}
	public void addCondition(Class<?> clazz,String fieldName,Object[] values) throws HaoException {
		addCondition(clazz,fieldName,SqlOperator.equal,values,SqlLinkOperator.and);
	}
	public void addCondition(Class<?> clazz,String fieldName,Object[] values,SqlLinkOperator linkOperator) throws HaoException {
		addCondition(clazz,fieldName,SqlOperator.equal,values,linkOperator);
	}
	public void addCondition(Class<?> clazz,String fieldName,SqlOperator operator,Object[] values) throws HaoException {
		addCondition(clazz,fieldName,operator,values,SqlLinkOperator.and);
	}
	public void addCondition(Class<?> clazz,String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) throws HaoException {
		EntityInfo entityInfo  = EntityInfo.getEntity(clazz);
		if(fieldName==null||fieldName.equals("")) {
			throw new HaoException("","创建sql语句失败，缺少字段名称");
		}
		ColumnInfo p = entityInfo.getColumnInfo(fieldName);
		if(p==null) {
			throw new HaoException("","该字段不存在");
		}
		if(p.isDB()==false) {
			throw new HaoException("","该字段为非数据库字段");
		}
		if(operator==null) {
			throw new HaoException("","创建sql语句失败，缺少操作符号");
		}
		if(values==null) {
			throw new HaoException("","创建sql语句失败，缺少值");
		}
		
		conditions.add(new SQLCondition(clazz,fieldName,operator,values,linkOperator));
//		if(values.length==1) {
//			params.put(fieldName, values[0]);
//		}else {
//			params.put(fieldName, values);
//		}
	}
}
