package hao.framework.database.dbmanage;

import java.util.List;

import hao.framework.database.dbmanage.dbtable.DBColumn;
import hao.framework.database.entity.ColumnInfo;
import hao.framework.database.entity.EntityInfo;



public interface DataMetaInterface {

	
	/**
	 * 获取创建数据表的语句
	 * @param entity
	 * @return
	 */
	public List<String> createTableDDL(EntityInfo entity);
	
	/**
	 * 获取表更新语句
	 * @param entity
	 * @return
	 */
	public List<String> alterTableDDL(EntityInfo entity);
	
	public String getColumnSql(ColumnInfo attribute);
	
	public DBColumn getColumn(ColumnInfo attribute);
	
	public List<String> getAlterColumnCommands(String tableName,ColumnInfo oldAttribute,ColumnInfo newAttribute);
	
	public List<String> getAddColumnCommands(String tableName,ColumnInfo newAttribute);
	
	public List<String> getDelColumnCommands(String tableName,String columnName);
}
