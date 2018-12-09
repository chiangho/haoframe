package hao.framework.core.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import hao.framework.core.CommonVar.SystemMsg;
import hao.framework.core.expression.HaoException;
import hao.framework.db.ddl_model.Attribute;
import hao.framework.db.ddl_model.Entity;
import hao.framework.utils.StringUtils;

/***
 * 类的对象信息
 * @author chianghao
 *
 */
public class EntityInfo  implements Cloneable{

	private Class<?> clazz;
	private hao.framework.annotation.Entity  annoTable;
	private String tableName;
	private String tableTitle;
	private ArrayList<Property> propertys;
	
	Map<String,Field> fileMaps = new HashMap<String,Field>();
	
	public EntityInfo(Class<?> clazz,hao.framework.annotation.Entity annoTable) {
		this.clazz = clazz;
		this.annoTable = annoTable;
		initTable();
	}
	
	/**
	 * 获取对象名称
	 * @return
	 */
	public String getName() {
		return clazz.getSimpleName();
	}
	
	/**
	 * 获取默认别名
	 * @return
	 */
	public String getDefaultAlias() {
		return StringUtils.firstCharLowercase(clazz.getSimpleName());
	}
	
	/***
	 * 初始化表格
	 */
	private void initTable() {
		this.tableName = this.annoTable.tableName();
		if(this.tableName==null||this.tableName.equals("")) {
			this.tableName = StringUtils.camelToUnderline(this.clazz.getSimpleName());
		}
		this.tableTitle = this.annoTable.title();
		propertys = initPropertys();
	}
	
	/***
	 * 初始化元素
	 * @return
	 */
	private ArrayList<Property> initPropertys() {
		ArrayList<Property> ps = new ArrayList<Property>();
		Vector<Class<?>> vectorClass = new Vector<Class<?>>();
		
		Class<?> tempClass = this.clazz;
		while(!tempClass.isAssignableFrom(Object.class)) {
			vectorClass.add(tempClass);
			tempClass = tempClass.getSuperclass();
		}
		
//		if(BaseModel.class.isAssignableFrom(this.clazz)) {
//			//当前类是baseModel的子类
//			Class<?> tempClass = this.clazz;
//			while(BaseModel.class.isAssignableFrom(tempClass)) {
//				vectorClass.add(tempClass);
//				tempClass = tempClass.getSuperclass();
//			}
//		}else {
//			vectorClass.add(this.clazz);
//		}
		
		for(int i=(vectorClass.size()-1);i>=0;i--) {
			Class<?> entityClazz = vectorClass.get(i);
			for(Field field:entityClazz.getDeclaredFields()) {
				fileMaps.put(field.getName(), field);
			}
		}
		for(String key:fileMaps.keySet()) {
			Property p = Property.createProperty(this.clazz,fileMaps.get(key));
			if(p!=null) {
				ps.add(p);
			}
		}
		return ps;
	}
	
	public hao.framework.annotation.Entity getAnnoTable() {
		return annoTable;
	}

	public void setAnnoTable(hao.framework.annotation.Entity annoTable) {
		this.annoTable = annoTable;
	}

	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<Property> getPropertys() {
		return propertys;
	}
	public void setPropertys(ArrayList<Property> propertys) {
		this.propertys = propertys;
	}
	public String getTableTitle() {
		return tableTitle;
	}
	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}


	/***
	 * 获取property
	 * @param field
	 * @return
	 */
	public Property getPropertyByFieldName(String fieldName) {
		for(Property p:this.propertys) {
			if(p.isDB()) {
				if(p.getField().getName().equals(fieldName)) {
					return p;
				}
			}
		}
		return null;
	}
	
	
	/***
	 * 获取property
	 * @param field
	 * @return
	 */
	public Property getProperty(String fieldName) {
		for(Property p:this.propertys) {
			if(p.isDB()) {
				if(p.getField().getName().equals(fieldName)) {
					return p;
				}
			}
		}
		return null;
	}


	/***
	 * 获取创建表结构的语句
	 * @return
	 * @throws HaoException 
	 */
	public String getCreateTableSql() throws HaoException {
		if(this.propertys==null||this.propertys.size()==0) {
			throw new HaoException(SystemMsg.dml_create_table_no_column.getCode(),SystemMsg.dml_create_table_no_column.getMsg());
		}
		StringBuffer sb = new StringBuffer(" create table "+this.tableName+" ( ");
		for(Property p:this.propertys) {
			if(p.isDB()) {
				String columnSql  = p.getCreateColumnSql();
				sb.append(columnSql+",");
			}
		}
		Set<String>  colomnskeys = getPrimaryKeys();
		if(colomnskeys!=null&&colomnskeys.size()>0) {
			String primarykeysql = "PRIMARY KEY(";
			for(String key:colomnskeys) {
				primarykeysql+=key+",";
			}
			if(primarykeysql.endsWith(",")) {
				primarykeysql =primarykeysql.substring(0,primarykeysql.lastIndexOf(","));
			}
			primarykeysql+=")";
			sb.append(primarykeysql);
		}
		
		String sql = sb.toString();
		if(sql.endsWith(",")) {
			sql = sql.substring(0,sql.lastIndexOf(","));
		}
		return sql+");";
	}


	public Set<String> getPrimaryKeys() {
		if(this.propertys==null||this.propertys.size()==0) {
			return new HashSet<String>();
		}
		Set<String> sets = new HashSet<String>();
		for(Property p:this.propertys) {
			if(p.isDB()) {
				if(p.isPrimary()) {
					sets.add(p.getColumnName());
				}
			}
		}
		return sets;
	}
	
	public Set<Property> getPropertyIsKey() {
		if(this.propertys==null||this.propertys.size()==0) {
			return new HashSet<Property>();
		}
		Set<Property> sets = new HashSet<Property>();
		for(Property p:this.propertys) {
			if(p.isDB()) {
				if(p.isPrimary()) {
					sets.add(p);
				}
			}
		}
		return sets;
	}


	@Override
	public EntityInfo clone() {
		EntityInfo cloneObject = null;  
        try{  
        	cloneObject = (EntityInfo)super.clone();  
        }catch(CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return cloneObject;  
	}

	
	public String[] getDBFields() {
		ArrayList<String> fields = new ArrayList<String>();
		for(Property p:this.getPropertys()) {
			if(p.isDB()) {
				fields.add(p.getName());
			}
		}
		return fields.toArray(new String[fields.size()]);
	}

	public Entity toDBEntity() {
		Entity entity = new Entity(this.getTableName());
		entity.setRemark(this.tableTitle);
		for(Property p:this.getPropertys()) {
			if(p.isDB()) {
				Attribute a = new Attribute(p.getColumnName(), p.getColumnType());
				a.setDefaultValue(p.getColumnDefaultValue());
				a.setLength(p.getLength());
				a.setPrecesion(p.getPrecision());
				a.setRemark(p.getColumnRemark());
				entity.addAttribute(a);
			}
		}
		return entity;
	}

   
	 
}
