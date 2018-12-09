package hao.framework.db.dao.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hao.framework.db.sql.SqlWrite;

/**
 * 批量更新对象
 * @author chianghao
 * @param 
 *
 */
public class BatchUpdate{

	Logger log = LogManager.getLogger("批量更新对象");
	
	private ArrayList<SqlWrite> sqlWrites;
	
	private BatchUpdate() {
		sqlWrites = new ArrayList<SqlWrite>();
	}
	
	public void addSqlWrite(SqlWrite sqlWrite) {
		sqlWrites.add(sqlWrite);
	}
	
	public static BatchUpdate createBatchUpdate() {
		return new BatchUpdate();
	}
	public List<SqlWrite> getSqlWrites() {
		return sqlWrites;
	}
	public void setSqlWrites(ArrayList<SqlWrite> sqlWrites) {
		this.sqlWrites = sqlWrites;
	}
	
	/**
	 * 获取执行内容
	 * @return
	 */
	public LinkedMap<String,List<Object[]>> getNamedParameterGroupExecutionContent() {
		if(sqlWrites==null||sqlWrites.size()==0) {
			return null;
		}
		LinkedMap<String,List<Object[]>> group = new LinkedMap<String,List<Object[]>>();
		for(SqlWrite sqlWrite:sqlWrites) {
			SQLExecutionContent exeContent = new SQLExecutionContent(sqlWrite);
			String sql = exeContent.getSql();
			List<Object[]> _i = group.get(sql);
			if(_i==null) {
				_i  = new ArrayList<Object[]>();
				_i.add(exeContent.getaParams());
			}else {
				_i.add(exeContent.getaParams());
			}
			group.put(sql, _i);
		}
		return group;
	}
	
	
}
