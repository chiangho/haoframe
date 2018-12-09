package hao.framework.db.dao.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import hao.framework.core.CommonVar.SystemMsg;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.core.expression.HaoException;
import hao.framework.db.sql.SqlCommon.SqlLinkOperator;
import hao.framework.db.sql.SqlCommon.SqlOperator;
import hao.framework.db.sql.SqlCondition;
import hao.framework.db.sql.SqlJoin;
import hao.framework.db.sql.SqlSearch;
import hao.framework.db.sql.SqlWrite;
import hao.framework.utils.StringUtils;

/**
 * spring jdbc sql执行内容
 * 
 * @author chianghao
 * @time 2018.04.10
 */
public class SQLExecutionContent {

	/**
	 * 字段的前缀
	 */
	// private static final String FIELD_PREFIX="field_";
	/**
	 * where条件字段的前缀
	 */
	// private static final String WHERE_PREFIX="where_";

	private String sql;
	private Vector<Object> params;
	private Object[] aParams;
	// private String namedParameterSql;
	// private Map<String,Object> namedParameterParams;
	/**
	 * SqlWrite写对象
	 */
	private SqlWrite sqlWrite;
	private SqlSearch sqlSearch;
	
	/**
	 * 字段映射关系
	 */
	private ArrayList<FieldMapper> fieldMappers = new ArrayList<FieldMapper>();
	/**
	 * 执行的序号
	 */
	//private long no;

	//private String fieldPrefix;
	//private String wherePrefix;

	public SQLExecutionContent(SqlWrite sqlWrite) {
		this.sqlWrite = sqlWrite;
		//this.no = Seq.getNextId();
		this.sql = "";
		this.params = new Vector<Object>();
		parseSqlWrite();
		aParams = params.toArray();
	}

	public SQLExecutionContent(SqlSearch sqlSearch) throws HaoException {
		this.sqlSearch = sqlSearch;
		//this.no = Seq.getNextId();
		this.sql = "";
		this.params = new Vector<Object>();
		parseSqlSearch();
		aParams = params.toArray();
	}

	private void parseSqlSearch() throws HaoException {
		EntityInfo entityInfo = sqlSearch.getEntityInfo();
		if (entityInfo == null) {
			throw new HaoException(SystemMsg.sql_build_no_entity.getCode(), SystemMsg.sql_build_no_entity.getMsg());
		}
		int index=1;
		List<String> selectFields = new ArrayList<String>();
		for (Property p : entityInfo.getPropertys()) {
			if (p.isDB() == false) {
				continue;
			}
			if (p.getColumnName() == null || p.getColumnName().equals("")) {
				continue;
			}
			selectFields.add(entityInfo.getDefaultAlias() + "." + p.getColumnName() + " column_"+index);
			fieldMappers.add(new FieldMapper(entityInfo.getClazz(),entityInfo.getClazz(),p,"column_"+index,index));
			index++;
		}
		for (String className : sqlSearch.getJoins().keySet()) {
			SqlJoin join = sqlSearch.getJoins().get(className);
			if (join.getEntity() == null) {
				continue;
			}
			if(join.getFields()==null||join.getFields().length==0) {
				join.setFields(join.getEntity().getDBFields());
			}
			for (String field : join.getFields()) {
				Property p = join.getEntity().getProperty(field);
				if (p == null) {
					continue;
				}
				if (p.isDB() == false) {
					continue;
				}
				selectFields.add(join.getEntity().getDefaultAlias()+"."+p.getColumnName() + " column_"+index);
				fieldMappers.add(new FieldMapper(join.getClazz(),join.getMainClazz(),p,"column_"+index,index));
				index++;
			}
		}
		if (selectFields.size() == 0) {
			throw new HaoException(SystemMsg.sql_build_entity_has_no_db_column);
		}
		Vector<String[]> orders = sqlSearch.getOrders();
		sql += "select " + StringUtils.arrayToStr(",", selectFields) + " from " + entityInfo.getTableName() + " "
				+ entityInfo.getDefaultAlias();
		for (String className : sqlSearch.getJoins().keySet()) {
			SqlJoin join = sqlSearch.getJoins().get(className);
			if (join.getEntity() == null) {
				continue;
			}
			sql += join.getSql();
		}
		getWhereSql(sqlSearch.getSqlWhere().getConditions(), true);
		String orderSql = "";
		for (int i = 0; i < orders.size(); i++) {
			String[] order = orders.get(i);
			Property p = entityInfo.getProperty(order[0]);
			if (p.isDB() == false) {
				continue;
			}
			if (p.getColumnName() == null || p.getColumnName().equals("")) {
				continue;
			}
			if (i == 0) {
				orderSql += entityInfo.getDefaultAlias() + "." + p.getColumnName() + " " + order[1];
			} else {
				orderSql += "," + entityInfo.getDefaultAlias() + "." + p.getColumnName() + " " + order[1];
			}
		}
		if(orderSql!=null&&!orderSql.equals("")) {
			sql+=" order by "+orderSql;
		}
	}

	private void parseSqlWrite() {
		EntityInfo entity = this.sqlWrite.getEntityInfo();
		switch (sqlWrite.getAction()) {
		case insert:
			if (this.sqlWrite.getFields() == null || this.sqlWrite.getFields().size() == 0) {
				break;
			}
			// namedParameterSql += "insert into "+entity.getTableName()+"(";
			sql += "insert into " + entity.getTableName() + "(";
			boolean isFirst = true;
			for (String field : this.sqlWrite.getFields().keySet()) {
				Property p = entity.getProperty(field);
				if(p!=null) {
					if (isFirst) {
						// namedParameterSql+=p.getColumnName();
						sql += p.getColumnName();
						isFirst = false;
					} else {
						// namedParameterSql+=","+p.getColumnName();
						sql += "," + p.getColumnName();
					}
				}
			}
			// namedParameterSql+=")values(";
			sql += ")values(";
			isFirst = true;
			for (String field : this.sqlWrite.getFields().keySet()) {
				Property p = entity.getProperty(field);
				//String newKey = fieldPrefix + field;
				if(p!=null) {
					if (isFirst) {
						// namedParameterSql+=":"+newKey;
						sql += "?";
						isFirst = false;
					} else {
						// namedParameterSql+=",:"+newKey;
						sql += ",?";
					}
					// namedParameterParams.put(newKey, this.sqlWrite.getFields().get(field));
					params.add(this.sqlWrite.getFields().get(field));
				}
			}
			// namedParameterSql+=")";
			sql += ")";
			break;
		case update:
			if (this.sqlWrite.getFields() == null || this.sqlWrite.getFields().size() == 0) {
				break;
			}
			// namedParameterSql+="update "+entity.getTableName()+" set ";
			sql += "update " + entity.getTableName() + " set ";
			isFirst = true;
			for (String field : this.sqlWrite.getFields().keySet()) {
				//String newKey = fieldPrefix + field;
				Property p = entity.getProperty(field);
				if(p!=null) {
					if (isFirst) {
						// namedParameterSql+=p.getColumnName()+"=:"+newKey;
						sql += p.getColumnName() + "=?";
						isFirst = false;
					} else {
						// namedParameterSql+=","+p.getColumnName()+"= :"+newKey;
						sql += "," + p.getColumnName() + "=?";
					}
					// namedParameterParams.put(newKey, this.sqlWrite.getFields().get(field));
					params.add(this.sqlWrite.getFields().get(field));
				}
			}
			if (this.sqlWrite.getSqlWhere().getConditions() == null
					|| this.sqlWrite.getSqlWhere().getConditions().size() == 0) {
				break;
			}
			getWhereSql(this.sqlWrite.getSqlWhere().getConditions(), false);
			// namedParameterSql+=" ";
			sql += " ";
			break;
		case delete:
			// namedParameterSql+="delete from "+entity.getTableName()+" ";
			sql += "delete from  " + entity.getTableName() + " ";
			getWhereSql(this.sqlWrite.getSqlWhere().getConditions(), false);
			// namedParameterSql+=" ";
			sql += " ";
			break;
		default:
			break;
		}
	}

	private void getWhereSql(ArrayList<SqlCondition> conditions, boolean isTabelAlias) {
		String tempSql = "";
		Vector<Object> tempParams = new Vector<Object>();
		// ArrayList<String> where = new ArrayList<String>();
		for (SqlCondition condition : conditions) {
			Object[] values = condition.getValues();
			SqlLinkOperator linkOperator = condition.getLinkOperator();
			SqlOperator operator = condition.getOperator();
			//String fieldName = condition.getFieldName();
			String tableAlias = "";
			if (isTabelAlias) {
				tableAlias = condition.getEntityInfo().getDefaultAlias() + ".";
			}
			Property p = condition.getProperty();
			if (linkOperator == null) {
				linkOperator = SqlLinkOperator.and;
			}
			//String newKey = wherePrefix + fieldName;
			// namedParameterParams.put(newKey,
			// this.sqlWrite.getSqlWhere().getParams().get(fieldName));
			switch (operator) {
			case greater:
				// where.add(tableAlias+p.getColumnName()+">:"+newKey+"
				// "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + ">? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case greaterEqual:
				// where.add(tableAlias+p.getColumnName()+">=:"+newKey+"
				// "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + ">=? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case equal:
				// where.add(tableAlias+p.getColumnName()+"=:"+newKey+"
				// "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + "=? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case notEqual:
				// where.add(tableAlias+p.getColumnName()+"!=:"+newKey+"
				// "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + "!=? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case less:
				// where.add(tableAlias+p.getColumnName()+"<:"+newKey+"
				// "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + "<? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case lessEqual:
				// where.add(tableAlias+p.getColumnName()+"<=:"+newKey+"
				// "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + "<=? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case in:
				if (values.length > 0) {
					// StringBuffer temp = new StringBuffer(tableAlias+p.getColumnName()+" in (");
					StringBuffer tSql = new StringBuffer(tableAlias + p.getColumnName() + " in (");
					for (int i = 0; i < values.length; i++) {
						if (i == 0) {
							// temp.append(":"+newKey+"["+i+"]");
							tSql.append("?");
						} else {
							// temp.append(",:"+newKey+"["+i+"]");
							tSql.append(",?");
						}
						tempParams.add(values[i]);
					}
					// temp.append(") ");
					tSql.append(") ");
					// where.add(temp.toString()+linkOperator.getOperator()+" ");
					tempSql += tSql.toString() + linkOperator.getOperator() + " ";
				}
				break;
			case notIn:
				if (values.length > 0) {
					// StringBuffer temp = new StringBuffer(tableAlias+p.getColumnName()+" not in
					// (");
					StringBuffer tSql = new StringBuffer(tableAlias + p.getColumnName() + " not in (");
					for (int i = 0; i < values.length; i++) {
						if (i == 0) {
							// temp.append(":"+newKey+"["+i+"]");
							tSql.append("?");
						} else {
							// temp.append(",:"+newKey+"["+i+"]");
							tSql.append(",?");
						}
						tempParams.add(values[i]);
					}
					// temp.append(") ");
					tSql.append(") ");
					// where.add(temp.toString()+linkOperator.getOperator()+" ");
					tempSql += tSql.toString() + linkOperator.getOperator() + " ";
				}
				break;
			case between:
				// where.add(tableAlias+p.getColumnName()+"between :"+newKey+"[0] and
				// :"+newKey+"[1] "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + "between ? and ? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				tempParams.add(values[1]);
				break;
			case isNot:
				// where.add(tableAlias+p.getColumnName()+" is not :"+newKey+"
				// "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + " is not ? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case like:
				// where.add(tableAlias+p.getColumnName()+" like CONCAT('%',:"+newKey+",'%')
				// "+linkOperator.getOperator()+" ");
				tempSql += tableAlias + p.getColumnName() + " like CONCAT('%',?,'%') " + linkOperator.getOperator()
						+ " ";
				tempParams.add(values[0]);
				break;
			default:
				break;
			}
		}
		// if(where.size()!=0) {
		// StringBuffer sb = new StringBuffer();
		// for(String s:where) {
		// sb.append(s);
		// }
		// String sql_where_str = sb.toString().trim();
		// for(SqlLinkOperator s:SqlLinkOperator.values()) {
		// if(sql_where_str.endsWith(s.getOperator())) {
		// sql_where_str =
		// sql_where_str.substring(0,sql_where_str.lastIndexOf(s.getOperator()));
		// break;
		// }
		// }
		// //namedParameterSql+=" where "+sql;
		// }
		if (tempSql != null && !tempSql.equals("")) {
			tempSql = tempSql.trim();
			for (SqlLinkOperator s : SqlLinkOperator.values()) {
				if (tempSql.endsWith(s.getOperator())) {
					tempSql = tempSql.substring(0, tempSql.lastIndexOf(s.getOperator()));
					break;
				}
			}
			sql += " where " + tempSql;
			for (Object v : tempParams) {
				params.add(v);
			}
		}
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Vector<Object> getParams() {
		return params;
	}

	public void setParams(Vector<Object> params) {
		this.params = params;
	}

	public Object[] getaParams() {
		return aParams;
	}

	public void setaParams(Object[] aParams) {
		this.aParams = aParams;
	}

	
	
	public SqlWrite getSqlWrite() {
		return sqlWrite;
	}

	public void setSqlWrite(SqlWrite sqlWrite) {
		this.sqlWrite = sqlWrite;
	}

	public SqlSearch getSqlSearch() {
		return sqlSearch;
	}

	public void setSqlSearch(SqlSearch sqlSearch) {
		this.sqlSearch = sqlSearch;
	}

	public ArrayList<FieldMapper> getFieldMappers() {
		return fieldMappers;
	}

	public void setFieldMappers(ArrayList<FieldMapper> fieldMappers) {
		this.fieldMappers = fieldMappers;
	}

	public static void main(String[] args) {
		Vector<Integer> z = new Vector<Integer>();
		z.add(0);
		z.add(1);
		z.add(2);

		Vector<Integer> b = new Vector<Integer>();
		b.add(3);
		b.add(4);
		b.add(5);

		for (Integer i : b) {
			z.add(i);
		}

		for (Integer a : z) {
			System.out.println(a + "");
		}

	}

}
