package hao.framework.db.dao.jdbc;

import hao.framework.core.entity.Property;

/**
 * 字段映射信息
 * @author chianghao
 *
 */
public class FieldMapper {
	
	//主class
	private Class<?> mainClazz;
	//自己
	private Class<?> clazz;
	//序列
	private int      index;
	private String   columnLabel;
	private Property property;
	
	
	public FieldMapper(Class<?> clazz,Class<?> mainClazz, Property property, String columnLabel, int index) {
		this.clazz         = clazz;
		this.mainClazz     = mainClazz;
		this.index         = index;
		this.columnLabel   = columnLabel;
		this.property      = property;
	}


	public Class<?> getClazz() {
		return clazz;
	}


	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public String getColumnLabel() {
		return columnLabel;
	}


	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}


	public Property getProperty() {
		return property;
	}


	public void setProperty(Property property) {
		this.property = property;
	}


	public Class<?> getMainClazz() {
		return mainClazz;
	}


	public void setMainClazz(Class<?> mainClazz) {
		this.mainClazz = mainClazz;
	}
	
	
	
	
}
