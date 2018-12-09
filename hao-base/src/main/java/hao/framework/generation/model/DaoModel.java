package hao.framework.generation.model;

public class DaoModel {

	private String pack;
	private String importFiles;
	private String className;
	private String functionQueryById;
	private String functionGetList;
	
	
	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
	}
	public String getImportFiles() {
		return importFiles;
	}
	public void setImportFiles(String importFiles) {
		this.importFiles = importFiles;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getFunctionQueryById() {
		return functionQueryById;
	}
	public void setFunctionQueryById(String functionQueryById) {
		this.functionQueryById = functionQueryById;
	}
	public String getFunctionGetList() {
		return functionGetList;
	}
	public void setFunctionGetList(String functionGetList) {
		this.functionGetList = functionGetList;
	}
	
}
