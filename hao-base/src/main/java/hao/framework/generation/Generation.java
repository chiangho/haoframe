package hao.framework.generation;

import java.io.File;
import java.util.List;
import java.util.Map;

import hao.framework.core.entity.EntityInfo;
import hao.framework.core.expression.HaoException;
import hao.framework.db.DBType;
import hao.framework.db.DataMeta;
import hao.framework.db.ddl_model.Entity;
import hao.framework.utils.FileUtils;

/***
 * 代码工具，用于生成代码
 * 
 * @author chianghao
 */
public class Generation {

	/***
	 * 代码生成
	 * 
	 * @param entitys
	 * @throws HaoException 
	 */
	public static void generation(Map<String, EntityInfo> entitys) throws HaoException {
//		String dir = HaoProperty.getValue("code_generation_dir");
//		File file = new File(dir);
//		if(!file.exists()) {
//			file.mkdirs();
//		}
		//创建数据库表
		createTable(entitys);
		//创建dao层
		//createDao(file,entitys);
		//创建servier层
		
		//创建action层
		
		//创建表单
		
		//创建list
		entitys = null;
	}

	
	/***
	 * 创建dao层代码
	 * @param file
	 * @param entitys
	 */
	public static void createDao(File parent, Map<String, EntityInfo> entitys) {
		// TODO Auto-generated method stub
		File daoFile = FileUtils.createChildDir(parent, "dao");
		for(String className:entitys.keySet()) {
			EntityInfo e = entitys.get(className);
			DaoGenerationTool tool = new DaoGenerationTool(daoFile,e);
			tool.createDaoClass();
			tool.createDaoMapper();
		}
	}

	/***
	 * 创建数据库表
	 * @param entitys
	 * @throws HaoException 
	 */
	public static void createTable(Map<String, EntityInfo> entitys) throws HaoException {
		for(String key:entitys.keySet()) {
			EntityInfo e = entitys.get(key);
			DataMeta dm = DataMeta.createDataMeta(DBType.mysql, "127.0.0.1",13306, "demo","root", "111111");
			Entity entity = e.toDBEntity();
			List<String> sqlCommands = dm.getDDLSql(entity);
			dm.executeBatch(sqlCommands);
		}
	}
	
}
