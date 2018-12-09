package hao.framework.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	/**
	 * 首字母小写
	 * @param content
	 * @return
	 */
	public static String firstCharLowercase(String content) {
		if(content==null||content.equals("")) {
			return "";
		}
		if(content.length()==1) {
			return content.toLowerCase();
		}
		String first = content.substring(0,1);
		String after = content.substring(1);
		return first.toLowerCase()+after;
	}
	
	/***
	 * 驼峰转下划线
	 * @param param
	 * @return
	 */
	public static String camelToUnderline(String content) {
		if(content==null||"".equals(content)){
            return "";
        }
		content=String.valueOf(content.charAt(0)).toUpperCase().concat(content.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(content);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(word.toLowerCase());
            sb.append(matcher.end()==content.length()?"":"_");
        }
        return sb.toString();
	}

	/***
	 * 转驼峰
	 * @param content  
	 * @param smallCamel 大小驼峰  studentName为小驼峰  StudentName为大驼峰
	 * @return
	 */
	public static String underlineToCamel(String content,boolean smallCamel) {
		if(content==null||"".equals(content)){
            return "";
        }
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher=pattern.matcher(content);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(smallCamel&&matcher.start()==0?Character.toLowerCase(word.charAt(0)):Character.toUpperCase(word.charAt(0)));
            int index=word.lastIndexOf('_');
            if(index>0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
	}

	
	/***
	 * 数组转字符串
	 * @param split
	 * @param array
	 * @return
	 */
	public static String arrayToStr(String split, String[] array) {
		if (split == null || array == null) {
			return "";
		}
		String result = "";
		for (int i = 0; i < array.length; i++) {
			if (i == array.length - 1) {
				result += array[i].toString();
			} else {
				result += array[i].toString() + split;
			}
		}
		return result;
	}
	
	
	public static String arrayToStr(String split, List<String> array) {
		if (split == null || array == null) {
			return "";
		}
		String result = "";
		for (int i = 0; i < array.size(); i++) {
			if (i == array.size() - 1) {
				result += array.get(i);
			} else {
				result += array.get(i) + split;
			}
		}
		return result;
	}
	
	
	/***
	 * md5 加密
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		return md5(str,true);
	}
	
	private static String md5(String str, boolean zero) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return null;
		}
		byte[] resultByte = messageDigest.digest(str.getBytes());
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < resultByte.length; ++i) {
			int v = 0xFF & resultByte[i];
			if(v<16 && zero)
				result.append("0");
			result.append(Integer.toHexString(v));
		}
		return result.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(firstCharLowercase("U"));
	}
	
	/**
	 * 校验数组中是否含有某个值
	 * @param array
	 * @param value
	 * @return
	 */
	public static boolean checkArrayHasValue(String[] array,String value) {
		for(String v:array) {
			if(v.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public static String listToString(List<String> list,char split) {
		int i=0;
		StringBuffer sb = new StringBuffer();
		for(String s:list) {
			if(i==0) {
				sb.append(s);
			}else {
				sb.append(split+s);
			}
			i++;
		}
		return sb.toString();
	}
	
}
