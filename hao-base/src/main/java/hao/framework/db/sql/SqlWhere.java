package hao.framework.db.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.core.expression.HaoException;
import hao.framework.db.sql.SqlCommon.SqlLinkOperator;
import hao.framework.db.sql.SqlCommon.SqlOperator;

public class SqlWhere {
	
	private ArrayList<SqlCondition> conditions;
	
	private Map<String,Object> params;
	
	
	public SqlWhere() {
		this.params = new HashMap<String,Object>();
		this.conditions = new ArrayList<SqlCondition>();
	}
	public ArrayList<SqlCondition> getConditions() {
		return conditions;
	}
	public void setConditions(ArrayList<SqlCondition> conditions) {
		this.conditions = conditions;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}


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
		EntityInfo entityInfo  = SystemConfig.getEntity(clazz);
		if(fieldName==null||fieldName.equals("")) {
			throw new HaoException("","创建sql语句失败，缺少字段名称");
		}
		Property p = entityInfo.getProperty(fieldName);
		if(p==null) {
			throw new HaoException("","该字段不存在");
		}
		if(p.isDB()==false) {
			throw new HaoException("","该字段为非数据库字段");
		}
		if(operator==null) {
			throw new HaoException("","创建sql语句失败，缺少操作符号");
		}
		if(values==null||values.length==0) {
			throw new HaoException("","创建sql语句失败，缺少值");
		}
		
		conditions.add(new SqlCondition(clazz,fieldName,operator,values,linkOperator));
		if(values.length==1) {
			params.put(fieldName, values[0]);
		}else {
			params.put(fieldName, values);
		}
	}
}
