package hao.framework.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hao.framework.app_view.AppInfo;
import hao.framework.app_view.FormInfo;
import hao.framework.app_view.ParseAppViewXml;
import hao.framework.core.CommonVar.JSON_TYPE;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.sequence.Seq;
import hao.framework.utils.PackageUtil;

/***
 * 系统配置
 * @author chianghao
 */
public class SystemConfig {

	
	static Logger log = LogManager.getLogger(SystemConfig.class);
	
	/***
	 * 返回的json形式  json或者jsonp
	 */
	public static JSON_TYPE jsonType=null;
	
	/***
	 * 系统默认时间格式
	 */
	public static String SYSTEM_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/***
	 * 对象信息
	 */
	private static HashMap<String,EntityInfo> entityMap = new HashMap<String,EntityInfo>();
	/***
	 * 表单信息
	 */
	private static HashMap<String,FormInfo>  formMap = new HashMap<String,FormInfo>();
	/***
	 * 表单信息
	 */
	private static HashMap<String,AppInfo>  appMap = new HashMap<String,AppInfo>();
	
	static {
		jsonType = JSON_TYPE.json;
	}
	
	
	////////////////////////////////////////////////////////////////////////
	public static void addApp(String name,AppInfo app) {
		appMap.put(name, app);
	}
	public static void removeApp(String name) {
		appMap.remove(name);
	}
	public static AppInfo getApp(String name) {
    	return appMap.get(name).clone();
    }
	public static HashMap<String,AppInfo> getAppMap(){
    	HashMap<String,AppInfo> clomeAppMap =   (HashMap<String, AppInfo>) appMap.clone();
		return clomeAppMap;
    }   
	/////////////////////////////////////////////////////////////////////////
	
	
	public static void addForm(String formName,FormInfo form) {
		formMap.put(formName, form);
	}
	
	public static void removeForm(String formName) {
		formMap.remove(formName);
	}
    public static FormInfo getFromInfo(String formName) {
    	FormInfo formInfo = (formMap.get(formName)).clone();
    	return formInfo;
    }
    
    @SuppressWarnings("unchecked")
	public static HashMap<String,FormInfo> getFormMap(){
    	HashMap<String,FormInfo> clomeFormMap =   (HashMap<String, FormInfo>) formMap.clone();
		return clomeFormMap;
    }
	//////////////////////////////////////////////////////////////////////
	/**
	 * 获取对象信息
	 * @param clazz
	 * @return
	 */
	public static EntityInfo getEntity(Class<?> clazz) {
		if(!entityMap.containsKey(clazz.getName())) {
			log.debug(clazz.getName()+"对应的实体对象信息不存在，请检查是否注解进来！");
			return null;
		}
		if(entityMap.get(clazz.getName())==null) {
			log.debug(clazz.getName()+"对应的实体对象信息不存在，请检查是否注解进来！");
			return null;
		}
		EntityInfo entityInfo = (entityMap.get(clazz.getName())).clone();
		return entityInfo;
	}
	/***
	 * 获取全部entity对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,EntityInfo> getEntitys() {
		HashMap<String,EntityInfo> clomeEntityMap =   (HashMap<String, EntityInfo>) entityMap.clone();
		return clomeEntityMap;
	}
	public static void systemEntityInit(String ENTITY_PACKAGE) {
		if(ENTITY_PACKAGE != null && !ENTITY_PACKAGE.equals("")) {
			Set<Class<?>> classes = PackageUtil.findAnnotationClass(ENTITY_PACKAGE, hao.framework.annotation.Entity.class);
			for(Class<?> clazz:classes) {
				hao.framework.annotation.Entity entityanno = clazz.getAnnotation(hao.framework.annotation.Entity.class);
				if(entityanno != null) {
					EntityInfo entityInfo = new EntityInfo(clazz,entityanno);
					entityMap.put(clazz.getName(), entityInfo);
				}
			}
		}
	}
	//////////////////////////////////////////////////////////
	/***
	 * 初始化json返回类型
	 * @param jsonTypeName
	 */
	public static void initJsonType(String jsonTypeName) {
		for(JSON_TYPE _json_type :JSON_TYPE.values()) {
			String _name = _json_type.getName().toUpperCase();
			if(_name.equals(jsonTypeName)) {
				jsonType = _json_type;
				break;
			}
		}
		
	}
	/**
	 * 初始化序列服务
	 */
	public static void initSeq() {
		Seq.setSnowflakeIdWorker();
	}

	/***
	 * 解析文件视图
	 * @param inputStream
	 */
	public static void parseAppViewConfig(InputStream inputStream) {
		ParseAppViewXml.parse(inputStream);
	}
	
}
