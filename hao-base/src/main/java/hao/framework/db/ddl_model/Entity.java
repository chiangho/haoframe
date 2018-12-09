package hao.framework.db.ddl_model;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表对象
 * @author chianghao
 *
 */
public class Entity {

	/**
	 * 表名
	 */
	public String tableName;
	/***
	 * 备注
	 */
	public String remark;
	/**
	 * 列信息
	 */
	public List<Attribute> attributes;
	
	/**
	 * 构造函数
	 */
	public Entity(String tableName){
		this.tableName  = tableName;
		this.attributes = new ArrayList<Attribute>();
	}
	public void addAttribute(Attribute attribute){
		this.attributes.add(attribute);
	}
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
