package hao.framework.db.sql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.expression.HaoException;
import hao.framework.db.sql.SqlCommon.SqlJoinType;
import hao.framework.db.sql.SqlCommon.SqlLinkOperator;
import hao.framework.db.sql.SqlCommon.SqlOperator;
import hao.framework.db.sql.SqlCommon.SqlSort;

/***
 * 查询子句
 * @author chianghao
 */
public class SqlSearch {
	
	
	//private ArrayList<SqlCondition> conditions;
	private Class<?> clazz;
	
	private Map<String,Object> params;
	
	private EntityInfo entityInfo;
	
	private Vector<String[]> orders;
	// (condition or (condition1 and condition and (condition and condition) and condition ))
	//private ArrayList<String> where;
	private LinkedHashMap<String,SqlJoin> joins;
	
	
	private SqlWhere sqlWhere;
	
	
	public SqlSearch(Class<?> clazz) {
		//where           =  new ArrayList<String>();
		sqlWhere        = new SqlWhere();
		orders          =  new Vector<String[]>();
		this.clazz      =  clazz;
		this.entityInfo =  SystemConfig.getEntity(clazz);
		this.joins      =  new LinkedHashMap<String,SqlJoin>();
	}
	
	
	
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public LinkedHashMap<String, SqlJoin> getJoins() {
		return joins;
	}
	public void setJoins(LinkedHashMap<String, SqlJoin> joins) {
		this.joins = joins;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}
	public SqlWhere getSqlWhere() {
		return sqlWhere;
	}
	public void setSqlWhere(SqlWhere sqlWhere) {
		this.sqlWhere = sqlWhere;
	}


	/**
	 * 默认降序
	 * @param fieldName
	 */
	public void order(String fieldName) {
		order(fieldName,SqlSort.desc);
	}
	
	public Vector<String[]> getOrders() {
		return orders;
	}
	public void setOrders(Vector<String[]> orders) {
		this.orders = orders;
	}
	
	
	/**
	 * 设置排序
	 * @param fieldName
	 * @param sort
	 */
	public void order(String fieldName,SqlSort sort) {
		orders.add(new String[] {fieldName,sort.getSort()});
	}
	
	
	
	public SqlSearch addCondition(String fieldName, SqlOperator operator, Object value) throws HaoException {
		return addCondition(fieldName,operator,new Object[] {value},SqlLinkOperator.and);
	}
	public SqlSearch addCondition(Class<?> tagClass,String fieldName, SqlOperator operator, Object value) throws HaoException {
		return addCondition(tagClass,fieldName,operator,new Object[] {value},SqlLinkOperator.and);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SqlSearch addCondition(String fieldName,SqlOperator operator,Object value,SqlLinkOperator linkOperator) throws HaoException {
		return addCondition(fieldName,operator,new Object[] {value},linkOperator);
	}
	public SqlSearch addCondition(Class<?> tagClass,String fieldName,SqlOperator operator,Object value,SqlLinkOperator linkOperator) throws HaoException {
		return addCondition(tagClass,fieldName,operator,new Object[] {value},linkOperator);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SqlSearch addCondition(String fieldName,Object value) throws HaoException {
		return addCondition(fieldName,SqlOperator.equal,new Object[] {value},SqlLinkOperator.and);
	}
	public SqlSearch addCondition(Class<?> tagClass,String fieldName,Object value) throws HaoException {
		return addCondition(tagClass,fieldName,SqlOperator.equal,new Object[] {value},SqlLinkOperator.and);
	}
	/***
	 * 添加查询条件
	 * @param fieldName
	 * @param operator
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SqlSearch addCondition(String fieldName,SqlOperator operator,Object[] values) throws HaoException {
		return addCondition(fieldName,operator,values,SqlLinkOperator.and);
	}
	public SqlSearch addCondition(Class<?> tagClass,String fieldName,SqlOperator operator,Object[] values) throws HaoException {
		return addCondition(tagClass,fieldName,operator,values,SqlLinkOperator.and);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param operator
	 * @param value
	 * @return
	 * @throws HaoException 
	 */
	public SqlSearch addCondition(String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) throws HaoException {
		this.sqlWhere.addCondition(this.clazz, fieldName, operator, values, linkOperator);
		return this;
	}
	public SqlSearch addCondition(Class<?> tagClass,String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) throws HaoException {
		this.sqlWhere.addCondition(tagClass, fieldName, operator, values, linkOperator);
		return this;
	}

	
	
	/**
	 * 添加链接查询
	 * @param clazz
	 * @param mainField
	 * @param joinField
	 * @return
	 * @throws HaoException 
	 */
	public SqlSearch join(Class<?> clazz,String mainField,String joinField) throws HaoException {
		return join(SqlJoinType.left_join,this.clazz,clazz,new String[] {},mainField,joinField);
	}
	public SqlSearch join(SqlJoinType joinType,Class<?> clazz,String mainField,String joinField) throws HaoException {
		return join(joinType,this.clazz,clazz,new String[] {},mainField,joinField);
	}
	public SqlSearch join(Class<?> mainClazz,Class<?> clazz,String mainField,String joinField) throws HaoException {
		return join(SqlJoinType.left_join,mainClazz,clazz,new String[] {},mainField,joinField);
	}
	public SqlSearch join(Class<?> clazz,String[] fileds,String mainField,String joinField) throws HaoException {
		return join(SqlJoinType.left_join,this.clazz,clazz,fileds,mainField,joinField);
	}
	public SqlSearch join(SqlJoinType type,Class<?> mainClazz,Class<?> clazz,String[] fileds,String mainField,String joinField) throws HaoException {
		if(type==null) {
			return this;
		}
		if(clazz==null) {
			return this;
		}
		if(mainField==null||mainField.equals("")) {
			return this;
		}
		if(joinField==null||joinField.equals("")) {
			return this;
		}
		SqlJoin sqlJoin = new SqlJoin(type,clazz,mainClazz);
		sqlJoin.setFields(fileds);
		sqlJoin.addCondtion(mainField, joinField);
		this.joins.put(clazz.getName(), sqlJoin);
		return this;
	}
	
}
