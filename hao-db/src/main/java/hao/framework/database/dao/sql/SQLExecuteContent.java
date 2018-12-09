package hao.framework.database.dao.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import hao.framework.core.expression.HaoException;
import hao.framework.core.utils.StringUtils;
import hao.framework.database.dao.sql.SQLCommon.SqlLinkOperator;
import hao.framework.database.dao.sql.SQLCommon.SqlOperator;
import hao.framework.database.entity.ColumnInfo;
import hao.framework.database.entity.EntityInfo;



/**
 * sql执行内容
 * @author chianghao
 * @time 2018.04.10
 */
public class SQLExecuteContent {

	private String sql;
	private List<Object> params;
	/**
	 * SqlWrite写对象
	 */
	private SQLWrite sQLWrite;
	private SQLRead sQLRead;
	

	public SQLExecuteContent(SQLWrite sQLWrite) {
		this.sQLWrite = sQLWrite;
		this.sql = "";
		this.params = new Vector<Object>();
		parseSqlWrite();
	}

	public SQLExecuteContent(SQLRead sQLRead) throws HaoException {
		this.sQLRead = sQLRead;
		this.sql = "";
		this.params = new Vector<Object>();
		parseSqlRead();
	}

	private void parseSqlRead() throws HaoException {
		EntityInfo entityInfo = sQLRead.getEntityInfo();
		if (entityInfo == null) {
			throw new HaoException("sql_build_no_entity","sql_build_no_entity");
		}
		List<String> selectFields = new ArrayList<String>();
		if(sQLRead.getFields().size()>0) {
			for (ColumnInfo p : sQLRead.getFields()) {
				if (p.isDB() == false) {
					continue;
				}
				if (p.getColumnName() == null || p.getColumnName().equals("")) {
					continue;
				}
				EntityInfo e = EntityInfo.getEntity(p.getClazz());
				selectFields.add(e.getDefaultAlias() + "." + p.getColumnName() + " "+e.getDefaultAlias()+"_"+p.getColumnName());
			}
		}
		
		for (String className : sQLRead.getJoins().keySet()) {
			SQLJoin join = sQLRead.getJoins().get(className);
			if (join.getEntity() == null) {
				continue;
			}
			if(join.getFields()==null||join.getFields().size()==0) {
				join.setFields(join.getEntity().getDBFields());
			}
			for (String field : join.getFields()) {
				ColumnInfo p = join.getEntity().getColumnInfo(field);
				if (p == null) {
					continue;
				}
				if (p.isDB() == false) {
					continue;
				}
				selectFields.add(join.getEntity().getDefaultAlias()+"."+p.getColumnName() + " "+join.getEntity().getDefaultAlias()+"_"+p.getColumnName());
			}
		}
		if (selectFields.size() == 0) {
			for (ColumnInfo p : entityInfo.getColumns()) {
				if (p.isDB() == false) {
					continue;
				}
				if (p.getColumnName() == null || p.getColumnName().equals("")) {
					continue;
				}
				selectFields.add(entityInfo.getDefaultAlias() + "." + p.getColumnName() + " "+entityInfo.getDefaultAlias()+"_"+p.getColumnName());
			}
		}
		
		sql += "select " + StringUtils.arrayToStr(",", selectFields) + " from " + entityInfo.getTableName() + " "
				+ entityInfo.getDefaultAlias();
		for (String className : sQLRead.getJoins().keySet()) {
			SQLJoin join = sQLRead.getJoins().get(className);
			if (join.getEntity() == null) {
				continue;
			}
			sql += join.getSql();
		}
		getWhereSql(sQLRead.getSqlWhere().getConditions(), true);
		
		
		//group by
		String groupSql = "";
		for (int i = 0; i < sQLRead.getGroups().size(); i++) {
			String group = sQLRead.getGroups().get(i);
			if (i == 0) {
				groupSql += group;
			} else {
				groupSql += "," + group;
			}
		}
		if(groupSql!=null&&!groupSql.equals("")) {
			sql+=" group by "+groupSql;
		}
		
		//order by
		String orderSql = "";
		for (int i = 0; i < sQLRead.getOrders().size(); i++) {
			String order = sQLRead.getOrders().get(i);
			if (i == 0) {
				orderSql += order;
			} else {
				orderSql += "," + order;
			}
		}
		if(orderSql!=null&&!orderSql.equals("")) {
			sql+=" order by "+orderSql;
		}
		
	}

	private void parseSqlWrite() {
		EntityInfo entity = this.sQLWrite.getEntityInfo();
		switch (sQLWrite.getAction()) {
		case insert:
			if (this.sQLWrite.getFields() == null || this.sQLWrite.getFields().size() == 0) {
				break;
			}
			sql += "insert into " + entity.getTableName() + "(";
			boolean isFirst = true;
			for (String field : this.sQLWrite.getFields().keySet()) {
				ColumnInfo p = entity.getColumnInfo(field);
				if(p!=null) {
					if (isFirst) {
						sql += p.getColumnName();
						isFirst = false;
					} else {
						sql += "," + p.getColumnName();
					}
				}
			}
			sql += ")values(";
			isFirst = true;
			for (String field : this.sQLWrite.getFields().keySet()) {
				ColumnInfo p = entity.getColumnInfo(field);
				if(p!=null) {
					if (isFirst) {
						sql += "?";
						isFirst = false;
					} else {
						sql += ",?";
					}
					params.add(this.sQLWrite.getFields().get(field));
				}
			}
			sql += ")";
			break;
		case update:
			if (this.sQLWrite.getFields() == null || this.sQLWrite.getFields().size() == 0) {
				break;
			}
			sql += "update " + entity.getTableName() + " set ";
			isFirst = true;
			for (String field : this.sQLWrite.getFields().keySet()) {
				ColumnInfo p = entity.getColumnInfo(field);
				if(p!=null) {
					if (isFirst) {
						sql += p.getColumnName() + "=?";
						isFirst = false;
					} else {
						sql += "," + p.getColumnName() + "=?";
					}
					params.add(this.sQLWrite.getFields().get(field));
				}
			}
			if (this.sQLWrite.getSqlWhere().getConditions() == null
					|| this.sQLWrite.getSqlWhere().getConditions().size() == 0) {
				break;
			}
			getWhereSql(this.sQLWrite.getSqlWhere().getConditions(), false);
			sql += " ";
			break;
		case delete:
			sql += "delete from  " + entity.getTableName() + " ";
			getWhereSql(this.sQLWrite.getSqlWhere().getConditions(), false);
			sql += " ";
			break;
		default:
			break;
		}
	}

	private void getWhereSql(ArrayList<SQLCondition> conditions, boolean isTabelAlias) {
		String tempSql = "";
		List<Object> tempParams = new ArrayList<Object>();
		for (SQLCondition condition : conditions) {
			Object[] values = condition.getValues();
			SqlLinkOperator linkOperator = condition.getLinkOperator();
			SqlOperator operator = condition.getOperator();
			String tableAlias = "";
			if (isTabelAlias) {
				tableAlias = condition.getEntityInfo().getDefaultAlias() + ".";
			}
			ColumnInfo p = condition.getColumnInfo();
			if (linkOperator == null) {
				linkOperator = SqlLinkOperator.and;
			}
			switch (operator) {
			case greater:
				tempSql += tableAlias + p.getColumnName() + ">? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case greaterEqual:
				tempSql += tableAlias + p.getColumnName() + ">=? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case equal:
				tempSql += tableAlias + p.getColumnName() + "=? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case notEqual:
				tempSql += tableAlias + p.getColumnName() + "!=? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case less:
				tempSql += tableAlias + p.getColumnName() + "<? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case lessEqual:
				tempSql += tableAlias + p.getColumnName() + "<=? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case in:
				if (values.length > 0) {
					StringBuffer tSql = new StringBuffer(tableAlias + p.getColumnName() + " in (");
					for (int i = 0; i < values.length; i++) {
						if (i == 0) {
							tSql.append("?");
						} else {
							tSql.append(",?");
						}
						tempParams.add(values[i]);
					}
					tSql.append(") ");
					tempSql += tSql.toString() + linkOperator.getOperator() + " ";
				}
				break;
			case notIn:
				if (values.length > 0) {
					StringBuffer tSql = new StringBuffer(tableAlias + p.getColumnName() + " not in (");
					for (int i = 0; i < values.length; i++) {
						if (i == 0) {
							tSql.append("?");
						} else {
							tSql.append(",?");
						}
						tempParams.add(values[i]);
					}
					tSql.append(") ");
					tempSql += tSql.toString() + linkOperator.getOperator() + " ";
				}
				break;
			case between:
				tempSql += tableAlias + p.getColumnName() + "between ? and ? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				tempParams.add(values[1]);
				break;
			case isNot:
				tempSql += tableAlias + p.getColumnName() + " is not ? " + linkOperator.getOperator() + " ";
				tempParams.add(values[0]);
				break;
			case like:
				tempSql += tableAlias + p.getColumnName() + " like CONCAT('%',?,'%') " + linkOperator.getOperator()
						+ " ";
				tempParams.add(values[0]);
				break;
			default:
				break;
			}
		}
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
	public SQLWrite getSqlWrite() {
		return sQLWrite;
	}
	public void setSqlWrite(SQLWrite sQLWrite) {
		this.sQLWrite = sQLWrite;
	}
	/**
	 * 获取数组格式的参数
	 * @return
	 */
	public Object[] getArgs() {
		return this.params.toArray(new Object[this.params.size()]);
	}
	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

	public SQLRead getSqlRead() {
		return sQLRead;
	}

	public void setSqlRead(SQLRead sQLRead) {
		this.sQLRead = sQLRead;
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
