package hao.framework.db.dao.jdbc.map;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import hao.framework.utils.ClassUtils;

public class HaoRowMapper<T> implements RowMapper<T>{

	private Class<?> tagerClass;

	private String[][] resultMap;

	public HaoRowMapper(Class<?> tagerClass,String[][] resultMap){
		this.tagerClass = tagerClass;
		this.resultMap  = resultMap;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T obj = null;  
		try{
			obj = (T) tagerClass.newInstance();  
			for(String[] item:resultMap){
				String fieldName  = item[0];
				String columnName = item[1];
				Object value = null;
				Class<?> fieldClazz = tagerClass.getDeclaredField(fieldName).getType();
				if (fieldClazz == int.class || fieldClazz == Integer.class) { 
					// int  
					value =  rs.getInt(columnName);  
                } else if (fieldClazz.isAssignableFrom(boolean.class) || fieldClazz.isAssignableFrom(Boolean.class)) { 
                	// boolean  
                	value = rs.getBoolean(columnName);  
                } else if (fieldClazz.isAssignableFrom(String.class) ) { 
                	// string  
                	value =  rs.getString(columnName);  
                } else if (fieldClazz.isAssignableFrom(float.class) ){ 
                	// float  
                	value = rs.getFloat(columnName);  
                } else if (fieldClazz.isAssignableFrom(double.class) || fieldClazz.isAssignableFrom(Double.class)) { 
                	// double  
                	value = rs.getDouble(columnName);  
                } else if (fieldClazz.isAssignableFrom(BigDecimal.class) ){ 
                	// bigdecimal  
                	value = rs.getBigDecimal(columnName);  
                } else if (fieldClazz.isAssignableFrom(short.class) || fieldClazz.isAssignableFrom(Short.class)) { 
                	// short  
                	value =  rs.getShort(columnName);  
                } else if (fieldClazz.isAssignableFrom(Date.class)) { 
                	// date  
                	value =  rs.getDate(columnName);  
                } else if (fieldClazz.isAssignableFrom(Timestamp.class)) { 
                	// timestamp  
                	value = rs.getTimestamp(columnName);  
                } else if (fieldClazz.isAssignableFrom(Long.class) || fieldClazz.isAssignableFrom(long.class)) { 
                	// long  
                	value =  rs.getLong(columnName);  
                }  
				ClassUtils.setFieldValue(obj, fieldName, value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	}

	public Class<?> getTagerClass() {
		return tagerClass;
	}
	public void setTagerClass(Class<?> tagerClass) {
		this.tagerClass = tagerClass;
	}
	public String[][] getResultMap() {
		return resultMap;
	}
	public void setResultMap(String[][] resultMap) {
		this.resultMap = resultMap;
	}
	
	
}

