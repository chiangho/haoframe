package hao.framework.generation;

import java.io.File;

import hao.framework.core.HaoProperty;
import hao.framework.core.entity.EntityInfo;
import hao.framework.utils.StringUtils;

public abstract class GenerationTool {

	
	static final String model_pack_name = "model";
	static final String dao_pack_name="dao";
	static final String service_pack_name="service";
	static final String action_pack_name = "action";
	
	EntityInfo entityInfo;
	File   basefile;
	
	public EntityInfo getEntity() {
		return entityInfo;
	}
	public void setEntity(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}
	public File getBasefile() {
		return basefile;
	}
	public void setBasefile(File basefile) {
		this.basefile = basefile;
	}
	/***
	 * 获取基础包
	 * @return
	 */
	public String getBasePack() {
		return "hao.webapp."+HaoProperty.getValue("webAppName")+".";
	}
	
	/***
	 * 获取相对包的地址
	 * @return
	 */
	public String getRelativePack() {
		String entityPackName =  entityInfo.getClazz().getPackage().getName();
		String prefix = this.getBasePack()+model_pack_name;
		if(entityPackName.equals(prefix)) {
			return "";
		}
		String relativePack = entityPackName.substring(prefix.length()+1);
		return relativePack;
	}
	
	
	public String getRelativePath() {
		if(getRelativePack().equals("")) {
			return StringUtils.camelToUnderline(entityInfo.getClazz().getSimpleName());
		}else {
			String path = getRelativePack().replace(".", File.separator);
			return path+File.separator+StringUtils.camelToUnderline(entityInfo.getClazz().getSimpleName());
		}
	}
	
	abstract public String getPack();
}
