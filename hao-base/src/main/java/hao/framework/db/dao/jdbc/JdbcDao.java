package hao.framework.db.dao.jdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Condition;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.expression.DBRuntimeException;
import hao.framework.core.expression.HaoException;
import hao.framework.core.sequence.Seq;
import hao.framework.db.page.Page;
import hao.framework.db.page.dialect.MySqlDialect;
import hao.framework.db.sql.SqlSearch;
import hao.framework.db.sql.SqlWrite;
import hao.framework.db.sql.SqlWrite.SqlWriterAction;
import hao.framework.utils.ClassUtils;

/**
 * jdbc数据库操作支撑
 * 
 * @author chianghao 2017-12-26下午01:54:18
 * @version v2.0 :对于spring jdbc 进行重构
 */
public class JdbcDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * 最大的读条数
	 */
	int maxReadRow  = 3000;
	
	int maxWirteSize = 6000;
	
	protected final Logger logger = LogManager.getLogger(this.getClass());// Logger.getLogger(getClass());

	public void execute(String sql, Object[] args) {
		logger.info("执行sql:"+sql);
		this.jdbcTemplate.update(sql, args);
	}
	public void execute(String sql) {
		logger.info("执行sql:"+sql);
		this.jdbcTemplate.update(sql);
	}

	/***
	 * 查询字符串
	 */
	public String queryString(String sql, Object[] params) throws DBRuntimeException {
		logger.info("执行sql:"+sql);
		return this.jdbcTemplate.queryForObject(sql, params, String.class);
	}

	/***
	 * 获取链接
	 * 
	 * @return
	 */
	public Connection getConnection() {
		try {
			return this.jdbcTemplate.getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 获取数据库的所有表信息
	 * 
	 * @return
	 * @throws HaoException
	 */
	public Set<String> getTables() throws HaoException {
		Connection conn = getConnection();
		if (conn == null) {
			throw new HaoException("000001", "没有找到链接");
		}
		try {
			DatabaseMetaData md = conn.getMetaData();
			if (md != null) {
				ResultSet rs = md.getTables(null, null, null, new String[] { "TABLE" });
				Set<String> sets = new HashSet<String>();
				while (rs.next()) {
					sets.add(rs.getString("TABLE_NAME"));
				}
				return sets;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new HaoException("000002", "查找databaseMetaData对象出错！");
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public int queryInt(String sql, Object[] objects) throws HaoException {
		logger.info("执行sql:"+sql);
		return this.jdbcTemplate.queryForObject(sql, objects, Integer.class);
	}

	public int queryCount(SqlSearch sqlSearch) throws HaoException {
		SQLExecutionContent exeContent = new SQLExecutionContent(sqlSearch);
		int totalRow = getRowNum(exeContent.getSql(), exeContent.getaParams());
		return totalRow;
	}
	/**
	 * 获取sql语句的记录数量
	 * 
	 * @param sql
	 * @param values
	 * @return
	 */
	private int getRowNum(String sql, Object[] args) {
		sql = "select count(1) from (" + sql + ") as row_num_temp_table";
		return this.jdbcTemplate.queryForObject(sql, args, Integer.class);
	}
	private <E> List<E> query(SQLExecutionContent content){
		return query(content,content.getSql());
	}
	
	class ClassMapper{
		
		private Class<?>           clazz;
		private Class<?>           mainClazz;
		private String             subClazzName;
		private List<FieldMapper>  fieldMapper;
		
		
		public ClassMapper(Class<?> clazz,List<FieldMapper> fieldMapper)  {
			this.clazz       =  clazz;
			this.fieldMapper =  fieldMapper;
			
			for(FieldMapper fm:fieldMapper) {
				mainClazz = fm.getMainClazz();
				if(mainClazz!=null&&!clazz.isAssignableFrom(mainClazz)) {
					for(Field f:mainClazz.getDeclaredFields()) {
						if(clazz.isAssignableFrom(f.getType())) {
							subClazzName = f.getName();
							break;
						}
					}
				}
				break;
			}
			
		} 
		
		public String getSubClazzName() {
			return subClazzName;
		}

		public void setSubClazzName(String subClazzName) {
			this.subClazzName = subClazzName;
		}

		public Class<?> getClazz() {
			return clazz;
		}
		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}
		public Class<?> getMainClazz() {
			return mainClazz;
		}
		public void setMainClazz(Class<?> mainClazz) {
			this.mainClazz = mainClazz;
		}
		public List<FieldMapper> getFieldMapper() {
			return fieldMapper;
		}
		public void setFieldMapper(List<FieldMapper> fieldMapper) {
			this.fieldMapper = fieldMapper;
		}
	}
	
	private List<ClassMapper> getClassMapper(List<FieldMapper> fieldMappers){
		Map<Class<?>,ArrayList<FieldMapper>> classFieldMapper = new HashMap<Class<?>,ArrayList<FieldMapper>>();
		for(FieldMapper fm:fieldMappers) {
			ArrayList<FieldMapper> temp = classFieldMapper.get(fm.getClazz());
			if(temp==null) {
				temp = new ArrayList<FieldMapper>();
			}
			temp.add(fm);
			classFieldMapper.put(fm.getClazz(), temp);
		}
		List<ClassMapper> classMapper =new  ArrayList<ClassMapper>();
		for(Class<?> clazz:classFieldMapper.keySet()) {
			ClassMapper cm = new ClassMapper(clazz,classFieldMapper.get(clazz));
			classMapper.add(cm);
		}
		return classMapper;
	}
	
	
	/**
	 * 执行查询
	 * @param content
	 * @param sql     需要将content中内容替换
	 * @return
	 */
	private <E> List<E> query(SQLExecutionContent content,String sql){
		if(sql==null||sql.equals("")) {
			sql = content.getSql();
		}
		List<E> list = new ArrayList<E>();
		Connection connection = DataSourceUtils.getConnection(this.jdbcTemplate.getDataSource());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			int i = 1;
			for (Object v : content.getaParams()) {
				ps.setObject(i, v);
				i++;
			}
			rs = ps.executeQuery();
//			Map<Class<?>,ArrayList<FieldMapper>> classFieldMapper = new HashMap<Class<?>,ArrayList<FieldMapper>>();
//			for(FieldMapper fm:content.getFieldMappers()) {
//				ArrayList<FieldMapper> temp = classFieldMapper.get(fm.getClazz());
//				if(temp==null) {
//					temp = new ArrayList<FieldMapper>();
//				}
//				temp.add(fm);
//				classFieldMapper.put(fm.getClazz(), temp);
//			}
//			List<ClassMapper> classMapper =new  ArrayList<ClassMapper>();
//			for(Class<?> clazz:classFieldMapper.keySet()) {
//				ClassMapper cm = new ClassMapper(clazz,classFieldMapper.get(clazz));
//				classMapper.add(cm);
//			}
			
			List<ClassMapper> classMapper = getClassMapper(content.getFieldMappers());
			while(rs.next()) {
				E bean  = rsToBean(rs,classMapper);
				list.add(bean);
			}
			return list;
		}catch(SQLException e) {
			e.printStackTrace();
			JdbcUtils.closeStatement(ps);
			ps = null;
			DataSourceUtils.releaseConnection(connection, this.jdbcTemplate.getDataSource());
			connection = null;
			throw this.jdbcTemplate.getExceptionTranslator().translate("PreparedStatementCallback",sql, e);
		} finally {
			logger.info("执行sql:"+sql);
			JdbcUtils.closeStatement(ps);
			JdbcUtils.closeResultSet(rs);
			DataSourceUtils.releaseConnection(connection, this.jdbcTemplate.getDataSource());
		}
		
	}
	
	/**
	 * 查询对象
	 * @param <E>
	 * @param clazz
	 * @return
	 * @throws HaoException 
	 */
	public  <E> List<E> queryList(Class<?> clazz) throws HaoException{
		SqlSearch sqlSearch = new SqlSearch(clazz);
		return queryList(sqlSearch);
	}
	
	/**
	 * 根据某一给条件查询list
	 * @param clazz
	 * @param field
	 * @param value
	 * @return
	 * @throws HaoException
	 */
	public  <E> List<E> queryList(Class<?> clazz,String field,Object value) throws HaoException{
		SqlSearch sqlSearch = new SqlSearch(clazz);
		sqlSearch.addCondition(field, value);
		return queryList(sqlSearch);
	}
	
	private String getLimitSql(String sql,int row,int offset) {
		return sql+" limit "+row+","+offset;
	}
	
	/**
	 * 分页查询
	 * @param page
	 * @param sqlSearch
	 * @return
	 * @throws HaoException 
	 */
	public <E> List<E> queryList(Page page,SqlSearch sqlSearch) throws HaoException{
		SQLExecutionContent exeContent = new SQLExecutionContent(sqlSearch);
		int totalRow = getRowNum(exeContent.getSql(), exeContent.getaParams());
		page.setRowNum(totalRow);
		MySqlDialect mySqlDialect = new MySqlDialect();
		String sql = mySqlDialect.getLimitString(exeContent.getSql(),page.getOffset(), page.getLimit());
		return query(exeContent, sql);
	}
	
	/**
	 * 更具sqlsearch查询
	 * @param sqlSearch
	 * @return
	 * @throws HaoException 
	 */
	public <E> List<E> queryList(SqlSearch sqlSearch) throws HaoException {
		SQLExecutionContent exeContent = new SQLExecutionContent(sqlSearch);
		// 先查询总数
		int totalRow = getRowNum(exeContent.getSql(), exeContent.getaParams());
		if (totalRow < maxReadRow) {
			return query(exeContent);
		}else {
			//计算创建几个线程，使用多线程来查询，查询结果再做归并
			int step = maxReadRow;
			int beginNum = 0;
			Vector<ExeReadThread> group = new Vector<ExeReadThread>();
			while(beginNum<=totalRow) {
				String newsql = "";
				newsql = getLimitSql(exeContent.getSql(),beginNum,step);
				ExeReadThread exeThread = new ExeReadThread(newsql,exeContent);
				group.add(exeThread);
				beginNum=beginNum+step;
			}
			Callable<List<E>> callable = new Callable<List<E>>() {
	            public List<E> call() throws Exception {
	            	for(ExeReadThread e:group) {
	            		e.start();
	            	}
	            	while(true) {
	            		int i=0;
	            		for(ExeReadThread e:group) {
		            		if(e.isEnd) {
		            			i++;
		            		}
		            	}
	            		if(i==group.size()) {
	            			break;
	            		}
	            	}
	            	List<E> list = new ArrayList<E>();
	            	for(ExeReadThread e:group) {
	            		list.addAll((List<E>) e.getResule());
	            	}
	            	return list;
	            }
	        };
	        ExecutorService executor = Executors.newCachedThreadPool();
	        Future<List<E>> future = executor.submit(callable);
			try {
				List<E> list =  future.get();
				return list;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}
	}

	@SuppressWarnings("unchecked")
	private <E> E rsToBean(ResultSet rs, List<ClassMapper> classMapper) throws SQLException {
		//取主对象信息
		try {
			Map<Class<?>,E> beans = new HashMap<Class<?>,E>();
			for(ClassMapper cm:classMapper) {
				beans.put(cm.getClazz(),(E) cm.getClazz().newInstance());
			}
			for(ClassMapper cm:classMapper) {
				Class<?> clazz = cm.getClazz();
				Object bean = beans.get(clazz);
				for(FieldMapper fm:cm.getFieldMapper()) {
					Object tagValue = rs.getObject(fm.getIndex());
					if(tagValue==null) {
						continue;
					}
					if(fm.getProperty()==null) {
						ClassUtils.setFieldValue(bean,fm.getProperty().getName(),tagValue);
					}else {
						Object value = fm.getProperty().formatterObjectValue(tagValue);//toObjectValue(fm.getProperty(),tagValue);
						ClassUtils.setFieldValue(bean,fm.getProperty().getName(),value);
					}
				}
			}
			for(ClassMapper cm:classMapper) {
				if(cm.getSubClazzName()!=null) {
					E bean  = beans.get(cm.getClazz());
					E _bean = beans.get(cm.getMainClazz());
					ClassUtils.setFieldValue(_bean,cm.getSubClazzName(),bean);
				}
			}
			E mainBean = null;
			for(ClassMapper cm:classMapper) {
				if(cm.getClazz().isAssignableFrom(cm.getMainClazz())) {
					mainBean = beans.get(cm.getClazz());
					break;
				}
			}
			return mainBean;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	
	
	/**
	 * 批量执行
	 * @param sql
	 * @param list
	 */
	public int[] executeBatch(String sql,List<Object[]> list) {
		Connection connection = DataSourceUtils.getConnection(this.jdbcTemplate.getDataSource());
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			for (Object[] args: list) {
				int i = 1;
				for (Object v : args) {
					ps.setObject(i, v);
					i++;
				}
				ps.addBatch();
			}
			int[] ends = ps.executeBatch();
			ps.clearBatch();
			ps.close();
			return ends;
		}catch (SQLException e) {
			e.printStackTrace();
			JdbcUtils.closeStatement(ps);
			ps = null;
			DataSourceUtils.releaseConnection(connection, this.jdbcTemplate.getDataSource());
			connection = null;
			throw this.jdbcTemplate.getExceptionTranslator().translate("PreparedStatementCallback", sql, e);
		} finally {
			logger.info("执行sql:"+sql);
			JdbcUtils.closeStatement(ps);
			DataSourceUtils.releaseConnection(connection, this.jdbcTemplate.getDataSource());
		}
	}
	
	/**
	 * 使用临时表的方式更新表
	 * @param sql
	 */
	private void useTempTableUpdatBatch(String sql,List<Object[]> list) {
		//创建mysql sql语句的解析器
		SQLStatementParser parser = new MySqlStatementParser(sql);
		// 使用Parser解析生成AST，这里SQLStatement就是AST
		MySqlUpdateStatement statement = (MySqlUpdateStatement) parser.parseStatement();
        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);
        
        Map<TableStat.Name,TableStat> tables = visitor.getTables();
        if(tables.size()==1) {
        	String tableName = "";
        	Vector<String> columns     = new Vector<String>();
        	Vector<String> conditons     = new Vector<String>();
        	
        	List<Condition> _conditions =  visitor.getConditions();
        	List<SQLUpdateSetItem>  _columns    =  statement.getItems();
        	
        	for(TableStat.Name name:tables.keySet()) {
        		tableName = name.getName();
        	}
        	for(SQLUpdateSetItem setItem:_columns) {
        		try {
        			SQLIdentifierExpr expr = (SQLIdentifierExpr) setItem.getColumn();
            		SQLExprTableSource tableSourse =   (SQLExprTableSource) expr.getResolvedOwnerObject();
            		SQLIdentifierExpr tableExpr = (SQLIdentifierExpr) tableSourse.getExpr();
            		if(tableExpr.getName().equals(tableName)) {
            			columns.add(expr.getName());
            		}
        		}catch(Exception e) {
        			continue;
        		}
        		
        	}
        	for(Condition c:_conditions) {
        		if(c.getColumn().getTable().equals(tableName)) {
        			conditons.add(c.getColumn().getName());
        		}
        	}
        	//创建临时表
        	String tempTableName = tableName+Seq.getNextId();
        	
        	StringBuffer sb = new StringBuffer(" create table "+tempTableName+" ( ");
    		for(String c:columns) {
				String columnSql  = c+" text null";
				sb.append(columnSql+",");
    		}
    		for(String c:conditons) {
    			String columnSql  = "where_"+c+" text null";
				sb.append(columnSql+",");
    		}
    		String createTable = sb.toString();
    		if(createTable.endsWith(",")) {
    			createTable = createTable.substring(0,createTable.lastIndexOf(","));
    		}
    		createTable+=");";
    		execute(createTable);
    		System.out.println(createTable);
        	//将数据插入临时表
        	String insertSql = "insert into "+tempTableName+"(";
        	for(String c:columns) {
				insertSql+=c+",";
        	}
        	for(String c:conditons) {
        		insertSql+="where_"+c+",";
        	}
        	if(insertSql.endsWith(",")) {
        		insertSql = insertSql.substring(0,insertSql.lastIndexOf(","));
    		}
        	insertSql+=")values(";
        	for(String c:columns) {
				insertSql+="?,";
        	}
        	for(String c:conditons) {
        		insertSql+="?,";
        	}
        	if(insertSql.endsWith(",")) {
        		insertSql = insertSql.substring(0,insertSql.lastIndexOf(","));
    		}
        	insertSql+=")";
    		executeBatch(insertSql, list);
    		System.out.println(insertSql);
    		
        	String batchSql = "update "+tableName+","+tempTableName+" set ";
        	int i=0;
        	for(String c:columns) {
        		if(i==0) {
        			batchSql+=tableName+"."+c+"="+tempTableName+"."+c;
        		}else {
        			batchSql+=","+tableName+"."+c+"="+tempTableName+"."+c;
        		}
        		i++;
        	}
        	batchSql+=" where ";
        	i=0;
        	for(String c:conditons) {
        		if(i==0) {
        			batchSql+=tableName+"."+c+"="+tempTableName+".where_"+c;
        		}else {
        			batchSql+=","+tableName+"."+c+"="+tempTableName+".where_"+c;
        		}
        		i++;
        	}
        	System.out.println(batchSql);
        	execute(batchSql);
        	execute("drop table "+tempTableName);
        }
	}
	
	/**
	 * 批量执行
	 * @param sqlWrites
	 */
	public void executeBatch(List<SqlWrite> sqlWrites) {
		for(SqlWrite sqlWrite:sqlWrites) {
			SQLExecutionContent exeContent = new SQLExecutionContent(sqlWrite);
			this.jdbcTemplate.update(exeContent.getSql(), exeContent.getaParams());
		}
	}
	
	/**
	 * 批量执行
	 * 
	 * @param batchUpdate
	 */
	public void executeBatch(BatchUpdate batchUpdate) {
		Map<String, List<Object[]>> map = batchUpdate.getNamedParameterGroupExecutionContent();
		for (String sql : map.keySet()) {
			List<Object[]> list = map.get(sql);
			if(list.size()<=maxWirteSize) {
				executeBatch(sql,list);
			}else {
				int beginNum = 0;
				while(beginNum<=list.size()) {
					List<Object[]> newaArge = new ArrayList<Object[]>();
					for(int i=beginNum;i<(beginNum+maxWirteSize);i++) {
						if((i+1)>=list.size()) {
							break;
						}
						newaArge.add(list.get(i));
					}
					beginNum=beginNum+maxWirteSize;
					sql = sql.trim();
					executeBatch(sql,newaArge);
				}
			}
		}
	}
	
	public void execute(SqlWrite sqlWrite) {
		SQLExecutionContent exeContent = new SQLExecutionContent(sqlWrite);
		this.jdbcTemplate.update(exeContent.getSql(), exeContent.getaParams());
	}

	/**
	 *  写的执行线程
	 * @author chianghao
	 *
	 */
	class ExeReadThread extends Thread{
		private boolean               isEnd =  false;
		private String                sql;
		private SQLExecutionContent   content;
		private Object     resule;
		
		public ExeReadThread(String sql,SQLExecutionContent content) {
			this.sql            =  sql;
			this.content        =  content;
		}
		@Override
		public void run() {
			resule = query(content,sql);
			isEnd = true;
		}
		public boolean isEnd() {
			return isEnd;
		}
		public void setEnd(boolean isEnd) {
			this.isEnd = isEnd;
		}
		public String getSql() {
			return sql;
		}
		public void setSql(String sql) {
			this.sql = sql;
		}
		public SQLExecutionContent getContent() {
			return content;
		}
		public void setContent(SQLExecutionContent content) {
			this.content = content;
		}
		public Object getResule() {
			return resule;
		}
		public void setResule(Object resule) {
			this.resule = resule;
		}
		
	}
	
	
	public static void main(String[] args) {
		JdbcDao dao = new JdbcDao();
		dao.useTempTableUpdatBatch("update table1 set a=?,b=?,c=? where c=?", null);
	}
	
	
	public <E> E queryBean(Class<?> clazz, String fieldName, Object value) throws HaoException {
		SqlSearch search = new SqlSearch(clazz);
		search.addCondition(fieldName, value);
		return queryBean(search);
	}
	
	public <E> E queryBean(SqlSearch search) throws HaoException {
		SQLExecutionContent exeContent = new SQLExecutionContent(search);
		List<ClassMapper> classMapper = getClassMapper(exeContent.getFieldMappers());
		Object resule = this.jdbcTemplate.query(exeContent.getSql(),exeContent.getaParams(),new ResultSetExtractor<Object>() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				try {
					while(rs.next()) {
						Map<Class<?>,E> beans = new HashMap<Class<?>,E>();
						for(ClassMapper cm:classMapper) {
							beans.put(cm.getClazz(),(E) cm.getClazz().newInstance());
						}
						for(ClassMapper cm:classMapper) {
							Class<?> clazz = cm.getClazz();
							Object bean = beans.get(clazz);
							for(FieldMapper fm:cm.getFieldMapper()) {
								Object tagValue = rs.getObject(fm.getIndex());
								if(tagValue==null) {
									continue;
								}
								if(fm.getProperty()==null) {
									ClassUtils.setFieldValue(bean,fm.getProperty().getName(),tagValue);
								}else {
									Object value = fm.getProperty().formatterObjectValue(tagValue);//toObjectValue(fm.getProperty(),tagValue);
									ClassUtils.setFieldValue(bean,fm.getProperty().getName(),value);
								}
							}
						}
						for(ClassMapper cm:classMapper) {
							if(cm.getSubClazzName()!=null) {
								E bean  = beans.get(cm.getClazz());
								E _bean = beans.get(cm.getMainClazz());
								ClassUtils.setFieldValue(_bean,cm.getSubClazzName(),bean);
							}
						}
						E mainBean = null;
						for(ClassMapper cm:classMapper) {
							if(cm.getClazz().isAssignableFrom(cm.getMainClazz())) {
								mainBean = beans.get(cm.getClazz());
								break;
							}
						}
						return mainBean;
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		return (E) resule;
	}
	
	/**
	 * 删除
	 * @param class1
	 * @param string
	 * @param id
	 * @throws HaoException 
	 */
	public void del(Class<?> clazz, String fieldName, String value) throws HaoException {
		SqlWrite sqlWrite  = new SqlWrite(clazz,SqlWriterAction.delete);
		sqlWrite.addCondition(fieldName, value);
		this.execute(sqlWrite);
	}
	
	/***
	 * 查询
	 * @param sql         sql查询语句
	 * @param args        参数
	 * @param rowMapper   对象映射
	 * @return
	 */
	public <E> List<E> queryList(String sql,Object[] args,RowMapper<E> rowMapper){
		return this.jdbcTemplate.query(sql, args, rowMapper);
	}
	
	/**
	 * 批量插入
	 * @param insert
	 * @param class1
	 * @param strings
	 * @param writeList
	 */
	public void batchInsert(Class<?> tagClass, String[] fields,List<Object[]> batchArgs) {
		if(fields==null||fields.length==0) {
			return;
		}
		EntityInfo entityInfo  = SystemConfig.getEntity(tagClass);
		StringBuffer sb = new StringBuffer();
		sb.append("insert into "+entityInfo.getTableName()+"(");
		int i =0;
		
		StringBuffer paramsb = new StringBuffer();
		for(String field:fields) {
			if(i==0) {
				sb.append(entityInfo.getProperty(field).getColumnName());
				paramsb.append("?");
			}else {
				sb.append(","+entityInfo.getProperty(field).getColumnName());
				paramsb.append(",?");
			}
			i++;
		}
		sb.append(")values(");
		sb.append(paramsb.toString()+")");
		this.jdbcTemplate.batchUpdate(sb.toString(), batchArgs);
	}
	
	public void batchUpdate(Class<?> tagClass,String conditionFileld,String[] fields,List<Object[]> batchArgs) {
		if(fields==null||fields.length==0) {
			return;
		}
		EntityInfo entityInfo  = SystemConfig.getEntity(tagClass);
		StringBuffer sb = new StringBuffer();
		sb.append("update  "+entityInfo.getTableName()+" set ");
		int i =0;
		for(String field:fields) {
			if(i==0) {
				sb.append(entityInfo.getProperty(field).getColumnName()+" =? ");
			}else {
				sb.append(","+entityInfo.getProperty(field).getColumnName()+" =? ");
			}
			i++;
		}
		sb.append(" where "+entityInfo.getProperty(conditionFileld).getColumnName()+" =? ");
		this.jdbcTemplate.batchUpdate(sb.toString(), batchArgs);
	}
	
	
}
