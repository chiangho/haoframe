package hao.framework.database.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

import hao.framework.database.DBType;

/**
 * 数据源管理
 * 
 * @author chianghao
 * @time 2018年4月18日
 * 
 */
public class DataSourceManage {

	
	/**
	 * 创建系统数据源映射
	 */
	public static Map<String, DataSource> dataSourses = new HashMap<String, DataSource>();

	/**
	 * 获取数据源的key
	 * @param type
	 * @param host
	 * @param port
	 * @param dbname
	 * @param access
	 * @param password
	 * @return
	 */
	private static String getKey(DBType type, String host, int port, String dbname, String access, String password) {
		String key = type.getCode() + "_" + host + "_" + port + "_" +dbname+"_" + access + "_" + password;
		return key;
	}

	/**
	 * 创建数据源
	 * @param type
	 * @param host
	 * @param port
	 * @param dbname
	 * @param access
	 * @param password
	 * @return
	 */
	public static DataSource createDataSource(DBType type, String host, int port, String dbname, String access,String password) {
		DruidDataSource dataSourceSSO = new DruidDataSource();
		// 设置连接参数
		dataSourceSSO.setUrl(type.getUrl(host, port + "", dbname));
		dataSourceSSO.setDriverClassName(type.getDriver());
		dataSourceSSO.setUsername(access);
		dataSourceSSO.setPassword(password);
		// 配置初始化大小、最小、最大
		dataSourceSSO.setInitialSize(5);
		dataSourceSSO.setMinIdle(1);
		dataSourceSSO.setMaxActive(20);
		// 连接泄漏监测
		dataSourceSSO.setRemoveAbandoned(true);
		dataSourceSSO.setRemoveAbandonedTimeout(30);
		// 配置获取连接等待超时的时间
		dataSourceSSO.setMaxWait(20000);
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		dataSourceSSO.setTimeBetweenEvictionRunsMillis(20000);
		// 防止过期
		dataSourceSSO.setValidationQuery("SELECT 'x'");
		dataSourceSSO.setTestWhileIdle(true);
		dataSourceSSO.setTestOnBorrow(true);
		return dataSourceSSO;
	}

	/**
	 * 获取数据源
	 * @param type
	 * @param host
	 * @param port
	 * @param dbname
	 * @param access
	 * @param password
	 * @return
	 */
	public static DataSource getDataSource(DBType type, String host, int port, String dbname, String access,String password) {
		String key = getKey(type, host, port, dbname, access, password);
		if (dataSourses.get(key) == null) {
			DataSource dataSource = createDataSource(type, host, port, dbname, access, password);
			dataSourses.put(key, dataSource);
			return dataSource;
		} else {
			return dataSourses.get(key);
		}
	}
	
	
}
