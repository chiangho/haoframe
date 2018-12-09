package hao.framework.core.utils;

import java.lang.reflect.Field;

import hao.framework.core.expression.HaoException;

public class ClassUtils {

	/***
	 * 获取对象的filed的值
	 * @param bean
	 * @param field
	 * @return
	 */
	public static <T> Object getFieldValue(T bean,String fieldName) {
		if(fieldName==null) {
			return null;
		}
		Field field = getField(bean.getClass(),fieldName);
		if(field==null) {
			return null;
		}
		boolean accessible = field.isAccessible();	
		field.setAccessible(true);  
		try {
			return field.get(bean);
		}catch (Exception e) {
			
		}finally {
			field.setAccessible(accessible);
		}
		return null;
	}
	
	public static Field getField(Class<?> clazz,String fieldName) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			
		} 
		if(field==null&&!clazz.getName().equals("java.lang.Object")) {
			field = getField(clazz.getSuperclass(),fieldName);
		}
		return field;
	}
	/***
	 * 给field设置值
	 * @param bean
	 * @param field
	 * @param string
	 * @throws HaoException 
	 */
	public static void setFieldValue(Object bean, String fieldName, Object value) {
		if(value==null) {
			return;
		}
		Field field = getField(bean.getClass(),fieldName);
		if(field==null) {
			return;
		}
		boolean accessible = field.isAccessible();	
		field.setAccessible(true);  
		try {
			field.set(bean, value);
		} catch (Exception e) {
		} 
		field.setAccessible(accessible);
	}
	
	public static void main(String[] args) {
		Test test = new Test();
		setFieldValue(test,"name",11);
		System.out.println(getFieldValue(test,"name"));
	}
	
}
