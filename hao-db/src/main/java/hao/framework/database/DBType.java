package hao.framework.database;

/**
 * 数据类型
 * @author chianghao
 * @time   2018年4月18日
 */
public enum DBType {

	mysql("MYSQL","com.mysql.jdbc.Driver","jdbc:mysql://#HOST#:#PORT#/#DBNAME#?characterEncoding=UTF-8"),
	oracle("ORACLE","oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:@//#HOST#:#PORT#/#DBNAME#"),
	mssql("MSSQL","net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sqlserver://#HOST#:#PORT#/#DBNAME#");
	
	private String code;
	private String driver;
	private String url;
	
	
	private DBType(String code,String driver,String url){
		this.code   = code;
		this.driver = driver;
		this.url    = url;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl(String host,String port,String dbname) {
		String temp_url = url.replace("#HOST#", host);
		temp_url = temp_url.replace("#PORT#", port);
		temp_url = temp_url.replace("#DBNAME#", dbname);
		return temp_url;
	}
	public static DBType getDbType(String code){
		for(DBType dbtype:DBType.values()){
			if(dbtype.getCode().equals(code)){
				return dbtype;
			}
		}
		return null;
	}
	
}
