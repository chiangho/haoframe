package org.hao.db.test;

import java.util.List;

import hao.framework.core.expression.HaoException;
import hao.framework.database.DBType;
import hao.framework.database.HaoDB;
import hao.framework.database.dao.JDBCDao;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		HaoDB.init("org.hao.db.test");
		JDBCDao dao = new JDBCDao(DBType.mysql,"127.0.0.1",13306,"demo","root","111111");
		List<Company> list;
		try {
			//Page page  =new Page(1,10);
			list = dao.queryList(Company.class);
			for(Company c:list) {
				System.out.println(c.getId()+","+c.getIsDel()+","+c.getName());
			}
			
			System.out.println("****************************");
			
		} catch (HaoException e) {
			e.printStackTrace();
		}
		assertTrue(true);
	}
}
