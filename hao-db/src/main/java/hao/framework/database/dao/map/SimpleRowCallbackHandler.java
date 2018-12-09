package hao.framework.database.dao.map;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;

import hao.framework.core.utils.ClassUtils;
import hao.framework.database.dao.sql.SQLJoin;
import hao.framework.database.dao.sql.SQLRead;
import hao.framework.database.entity.ColumnInfo;

public class SimpleRowCallbackHandler implements RowCallbackHandler{

	private SQLRead sqlRead;
	
	private List<Object> list;
	
	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	public SimpleRowCallbackHandler(SQLRead sqlRead) {
		this.sqlRead = sqlRead;
		this.list    = new ArrayList<Object>();
	}
	
	/**
	 * 获取tagClass目标对象中的 class的对象名称
	 * @param tagClass
	 * @param clazz
	 * @return
	 */
	private String getObjectNameInBean(Class<?> tagClass,Class<?> clazz) {
		for(Field f:tagClass.getDeclaredFields()) {
			if(f.getType().isAssignableFrom(clazz)) {
				return f.getName();
			}
		}
		return null;
	}
	
	@Override
	public void processRow(ResultSet rs) throws SQLException {
		try {
			Object bean = sqlRead.getClazz().newInstance();
			//获取bean对应的数据
			for (ColumnInfo p : sqlRead.getEntityInfo().getColumns()) {
				if (p.isDB() == false) {
					continue;
				}
				if (p.getColumnName() == null || p.getColumnName().equals("")) {
					continue;
				}
				String columnName = sqlRead.getEntityInfo().getDefaultAlias()+"_"+p.getColumnName();
				Object value      = rs.getObject(columnName);
				if(value!=null) {
					Object tagValue = p.formatterObjectValue(value);
					ClassUtils.setFieldValue(bean, p.getName(), tagValue);
				}
			}
			for (String className : sqlRead.getJoins().keySet()) {
				//检查bean对象中有无json是对象
				SQLJoin join = sqlRead.getJoins().get(className);
				if (join.getEntity() == null) {
					continue;
				}
				String fieldName = getObjectNameInBean(sqlRead.getClazz(),join.getClazz());
				if(fieldName==null||fieldName.equals("")) {
					continue;
				}
				if(join.getFields()==null||join.getFields().size()==0) {
					join.setFields(join.getEntity().getDBFields());
				}
				Object tempBean = join.getClazz().newInstance();
				for (String field : join.getFields()) {
					ColumnInfo p = join.getEntity().getColumnInfo(field);
					if (p == null) {
						continue;
					}
					if (p.isDB() == false) {
						continue;
					}
					String columnName = join.getEntity().getDefaultAlias()+"_"+p.getColumnName();
					Object value      = rs.getObject(columnName);
					if(value!=null) {
						Object tagValue = p.formatterObjectValue(value);
						ClassUtils.setFieldValue(tempBean, p.getName(), tagValue);
					}
				}
				ClassUtils.setFieldValue(bean, fieldName, tempBean);
			}
			this.list.add(bean);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
