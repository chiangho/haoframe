package hao.framework.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.METHOD})   //用于字段，方法，参数
@Retention(RetentionPolicy.RUNTIME) //在运行时加载到Annotation到JVM中

/***
 * 校验必填项
 * @author chianghao
 */
public @interface Required{
	
	/***
	 * 必填字段
	 * @return
	 */
	String requirKeys() default "";//设置是否需要登录，默认是需要登录，如果不标注则说明不登录也可以访问
}
