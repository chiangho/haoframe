package hao.framework.app_view;

import java.util.ArrayList;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;


/***
 * 视图的网格信息
 * @author chianghao
 *
 */
public class DataDatagrid {

	/**
	 * 实体类
	 */
	private Class<?> clazz;
	
	/***
	 * 实体类信息
	 */
	private EntityInfo entity;
	
	/***
	 * 是否分页
	 */
	private boolean isPage;
	
	/**
	 * 字段信息
	 */
	private ArrayList<DataDatagridColumn> columns;

	public DataDatagrid(String clazz,boolean isPage) throws ClassNotFoundException {
		this.clazz = Class.forName(clazz);
		this.entity = SystemConfig.getEntity(this.clazz);
		this.isPage = isPage;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public EntityInfo getEntity() {
		return entity;
	}

	public void setEntity(EntityInfo entity) {
		this.entity = entity;
	}

	public ArrayList<DataDatagridColumn> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<DataDatagridColumn> columns) {
		this.columns = columns;
	}

	public boolean isPage() {
		return isPage;
	}

	public void setPage(boolean isPage) {
		this.isPage = isPage;
	}

	public void putColumn(DataDatagridColumn column) {
		columns.add(column);
	}
	
	
	
	
	
}
