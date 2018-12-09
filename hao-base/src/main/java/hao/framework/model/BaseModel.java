package hao.framework.model;

import java.util.Date;

import hao.framework.annotation.Attribute;
import hao.framework.annotation.Column;

/***
 * 数据模型的基础类
 * @author chianghao
 *
 */
public class BaseModel{

	/***
	 * 主键
	 */
	@Attribute(title="主键", length = 20)
	@Column(primary = true)
	public long id;
	
	
	/***
	 * 创建时间
	 */
	@Attribute(title="创建时间")
	@Column(isNull=true)
	public Date createTime;
	
	/**
	 * 创建人
	 */
	@Attribute(title="创建人")
	@Column(isNull=true)
	public String createUser;
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
	
}
