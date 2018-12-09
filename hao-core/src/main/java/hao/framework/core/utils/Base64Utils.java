package hao.framework.core.utils;

import org.apache.shiro.codec.Base64;

public class Base64Utils {

	public static String encode(String content) {
		return Base64.encodeToString(content.getBytes());
	}
	
	public static String decode(String content) {
		return Base64.decodeToString(content);
	}
	
	
	public static void main(String[] args) {
		String basecode = encode("!@#$%^&*()_+");
		System.out.println(basecode);
		System.out.println(decode(basecode));
	}
}
