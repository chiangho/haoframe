package hao.framework.generation.model;

public class DaoMapperModel {

	
	private String namespace;
	private String baseResultMap;
	private String functionQueryByIdMapper;
	private String functionGetListMapper;
	
	
	
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getBaseResultMap() {
		return baseResultMap;
	}
	public void setBaseResultMap(String baseResultMap) {
		this.baseResultMap = baseResultMap;
	}
	public String getFunctionQueryByIdMapper() {
		return functionQueryByIdMapper;
	}
	public void setFunctionQueryByIdMapper(String functionQueryByIdMapper) {
		this.functionQueryByIdMapper = functionQueryByIdMapper;
	}
	public String getFunctionGetListMapper() {
		return functionGetListMapper;
	}
	public void setFunctionGetListMapper(String functionGetListMapper) {
		this.functionGetListMapper = functionGetListMapper;
	}
	
	
}
