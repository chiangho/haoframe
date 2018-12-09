package hao.framework.db.page;

import java.util.HashMap;

public class PageHelper {

	/**
	 * 分页设置
	 */
	protected static final ThreadLocal<HashMap<String, Page>> LOCAL_PAGE = new ThreadLocal<HashMap<String, Page>>();

	private static void setPage(String code, Page page) {
		if (LOCAL_PAGE.get() == null) {
			HashMap<String, Page> map = new HashMap<String, Page>();
			map.put(code, page);
			LOCAL_PAGE.set(map);
		} else {
			HashMap<String, Page> map = LOCAL_PAGE.get();
			map.put(code, page);
			LOCAL_PAGE.set(map);
		}
	}

	/***
	 * 获取唯一编号
	 * 
	 * @param clazz
	 * @param method
	 * @return
	 */
	public static String getCode(Class<?> clazz, String method) {
		String code = clazz.getName() + "." + method;
		return code;
	}

	/**
	 * 设置 Page 参数
	 *
	 * @param page
	 */
	public static void setPage(Class<?> clazz, String method, Page page) {
		setPage(getCode(clazz, method), page);
	}

	/**
	 * 获取 Page 参数
	 *
	 * @return
	 */
	public static Page getPage(String code) {
		if (code == null || code.equals("")) {
			return null;
		}
		if (LOCAL_PAGE.get() == null) {
			return null;
		}
		HashMap<String, Page> map = LOCAL_PAGE.get();
		return map.get(code);
	}

	/**
	 * 移除本地变量
	 */
	public static void clear() {
		LOCAL_PAGE.remove();
	}

	/**
	 * 移除分页
	 * 
	 * @param clazz
	 * @param method
	 */
	public static void removePage(Class<?> clazz, String method) {
		removePage(getCode(clazz, method));
	}

	/***
	 * 删除分页信息
	 * 
	 * @param code
	 */
	public static void removePage(String code) {
		if (code == null || code.equals("")) {
			return;
		}
		if (LOCAL_PAGE.get() == null) {
			return;
		}
		HashMap<String, Page> map = LOCAL_PAGE.get();
		map.remove(code);
		LOCAL_PAGE.set(map);
	}

	/***
	 * 销毁
	 */
	public static void destroyLocalPage() {
		if (LOCAL_PAGE.get() == null) {
			return;
		}
		LOCAL_PAGE.remove();
	}

}
