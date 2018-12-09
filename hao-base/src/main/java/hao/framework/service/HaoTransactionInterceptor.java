package hao.framework.service;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import hao.framework.model.ServiceActivityInfo;
import hao.framework.utils.DateUtils;

public class HaoTransactionInterceptor extends TransactionInterceptor{

	private static final long serialVersionUID = 1L;
	
	Logger log = LogManager.getLogger(this.getClass());//Logger.getLogger(this.getClass());

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String clazz = invocation.getThis().getClass().getName();
		String method= invocation.getMethod().getName();
		Object[] objects = invocation.getArguments();
		ServiceActivityInfo activityInfo =new ServiceActivityInfo();
		activityInfo.setClazz(clazz);
		activityInfo.setUserId("");
		activityInfo.setCode(clazz+"."+method);
		activityInfo.setMethod(method);
		activityInfo.setBeginTime(DateUtils.getTime("yyyy-MM-dd HH:mm:ss:SSS"));
		activityInfo.setParams(objects);
		Object end = null;
		Throwable throwable = null;
		try{
			end =  super.invoke(invocation);
			activityInfo.setEnd(1);
		}catch(Throwable e){
			activityInfo.setEnd(0);
			throwable = e;
		}
		handleServiceActivity(activityInfo);
		if(throwable!=null) {
			throw throwable;
		}
		return end;
	}
	
	
	/***
	 * 对调用的服务器处理
	 * @param activityInfo
	 */
	private void handleServiceActivity(ServiceActivityInfo activityInfo) {
		if(log.isDebugEnabled()) {
			log.info("=================>系统正在执行"+activityInfo.getCode());
		}
	}
	
}
