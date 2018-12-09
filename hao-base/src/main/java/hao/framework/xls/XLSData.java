package hao.framework.xls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hao.framework.core.expression.HaoException;

/**
 * xls表数据
 * @author  chianghao
 * @time    2018年4月3日
 * @version 0.1
 */
public class XLSData {

	private XLSHeader xlsHeader;
	
	private ArrayList<Map<String,String>> data;
	
	private List<String[]> dataArray;
	
	public XLSData(XLSHeader header,ArrayList<Map<String,String>> data) {
		this.xlsHeader = header;
		this.data     = data;
	}
	
	public XLSHeader getXlsHeader() {
		return xlsHeader;
	}
	public void setXlsHeader(XLSHeader xlsHeader) {
		this.xlsHeader = xlsHeader;
	}

	public ArrayList<Map<String, String>> getData() {
		return data;
	}

	public void setData(ArrayList<Map<String, String>> data) {
		this.data = data;
	}

	/**
	 * 校验是否含有重复数据 
	 * @param idcard
	 * @throws HaoException 
	 */
	public void checkRepeat(String item) throws HaoException {
		if(item==null||item.equals("")) {
			return;
		}
		for(Map<String,String> map:data) {
			String value = map.get(item);
			if(value==null||value.equals("")) {
				continue;
			}
			int num=0;
			int index = 0;
			for(Map<String,String> map2:data) {
				String value2 = map2.get(item);
				if(value2==null||value2.equals("")) {
					continue;
				}
				if(value2.equals(value)) {
					num++;
				}
				if(num>1) {
					throw new HaoException("repeat data","第"+index+"行"+item+"数据重复：值为"+value2);
				}
				index++;
			}
		}
	}

	public List<String[]> getDataArray() {
		return dataArray;
	}

	public void setDataArray(List<String[]> dataArray) {
		this.dataArray = dataArray;
	}
	
}
