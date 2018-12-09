package hao.framework.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import hao.framework.core.spring.Property;
import hao.framework.database.DBType;
import hao.framework.database.HaoDB;

public class StartupWeb extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		//扫描实例对象
		HaoDB.init(Property.getValue("ModelPackage"));
		//自动创建表
		HaoDB.createTable(
				DBType.mysql,
				Property.getValue("jdbc.host"),
				Integer.parseInt(Property.getValue("jdbc.port")),
				Property.getValue("jdbc.db_name"),
				Property.getValue("jdbc.account"),
				Property.getValue("jdbc.password")
				);
		
	}

	
}
