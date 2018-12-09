package hao.framework.transaction;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.interceptor.TransactionInterceptor;


/****
 * 
 * <!-- 事务代理执行，或者拦截器 -->
 * 
 * 
 * @author chianghao
 *
 */
public class HaoTransactionInterceptor extends TransactionInterceptor{

	private static final long serialVersionUID = 1L;
	
	Logger log =LogManager.getLogger(this.getClass());// Logger.getLogger(this.getClass());

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return super.invoke(invocation);
	}

	
}
