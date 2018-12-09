package hao.framework.service;

import org.aspectj.lang.JoinPoint;

/***
 * 服务被调用活跃的信息
 * @author chianghao
 *
 */
public class ServiceActivityUtils {


	/**
	 * 执行成功
	 * @param jp
	 */
	public void afterReturn(JoinPoint jp) {
		System.out.println("---------------------------------------------类："+jp.getTarget().getClass().getName()+"方法："+jp.getSignature().getName()+"--------------------------------------");
		System.out.println("---------------------------------------------成功--------------------------------------");
	}
	/***
	 * 执行失败
	 * @param jp
	 */
	public void afterThrowing(JoinPoint jp) {
		System.out.println("---------------------------------------------类："+jp.getTarget().getClass().getName()+"方法："+jp.getSignature().getName()+"--------------------------------------");
		System.out.println("---------------------------------------------异常--------------------------------------");
	}
}
