package hao.framework.database.dao.sql;

import java.util.ArrayList;
import java.util.Set;

import hao.framework.core.expression.HaoException;
import hao.framework.database.dao.sql.SQLCommon.SqlJoinType;
import hao.framework.database.dao.sql.SQLCommon.SqlLinkOperator;
import hao.framework.database.dao.sql.SQLCommon.SqlOperator;
import hao.framework.database.entity.EntityInfo;


/***
 * join 关联查询
 * @author chianghao
 */
public class SQLJoin {

	
	class SQLJoinCondtion{
		
		private String           field;
		private SqlOperator      operator;
		private SqlLinkOperator  link;
		//private boolean          isMainFiled = true;
		private String           mainField;
		private Object           value;
		
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public SqlOperator getOperator() {
			return operator;
		}
		public void setOperator(SqlOperator operator) {
			this.operator = operator;
		}
		public SqlLinkOperator getLink() {
			return link;
		}
		public void setLink(SqlLinkOperator link) {
			this.link = link;
		}
		public String getMainField() {
			return mainField;
		}
		public void setMainField(String mainField) {
			this.mainField = mainField;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
	}
	
	private Class<?>                   clazz;
	private Class<?>                   mainClazz;
	private SqlJoinType                joinType;
	private Set<String>                fields;
	private EntityInfo                 entity;
	private EntityInfo                 mainEntity;
	private ArrayList<SQLJoinCondtion> conditons;
	
	public SQLJoin(SqlJoinType type,Class<?> clazz,Class<?> mainClazz) throws HaoException {
		this.joinType      =  type;
		this.clazz         =  clazz;
		this.mainClazz     =  mainClazz;
		this.conditons     =  new ArrayList<SQLJoinCondtion>();
		this.entity        =  EntityInfo.getEntity(clazz);
		if(this.entity==null) {
			throw new HaoException("999999","设置sql的join配置，发生错误，提供的Class["+clazz.getName()+"]未注册为entity");
		}
		this.mainClazz     =  mainClazz;
		this.mainEntity    =  EntityInfo.getEntity(mainClazz);
		if(mainEntity==null) {
			throw new HaoException("999999","设置sql的join配置，发生错误，提供的Class["+mainClazz.getName()+"]未注册为entity");
		}
	};
	
	/**
	 * 设置条件
	 * @param mainField
	 * @param joinField
	 * @throws HaoException
	 */
	public void addCondtion(String mainField,String joinField) throws HaoException {
		if(mainEntity.getColumnInfo(mainField)==null||!mainEntity.getColumnInfo(mainField).isDB()) {
			throw new HaoException("999999","设置sql的join配置发生错误，提供["+mainClazz.getName()+":"+mainField+"不存在或者未注册为数据库列]");
		}
		if(entity.getColumnInfo(joinField)==null||!entity.getColumnInfo(joinField).isDB()) {
			throw new HaoException("999999","设置sql的join配置发生错误，提供["+clazz.getName()+":"+joinField+"不存在或者未注册为数据库列]");
		}
		SQLJoinCondtion condition = new SQLJoinCondtion();
		condition.setField(joinField);
		condition.setMainField(mainField);
		condition.setOperator(SqlOperator.equal);
		condition.setLink(SqlLinkOperator.and);
		conditons.add(condition);
	}
	
	private String getJoinCondtionSql() {
		if(conditons==null||conditons.size()==0) {
			return "";
		}
		String sql = "";
		for(SQLJoinCondtion condition:conditons) {
			sql+=" "+mainEntity.getDefaultAlias()+"."+mainEntity.getColumnInfo(condition.getMainField()).getColumnName()
					+condition.getOperator().getOperator()
					+entity.getDefaultAlias()+"."+entity.getColumnInfo(condition.getField()).getColumnName()
					+condition.getLink();
		}
		sql = sql.trim();
		for(SqlLinkOperator l:SqlLinkOperator.values()) {
			if(sql.endsWith(l.getOperator())) {
				sql = sql.substring(0, sql.lastIndexOf(l.getOperator()));
				break;
			}
		}
		return sql;
	}
	
	public String getSql() {
		String sql="";
		switch (joinType) {
		case join:
			sql += " join " +entity.getTableName()+" "+entity.getDefaultAlias()+" on "+ getJoinCondtionSql();
			break;
		case left_join:
			sql += " left join " + entity.getTableName() + " " + entity.getDefaultAlias() + " on " + getJoinCondtionSql();
			break;
		case inner_join:
			sql += " inner join " + entity.getTableName() + " " + entity.getDefaultAlias() + " on " + getJoinCondtionSql();
			break;
		case right_join:
			sql += " right join " + entity.getTableName() + " " + entity.getDefaultAlias() + " on " + getJoinCondtionSql();
			break;
		default:
			break;
		}
		return sql;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public SqlJoinType getJoinType() {
		return joinType;
	}
	public void setJoinType(SqlJoinType joinType) {
		this.joinType = joinType;
	}
	public String getJoinLinkOperator() {
		return this.joinType.getType();
	}

	public Set<String> getFields() {
		return fields;
	}
	public void setFields(Set<String> fields) {
		this.fields = fields;
	}

	public EntityInfo getEntity() {
		return entity;
	}

	public void setEntity(EntityInfo entity) {
		this.entity = entity;
	}

	public Class<?> getMainClazz() {
		return mainClazz;
	}

	public void setMainClazz(Class<?> mainClazz) {
		this.mainClazz = mainClazz;
	}

	public ArrayList<SQLJoinCondtion> getConditons() {
		return conditons;
	}

	public void setConditons(ArrayList<SQLJoinCondtion> conditons) {
		this.conditons = conditons;
	}

	public void addField(String field) {
		this.fields.add(field);
	}
	
	
	
}
