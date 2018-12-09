package hao.framework.annotation;


/***
 * 默认就是数据库字典的
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.FIELD})   //用于字段，方法，参数
@Retention(RetentionPolicy.RUNTIME) //在运行时加载到Annotation到JVM中

/***
 * 表字段属性
 * @author chianghao
 */
public @interface Column{
	/***
	 * 数据库字段名称
	 * @return
	 */
	String columnName() default "";
	
	/***
	 * 数据库字段是否主键
	 * @return
	 */
	boolean primary() default false;
	
	/**
	 * 是否为空,默认不为空
	 * @return
	 */
	boolean isNull() default false;
	
	/**
	 * 默认值
	 * @return
	 */
	String defaultValue() default "";
	
}
