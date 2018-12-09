package hao.framework.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hao.framework.core.expression.HaoException;

public class ClassUtils {

	/***
	 * 获取对象的filed的值
	 * @param bean
	 * @param field
	 * @return
	 */
	public static <T> Object getFieldValue(T bean,String fieldName) {
		PropertyDescriptor pd = null;
		try {
			pd = new PropertyDescriptor(fieldName,bean.getClass());
		} catch (Exception e) {
			//e.printStackTrace();
		}
		if(pd!=null) {
			Method  getMethod = pd.getReadMethod();
			try {
				return getMethod.invoke(bean);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}else {
			try {
				Field field = bean.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);  
				return field.get(bean);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}  
		}
		return null;
	}
	
	
	
	/***
	 * 获取对象的filed的值
	 * @param bean
	 * @param field
	 * @return
	 */
	public static <T> Object getFieldValue(T bean,Field field) {
		try {
			PropertyDescriptor pd = new PropertyDescriptor(field.getName(),bean.getClass());
			Method  getMethod = pd.getReadMethod();
			return getMethod.invoke(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/***
	 * 设置方法为public
	 * @param method
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}
	
	/**
	 * 设置元素为public
	 * @param field
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
				.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
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
		PropertyDescriptor pd=null;
		try {
			pd = new PropertyDescriptor(fieldName,bean.getClass());
		} catch (Exception e) {
			
		}
		
		if(pd!=null) {
			Method  setMethod = pd.getWriteMethod();
			try {
				setMethod.invoke(bean, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}else {
			try {
				Field field = bean.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);  
				field.set(bean, value);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}  
		}
	}
	
	
	public static <T> Map<String,T> listToMap(List<T> list,String fieldName){
		if(list==null||list.size()==0) {
			return new HashMap<String,T>();
		}
		Map<String,T> map = new HashMap<String,T>();
		for(T bean:list) {
			Object keyValue = getFieldValue(bean,fieldName);
			map.put(ConverterUtils.objectToString(keyValue), bean);
		}
		return map;
	}
	
}
