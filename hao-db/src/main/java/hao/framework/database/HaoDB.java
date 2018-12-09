package hao.framework.database;

import java.util.List;
import java.util.Map;
import java.util.Set;

import hao.framework.core.expression.HaoException;
import hao.framework.core.utils.PackageUtil;
import hao.framework.database.dbmanage.DataMeta;
import hao.framework.database.entity.EntityInfo;
import hao.framework.database.entity.annotation.Entity;

/**
 * hao DB 初始化文件
 * @author chianghao
 */
public class HaoDB {
	
	/**
	 * 初始化db
	 * @param packages 包名。扫描包下被Entity注解的类
	 */
	public static void init(String...packages) {
		for(String p:packages) {
			Set<Class<?>> classes = PackageUtil.findAnnotationClass(p, Entity.class);
			for(Class<?> clazz:classes) {
				Entity entityanno = clazz.getAnnotation(Entity.class);
				if(entityanno != null) {
					EntityInfo entityInfo = new EntityInfo(clazz,entityanno);
					EntityInfo.putEntity(entityInfo);
				}
			}
		}
	}
	
	
	/***
	 * 自动维护创建数据库表
	 * @param entitys
	 * @throws HaoException 
	 */
	public static void createTable(DBType dbType,String host,int port,String dbName,String account, String password){
		Map<String,EntityInfo> eMap = EntityInfo.getEntityInfos(); 
		for(String key:eMap.keySet()) {
			EntityInfo e = eMap.get(key);
			DataMeta dm = DataMeta.createDataMeta(dbType,host,port,dbName,account,password);
			List<String> sqlCommands = dm.getDDLSql(e);
			try {
				dm.executeBatch(sqlCommands);
			} catch (HaoException e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
