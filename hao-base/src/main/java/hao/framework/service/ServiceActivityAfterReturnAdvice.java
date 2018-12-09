package hao.framework.service;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;



/***
 * 记录service行为
 * @author chianghao
 *
 */
public class ServiceActivityAfterReturnAdvice implements AfterReturningAdvice,ThrowsAdvice{

	Logger log = LogManager.getLogger("------------service行为记录------------");
	
	
	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		if(log.isDebugEnabled()) {
			log.debug("类："+target.getClass().getName()+"，方法："+method.getName()+"，结果：成功");
		}
		
	}

	public void afterThrowing(Method method, Object[] args, Object target, Exception ex) {
		if(log.isDebugEnabled()) {
			log.debug("类："+target.getClass().getName()+"，方法："+method.getName()+"，结果：失败");
		}
	}


}
