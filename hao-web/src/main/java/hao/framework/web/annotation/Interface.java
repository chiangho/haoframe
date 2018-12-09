package hao.framework.web.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.METHOD})   
@Retention(RetentionPolicy.RUNTIME) //在运行时加载到Annotation到JVM中

/***
 * table标签
 * @author chianghao
 */
public @interface Interface{
	/***
	 * 接口标题
	 * @return
	 */
	String title() default "";
	
	/**
	 * 备注
	 * @return
	 */
	String remark() default "";
	/***
	 * 接口编号
	 * @return
	 */
	String id();
	
}
