package hao.framework.db.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页对象
 * 
 * @author chianghao
 */
public class Page {
	
	/***
	 * 页面大小
	 */
	//public static final String PAGE_SIZE="pageSize";
	
	/***
	 * 页面序号 即第几页
	 */
	//public static final String PAGE_NO="pageNo";
	
	/***
	 * 页面大小
	 */
	public static final String PAGE_SIZE="rows";
	
	/***
	 * 页面序号 即第几页
	 */
	public static final String PAGE_NO="page";
	
	/**
	 * 页面默认大小
	 */
	public static int PAGE_SIZE_DEFAULT_VALUE=15;

	/**
	 * 页码
	 */
	private int pageNo;
	/***
	 * 每页大小
	 */
	private int pageSize;
	/***
	 * 条数
	 */
	private int rowNum;
	/***
	 * 总页数
	 */
	private int pageCount;

	private int offset;

	private int limit;

	public Page(int pageNo, int pageSize) {
		this.pageNo = pageNo==0?1:pageNo;
		this.pageSize = pageSize==0?PAGE_SIZE_DEFAULT_VALUE:pageSize;
		this.calcOffset();
		this.calcLimit();
	}

	private void calcOffset() {
		this.offset = ((pageNo - 1) * pageSize);
	}

	private void calcLimit() {
		this.limit = pageSize;
	}
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
		calculationPageCount();
	}

	public int getPageCount() {
		return pageCount;
	}

	/***
	 * 计算总页数
	 */
	private void calculationPageCount() {
		if (rowNum <= 0) {
			this.pageCount = 0;
		} else if (rowNum <= this.pageSize && rowNum > 0) {
			this.pageCount = 1;
		} else {
			int pages = rowNum / pageSize;
			this.pageCount = rowNum % pageSize > 0 ? ++pages : pages;
		}
	}

	/**
	 * 获取分页数据
	 * @param <T>
	 * @param obj
	 * @return
	 */
	public <T> Map<String,Object> getPageData(List<T> list) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();//定义map  
        jsonMap.put("total",rowNum);//total键 存放总记录数，必须的  
        jsonMap.put("rows", list);//rows键 存放每页记录 list  
		return jsonMap;
	}

}
