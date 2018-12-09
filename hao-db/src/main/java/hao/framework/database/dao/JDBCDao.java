package hao.framework.database.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import hao.framework.core.expression.HaoException;
import hao.framework.database.DBType;
import hao.framework.database.dao.map.SimpleRowCallbackHandler;
import hao.framework.database.dao.sql.SQLExecuteContent;
import hao.framework.database.dao.sql.SQLRead;
import hao.framework.database.dao.sql.SQLWrite;
import hao.framework.database.dao.sql.SQLWrite.SQLWriteAction;
import hao.framework.database.datasource.DataSourceManage;
import hao.framework.database.entity.EntityInfo;
import hao.framework.database.page.Page;
import hao.framework.database.page.dialect.Dialect;
import hao.framework.database.page.dialect.imp.MSDialect;
import hao.framework.database.page.dialect.imp.MySql5Dialect;
import hao.framework.database.page.dialect.imp.OracleDialect;


/**
 * jdbc数据库操作支撑
 * 
 * @author chianghao 2017-12-26下午01:54:18
 * @version v2.0 :对于spring jdbc 进行重构
 */
public class JDBCDao {

	private JdbcTemplate jdbcTemplate;
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	
	public JDBCDao() {}
	
	/**
	 * 构造函数
	 * @param dataSource
	 */
	public JDBCDao(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * 构造函数
	 * @param type
	 * @param host
	 * @param port
	 * @param dbname
	 * @param access
	 * @param password
	 */
	public JDBCDao(DBType type,String host,int port,String dbname,String access,String password) {
		jdbcTemplate = new JdbcTemplate(DataSourceManage.getDataSource(type, host, port, dbname, access, password));
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void execute(SQLWrite write) throws HaoException {
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(write);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		try {
			this.jdbcTemplate.update(sql, args);
		}catch(Exception e) {
			throw new HaoException("execute sql error",e.getMessage());
		}
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
			JdbcUtils.closeStatement(ps);
			DataSourceUtils.releaseConnection(connection, this.jdbcTemplate.getDataSource());
		}
	}
	
	/**
	 * 执行sql语句
	 * @param sql
	 * @param args
	 * @throws HaoException 
	 */
	public void executeUpdate(String sql,Object... args) throws HaoException {
		try {
			this.jdbcTemplate.update(sql, args);
		}catch(Exception e) {
			throw new HaoException("execute sql error",e.getMessage());
		}
	}
	
	/**
	 * 批量执行
	 * @param sql
	 * @param args
	 * @throws HaoException 
	 */
	public int[] batchExecuteUpdate(String sql,List<Object[]> batchArgs) throws HaoException {
		try {
			return this.jdbcTemplate.batchUpdate(sql, batchArgs);
		}catch(Exception e) {
			throw new HaoException("execute sql error",e.getMessage());
		}
	}
	
	
	/**
	 * 查询多条数据
	 * @param sql
	 * @param args
	 * @param rowMapper
	 * @return
	 */
	public <T> List<T> queryList(String sql,Object[] args,RowMapper<T> rowMapper) {
		return this.jdbcTemplate.query(sql, args, rowMapper);
	}
	
	/**
	 * 查询多条记录
	 * @param clazz
	 * @return
	 * @throws HaoException
	 */
	public <T> List<T> queryList(Class<?> clazz) throws HaoException {
		SQLRead read = new SQLRead(clazz);
		return this.queryList(read);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T queryBean(SQLRead sqlread) throws HaoException {
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(sqlread);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		SimpleRowCallbackHandler simpel = new SimpleRowCallbackHandler(sqlread);
		this.jdbcTemplate.query(sql,args,simpel);
		if(simpel.getList().size()==1) {
			return (T) simpel.getList().get(0);
		}else if(simpel.getList().size()==0){
			return null;
		}else {
			throw new HaoException("query_db_error:has more recode", "存在多条语句");
		}
	}
	
	
	/**
	 * 查询分页多条数据
	 * @param clazz
	 * @param page
	 * @return
	 * @throws HaoException
	 */
	public <T> List<T> queryPageList(Class<?> clazz,Page page) throws HaoException {
		SQLRead read = new SQLRead(clazz);
		return this.queryPageList(page, read);
	}
	/**
	 * 查询单条数据
	 * @param sql
	 * @param args
	 * @param rowMapper
	 * @return
	 */
	public <T> T queryBean(String sql,Object[] args,RowMapper<T> rowMapper) {
		return this.jdbcTemplate.queryForObject(sql, args, rowMapper);
	}

	/**
	 * 格式化sql语句
	 * @param sql
	 * @return
	 */
	private String formatterSql(String sql){
		sql  = sql.replace("　", "  ");
		sql  = sql.trim();
		if(sql.endsWith(";")){
			sql  = sql.substring(0, sql.lastIndexOf(";"));
		}
		return sql;
	}
	
	/**
	 * 构建方言对象
	 * @param dbName
	 * @return
	 */
	private Dialect createDialect(String dbName){
    	if(dbName.toLowerCase().contains("SQL Server".toLowerCase())){
    		return new MSDialect();
    	}
    	if(dbName.toLowerCase().contains("mysql".toLowerCase())){
    		return new MySql5Dialect();
    	}
    	if(dbName.toLowerCase().contains("oracle".toLowerCase())){
    		return new OracleDialect();
    	}
    	return null;
    } 
	/**
	 * 获取限制查询的函数
	 * @param sql    sql语句
	 * @param size   大小
	 * @param offset 偏移量
	 * @return
	 */
	private String getLimitSql(String sql,int limit,int offset) {
		sql = formatterSql(sql);
    	Connection conn = null;
    	try{
    		conn = this.jdbcTemplate.getDataSource().getConnection();
	    	DatabaseMetaData meta = conn.getMetaData();
	    	String dbName = meta.getDatabaseProductName();
	    	Dialect dialect = createDialect(dbName);
	    	if(dialect!=null){
	    		return dialect.getLimitString(sql, offset, limit);
	    	}
	    	return "";
    	}catch(Exception e){
    		e.printStackTrace();
    		return "";
    	}finally {
    		if(conn!=null){
	    		try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}
		}
	}
	
	/**
	 * 查询sql数量
	 * @param sql
	 * @param args
	 * @return
	 */
	private int getCount(String sql,Object[] args) {
		String countsql = "select count(1) from ("+sql+") as temp_count_table";
		return this.queryInt(countsql, args);
	}
	
	/**
	 * 查询分页记录
	 * @param sql
	 * @param args
	 * @param rowMapper
	 * @param page
	 * @return
	 */
	public <T> List<T> queryPageList(String sql,Object[] args,RowMapper<T> rowMapper,Page page){
		int count = getCount(sql,args);
		page.setRowNum(count);
		sql = getLimitSql(sql,page.getLimit(),page.getOffset());
		if(sql==null||sql.equals("")) {
			return null;
		}
		return queryList(sql,args,rowMapper);
	}
	
	/**
	 * 查询一个整数
	 * @param sql
	 * @param args
	 * @return
	 */
	public int queryInt(String sql,Object[] args) {
		return this.jdbcTemplate.queryForObject(sql, args, Integer.class);
	}
	
	public int queryCount(SQLRead read) throws HaoException {
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(read);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		return getCount(sql,args);
	}
	
	/**
	 * 查询对象的数字
	 * @param clazz
	 * @param filedName
	 * @param value
	 * @return
	 * @throws HaoException
	 */
	public int queryCount(Class<?> clazz,String filedName,Object value) throws HaoException {
		SQLRead read = new SQLRead(clazz);
		read.addCondition(filedName, value);
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(read);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		return getCount(sql,args);
	}
	
	/**
	 * 查询一个字符串
	 * @param sql
	 * @param args
	 * @return
	 */
	public String queryString(String sql,Object[] args) {
		return this.jdbcTemplate.queryForObject(sql, args, String.class);
	}
	
	/**
	 * 获取分页多条数据
	 * @param page
	 * @param sqlread
	 * @return
	 * @throws HaoException
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryPageList(Page page,SQLRead sqlread) throws HaoException{
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(sqlread);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		int count = getCount(sql,args);
		page.setRowNum(count);
		sql = getLimitSql(sql,page.getLimit(),page.getOffset());
		if(sql==null||sql.equals("")) {
			return null;
		}
		SimpleRowCallbackHandler simpel = new SimpleRowCallbackHandler(sqlread);
		this.jdbcTemplate.query(sql,args,simpel);
		return (List<T>) simpel.getList();
	}
	
	
	
	/**
	 * 查询多条记录
	 * @param sqlread
	 * @return
	 * @throws HaoException
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryList(SQLRead sqlread) throws HaoException{
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(sqlread);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		SimpleRowCallbackHandler simpel = new SimpleRowCallbackHandler(sqlread);
		this.jdbcTemplate.query(sql,args,simpel);
		return (List<T>) simpel.getList();
	}

	/**
	 * 查询返回List<Map>
	 * @param sqlread
	 * @return
	 * @throws HaoException
	 */
	public List<Map<String,Object>> queryForList(SQLRead sqlread) throws HaoException{
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(sqlread);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		return this.jdbcTemplate.queryForList(sql, args);
	}
	
	
	/**
	 * 查询对象
	 * @param tagerClass
	 * @param fieldName
	 * @param value
	 * @return
	 * @throws HaoException
	 */
	public <T> T queryBean(Class<?> tagerClass, String fieldName, Object value) throws HaoException {
		SQLRead read = new SQLRead(tagerClass);
		read.addCondition(fieldName, value);
		return this.queryBean(read);
	}

	/**
	 * 查询字符串
	 * @param tagerClass
	 * @param tagerField
	 * @param conditonField
	 * @param conditonValue
	 * @return
	 * @throws HaoException
	 */
	public String queryString(Class<?> tagerClass, String tagerField, String conditonField,Object conditonValue){
		try {
			SQLRead read = new SQLRead(tagerClass);
			read.setField(tagerField);
			read.addCondition(conditonField, conditonValue);
			SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(read);
			return this.queryString(sqlExecuteContent.getSql(), sqlExecuteContent.getArgs());
		}catch(Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	/**
	 * 按照主键删除对象
	 * @param tagClass
	 * @param id
	 * @throws HaoException
	 */
	public void deleteById(Class<?> tagClass, String id) throws HaoException {
		SQLWrite sqlWrite = new SQLWrite(tagClass,SQLWriteAction.delete);
		sqlWrite.addCondition("id",id);
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(sqlWrite);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		this.executeUpdate(sql, args);
	}

	public void delete(Class<?> tagClass, String fieldName, Object value) throws HaoException {
		SQLWrite sqlWrite = new SQLWrite(tagClass,SQLWriteAction.delete);
		sqlWrite.addCondition(fieldName,value);
		SQLExecuteContent sqlExecuteContent = new SQLExecuteContent(sqlWrite);
		String        sql     = sqlExecuteContent.getSql();
		Object[]      args    = sqlExecuteContent.getArgs();
		this.executeUpdate(sql, args);
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
		EntityInfo entityInfo  = EntityInfo.getEntity(tagClass);
		StringBuffer sb = new StringBuffer();
		sb.append("insert into "+entityInfo.getTableName()+"(");
		int i =0;
		
		StringBuffer paramsb = new StringBuffer();
		for(String field:fields) {
			if(i==0) {
				sb.append(entityInfo.getColumnInfo(field).getColumnName());
				paramsb.append("?");
			}else {
				sb.append(","+entityInfo.getColumnInfo(field).getColumnName());
				paramsb.append(",?");
			}
			i++;
		}
		sb.append(")values(");
		sb.append(paramsb.toString()+")");
		this.jdbcTemplate.batchUpdate(sb.toString(), batchArgs);
	}

	public <T> List<T> queryList(Class<?> tagClass, String fieldName,Object value) throws HaoException {
		SQLRead sqlread = new SQLRead(tagClass);
		sqlread.addCondition(fieldName, value);
		return this.queryList(sqlread);
	}

}
