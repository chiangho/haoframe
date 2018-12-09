package hao.framework.database.dao.sql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hao.framework.core.expression.HaoException;
import hao.framework.database.dao.sql.SQLCommon.SqlJoinType;
import hao.framework.database.dao.sql.SQLCommon.SqlLinkOperator;
import hao.framework.database.dao.sql.SQLCommon.SqlOperator;
import hao.framework.database.dao.sql.SQLCommon.SqlSort;
import hao.framework.database.entity.ColumnInfo;
import hao.framework.database.entity.EntityInfo;

/***
 * sql读操作
 * @author chianghao
 */
public class SQLRead {
	
	private Class<?> clazz;
	
	private Set<ColumnInfo> fields;
	
	private Map<String,Object> params;
	
	private EntityInfo entityInfo;
	
	private List<String> orders;
	
	private List<String> groups;

	private LinkedHashMap<String,SQLJoin> joins;
	
	private SQLWhere sQLWhere;
	
	public SQLRead(Class<?> clazz) {
		this.sQLWhere        =  new SQLWhere();
		this.orders          =  new ArrayList<String>();
		this.groups          =  new ArrayList<String>();
		this.clazz      =  clazz;
		this.entityInfo =  EntityInfo.getEntity(clazz);
		this.joins      =  new LinkedHashMap<String,SQLJoin>();
		this.fields     =  new HashSet<ColumnInfo>();
	}
	
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public LinkedHashMap<String, SQLJoin> getJoins() {
		return joins;
	}
	public void setJoins(LinkedHashMap<String, SQLJoin> joins) {
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
	public SQLWhere getSqlWhere() {
		return sQLWhere;
	}
	public void setSqlWhere(SQLWhere sQLWhere) {
		this.sQLWhere = sQLWhere;
	}
	/**
	 * 默认降序
	 * @param fieldName
	 */
	public void order(String fieldName) {
		order(fieldName,SqlSort.desc);
	}
	
	public List<String> getOrders() {
		return orders;
	}
	
	public List<String> getGroups(){
		return groups;
	}
	
	/**
	 * 设置排序
	 * @param fieldName
	 * @param sort
	 */
	public void order(String fieldName,SqlSort sort) {
		String alias      = this.entityInfo.getDefaultAlias();
		String columnName = this.entityInfo.getColumnInfo(fieldName).getColumnName();
		String sql = alias+"."+columnName;
		orders.add(sql+" "+sort.getSort());
	}
	
	/**
	 * 设置排序
	 * @param clazz
	 * @param fieldName
	 * @param sort
	 */
	public void order(Class<?> clazz,String fieldName,SqlSort sort) {
		EntityInfo entity = EntityInfo.getEntity(clazz);
		String alias      = entity.getDefaultAlias();
		String columnName = entity.getColumnInfo(fieldName).getColumnName();
		String sql = alias+"."+columnName;
		orders.add(sql+" "+sort.getSort());
	}
	
	public SQLRead addCondition(String fieldName, SqlOperator operator, Object value) throws HaoException {
		return addCondition(fieldName,operator,new Object[] {value},SqlLinkOperator.and);
	}
	public SQLRead addCondition(Class<?> tagClass,String fieldName, SqlOperator operator, Object value) throws HaoException {
		return addCondition(tagClass,fieldName,operator,new Object[] {value},SqlLinkOperator.and);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SQLRead addCondition(String fieldName,SqlOperator operator,Object value,SqlLinkOperator linkOperator) throws HaoException {
		return addCondition(fieldName,operator,new Object[] {value},linkOperator);
	}
	public SQLRead addCondition(Class<?> tagClass,String fieldName,SqlOperator operator,Object value,SqlLinkOperator linkOperator) throws HaoException {
		return addCondition(tagClass,fieldName,operator,new Object[] {value},linkOperator);
	}
	/**
	 * 添加查询条件
	 * @param fieldName
	 * @param values
	 * @return
	 * @throws HaoException
	 */
	public SQLRead addCondition(String fieldName,Object value) throws HaoException {
		return addCondition(fieldName,SqlOperator.equal,new Object[] {value},SqlLinkOperator.and);
	}
	public SQLRead addCondition(Class<?> tagClass,String fieldName,Object value) throws HaoException {
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
	public SQLRead addCondition(String fieldName,SqlOperator operator,Object[] values) throws HaoException {
		return addCondition(fieldName,operator,values,SqlLinkOperator.and);
	}
	public SQLRead addCondition(Class<?> tagClass,String fieldName,SqlOperator operator,Object[] values) throws HaoException {
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
	public SQLRead addCondition(String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) throws HaoException {
		this.sQLWhere.addCondition(this.clazz, fieldName, operator, values, linkOperator);
		return this;
	}
	public SQLRead addCondition(Class<?> tagClass,String fieldName,SqlOperator operator,Object[] values,SqlLinkOperator linkOperator) throws HaoException {
		this.sQLWhere.addCondition(tagClass, fieldName, operator, values, linkOperator);
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
	public SQLRead join(Class<?> clazz,String mainField,String joinField) throws HaoException {
		return join(SqlJoinType.left_join,this.clazz,clazz,new String[] {},mainField,joinField);
	}
	public SQLRead join(SqlJoinType joinType,Class<?> clazz,String mainField,String joinField) throws HaoException {
		return join(joinType,this.clazz,clazz,new String[] {},mainField,joinField);
	}
	public SQLRead join(Class<?> mainClazz,Class<?> clazz,String mainField,String joinField) throws HaoException {
		return join(SqlJoinType.left_join,mainClazz,clazz,new String[] {},mainField,joinField);
	}
	public SQLRead join(Class<?> clazz,String[] fileds,String mainField,String joinField) throws HaoException {
		return join(SqlJoinType.left_join,this.clazz,clazz,fileds,mainField,joinField);
	}
	public SQLRead join(SqlJoinType type,Class<?> mainClazz,Class<?> clazz,String[] fileds,String mainField,String joinField) throws HaoException {
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
		SQLJoin sQLJoin = new SQLJoin(type,clazz,mainClazz);
		for(String field:fileds) {
			sQLJoin.addField(field);
		}
		sQLJoin.addCondtion(mainField, joinField);
		this.joins.put(clazz.getName(), sQLJoin);
		return this;
	}

	public void setField(String fieldName) {
		this.setField(clazz, fieldName);
	}
	public void setField(Class<?> tagerClass,String fieldName) {
		EntityInfo e =  EntityInfo.getEntity(tagerClass);
		ColumnInfo columnInfo = e.getColumnInfo(fieldName);
		if(columnInfo == null) {
			return;
		}
		fields.add(columnInfo);
	}

	public Set<ColumnInfo> getFields() {
		return fields;
	}

	public void setFields(Set<ColumnInfo> fields) {
		this.fields = fields;
	}

	public SQLWhere getsQLWhere() {
		return sQLWhere;
	}

	public void setsQLWhere(SQLWhere sQLWhere) {
		this.sQLWhere = sQLWhere;
	}

	public void setOrders(List<String> orders) {
		this.orders = orders;
	}

	public void group(String fieldName) {
		String alias      = this.entityInfo.getDefaultAlias();
		String columnName = this.entityInfo.getColumnInfo(fieldName).getColumnName();
		String sql = alias+"."+columnName;
		groups.add(sql);
	}
	
	public void group(Class<?> clazz,String fieldName,SqlSort sort) {
		EntityInfo entity = EntityInfo.getEntity(clazz);
		String alias      = entity.getDefaultAlias();
		String columnName = entity.getColumnInfo(fieldName).getColumnName();
		String sql = alias+"."+columnName;
		groups.add(sql);
	}
	
}
