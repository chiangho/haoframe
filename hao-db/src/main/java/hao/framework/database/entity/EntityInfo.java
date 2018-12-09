package hao.framework.database.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hao.framework.core.expression.HaoException;
import hao.framework.core.utils.StringUtils;
import hao.framework.database.entity.annotation.Entity;



/***
 * 类的对象信息
 * @author chianghao
 *
 */
public class EntityInfo  implements Cloneable{

	private static Map<String,EntityInfo> entityInfoMap = new HashMap<String,EntityInfo>();
	
	public static EntityInfo getEntity(String classname) {
		if(entityInfoMap.containsKey(classname)) {
			return null;
		}
		return entityInfoMap.get(classname); 
	}
	
	public static Map<String,EntityInfo> getEntityInfos() {
		return entityInfoMap;
	}
	
	public static EntityInfo getEntity(Class<?> clazz) {
		if(!entityInfoMap.containsKey(clazz.getName())) {
			return null;
		}
		return entityInfoMap.get(clazz.getName()); 
	}
	
	public static void putEntity(EntityInfo entityInfo) {
		entityInfoMap.put(entityInfo.getClazz().getName(), entityInfo);
	}
	
	private Class<?> clazz;
	private Entity   annoTable;
	private String   tableName;
	private String   tableTitle;
	private List<ColumnInfo> columns;
	Map<String,Field> fileMaps = new HashMap<String,Field>();
	
	public EntityInfo(Class<?> clazz,Entity annoTable) {
		this.clazz = clazz;
		this.annoTable = annoTable;
		initTable();
	}
	
	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Entity getAnnoTable() {
		return annoTable;
	}

	public void setAnnoTable(Entity annoTable) {
		this.annoTable = annoTable;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

	public List<ColumnInfo> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnInfo> columns) {
		this.columns = columns;
	}

	public Map<String, Field> getFileMaps() {
		return fileMaps;
	}

	public void setFileMaps(Map<String, Field> fileMaps) {
		this.fileMaps = fileMaps;
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
		this.tableName = this.annoTable.name();
		if(this.tableName==null||this.tableName.equals("")) {
			this.tableName = StringUtils.camelToUnderline(this.clazz.getSimpleName());
		}
		this.tableTitle = this.annoTable.title();
		columns = initcolumns();
	}
	
	/***
	 * 初始化元素
	 * @return
	 */
	private ArrayList<ColumnInfo> initcolumns() {
		ArrayList<ColumnInfo> ps = new ArrayList<ColumnInfo>();
		List<Class<?>> vectorClass = new ArrayList<Class<?>>();
		
		Class<?> tempClass = this.clazz;
		while(!tempClass.isAssignableFrom(Object.class)) {
			vectorClass.add(tempClass);
			tempClass = tempClass.getSuperclass();
		}
		for(int i=(vectorClass.size()-1);i>=0;i--) {
			Class<?> entityClazz = vectorClass.get(i);
			for(Field field:entityClazz.getDeclaredFields()) {
				fileMaps.put(field.getName(), field);
			}
		}
		for(String key:fileMaps.keySet()) {
			ColumnInfo p = ColumnInfo.createProperty(this.clazz,fileMaps.get(key));
			if(p!=null) {
				ps.add(p);
			}
		}
		return ps;
	}

	
	
	/***
	 * 获取property
	 * @param field
	 * @return
	 */
	public ColumnInfo getColumnInfo(String fieldName) {
		for(ColumnInfo p:this.columns) {
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
		if(this.columns==null||this.columns.size()==0) {
			throw new HaoException("","");
		}
		StringBuffer sb = new StringBuffer(" create table "+this.tableName+" ( ");
		for(ColumnInfo p:this.columns) {
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
		if(this.columns==null||this.columns.size()==0) {
			return new HashSet<String>();
		}
		Set<String> sets = new HashSet<String>();
		for(ColumnInfo p:this.columns) {
			if(p.isDB()) {
				if(p.isPrimary()) {
					sets.add(p.getColumnName());
				}
			}
		}
		return sets;
	}
	
	public Set<ColumnInfo> getPropertyIsKey() {
		if(this.columns==null||this.columns.size()==0) {
			return new HashSet<ColumnInfo>();
		}
		Set<ColumnInfo> sets = new HashSet<ColumnInfo>();
		for(ColumnInfo p:this.columns) {
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

	
	public Set<String> getDBFields() {
		Set<String> fields = new HashSet<String>();
		for(ColumnInfo p:this.getColumns()) {
			if(p.isDB()) {
				fields.add(p.getName());
			}
		}
		return fields;
	}
	
	 
}
