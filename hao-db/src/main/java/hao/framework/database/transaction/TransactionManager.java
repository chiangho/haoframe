package hao.framework.database.transaction;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import hao.framework.core.expression.HaoException;
import hao.framework.database.DBType;
import hao.framework.database.dao.JDBCDao;
import hao.framework.database.datasource.DataSourceManage;


/**
 * 智远事务管理
 * @author chianghao
 * @time   2018-03-28
 *
 */
public class TransactionManager {

	
	private DataSource dataSourse;
	private DataSourceTransactionManager transactionManager;
	private TransactionStatus status;
	
	
	/**
	 * 构造函数
	 * @param dataSource      数据源
	 * @throws HaoException 
	 */
	public TransactionManager(DataSource dataSource) throws HaoException{
		this(dataSource,TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}
	
	/**
	 * 
	 * @param type
	 * @param host
	 * @param port
	 * @param dbname
	 * @param access
	 * @param password
	 * @throws HaoException
	 */
	public TransactionManager(DBType type,String host,int port,String dbname,String access,String password) throws HaoException{
		this(type,host,port,dbname,access,password,TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}
	/**
	 * 构造函数
	 * @param dataSource               数据源
	 * @param transactionDefinition    事务的级别
	 * @throws HaoException
	 */
	public TransactionManager(DataSource dataSource,int transactionDefinition) throws HaoException{
		this.dataSourse = dataSource;
		if(dataSourse==null){
			throw new HaoException("no find data source","数据源不存在！");
		}
		transactionManager = new DataSourceTransactionManager(); 
		transactionManager.setDataSource(dataSourse);
		init(transactionDefinition);
	}
	/**
	 * 构造函数
	 * @param dataSource               数据源
	 * @param transactionDefinition    事务的级别
	 * @throws HaoException
	 */
	public TransactionManager(DBType type,String host,int port,String dbname,String access,String password,int transactionDefinition) throws HaoException{
		dataSourse = DataSourceManage.getDataSource(type,host,port,dbname,access,password);
		if(dataSourse==null){
			throw new HaoException("no find data source","数据源不存在！");
		}
		transactionManager = new DataSourceTransactionManager(); 
		transactionManager.setDataSource(dataSourse);
		init(transactionDefinition);
	}
	
	
	public JDBCDao getDao() throws HaoException{
		JDBCDao dao = new JDBCDao(transactionManager.getDataSource());
		return dao;
	}
	
	/**
	 * 初始化事务
	 */
	private void init(int transactionDefinition){
		 DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		 def.setPropagationBehavior(transactionDefinition);
		 status = transactionManager.getTransaction(def);
	}
	
	/**
	 * 提交事务,如果提交不成功则将事务回滚
	 */
	public void commit(){
		try{
			transactionManager.commit(status);
		}catch(Exception e){
			e.printStackTrace();
			transactionManager.rollback(status);
		}finally {
			dataSourse = null;
			transactionManager = null;
			status = null;
			try {
				this.finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 回滚事务
	 */
	public void rollback(){
		transactionManager.rollback(status);
		dataSourse = null;
		transactionManager = null;
		status = null;
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
