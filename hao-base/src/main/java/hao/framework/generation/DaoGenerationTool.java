package hao.framework.generation;

import java.io.File;

import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.db.ColumnDataType;
import hao.framework.generation.model.DaoMapperModel;
import hao.framework.generation.model.DaoModel;
import hao.framework.utils.FileUtils;
import hao.framework.utils.StringUtils;

/**
 * dao层工具
 * @author chianghao
 */
public class DaoGenerationTool extends GenerationTool{

	/***
	 * dao层模版
	 */
	private static final String DAO_MODEL_NAME="dao.ftl";
	/***
	 * dao层mapper映射文件模版
	 */
	private static final String DAO_MAPPER_MODEL_NAME="dao_mapper.ftl";
	
	public DaoGenerationTool(File f,EntityInfo e) {
		entityInfo = e;
		basefile = f;
	}
	
	public String getPack() {
		if(getRelativePack().equals("")) {
			return this.getBasePack()+dao_pack_name+"."+StringUtils.camelToUnderline(entityInfo.getClazz().getSimpleName());
		}else {
			return (this.getBasePack()+dao_pack_name+"."+getRelativePack())+"."+StringUtils.camelToUnderline(entityInfo.getClazz().getSimpleName());
		}
	}
	
	
	public String getClassName() {
		return entityInfo.getClazz().getSimpleName()+"Dao";
	}
	
	
	public String getImportFiles() {
		StringBuffer sb = new StringBuffer();
		sb.append("import "+entityInfo.getClazz().getName()+";");
		return sb.toString();
	}
	
	public String getFunctionGetList() {
		String modelName = entityInfo.getClazz().getSimpleName();
		String firstCharModelName = StringUtils.firstCharLowercase(modelName);
		StringBuffer sb = new StringBuffer();
		sb.append("List<"+modelName+"> getList(@Param(\""+firstCharModelName+"\") "+modelName+" "+firstCharModelName+");");
		return sb.toString();
	}
	
	public String getFunctionQueryById() {
		String modelName = entityInfo.getClazz().getSimpleName();
		StringBuffer sb = new StringBuffer();
		sb.append(""+modelName+" queryById(@Param(\"id\") long id);");
		return sb.toString();
	}
	
	
	/***
	 * 创建dao层java 文件
	 */
	public void createDaoClass() {
		File dir  =  FileUtils.createChildDir(basefile, getRelativePath());
		DaoModel daoModel = new DaoModel();
		daoModel.setPack(getPack());
		daoModel.setClassName(getClassName());
		daoModel.setImportFiles(getImportFiles());
		daoModel.setFunctionGetList(getFunctionGetList());
		daoModel.setFunctionQueryById(getFunctionQueryById());
		GenCodeIOUtils.print(DAO_MODEL_NAME, daoModel, dir,  getClassName()+".java");
	}

	
	public String getNamespace() {
		return getPack()+"."+getClassName();
	}
	
	
	public String getBaseMapId() {
		return StringUtils.firstCharLowercase(entityInfo.getClazz().getSimpleName())+"Map";
	}
	
	public String getBaseResultMap() {
		StringBuffer sb = new StringBuffer();
		sb.append(" \n");
		sb.append("<resultMap id=\""+getBaseMapId()+"\" type=\""+entityInfo.getClazz().getName()+"\">\n");
		for(Property p:entityInfo.getPropertys()) {
			sb.append("    <result column=\""+p.getColumnName()+"\" property=\""+p.getField().getName()+"\" />\n");
		}
		sb.append("</resultMap>\n");
		return sb.toString();
	}
	
	
	public String getFunctionQueryByIdMapper() {
		StringBuffer sb = new StringBuffer();
		sb.append(" \n");
		sb.append("<select id=\"queryById\" resultMap=\""+getBaseMapId()+"\" >\n");
		sb.append("  select * from "+entityInfo.getTableName()+" where id = #{id}   \n");
		sb.append("</select> \n");
		return sb.toString();
	}
	
	public String getFunctionGetListMapper() {
		StringBuffer sb = new StringBuffer();
		sb.append(" \n");
		sb.append("<select id=\"getList\" resultMap=\""+getBaseMapId()+"\" >  \n");
		sb.append("  select * from "+entityInfo.getTableName()+" \n");
		sb.append("  <where>\n");
		for(Property p:entityInfo.getPropertys()) {
			String fileName = p.getField().getName();
			String columnName = p.getColumnName();
			ColumnDataType pType = p.getColumnType();
			switch (pType){
			case STRING:
				sb.append("      <if test=\" "+fileName+" !=null and "+fileName+" != '' \">\n");
				sb.append("          <![CDATA[ and "+columnName+"=#{"+fileName+"}]]>\n");
				sb.append("      </if>\n");	
				break;
			case DECIMAL:
				sb.append("      <if test=\" "+fileName+" !=null  \">\n");
				sb.append("          <![CDATA[ and "+columnName+"=#{"+fileName+"}]]>\n");
				sb.append("      </if>\n");	
				break;
			case INT:
				sb.append("      <if test=\" "+fileName+" !=null  \">\n");
				sb.append("          <![CDATA[ and "+columnName+"=#{"+fileName+"}]]>\n");
				sb.append("      </if>\n");	
				break;
			case TEXT:
				sb.append("      <if test=\" "+fileName+" !=null and "+fileName+" != '' \">\n");
				sb.append("          <![CDATA[ and "+columnName+"=#{"+fileName+"}]]>\n");
				sb.append("      </if>\n");	
				break;
			case TIME:
				sb.append("      <if test=\" "+fileName+" !=null and "+fileName+" != '' \">\n");
				sb.append("          <![CDATA[ and "+columnName+"=#{"+fileName+"}]]>\n");
				sb.append("      </if>\n");	
				break;
			case DATE:
				sb.append("      <if test=\" "+fileName+" !=null and "+fileName+" != '' \">\n");
				sb.append("          <![CDATA[ and "+columnName+"=#{"+fileName+"}]]>\n");
				sb.append("      </if>\n");	
				break;
			default :
				sb.append("      <if test=\" "+fileName+" !=null and "+fileName+" != '' \">\n");
				sb.append("          <![CDATA[ and "+columnName+"=#{"+fileName+"}]]>\n");
				sb.append("      </if>\n");	
				break;
			}
		}
		sb.append("  </where>\n");
		sb.append("</select>\n");
		return sb.toString();
	}
	
	/***
	 * 创建dao层 mapping映射文件
	 */
	public void createDaoMapper() {
		File dir  =  FileUtils.createChildDir(basefile, getRelativePath());
		DaoMapperModel model = new DaoMapperModel();
		model.setNamespace(getNamespace());
		model.setBaseResultMap(getBaseResultMap());
		model.setFunctionQueryByIdMapper(getFunctionQueryByIdMapper());
		model.setFunctionGetListMapper(getFunctionGetListMapper());
		GenCodeIOUtils.print(DAO_MAPPER_MODEL_NAME, model, dir, getClassName()+"Mapper.xml");
	}
	
	
	
}
