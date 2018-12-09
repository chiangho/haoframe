package org.hao.db.test;

import hao.framework.database.entity.annotation.Column;
import hao.framework.database.entity.annotation.Entity;

@Entity(title="公司信息")
public class Company {
	
	@Column(title="主键")
	private long id;
	
	@Column(title="名称")
	private String name;
	
	@Column(title="是否删除")
	private String isDel;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getIsDel() {
		return isDel;
	}
	public void setIsDel(String isDel) {
		this.isDel = isDel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	
	
}
