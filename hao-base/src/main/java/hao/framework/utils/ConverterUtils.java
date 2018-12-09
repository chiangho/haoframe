package hao.framework.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据类型转换工具类
 * 
 * @author chianghao
 */
public class ConverterUtils {
	

	
	public static BigDecimal toDecimal(String obj) {
		return toDecimal(obj,BigDecimal.ZERO);
	}
	
	public static BigDecimal toDecimal(String obj,BigDecimal defaultVal) {
		if(obj==null) {
			return defaultVal;
		}
		try {
			BigDecimal value = new BigDecimal(obj);
			return value;
		}catch(Exception e) {
			e.printStackTrace();
			return defaultVal;
		}
		
	}

	public static Object stringToInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}

	public static Object stringToLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}

	public static Object stringToFloat(String value) {
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}

	public static Object stringToDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}

	public static Object stringToDecimal(String value) {
		try {
			BigDecimal decimal = new BigDecimal(value);
			return decimal;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}

	public static Date stringToDate(String value,String formatter) {
		SimpleDateFormat format = new SimpleDateFormat(formatter);
		try {
			return format.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object stringToChar(String value) {
		if(value.length()==1) {
			return value.toCharArray()[0];
		}
		return null;
	}

	public static Object stringToByte(String value) {
		return value.getBytes();
	}

	public static int stringToInt(String value, int i) {
		return stringToInt(value)==null?i:(int) stringToInt(value);
	}

	/***
	 * 将object对象装换成数字
	 * @param value
	 * @return
	 */
	public static long objectToLong(Object value) {
		if(value instanceof String) {
			return (long) stringToLong((String)value);
		}else if(value instanceof Long) {
			return (long) value;
		}else if(value instanceof Integer) {
			Integer temp = (Integer) value;
			return (long) temp;
		}else if(value instanceof Float) {
			float temp = (float) value;
			return (long) temp;
		}else if(value instanceof Double) {
			double temp = (double) value;
			return (long) temp;
		}else if(value instanceof Character) {
			Character temp = (Character) value;
			return (long) temp;
		}else if(value instanceof Short) {
			short temp = (short ) value;
			return (long) temp;
		}else if(value instanceof BigDecimal){
			BigDecimal temp = (BigDecimal) value;
			return temp.longValue();
		}else {
			return 0;
		}
	}
	

	public static int objectToInt(Object value) {
		if(value instanceof String) {
			return (int) stringToInt((String)value);
		}else if(value instanceof Long) {
			long temp = (long) value;
			return (int) temp;
		}else if(value instanceof Integer) {
			int temp = (int) value;
			return (int) temp;
		}else if(value instanceof Float) {
			float temp = (float) value;
			return (int) temp;
		}else if(value instanceof Double) {
			double temp = (double) value;
			return (int) temp;
		}else if(value instanceof Character) {
			Character temp = (Character) value;
			return (int) temp;
		}else if(value instanceof Short) {
			short temp = (short ) value;
			return (int) temp;
		}else if(value instanceof BigDecimal){
			BigDecimal temp = (BigDecimal) value;
			return temp.intValue();
		}else {
			return 0;
		}
	}

	public static float objectToFloat(Object value) {
		if(value instanceof String) {
			return (float) stringToFloat((String)value);
		}else if(value instanceof Long) {
			long temp = (long) value;
			return (float) temp;
		}else if(value instanceof Integer) {
			int temp = (int) value;
			return (float) temp;
		}else if(value instanceof Float) {
			float temp = (float) value;
			return (float) temp;
		}else if(value instanceof Double) {
			double temp = (double) value;
			return (float) temp;
		}else if(value instanceof Character) {
			Character temp = (Character) value;
			return (float) temp;
		}else if(value instanceof Short) {
			short temp = (short ) value;
			return (float) temp;
		}else if(value instanceof BigDecimal){
			BigDecimal temp = (BigDecimal) value;
			return temp.floatValue();
		}else {
			return 0F;
		}
	}

	public static double objectToDouble(Object value) {
		if(value instanceof String) {
			return (double) stringToDouble((String)value);
		}else if(value instanceof Long) {
			long temp = (long) value;
			return (double) temp;
		}else if(value instanceof Integer) {
			int temp = (int) value;
			return (double) temp;
		}else if(value instanceof Float) {
			float temp = (float) value;
			return (double) temp;
		}else if(value instanceof Double) {
			double temp = (double) value;
			return (double) temp;
		}else if(value instanceof Character) {
			Character temp = (Character) value;
			return (double) temp;
		}else if(value instanceof Short) {
			short temp = (short ) value;
			return (double) temp;
		}else if(value instanceof BigDecimal){
			BigDecimal temp = (BigDecimal) value;
			return temp.doubleValue();
		}else {
			return 0D;
		}
	}

	public static BigDecimal objectToDecimal(Object value) {
		if(value instanceof String) {
			return toDecimal((String)value);
		}else if(value instanceof Long) {
			long temp = (long) value;
			return new BigDecimal(temp);
		}else if(value instanceof Integer) {
			int temp = (int) value;
			return new BigDecimal(temp);
		}else if(value instanceof Float) {
			float temp = (float) value;
			return new BigDecimal(temp);
		}else if(value instanceof Double) {
			double temp = (double) value;
			return new BigDecimal(temp);
		}else if(value instanceof Character) {
			Character temp = (Character) value;
			return new BigDecimal(temp);
		}else if(value instanceof Short) {
			short temp = (short ) value;
			return new BigDecimal(temp);
		}else if(value instanceof BigDecimal){
			BigDecimal temp = (BigDecimal) value;
			return temp;
		}else {
			return BigDecimal.ZERO;
		}
	}

	public static Date objectToDate(Object value, String formatter) {
		SimpleDateFormat format = new SimpleDateFormat(formatter);
		if(value instanceof String) {
			return stringToDate((String)value, formatter);
		}else if(value instanceof java.util.Date) {
			java.util.Date temp = (java.util.Date) value;
			return stringToDate(format.format(temp),formatter);
		}else if(value instanceof java.sql.Date) {
			java.sql.Date date=(java.sql.Date) value; 
			java.util.Date d=new java.util.Date (date.getTime());
			return d;
		}
		return null;
	}

	public static Timestamp objectToTimestamp(Object value,String formatter) {
		SimpleDateFormat format = new SimpleDateFormat(formatter);
		Date time = null;
		if(value instanceof String) {
			time = stringToDate((String)value, formatter);
		}else if(value instanceof java.util.Date) {
			java.util.Date temp = (java.util.Date) value;
			time = stringToDate(format.format(temp),formatter);
		}else if(value instanceof java.sql.Date) {
			java.sql.Date date=(java.sql.Date) value; 
			time = new java.util.Date (date.getTime());
		}else if(value instanceof java.sql.Timestamp) {
			return (java.sql.Timestamp)value;
		}
		if(time!=null) {
			return new java.sql.Timestamp(time.getTime());
		}
		return null;
	}
	
	public static char objectToChar(Object value) {
		if(value instanceof String) {
			return (char) stringToChar((String)value);
		}else if(value instanceof Long) {
			long temp = (long) value;
			return (char) temp;
		}else if(value instanceof Integer) {
			int temp = (int) value;
			return (char) temp;
		}else if(value instanceof Float) {
			float temp = (float) value;
			return (char) temp;
		}else if(value instanceof Double) {
			double temp = (double) value;
			return (char) temp;
		}else if(value instanceof Character) {
			Character temp = (Character) value;
			return (char) temp;
		}else if(value instanceof Short) {
			short temp = (short ) value;
			return (char) temp;
		}else if(value instanceof BigDecimal){
			BigDecimal temp = (BigDecimal) value;
			return (char) temp.intValue();
		}
		return 0;
	}

	public static Object objectToByte(Object value) {
		return null;
	}

	/**
	 * 装换成字符
	 * @param value
	 * @return
	 */
	public static String objectToString(Object value) {
		return String.valueOf(value);
	}
}
