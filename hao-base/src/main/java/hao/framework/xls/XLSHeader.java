package hao.framework.xls;

import java.util.ArrayList;

/**
 * xls表数据的列头
 * @author chianghao
 *
 */
public class XLSHeader {

	
	class Cell{
		/**
		 * 列序号
		 */
		private int     index;
		/**
		 * 名称
		 */
		private String  name;
		
		public Cell(int index,String name) {
			this.index = index;
			this.name  = name;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	
	private ArrayList<Cell> header= null;
	
	public XLSHeader(){
		header = new ArrayList<Cell>(); 
	}
	
	/**
	 * 添加列，移除重复的，放置最新的
	 * @param index
	 * @param name
	 */
	public void addCell(int index,String name) {
		Cell cell = new Cell(index,name);
		int coverIndex = -1;
		for(Cell c:header) {
			if(c.getIndex()==index) {
				coverIndex = c.getIndex();
				break;
			}
		}
		if(coverIndex>=0) {
			header.remove(coverIndex);
		}
		header.add(cell);
	}
	
	/**
	 *获取列名
	 * @return
	 */
	public String[] getCellNames() {
		String[] cells = new String[header.size()];
		for(int i=0;i<header.size();i++) {
			Cell c = header.get(i);
			cells[i] = c.getName();
		}
		return cells;
	}

	public int[] getCellIndexs() {
		int[] cellIndexs = new int[header.size()];
		for(int i=0;i<header.size();i++) {
			Cell c = header.get(i);
			cellIndexs[i] = c.getIndex();
		}
		return cellIndexs;
	}

	
	public String getName(int cellIndex) {
		for(int i=0;i<header.size();i++) {
			Cell c = header.get(i);
			if(c.getIndex()==cellIndex) {
				return c.getName();
			}
		}
		return null;
	}
	
	
	
}
