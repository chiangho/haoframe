package hao.framework.db.ddl_model;

import hao.framework.db.ColumnDataType;



/**
 * 数据类表字段对象
 * @author Administrator
 *
 */
public class Attribute {
	/**
	 * 列名
	 */
	private String columnName;
	/**
	 * 类型
	 */
	private ColumnDataType type;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 长度
	 */
	private int length;
	/**
	 * 精度
	 */
	private int precesion;
	/**
	 * 默认值
	 */
	private String defaultValue;
	
	/**
	 * 初始化，默认没有长度和精度
	 */
	public Attribute(String columnName,ColumnDataType type){
		this.columnName = columnName;
		this.type       = type;
		length = -1;
		precesion = -1;
	}
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public ColumnDataType getType() {
		return type;
	}
	public void setType(ColumnDataType type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getPrecesion() {
		return precesion;
	}
	public void setPrecesion(int precesion) {
		this.precesion = precesion;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	} 
	
}
