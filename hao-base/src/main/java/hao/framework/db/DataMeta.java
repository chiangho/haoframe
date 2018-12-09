package hao.framework.db;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hao.framework.core.expression.HaoException;
import hao.framework.db.ddl_model.Attribute;
import hao.framework.db.ddl_model.Entity;
import hao.framework.db.impl.MssqlDataMeta;
import hao.framework.db.impl.MysqlDataMeta;
import hao.framework.db.impl.OracleDataMeta;
import hao.framework.db.table.ColumnInfo;
import hao.framework.db.table.TableInfo;


/***
 * 
 * 
 * catalog - 类别名称；它必须与存储在数据库中的类别名称匹配；该参数为 "" 表示获取没有类别的那些描述；为 null
 * 则表示该类别名称不应该用于缩小搜索范围 
 * schemaPattern - 模式名称的模式；它必须与存储在数据库中的模式名称匹配；该参数为 ""
 * 表示获取没有模式的那些描述；为 null 则表示该模式名称不应该用于缩小搜索范围 
 * tableNamePattern -
 * 表名称模式；它必须与存储在数据库中的表名称匹配 
 * types - 要包括的表类型所组成的列表，必须取自从 getTableTypes()
 * 返回的表类型列表；null 表示返回所有类型
 * 
 * @author Administrator
 *
 */

public abstract class DataMeta implements DataMetaInterface {
	
	Logger log = LogManager.getLogger("DataMeta");
	
	/**
	 * 连接类型
	 */
	protected DBType dbType;
	/**
	 * host
	 */
	protected String host;
	/**
	 * 端口
	 */
	protected int port;
	/**
	 * 库名
	 */
	protected String dbName;
	/**
	 * 账号
	 */
	protected String access;
	/**
	 * 密码
	 */
	protected String password;
	
	
	protected Map<Integer,Integer[]> dataMapper = new HashMap<Integer,Integer[]>();

	
	public Connection getConection() {
		try {
			Class.forName(this.dbType.getDriver());
			Connection con = DriverManager.getConnection(this.dbType.getUrl(host, port+"", this.dbName),access, password); 
			return con;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}  
		return null;
	}
	
	/**
	 * 检查网络连接是否成功
	 * 
	 * @param millisecondsTimeout
	 *            网络连接超时时间
	 * @return
	 */
	public boolean TestConnection(int millisecondsTimeout) {
		Socket socket = new Socket();
		try {
			InetAddress addr = InetAddress.getByName(host);
			boolean isReachable = addr.isReachable(millisecondsTimeout);
			if (isReachable) {
				socket.connect(new InetSocketAddress(addr, port));
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	/**
	 * 检查sql连接是否成功
	 * 
	 * @return
	 */
	public boolean TeatSqlConection() {
		if (!TestConnection(3000)) {
			return false;
		}
		Connection conn = null;
		try {
			conn = this.getConection();
			if (conn == null) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 批量执行
	 * @param sqlCommands
	 * @throws HaoException
	 */
	public void executeBatch(List<String> sqlCommands) throws HaoException{
		if(sqlCommands==null||sqlCommands.size()==0){
			return;
		}
		Connection conn = this.getConection();
		Statement st = null;
		try{
			conn.setAutoCommit(false);
			st = conn.createStatement();
			for(String sql:sqlCommands){
				log.info("执行命令："+sql);
				st.addBatch(sql);
			}
			st.executeBatch();
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try {
				if (conn!=null&&!conn.isClosed()) {   
					 conn.rollback();   
				    conn.setAutoCommit(true);   
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}  
			throw new HaoException("DB_ERROR", e.getMessage());
		}finally {
			if(st!=null){
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
	 * 查询sql获取list<map>对象
	 * @param sql
	 * @return
	 */
	public List<Map<String,Object>> queryList(String sql){
		Connection conn = this.getConection();
		PreparedStatement ps = null;
		CachedRowSet crs =null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			RowSetFactory factory = RowSetProvider.newFactory();  
			crs = factory.createCachedRowSet(); 
			ResultSetMetaData rsmd = rs.getMetaData();
			int length = rsmd.getColumnCount();
			String[] headerNames = new String[length];
			for(int i=0;i<length;i++){
				String headerName = rsmd.getColumnName((i+1));
				String headerLabel = rsmd.getColumnLabel((i+1));
				String value = (headerLabel==null||headerLabel.equals(""))?headerName:headerLabel;
				headerNames[i] = value;
			}
			crs.populate(rs);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			while(crs.next()){
				Map<String,Object> map = new HashMap<String,Object>();
				for(int i=0;i<length;i++){
					Object value = crs.getObject((i+1));
					map.put(headerNames[i], value);
				}
				list.add(map);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(crs!=null){
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 查询字符串
	 * @param sql
	 * @return
	 */
	public String queryString(String sql){
		Connection conn = this.getConection();
		PreparedStatement ps = null;
		CachedRowSet crs =null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			RowSetFactory factory = RowSetProvider.newFactory();  
			crs = factory.createCachedRowSet(); 
			crs.populate(rs);
			while(crs.next()){
				return (crs.getObject(1)).toString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(crs!=null){
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
	
	/**
	 * 获取表信息
	 * 
	 * @param tableName
	 * @return
	 */
	public TableInfo getTableInfo(String tableName) {
		TableInfo tableInfo = new TableInfo();
		Connection conn = this.getConection();
		try {
			DatabaseMetaData dm = conn.getMetaData();
			String[] types = { "TABLE" };
			ResultSet rs = dm.getTables(null, null, tableName, types);
			ResultSetMetaData rsmd = rs.getMetaData();
			String[] header = new String[rsmd.getColumnCount()];
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				header[i] = rsmd.getColumnName((i + 1));
			}
			while (rs.next()) {
				for (int i = 0; i < header.length; i++) {
					String value = rs.getString(header[i]) == null ? "" : rs.getString(header[i]);
					try {
						PropertyDescriptor pd = new PropertyDescriptor(header[i], TableInfo.class);
						Method wM = pd.getWriteMethod();
						wM.invoke(tableInfo, value);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			rs.close();
			ResultSet rscolumn = dm.getColumns(null, null, tableName, null);
			ResultSetMetaData rsmdcolumn = rscolumn.getMetaData();
			String[] headercolumn = new String[rsmdcolumn.getColumnCount()];
			for (int i = 0; i < rsmdcolumn.getColumnCount(); i++) {
				headercolumn[i] = rsmdcolumn.getColumnName((i + 1));
			}
			List<ColumnInfo> list = new ArrayList<ColumnInfo>();
			while (rscolumn.next()) {
				ColumnInfo columnInfo = new ColumnInfo();
				for (int i = 0; i < headercolumn.length; i++) {
					String value = rscolumn.getString(headercolumn[i]) == null ? ""
							: rscolumn.getString(headercolumn[i]);
					try {
						PropertyDescriptor pd = new PropertyDescriptor(headercolumn[i], ColumnInfo.class);
						Method wM = pd.getWriteMethod();
						wM.invoke(columnInfo, value);
					} catch (Exception e) {
						System.out.println(headercolumn[i] + "=" + value);
						e.printStackTrace();
					}
				}
				list.add(columnInfo);
			}
			rscolumn.close();
			tableInfo.setColumns(list);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tableInfo;
	}

	/**
	 * 判断表是否在数据库存在
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean checkTableExist(String tableName) {
		Connection conn = this.getConection();
		try {
			DatabaseMetaData dm = conn.getMetaData();
			String[] types = { "TABLE" };
			ResultSet rs = dm.getTables(null, null, tableName, types);
			int rowCount = 0;
			while (rs.next()) {
				rowCount++;
			}
			if(rowCount==1){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取操作数据库的DDL语句
	 * 
	 * @param entity
	 * @return
	 */
	public List<String> getDDLSql(Entity entity) {
		if (entity == null || entity.getTableName() == null || entity.getTableName().equals("")
				|| entity.getAttributes() == null || entity.getAttributes().size() == 0) {
			return null;
		}
		if (!checkTableExist(entity.getTableName())) {
			return createTableDDL(entity);
		}else{
			return alterTableDDL(entity);
		}
	}
	/**
	 * 创建DataMeta
	 * 
	 * @param type
	 * @param host
	 * @param port
	 * @param dbName
	 * @param assess
	 * @param password
	 * @return
	 */
	public static DataMeta createDataMeta(DBType type, String host, int port, String dbName, String assess,
			String password) {
		switch (type) {
		case mysql:
			return new MysqlDataMeta(type,host, port, dbName, assess, password);
		case oracle:
			return new OracleDataMeta(type,host, port, dbName, assess, password);
		case mssql:
			return new MssqlDataMeta(type,host, port, dbName, assess, password);
		default:
			return null;
		}
	}
	
	public Attribute getEntityColumn(String columnName,List<Attribute> attributes){
		for(Attribute a:attributes){
			if(columnName.toUpperCase().equals(a.getColumnName().toUpperCase())){
				return a;
			}
		}
		return null;
	}
	
	public ColumnInfo getTableColumn(String columnName,List<ColumnInfo> columns){
		for(ColumnInfo a:columns){
			if(columnName.toUpperCase().equals(a.getCOLUMN_NAME().toUpperCase())){
				return  a;
			}
		}
		return null;
	}
	
	/***
	 * 检查列是否存在
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public boolean checkColumnExist(String tableName,String columnName){
		Connection conn =  this.getConection();
		ResultSet rs    =  null;
		try{
			if(conn!=null){
				DatabaseMetaData dm = conn.getMetaData();
				rs = dm.getColumns(null, null, tableName, columnName);
				int count = 0;
				while(rs.next()){
					count ++;
				}
				if(count==1){
					return true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * 删除列信息
	 * @param tableName
	 * @param columnName
	 * @throws HaoException 
	 */
	public void delColumn(String tableName,String columnName) throws HaoException{
		//检查表是否存在，如果不存在则直接跳出
		if(this.checkTableExist(tableName)){
			if(this.checkColumnExist(tableName,columnName)){
				//获取删除字段的命令
				List<String> commands = getDelColumnCommands(tableName, columnName);
				//执行删除字段的命令
				this.executeBatch(commands);
			}
		}
	}
	
	public static void main(String[] args) {
		//DataMeta dataMeta = createDataMeta(DBType.mssql, "192.168.1.172", 1433, "nfk_dcm_qsgz", "sa", "jszy2010!@#");
		DataMeta dataMeta = createDataMeta(DBType.oracle, "192.168.1.162", 1521, "orcl", "vipmgr", "vipmgr");
		
		Entity entity = new Entity("temp_table1");
		entity.setRemark("测试表");
		Attribute attribute1 = new Attribute("id",ColumnDataType.INT);
		attribute1.setLength(20);
		attribute1.setRemark("主键");
		
		Attribute attribute2 = new Attribute("name",ColumnDataType.STRING);
		attribute2.setLength(400);
		attribute2.setRemark("名称");
		attribute2.setDefaultValue("hello111");
		
		Attribute attribute3 = new Attribute("monay",ColumnDataType.DECIMAL);
		attribute3.setLength(10);
		attribute3.setPrecesion(2);
		attribute3.setRemark("钱bbb");
		
		Attribute attribute4 = new Attribute("content",ColumnDataType.TEXT);
		attribute4.setRemark("文本内容");
		Attribute attribute5 = new Attribute("begint_time",ColumnDataType.DATE);
		attribute5.setRemark("日期");
		Attribute attribute6 = new Attribute("end_time",ColumnDataType.TIME);
		attribute6.setRemark("时间");
		
		entity.addAttribute(attribute1);
		entity.addAttribute(attribute2);
		entity.addAttribute(attribute3);
		entity.addAttribute(attribute4);
		entity.addAttribute(attribute5);
		entity.addAttribute(attribute6);
		
		List<String> ends = dataMeta.getDDLSql(entity);
		for(String e:ends){
			System.out.println(e);
		}
		
		TableInfo  tableInfo  = dataMeta.getTableInfo("temp_table1");
		System.out.println(tableInfo.getTABLE_NAME());
		
		
		
	}

}
