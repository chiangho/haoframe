package hao.framework.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***
 * 数据表字段类型
 * @author chianghao
 *
 */
public enum ColumnType {

	STRING(1,"字符串"),
	INT(2,"整数"),
	BINARY(3,"二级制"),
	DECIMAL(4,"小数"),
	TIME(5,"时间"),
	DATE(6,"日期"),
	TEXT(7,"大文本")
	//BLOB(8,"blob")
	;
	
	
	private int no;
	private String name;
	
	private ColumnType(int no,String name) {
		this.no = no;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}
	
	public static List<Map<String,String>> toList(){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		for(ColumnType d:ColumnType.values()){
			Map<String,String> item = new HashMap<String,String>();
			item.put("no",d.getNo()+"");
			item.put("name",d.getName());
			list.add(item);
		}
		return list;
	}
	
	public static String toSpilt(){
		String list = "";
		int i=0;
		for(ColumnType d:ColumnType.values()){
			Map<String,String> item = new HashMap<String,String>();
			item.put("no",d.getNo()+"");
			item.put("name",d.getName());
			String temp = d.getNo()+"\\n"+d.getName();
			if(i==0){
				list+=temp;
			}else{
				list+="\\n"+temp;
			}
			i++;
		}
		return list;
	}
	
	
	public static ColumnType get(String no){
		for(ColumnType d:ColumnType.values()){
			if((d.getNo()+"").equals(no)){
				return d;
			}
		}
		return null;
	}
	
	
}
