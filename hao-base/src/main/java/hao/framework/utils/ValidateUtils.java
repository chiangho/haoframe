package hao.framework.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {

	private static boolean isMatch(String regex, String orginal) {
		if (orginal == null || orginal.trim().equals("")) {
			return false;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher isNum = pattern.matcher(orginal);
		return isNum.matches();
	}

	/**
	 * 验证手机号码格式是否正确 ^匹配开始地方$匹配结束地方，[3|4|5|7|8] 选择其中一个{4,8},\d从[0-9]选择 {4,8}匹配次数4~8
	 * ，java中/表示转义，所以在正则表达式中//匹配/,/匹配""
	 */
	public static boolean checkMobile(String mobile) {
		if (mobile == null || mobile.equals(null)) {
			return false;
		}
		if (mobile.matches("^1[3|4|5|7|8][0-9]\\d{8}$")) {
			return true;
		}
		return false;
	}

	

	/**
	 * 校验地址是否是 http协议的地址
	 * 
	 * @param url
	 * @return
	 */
	public static boolean checkUrl(String url) {
		Pattern pattern = Pattern
				.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
		return pattern.matcher(url).matches();
	}

	/***
	 * 校验是否是银行卡
	 * 
	 * @param bankcard
	 * @return
	 */
	public static boolean checkBankcard(String bankCard) {
		if (bankCard.length() < 15 || bankCard.length() > 19) {
			return false;
		}
		char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return bankCard.charAt(bankCard.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * 
	 * @param nonCheckCodeBankCard
	 * @return
	 */
	private static char getBankCardCheckCode(String nonCheckCodeBankCard) {
		if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
				|| !nonCheckCodeBankCard.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeBankCard.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * 校验是否是小数
	 * 
	 * @param orginal
	 * @return
	 */
	public static boolean checkDecimal(String orginal) {
		return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
	}

	/**
	 * 校验奇数
	 * 
	 * @param content
	 * @return
	 */
	public static boolean checkEven(String content) {
		try {
			BigDecimal decimal = new BigDecimal(content);
			BigDecimal end = decimal.divideAndRemainder(new BigDecimal(2))[1];
			if (end.compareTo(BigDecimal.ZERO) == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean checkOdd(String content) {
		try {
			BigDecimal decimal = new BigDecimal(content);
			BigDecimal end = decimal.divideAndRemainder(new BigDecimal(2))[1];
			if (end.compareTo(BigDecimal.ONE) == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 正整数
	 * @param orginal
	 * @return
	 */
	public static boolean isPositiveInteger(String orginal) {
		return isMatch("^\\+{0,1}[1-9]\\d*", orginal);
	}

	/**
	 * 负整数
	 * @param orginal
	 * @return
	 */
	public static boolean isNegativeInteger(String orginal) {
		return isMatch("^-[1-9]\\d*", orginal);
	}

	/**
	 * 整数
	 * @param orginal
	 * @return
	 */
	public static boolean checkInteger(String orginal) {
		return isMatch("[+-]{0,1}0", orginal) || isPositiveInteger(orginal) || isNegativeInteger(orginal);
	}

	/***
	 * 校验正数 暨大于等于零的数
	 * 
	 * @param content
	 * @return
	 */
	public static boolean checkPositive(String content) {
		try {
			BigDecimal decimal = new BigDecimal(content);
			if (decimal.compareTo(BigDecimal.ZERO) == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param content
	 * @return
	 */
	public static boolean checkNegative(String content) {
		try {
			BigDecimal decimal = new BigDecimal(content);
			if (decimal.compareTo(BigDecimal.ZERO) == -1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean checkEmail(String content) {
		return isMatch("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$",content);
	}
	
	public static void main(String[] args) {
		 String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";    
		// String check = "^([a-z0-9A-Z]+[-|\\ \\ \\ \\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\\\\\\\.)+[a-zA-Z]{2,}$";    
		 Pattern regex = Pattern.compile(check);    
		 Matcher matcher = regex.matcher("jh@126.com");    
		 boolean isMatched = matcher.matches();    
		 System.out.println(isMatched);    
		
		System.out.println(checkEmail("jh@126.com"));
	}


}
