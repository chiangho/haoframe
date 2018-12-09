package hao.framework.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/***
	 * 获取时间
	 * @param formatter 时间格式 比如yyyy-MM-dd HH:mm:ss
	 * @return  返回字符串格式的时间
	 */
	public static String getTime(String formatter) {
		SimpleDateFormat format = new SimpleDateFormat(formatter);
		return format.format(new Date());
	}
	
}
