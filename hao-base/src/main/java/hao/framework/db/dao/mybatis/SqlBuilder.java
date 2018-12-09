package hao.framework.db.dao.mybatis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hao.framework.core.CommonVar.SystemMsg;
import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.core.expression.HaoException;
import hao.framework.db.sql.SqlCommon.SqlLinkOperator;
import hao.framework.db.sql.SqlCommon.SqlOperator;
import hao.framework.db.sql.SqlCondition;
import hao.framework.db.sql.SqlJoin;
import hao.framework.db.sql.SqlSearch;
import hao.framework.db.sql.SqlWhere;
import hao.framework.utils.ClassUtils;
import hao.framework.utils.ConverterUtils;
import hao.framework.utils.StringUtils;

public class SqlBuilder {
	
	Logger log = LogManager.getLogger(SqlBuilder.class);
	
	
	/***
	 * 构建insert语句
	 * @param bean  
	 * @return
	 * @throws HaoException 
	 */
	public <T> String buildInsertSql(T bean) throws HaoException {
		EntityInfo entityInfo = SystemConfig.getEntity(bean.getClass());
		if(entityInfo==null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		int dbColumnNum = 0;
		for(Property p:entityInfo.getPropertys()) {
			if(p.isDB()==false) {
				continue;
			}
			if(p.getColumnName()==null||p.getColumnName().equals("")) {
				continue;
			}
			Object value = ClassUtils.getFieldValue(bean, p.getField());
			if(value==null) {
				continue;
			}
			dbColumnNum++;
		}
		if(dbColumnNum==0) {
			throw new HaoException(SystemMsg.sql_build_entity_has_no_db_column);
		}
		return new SQL() {{
			INSERT_INTO(entityInfo.getTableName());
			for(Property p:entityInfo.getPropertys()) {
				if(p.isDB()==false) {
					continue;
				}
				if(p.getColumnName()==null||p.getColumnName().equals("")) {
					continue;
				}
				Object value = ClassUtils.getFieldValue(bean, p.getField());
				if(value==null) {
					continue;
				}
				if(log.isDebugEnabled()) {
					log.debug("insert table column is=>"+p.getColumnName()+"，value is =>"+value);
				}
				VALUES(p.getColumnName(),"#{"+p.getField().getName()+"}");
			}
		}}.toString();
	}
	
	/***
	 * 构建update 语句
	 * @param clazz   需要操作的表
	 * @param fields  set元素，key值需跟clazz种元素名称一致
	 * @param id      主键
	 * @return
	 * @throws HaoException 
	 */
	@SuppressWarnings("unchecked")
	public String buildUpdateByIdSql(Map<String, Object> param) throws HaoException {
		long id = ConverterUtils.objectToLong(param.get("id"));
		Class<?> clazz = (Class<?>) param.get("clazz");
		Map<String,Object> fields = (Map<String, Object>) param.get("fields");
		
		if(fields==null||fields.keySet().size()<=0) {
			throw new HaoException(SystemMsg.sql_builder_update_fields_null.getCode(),SystemMsg.sql_builder_update_fields_null.getMsg());
		}
		if(id<=0) {
			throw new HaoException(SystemMsg.sql_builder_no_id.getCode(),SystemMsg.sql_builder_no_id.getMsg());
		}
		EntityInfo entityInfo = SystemConfig.getEntity(clazz);
		if(entityInfo==null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		return new SQL() {{
			UPDATE(entityInfo.getTableName());
			for(String fieldName:fields.keySet()) {
				Property p = entityInfo.getPropertyByFieldName(fieldName);
				if(p.isDB()) {
					if(p!=null) {
						SET(p.getColumnName()+"=#{fields."+fieldName+"}");
					}
				}
			}
			WHERE("id = #{id}");
		}}.toString();
	}
	/***
	 * 构建update 语句
	 * @param clazz   需要操作的表
	 * @param fields  set元素，key值需跟clazz种元素名称一致
	 * @param id      主键
	 * @return
	 * @throws HaoException 
	 */
	@SuppressWarnings("unchecked")
	public String buildUpdateSql(Map<String, Object> param) throws HaoException {
		Class<?> clazz = (Class<?>) param.get("clazz");
		Map<String,Object> fields = (Map<String, Object>) param.get("fields");
		SqlWhere sqlWhere = (SqlWhere) param.get("sqlWhere");
		
		if(fields==null||fields.keySet().size()<=0) {
			throw new HaoException(SystemMsg.sql_builder_update_fields_null.getCode(),SystemMsg.sql_builder_update_fields_null.getMsg());
		}
		EntityInfo entityInfo = SystemConfig.getEntity(clazz);
		if(entityInfo==null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		
		
		//校验where条件是否存在
		if(sqlWhere==null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		//校验where条件是否存在
		if(sqlWhere.getConditions()==null||sqlWhere.getConditions().size()==0) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		int i=0;
		for(SqlCondition condition:sqlWhere.getConditions()) {
			if(clazz.isAssignableFrom(condition.getClazz())) {
				i++;
			}
		}
		if(i==0) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		
		return new SQL() {{
			UPDATE(entityInfo.getTableName());
			for(String fieldName:fields.keySet()) {
				Property p = entityInfo.getPropertyByFieldName(fieldName);
				if(p!=null) {
					if(p.isDB()) {
						SET(p.getColumnName()+"=#{fields."+fieldName+"}");
					}
				}
			}
			WHERE(getWhereSql("sqlWhere.params",sqlWhere.getConditions(),false));
		}}.toString();
	}
	
	/***
	 * 构建删除的sql语句
	 * @param clazz 被执行的对象
	 * @param id    主建值
	 * @return
	 * @throws HaoException 
	 */
	public String buildDeleteByIdSql(Map<String, Object> param) throws HaoException {
		if(param.get("id")==null) {
			throw new HaoException(SystemMsg.sql_builder_no_id.getCode(),SystemMsg.sql_builder_no_id.getMsg());
		}
//		String id = (String) param.get("id") ;
//		if(id.equals("")) {
//			throw new HaoException(SystemMsg.sql_builder_no_id.getCode(),SystemMsg.sql_builder_no_id.getMsg());
//		}
		Class<?> clazz = (Class<?>) param.get("clazz");
		
		EntityInfo entityInfo = SystemConfig.getEntity(clazz);
		if(entityInfo==null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		SQL sql = new SQL() {{
			DELETE_FROM(entityInfo.getTableName());
			WHERE(" id = #{id} ");
		}};
		
		if(log.isDebugEnabled()) {
			  log.debug("______sql 调试信息______the sql search is:\n"+sql.toString());
		}
		
		return sql.toString();
	}
	
	
	
	public String buildDeleteSql(Map<String, Object> param)throws HaoException {
		Class<?> clazz =(Class<?>) param.get("clazz");
		SqlWhere sqlWhere=(SqlWhere) param.get("sqlWhere");
		EntityInfo entityInfo = SystemConfig.getEntity(clazz);
		if(entityInfo==null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		
		//校验where条件是否存在
		if(sqlWhere==null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		//校验where条件是否存在
		if(sqlWhere.getConditions()==null||sqlWhere.getConditions().size()==0) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		int i=0;
		for(SqlCondition condition:sqlWhere.getConditions()) {
			if(clazz.isAssignableFrom(condition.getClazz())) {
				i++;
			}
		}
		if(i==0) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		
		SQL sql =  new SQL() {{
			DELETE_FROM(entityInfo.getTableName());
			WHERE(getWhereSql("sqlWhere.params",sqlWhere.getConditions(),false));
		}};
		
		if(log.isDebugEnabled()) {
			  log.debug("______sql 调试信息______the sql search is:\n"+sql);
		}
		
		return sql.toString();
	}
	
	
	public String buildQuerySql(Map<String, Object> param) throws HaoException {
		
		SqlSearch sqlSearch = (SqlSearch) param.get("sqlSearch");
		sqlSearch.setParams(sqlSearch.getSqlWhere().getParams());
		EntityInfo entityInfo =sqlSearch.getEntityInfo();
		if(entityInfo==null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(),SystemMsg.sql_build_no_entity.getMsg());
		}
		List<String> selectFields = new ArrayList<String>();
		for(Property p:entityInfo.getPropertys()) {
			if(p.isDB()==false) {
				continue;
			}
			if(p.getColumnName()==null||p.getColumnName().equals("")) {
				continue;
			}
			selectFields.add(entityInfo.getDefaultAlias()+"."+p.getColumnName() +" "+p.getName());
		}
		//json字段
//		for(Class<?> joinKey : sqlSearch.getJoins().keySet()) {
//			EntityInfo joinEntityInfo = SystemConfig.getEntity(joinKey);
//			SqlJoin  join = sqlSearch.getJoins().get(joinKey);
//			if(joinEntityInfo==null) {
//				continue;
//			}
//			for(String[] field:join.getFields()) {
//				Property p = joinEntityInfo.getProperty(field[0]);
//				if(p==null) {
//					continue;
//				}
//				if(p.isDB()==false) {
//					continue;
//				}
//				selectFields.add(joinEntityInfo.getDefaultAlias()+"."+p.getColumnName() +" "+field[1]);
//			}
//		}
		if(selectFields.size()==0) {
			throw new HaoException(SystemMsg.sql_build_entity_has_no_db_column);
		}
		Vector<String[]> orders =  sqlSearch.getOrders();
		String sql =  new SQL() {{
		    SELECT(StringUtils.arrayToStr(",", selectFields));
		    FROM(entityInfo.getTableName()+" "+entityInfo.getDefaultAlias());
//		    for(Class<?> joinKey : sqlSearch.getJoins().keySet()) {
//				EntityInfo joinEntityInfo = SystemConfig.getEntity(joinKey);
//				SqlJoin  join = sqlSearch.getJoins().get(joinKey);
//				if(joinEntityInfo==null) {
//					continue;
//				}
//				switch(join.getJoinType()) {
//				case join:
//					JOIN(joinEntityInfo.getTableName()+" "+joinEntityInfo.getDefaultAlias()+" on "+getJoinCondtion(join));
//					break;
//				case left_join:
//					LEFT_OUTER_JOIN(joinEntityInfo.getTableName()+" "+joinEntityInfo.getDefaultAlias()+" on "+getJoinCondtion(join));
//					break;
//				case inner_join:
//					INNER_JOIN(joinEntityInfo.getTableName()+" "+joinEntityInfo.getDefaultAlias()+" on "+getJoinCondtion(join));
//					break;
//				case right_join:
//					RIGHT_OUTER_JOIN(joinEntityInfo.getTableName()+" "+joinEntityInfo.getDefaultAlias()+" on "+getJoinCondtion(join));
//					break;
//				default:
//					break;
//				}
//			}
		    String wheresql = getWhereSql("sqlSearch.params",sqlSearch.getSqlWhere().getConditions(),true);
		    if(wheresql !=null && !wheresql.equals("")) {
		    	 WHERE(wheresql);
		    }
		    for(String[] order:orders) {
				Property p = entityInfo.getProperty(order[0]);
				if(p.isDB()==false) {
					continue;
				}
				if(p.getColumnName()==null||p.getColumnName().equals("")) {
					continue;
				}
				ORDER_BY(entityInfo.getDefaultAlias()+"."+p.getColumnName()+" "+order[1]);
			}
		  }}.toString();
		  
		  if(log.isDebugEnabled()) {
			  log.debug("______sql 调试信息______the sql search is:\n"+sql);
		  }
		  return sql;
	}
	
	private String getWhereSql(String alias,ArrayList<SqlCondition> conditions,boolean isTabelAlias) {
		ArrayList<String> where  = new ArrayList<String>();
		for(SqlCondition condition:conditions) {
			Object[] values = condition.getValues();
			SqlLinkOperator linkOperator = condition.getLinkOperator();
			SqlOperator operator = condition.getOperator();
			String fieldName = condition.getFieldName();
			String tableAlias = "";
			if(isTabelAlias) {
				tableAlias = condition.getEntityInfo().getDefaultAlias()+".";
			}
			Property p = condition.getProperty();
			if(linkOperator==null) {
				linkOperator = SqlLinkOperator.and;
			}
			switch(operator){
			case greater:
				where.add(tableAlias+p.getColumnName()+">#{"+alias+"."+fieldName+"} "+linkOperator.getOperator()+" ");
				break;
			case greaterEqual:
				where.add(tableAlias+p.getColumnName()+">=#{"+alias+"."+fieldName+"} "+linkOperator.getOperator()+" ");
				break;
			case equal:
				where.add(tableAlias+p.getColumnName()+"=#{"+alias+"."+fieldName+"} "+linkOperator.getOperator()+" ");
				break;
			case notEqual:
				where.add(tableAlias+p.getColumnName()+"!=#{"+alias+"."+fieldName+"} "+linkOperator.getOperator()+" ");
				break;
			case less:
				where.add(tableAlias+p.getColumnName()+"<#{"+alias+"."+fieldName+"} "+linkOperator.getOperator()+" ");
				break;
			case lessEqual:
				where.add(tableAlias+p.getColumnName()+"<=#{"+alias+"."+fieldName+"} "+linkOperator.getOperator()+" ");
				break;
			case in:
				if(values.length>0) {
					StringBuffer temp = new StringBuffer(tableAlias+p.getColumnName()+" in (");
					for(int i=0;i<values.length;i++) {
						if(i==0) {
							temp.append("#{"+alias+"."+fieldName+"["+i+"]}");
						}else {
							temp.append(",#{"+alias+"."+fieldName+"["+i+"]}");
						}
					}
					temp.append(") ");		
					where.add(temp.toString()+linkOperator.getOperator()+" ");
				}
				break;
			case notIn:
				if(values.length>0) {
					StringBuffer temp = new StringBuffer(tableAlias+p.getColumnName()+" not in (");
					for(int i=0;i<values.length;i++) {
						if(i==0) {
							temp.append("#{"+alias+"."+fieldName+"["+i+"]}");
						}else {
							temp.append(",#{"+alias+"."+fieldName+"["+i+"]}");
						}
					}
					temp.append(") ");		
					where.add(temp.toString()+linkOperator.getOperator()+" ");
					//where.add(" "+p.getColumnName()+" not in \n\t <foreach collection=\""+alias+"."+fieldName+"\" index=\"i\" open=\"(\" separator=\",\" close=\")\"> \n\t #{"+alias+"."+fieldName+"[${i}]} \n\t</foreach> "+linkOperator.getOperator());
				}
				break;
			case between:
				where.add(tableAlias+p.getColumnName()+"between #{"+alias+"."+fieldName+"[0]} and #{"+alias+"."+fieldName+"[1]} "+linkOperator.getOperator()+" ");
				break;
			case isNot:
				where.add(tableAlias+p.getColumnName()+" is not #{"+alias+"."+fieldName+"} "+linkOperator.getOperator()+" ");
				break;
			case like:
				where.add(tableAlias+p.getColumnName()+" like CONCAT('%',#{"+alias+"."+fieldName+"},'%') "+linkOperator.getOperator()+" ");
				break;
			default:
				break;
			}
		
		}
		if(where.size()==0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for(String s:where) {
			sb.append(s);
		}
		String sql = sb.toString().trim();
		for(SqlLinkOperator s:SqlLinkOperator.values()) {
			if(sql.endsWith(s.getOperator())) {
				sql =  sql.substring(0,sql.lastIndexOf(s.getOperator()));
				break;
			}
		}
		return sql;
	}
	
	
}
