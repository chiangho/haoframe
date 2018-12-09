package hao.framework.db;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.alibaba.druid.pool.DruidDataSource;

import hao.framework.core.SpringContext;

/**
 * 运行sql脚本
 * @author chianghao
 *
 */
public class SqlScriptRunner {
	
	
	private Connection connection;
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * spring 框架配置的数据源名称
	 * @param dataSourceName
	 */
	public SqlScriptRunner(String dataSourceName) {
		DruidDataSource dataSourse = SpringContext.getBean(dataSourceName);
		this.connection = DataSourceUtils.getConnection(dataSourse);
	}
	
	public void run() {
		ScriptRunner runner = new ScriptRunner(connection);  
        try {
        	Resources.setCharset(Charset.forName("UTF-8"));
			runner.runScript(Resources.getResourceAsReader("sql/init-system.sql"));
			runner.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			clean();
		}
        
	}
	
	/***
	 * 清理，
	 * 1、关闭链接
	 * @throws SQLException
	 */
	public void clean(){
		if(this.connection!=null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
