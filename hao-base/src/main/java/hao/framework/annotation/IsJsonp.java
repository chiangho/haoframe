package hao.framework.annotation;


/**
 * ajax异步请求返回的数据类型
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.METHOD})   //用于字段，方法，参数
@Retention(RetentionPolicy.RUNTIME) //在运行时加载到Annotation到JVM中

/***
 * 是否是表jsonp
 * @author chianghao
 */
public @interface IsJsonp{
	String  jsonpCallback() default "jsonpCallback";
}
