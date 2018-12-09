package hao.framework.db;

import java.sql.Connection;
import java.util.List;

import hao.framework.db.ddl_model.Attribute;
import hao.framework.db.ddl_model.Entity;
import hao.framework.db.table.ColumnInfo;

public interface DataMetaInterface {

	
	/**
	 * 获取创建数据表的语句
	 * @param entity
	 * @return
	 */
	public List<String> createTableDDL(Entity entity);
	
	/**
	 * 获取表更新语句
	 * @param entity
	 * @return
	 */
	public List<String> alterTableDDL(Entity entity);
	
	public String getColumnSql(Attribute attribute);
	
	public ColumnInfo getColumn(Attribute attribute);
	
	public List<String> getAlterColumnCommands(String tableName,Attribute oldAttribute,Attribute newAttribute);
	
	public List<String> getAddColumnCommands(String tableName,Attribute newAttribute);
	
	public List<String> getDelColumnCommands(String tableName,String columnName);
}
