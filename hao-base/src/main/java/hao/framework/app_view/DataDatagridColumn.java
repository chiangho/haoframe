package hao.framework.app_view;

import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;

public class DataDatagridColumn {

	private String title;
	private String field;
	private EntityInfo entity;
	private Property   property;
	private String     action;
	
	
	public DataDatagridColumn(String title, String field, String action, EntityInfo entity) {
		this.title = title;
		this.field = field;
		this.entity = entity;
		this.property = entity.getPropertyByFieldName(field);
		this.action = action;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public EntityInfo getEntity() {
		return entity;
	}
	public void setEntity(EntityInfo entity) {
		this.entity = entity;
	}
	public Property getProperty() {
		return property;
	}
	public void setProperty(Property property) {
		this.property = property;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	
	
	
	
}
